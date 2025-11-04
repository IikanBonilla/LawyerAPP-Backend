package Development.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Development.Model.Process;

public interface ProcessRepository extends JpaRepository<Process, String>{
	
}
