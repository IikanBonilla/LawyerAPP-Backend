package Development.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Development.DTOs.GetProcessDTO;
import Development.DTOs.GetProcessIdentificationDTO;
import Development.Model.Process;

public interface ProcessRepository extends JpaRepository<Process, String>{
	@Query("""
        SELECT new Development.DTOs.GetProcessDTO(
            p.identification,
            p.radicationDate,
            p.officeName,
            p.ponente,
            p.processType,
            p.processClass,
            p.subClassProcess,
            p.recurso,
            p.contenidoDeRadicacion
        )
        FROM Process p
        JOIN p.idLawyer l
        JOIN l.idUser u
        where u.id = :idUser
            """)
    public List<GetProcessDTO> findByIdUserId(@Param("idUser") String idUser);
    
    @Query("""
        SELECT new Development.DTOs.GetProcessIdentificationDTO(
            p.identification,
            p.processType

        )
        FROM ClientProcess cp
        JOIN cp.idProcess p
        JOIN cp.idClient c
        WHERE c.id = :idClient
        ORDER BY p.radicationDate desc
        """)
    public List<GetProcessIdentificationDTO> findIdentificationByClientId(@Param("idClient") String idClient);

    @Query("""
        SELECT new Development.DTOs.GetProcessDTO(
            p.identification,
            p.radicationDate,
            p.officeName,
            p.ponente,
            p.processType,
            p.processClass,
            p.subClassProcess,
            p.recurso,
            p.contenidoDeRadicacion
        )
        FROM Process p
        WHERE p.identification = :identification            
            """)
    Optional<GetProcessDTO> findByIdentification(@Param("identification") String identification);


    boolean existsByIdentification(String identification);

    @Query("""  
        SELECT new Development.DTOs.GetProcessIdentificationDTO(
            p.identification,
            p.processType
        )
        FROM Process p 
        WHERE p.idLawyer.id = :idLawyer 
        AND p.status = :status
            """)
    List<GetProcessIdentificationDTO> findByLawyerAndStatus(@Param("idLawyer") String idLawyer, 
                                              @Param("status") String status);

}
