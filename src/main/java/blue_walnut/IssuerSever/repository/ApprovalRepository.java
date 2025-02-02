package blue_walnut.IssuerSever.repository;

import blue_walnut.IssuerSever.domain.Approval;
import blue_walnut.IssuerSever.domain.IssuerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, String> {
    @Override
    <T extends Approval> T save(T approval);
    Optional<Approval> findBySrl(Long srl);
    Optional<Approval> findByTrTid(String trTid);

    List<Approval> findAll();
}
