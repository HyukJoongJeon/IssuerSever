package blue_walnut.IssuerSever.service;

import blue_walnut.IssuerSever.client.TspClient;
import blue_walnut.IssuerSever.domain.Approval;
import blue_walnut.IssuerSever.domain.IssuerLog;
import blue_walnut.IssuerSever.exception.ApprovalException;
import blue_walnut.IssuerSever.exception.ErrorCode;
import blue_walnut.IssuerSever.model.CardInfo;
import blue_walnut.IssuerSever.model.PaymentReq;
import blue_walnut.IssuerSever.model.PaymentRes;
import blue_walnut.IssuerSever.model.TokenReq;
import blue_walnut.IssuerSever.model.enums.ActionType;
import blue_walnut.IssuerSever.model.enums.ServiceType;
import blue_walnut.IssuerSever.model.enums.StatusType;
import blue_walnut.IssuerSever.repository.ApprovalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Calendar;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssuerService {
    private final String TID_PREFIX = "ISSUER-SERVICE";

    private final TspClient tspClient;
    private final IssuerLogService issuerLogService;
    private final ApprovalRepository approvalRepository;

    public Boolean validateCard(CardInfo cardInfo) {
        // 카드정보 복호화후 DB에서 조회후 유효성 체크를 해야하지만
        // 무조건 TRUE 리턴
        return true;
    }

    public PaymentRes payment(PaymentReq req)  {
        // 중복 결제 요청 처리
        if (isDuplicateRequest(req.trTid())) {
            log.warn("중복 결제 요청 : trTid={} 이미 처리된 요청입니다.", req.trTid());
            throw new ApprovalException(ErrorCode.APPROVAL_DUPLICATE_FAILED);
        }

        String issuerTid = generateIssuerTid();
        Approval approval = createApproval(req, issuerTid);
        IssuerLog issuerLog = createIssuerLog(approval);

        issuerLogService.updateStatus(approval, issuerLog,  ServiceType.APPROVAL, StatusType.WT, null);
        try {
            verifyToken(new TokenReq(req.token(), req.userCi(), false), approval);
        } catch (ApprovalException e) {
            issuerLogService.updateStatus(approval, issuerLog, ServiceType.TKN_USED, StatusType.FL, e.getMessage());
            rollbackToken(new TokenReq(req.token(), req.userCi(), true), approval);
        }

        return processApproval(req, approval, issuerLog, issuerTid);
    }
    private PaymentRes processApproval(PaymentReq req, Approval approval, IssuerLog issuerLog, String issuerTid) {
        try {
            PaymentRes result = createPaymentResponse(req, issuerTid);
            issuerLogService.updateStatus(approval, issuerLog, ServiceType.APPROVAL, StatusType.DN, "승인 성공");
            return result;
        } catch (Exception e) {
            rollbackToken(new TokenReq(req.token(), req.userCi(), true), approval);
            issuerLogService.updateStatus(approval, issuerLog, ServiceType.APPROVAL, StatusType.FL, e.getMessage());
            throw e;
        }
    }
    private boolean isDuplicateRequest(String trTid) {
        return approvalRepository.findByTrTid(trTid).isPresent();
    }

    private String generateIssuerTid() {
        return TID_PREFIX + String.format("%015d", Calendar.getInstance().getTimeInMillis());
    }

    private Approval createApproval(PaymentReq req, String issuerTid) {
        return Approval.builder()
                .issuerTid(issuerTid)
                .token(req.token())
                .trTid(req.trTid())
                .userCi(req.userCi())
                .amount(req.amount())
                .statusType(StatusType.WT)
                .actionType(ActionType.PAY)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    private IssuerLog createIssuerLog(Approval approval) {
        return IssuerLog.builder()
                .userCi(approval.getUserCi())
                .trTid(approval.getTrTid())
                .issuerTid(approval.getIssuerTid())
                .statusType(StatusType.WT)
                .serviceType(ServiceType.INIT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private void verifyToken(TokenReq tokenReq, Approval approval) {
        log.info("토큰 사용처리  : userCi = {}, token ={}", approval.getUserCi(), approval.getToken());
        tspClient.verifyToken(tokenReq);
    }

    private void rollbackToken(TokenReq tokenReq, Approval approval) {
        tspClient.verifyToken(tokenReq);
        log.error("토큰 사용처리 롤백 성공 : userCi = {}, token ={}", approval.getUserCi(), approval.getToken());
    }

    private PaymentRes createPaymentResponse(PaymentReq req, String issuerTid) {
        return PaymentRes.builder()
                .token(req.token())
                .actionType(ActionType.PAY)
                .trTid(req.trTid())
                .issuerTid(issuerTid)
                .amount(req.amount())
                .depositedAt(LocalDateTime.now())
                .errCode("0000")
                .errMsg("승인 성공")
                .build();
    }
    private void test() {
            throw new ApprovalException(ErrorCode.APPROVAL_FAILED);
    }
}
