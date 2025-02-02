package blue_walnut.IssuerSever.repository;

import blue_walnut.IssuerSever.domain.IssuerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IssuerLogRepository extends JpaRepository<IssuerLog, Long> {
    @Override
    <T extends IssuerLog> T save(T history);
    Optional<IssuerLog> findBySrl(Long srl);
    Optional<IssuerLog> findByUserCi(String userCi);
    Optional<IssuerLog> findByCreatedAtBetween(LocalDateTime startDt, LocalDateTime endDt);

    List<IssuerLog> findAll();
}
