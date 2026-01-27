package Development.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name="User_Table")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String username;
    
    private String password;

    private String email;

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN o LAWYER

    @JsonManagedReference
    @OneToMany(mappedBy = "idUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ForgotPassword> forgotPassword = new ArrayList<>();

    @JsonIgnore
    @OneToOne(mappedBy = "idUser", fetch = FetchType.LAZY)
    private LawyerProfile lawyerProfile;

    @JsonIgnore
    @OneToOne(mappedBy = "idUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LawFirm lawFirm;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

}