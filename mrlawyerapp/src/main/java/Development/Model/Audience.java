package Development.Model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Data;

@Entity
@Table(name = "Audience")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Audience {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String address;
    private LocalDate date;
    private String status;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "idProcess")
    private Process idProcess;
}
