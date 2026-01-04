package Development.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Development.Model.Role;
import Development.Model.User;

public interface UserRepository extends JpaRepository<User, String>{
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByRole(Role role);
}
