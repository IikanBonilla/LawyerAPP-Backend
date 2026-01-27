package Development.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Development.Model.ForgotPassword;
import Development.Model.User;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, String> {

    @Query("Select fp from ForgotPassword fp where fp.otp = ?1 and fp.idUser = ?2")
    public Optional<ForgotPassword> findByOtpAndUser(int otp, User user);

    @Query("SELECT fp FROM ForgotPassword fp WHERE fp.idUser = :user")
    ForgotPassword findByIdUser(@Param("user") User user);
}