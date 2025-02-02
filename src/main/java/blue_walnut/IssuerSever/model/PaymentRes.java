package blue_walnut.IssuerSever.model;

import blue_walnut.IssuerSever.model.enums.ActionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long srl;

    @Schema(description = "토큰")
    private String token;

    @Schema(description = "구분")
    private ActionType actionType;

    @Schema(description = "결제 주문번호")
    private String trTid;

    @NotBlank(message = "결제 금액을 입력해 주세요")
    @Min(0)
    @Schema(description = "결제금액")
    private Long amount;

    @Schema(description = "카드사 승인번호")
    private String issuerTid;


    @Schema(description = "에러 코드")
    private String errCode;


    @Schema(description = "에러 메시지")
    private String errMsg;


    @Schema(description = "결제완료시간")
    private LocalDateTime depositedAt;



}
