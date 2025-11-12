package Development.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Development.Model.LawyerProfile;

public interface LawyerRepository extends JpaRepository<LawyerProfile, String>{
    
    //Find by User 
    public Optional<LawyerProfile> findByIdUserId(String userId);
    boolean existsByEmail(String email); 

}
