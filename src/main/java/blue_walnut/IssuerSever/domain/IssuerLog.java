package blue_walnut.IssuerSever.domain;

import blue_walnut.IssuerSever.model.enums.ServiceType;
import blue_walnut.IssuerSever.model.enums.StatusType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssuerLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long srl;
    private String userCi;

    private String trTid;
    private String issuerTid;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    private StatusType statusType;

    private String errMsg;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
