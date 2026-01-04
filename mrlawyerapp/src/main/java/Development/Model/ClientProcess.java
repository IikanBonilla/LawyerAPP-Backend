package Development.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "client_process")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientProcess {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "idClient")
    private Client idClient;

    @ManyToOne
    @JoinColumn(name = "idProcess")
    private Process idProcess;

}
    