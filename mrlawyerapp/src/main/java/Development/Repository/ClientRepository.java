package Development.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Development.DTOs.GetClientDTO;
import Development.DTOs.GetClientFullNameDTO;
import Development.Model.Client;

public interface ClientRepository extends JpaRepository<Client, String>{
    @Query("""
        SELECT DISTINCT new Development.DTOs.GetClientDTO(
            c.identification,
            c.firstName,
            c.lastName,
            c.email,
            c.phoneNumber
        )
        FROM ClientLawyer cl
        JOIN cl.idClient c
        JOIN cl.idLawyer l
        JOIN l.idUser u
        WHERE u.id = :idUser
        ORDER BY c.lastName, c.firstName
        """)
    List<GetClientDTO> findByUserId(@Param("idUser") String idUser);

    @Query("""
            SELECT new Development.DTOs.GetClientDTO(
            c.identification,
            c.firstName,
            c.lastName,
            c.email,
            c.phoneNumber
            )
            FROM Client c
            WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            """)
    List<GetClientDTO> searchClientsByName(@Param("searchTerm") String searchTerm);

    @Query("""
        SELECT new Development.DTOs.GetClientFullNameDTO(
            c.firstName,
            c.lastName
        )
        FROM ClientProcess cp
        JOIN cp.idClient c
        JOIN cp.idProcess p
        WHERE p.id = :idProcess
        """)
    List<GetClientFullNameDTO> findByIdProcess(@Param("idProcess") String idProcess);

    boolean existsByIdentification(Long identification);
}
