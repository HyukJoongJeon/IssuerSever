package blue_walnut.IssuerSever.domain;

import blue_walnut.IssuerSever.model.enums.ActionType;
import blue_walnut.IssuerSever.model.enums.StatusType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long srl;

    private String userCi;
    private String token;
    private String trTid;
    private String issuerTid;
    private Long amount;

    @Enumerated(EnumType.STRING)
    private StatusType statusType;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private String errMsg;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
