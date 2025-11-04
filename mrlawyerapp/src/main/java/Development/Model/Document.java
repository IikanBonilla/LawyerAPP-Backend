package Development.Model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "Document")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String type; // "CONTRATO", "PODER", "EVIDENCIA"
    private String originalName ;

    @Lob
    private byte[] data;
    

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "idClient")
    private Client idClient;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "idProcess")
    private Process idProcess;
    
    
}
