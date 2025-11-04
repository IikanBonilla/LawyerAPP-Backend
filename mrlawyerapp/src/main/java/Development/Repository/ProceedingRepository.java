package Development.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import Development.Model.Proceeding;

public interface ProceedingRepository extends JpaRepository<Proceeding, String>{
    
}
