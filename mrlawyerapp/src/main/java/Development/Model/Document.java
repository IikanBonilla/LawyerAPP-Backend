package Development.Model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "document")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private String type;
    private String originalName ;

    @Lob
    private byte[] data;
    
    private String filePath;
    private Long size;

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
