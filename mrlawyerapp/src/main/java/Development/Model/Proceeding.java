package Development.Model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "proceeding")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Proceeding{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private String proceeding;
    private String anotation;
    
    private LocalDate proceedingDate;
    private LocalDate startTermDate;
    private LocalDate endTermDate;
    private LocalDate registerDate;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "idProcess")
    private Process idProcess;

}