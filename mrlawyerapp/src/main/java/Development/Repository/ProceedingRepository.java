package Development.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import Development.DTOs.GetProceedingDTO;
import Development.Model.Proceeding;
import java.util.List;


public interface ProceedingRepository extends JpaRepository<Proceeding, String>{
    
    @Query("""
            SELECT new Development.DTOs.GetProceedingDTO(
            p.proceeding,
            p.anotation,
            p.proceedingDate,
            p.startTermDate,
            p.endTermDate,
            p.registerDate
            )
            FROM Proceeding p
            WHERE p.idProcess.id = :idProcess
            ORDER BY p.proceedingDate desc
            """)
    List<GetProceedingDTO> findByIdProcess(String idProcess);

    
    
}
