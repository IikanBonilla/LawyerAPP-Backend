package Development.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Development.Model.LawFirm;

public interface LawFirmRepository extends JpaRepository<LawFirm, String>{
    Optional<LawFirm> findByIdUserId(String idUser);
    boolean existsByFirmName(String firmName);
}
