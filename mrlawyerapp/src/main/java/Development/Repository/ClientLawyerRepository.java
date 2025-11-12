package Development.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import Development.Model.ClientLawyer;

public interface ClientLawyerRepository extends JpaRepository<ClientLawyer, String>{
    boolean existsByIdClientIdAndIdLawyerId(String clientId, String lawyerId);
}
