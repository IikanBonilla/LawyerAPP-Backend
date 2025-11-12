package Development.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Development.DTOs.GetAudienceDTO;
import Development.Model.Audience;
import Development.Model.Status;

public interface AudienceRepository extends JpaRepository<Audience, String>{

    @Query("""
        SELECT new Development.DTOs.GetAudienceDTO(
            a.address,
            a.meetingLink,
            a.date,
            a.status
        )
        FROM Audience a
        WHERE a.idProcess.id = :idProcess
        """)
    List<GetAudienceDTO> findByProcessId(@Param("idProcess") String idProcess);

    @Query("""
    SELECT new Development.DTOs.GetAudienceDTO(
            a.address,
            a.meetingLink,
            a.date,
            a.status
    ) 
    FROM Audience a
    JOIN a.idProcess p
    JOIN p.clients cp
    WHERE cp.idClient.id = :clientId
    """)
    List<GetAudienceDTO> findByClientId(@Param("clientId") String clientId);

    List<Audience> findByStatus(Status status);
}
