package Development.Repository;



import org.springframework.data.jpa.repository.JpaRepository;

import Development.Model.ClientProcess;

public interface ClientProcessRepository extends JpaRepository<ClientProcess, String>{
    boolean existsByIdClientIdAndIdProcessId(String clientId, String processId);
}
