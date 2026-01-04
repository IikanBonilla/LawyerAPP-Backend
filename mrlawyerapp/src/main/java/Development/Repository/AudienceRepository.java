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
            a.id,
            a.address,
            a.meetingLink,
            a.audience_date,
            a.status,
            a.idClient.id
        )
        FROM Audience a
        WHERE a.idClient.id = :idClient
        ORDER BY a.audience_date DESC
        """)
    List<GetAudienceDTO> findByClientId(@Param("idClient") String idClient);

    @Query("""
    SELECT new Development.DTOs.GetAudienceDTO(
        a.id,
        a.address,
        a.meetingLink,
        a.audience_date,
        a.status,
        a.idClient.id
    )
    FROM Audience a
    JOIN a.idLawyer l
    JOIN l.idUser u
    WHERE u.id = :idUser
    ORDER BY a.audience_date DESC
    """)
    List<GetAudienceDTO> findByUserId(@Param("idUser") String idUser);

    @Query("""
    SELECT new Development.DTOs.GetAudienceDTO(
        a.id,
        a.address,
        a.meetingLink,
        a.audience_date,
        a.status,
        a.idClient.id
    )
    FROM Audience a
    JOIN a.idLawyer l
    JOIN l.idUser u
    WHERE u.id = :idUser
    AND a.status = :status
    """)
    List<GetAudienceDTO> findByUserAndStatus(@Param("idUser") String idUser, @Param("status") Status status);
}
