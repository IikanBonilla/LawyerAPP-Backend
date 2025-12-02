package Development.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Development.DTOs.LawyerInvitationDTO;
import Development.Model.LawFirm;
import Development.Model.LawyerInvitation;

public interface LawyerInvitationRepository extends JpaRepository<LawyerInvitation, String>{
    Optional<LawyerInvitation> findByEmailAndIdentificationAndUsedFalse(String email, Long identification);
    boolean existsByEmailAndIdLawFirmAndUsedFalse(String email, LawFirm lawFirm);
    boolean existsByIdentificationAndIdLawFirmAndUsedFalse(Long Identification, LawFirm lawFirm);

    @Query("""
        SELECT DISTINCT new Development.DTOs.LawyerInvitationDTO(
            i.id,
            i.identification,
            i.email,
            i.used

        )
        FROM LawyerInvitation i
        JOIN i.idLawFirm f
        WHERE f.id = :idFirm     
            
        """)
    List<LawyerInvitationDTO> findByIdLawFirm(@Param("idFirm") String idFirm);
}
