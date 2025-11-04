package Development.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Development.Model.Client;
import Development.Model.ClientDTO;

public interface ClientRepository extends JpaRepository<Client, String>{
    @Query("""
    SELECT DISTINCT new Development.Model.ClientDTO(
        c.identification,
        c.firstName,
        c.lastName,
        c.email,
        c.phoneNumber
    )
    FROM ClientLawyer cl
    JOIN cl.idClient c
    WHERE cl.idLawyer.id = :idLawyer
    """)
    List<ClientDTO> listClientsByLawyerId(@Param("idLawyer") String idLawyer);

    @Query("""
    SELECT DISTINCT new Development.Model.ClientDTO(
        c.identification,
        c.firstName,
        c.lastName,
        c.email,
        c.phoneNumber
    )
    FROM Process p
    JOIN p.clients cp
    JOIN cp.idClient c
    WHERE p.id = :idProcess
    AND p.idLawyer.id = :idLawyer
    """)
    List<ClientDTO> listClientsByProcessAndLawyer(
        @Param("idProcess") String idProcess,
        @Param("idLawyer") String idLawyer
    );

}
