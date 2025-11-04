package Development.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "client_lawyer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientLawyer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "idClient")
    private Client idClient;

    @ManyToOne
    @JoinColumn(name = "idLawyer")
    private LawyerProfile idLawyer;

}
    