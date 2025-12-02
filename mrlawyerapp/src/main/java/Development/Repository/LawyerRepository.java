package Development.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Development.DTOs.GetLawyerDTO;
import Development.Model.LawyerProfile;

public interface LawyerRepository extends JpaRepository<LawyerProfile, String>{
    
    //Find by User 
    @Query("""
    SELECT new Development.DTOs.GetLawyerDTO(
        l.id,
        l.identification,
        l.fullName,
        l.email,
        l.idUser.id,
        l.idLawFirm.id,
        l.status
        
    )
    FROM LawyerProfile l
    WHERE l.idUser.id = :userId
    """)
    GetLawyerDTO findByIdUser(String userId);
    boolean existsByEmail(String email); 
    boolean existsByIdentification(Long identification);

    @Query("""
        SELECT new Development.DTOs.GetLawyerDTO(
            l.id,
            l.identification,
            l.fullName,
            l.email,
            l.idUser.id,
            l.idLawFirm.id,
            l.status
            
        )
        FROM LawyerProfile l
        WHERE l.idLawFirm.id = :idFirm
        ORDER BY l.fullName ASC
            """)
    List<GetLawyerDTO> findAllLawyersByFirm(@Param("idFirm") String idFirm);

@Query("SELECT lp FROM LawyerProfile lp WHERE lp.idUser.username = :username")
LawyerProfile findByUserUsername(@Param("username") String username);

}
