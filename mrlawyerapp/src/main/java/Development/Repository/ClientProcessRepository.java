package Development.Repository;



import org.springframework.data.jpa.repository.JpaRepository;

import Development.Model.ClientProcess;

public interface ClientProcessRepository extends JpaRepository<ClientProcess, String>{
    boolean existsByIdClientIdAndIdProcessId(String idClient, String idProcess);
    void deleteByIdClientId(String idClient);
    void deleteAllByIdClientId(String idClient);
    void deleteAllByIdProcessId(String idProcess);

    ClientProcess findByIdProcessId(String idProcess);
}
