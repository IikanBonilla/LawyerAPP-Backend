package Development.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import Development.Model.Audience;

public interface AudienceRepository extends JpaRepository<Audience, String>{

    
}
