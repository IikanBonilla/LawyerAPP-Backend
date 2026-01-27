package Development.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Development.DTOs.ClientDTO;
import Development.DTOs.GetClientFullNameDTO;
import Development.Model.Client;
import Development.Model.Status;

public interface ClientRepository extends JpaRepository<Client, String>{
    @Query("""
        SELECT DISTINCT new Development.DTOs.ClientDTO(
            c.id,
            c.identification,
            c.firstName,
            c.lastName,
            c.email,
            c.phoneNumber,
            c.status
        )
        FROM Client c
        JOIN c.idLawyer l
        JOIN l.idUser u
        WHERE u.id = :idUser
        ORDER BY c.lastName, c.firstName
        """)
    List<ClientDTO> findByUserId(@Param("idUser") String idUser);

        @Query("""
        SELECT DISTINCT new Development.DTOs.ClientDTO(
            c.id,
            c.identification,
            c.firstName,
            c.lastName,
            c.email,
            c.phoneNumber,
            c.status
        )
        FROM Client c
        JOIN c.idLawyer l
        JOIN l.idUser u
        WHERE u.id = :idUser
        AND c.status = :status
        ORDER BY c.lastName, c.firstName
        """)
    List<ClientDTO> findByUserIdAndStatus(@Param("idUser") String idUser, @Param("status") Status status);

    @Query("""
            SELECT new Development.DTOs.ClientDTO(
            c.id,
            c.identification,
            c.firstName,
            c.lastName,
            c.email,
            c.phoneNumber,
            c.status
            )
            FROM Client c
            WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            """)
    List<ClientDTO> searchClientsByName(@Param("searchTerm") String searchTerm);

    @Query("""
        SELECT new Development.DTOs.GetClientFullNameDTO(
            c.id,
            c.firstName,
            c.lastName
        )
        FROM ClientProcess cp
        JOIN cp.idClient c
        JOIN cp.idProcess p
        WHERE p.id = :idProcess
        """)
    List<GetClientFullNameDTO> findAllNamesByIdProcess(@Param("idProcess") String idProcess);

    @Query("""
    SELECT new Development.DTOs.GetClientFullNameDTO(
        c.id,
        c.firstName,
        c.lastName
    )
    FROM ClientProcess cp
    JOIN cp.idClient c
    JOIN cp.idProcess p
    WHERE p.id = :idProcess
    """)
    Optional<GetClientFullNameDTO> findNameByIdProcess(@Param("idProcess") String idProcess);

    @Query("""
    SELECT new Development.DTOs.GetClientFullNameDTO(
        c.id,
        c.firstName,
        c.lastName
    )
    FROM Client c 
    WHERE c.id = ?1
    """)
    Optional<GetClientFullNameDTO> findNameById(String id);

    boolean existsByIdentification(Long identification);
    boolean existsByIdentificationAndIdLawyerId(Long identification, String idLawyer);
    
    @Query("SELECT c FROM Client c WHERE c.id = :idClient AND c.idLawyer.id = :idLawyer")
    Optional<Client> findByIdAndLawyerId(@Param("idClient") String clientId, @Param("idLawyer") String lawyerId);

}

