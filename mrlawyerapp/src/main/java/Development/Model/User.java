package Development.Model;

import jakarta.persistence.*;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name="User")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN o LAWYER

    @OneToOne(mappedBy = "idUser", cascade = CascadeType.ALL)
    private LawyerProfile lawyerProfile;
}