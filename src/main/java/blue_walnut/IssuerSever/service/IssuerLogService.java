package blue_walnut.IssuerSever.service;

import blue_walnut.IssuerSever.domain.Approval;
import blue_walnut.IssuerSever.domain.IssuerLog;
import blue_walnut.IssuerSever.model.enums.ServiceType;
import blue_walnut.IssuerSever.model.enums.StatusType;
import blue_walnut.IssuerSever.repository.ApprovalRepository;
import blue_walnut.IssuerSever.repository.IssuerLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class IssuerLogService {
    private final ApprovalRepository approvalRepository;
    private final IssuerLogRepository issuerLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(Approval approval, IssuerLog issuerLog, ServiceType serviceType, StatusType statusType, String errMsg) {
        approval.setStatusType(statusType);
        approval.setUpdatedAt(LocalDateTime.now());
        approval.setErrMsg(StringUtils.substring(errMsg, 0, 254));
        approvalRepository.save(approval);

        issuerLog.setStatusType(statusType);
        issuerLog.setServiceType(serviceType);
        issuerLog.setUpdatedAt(LocalDateTime.now());
        issuerLog.setErrMsg(StringUtils.substring(errMsg, 0, 254));
        issuerLogRepository.save(issuerLog);
    }
}
