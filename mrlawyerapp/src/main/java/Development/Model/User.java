package Development.Model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE; // ACTIVE, INACTIVE, SUSPENDED

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN o LAWYER

    @JsonIgnore
    @OneToOne(mappedBy = "idUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LawyerProfile lawyerProfile;

    @JsonIgnore
    @OneToOne(mappedBy = "idAdmin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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

    @Override
    public boolean isAccountNonExpired() {
        return userStatus == UserStatus.ACTIVE; // La cuenta nunca expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return userStatus == UserStatus.ACTIVE;  // La cuenta nunca se bloquea
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userStatus == UserStatus.ACTIVE;  // Las credenciales nunca expiran
    }

    @Override
    public boolean isEnabled() {
        return userStatus == UserStatus.ACTIVE;  // El usuario siempre está habilitado
    }
}