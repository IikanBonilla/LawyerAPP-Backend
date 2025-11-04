package Development.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Development.Model.LawyerProfile;

public interface LawyerRepository extends JpaRepository<LawyerProfile, String>{
    
}
