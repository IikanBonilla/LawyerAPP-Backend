package Development.Model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="Audience")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Audience {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String address;
    private String meetingLink;
    private LocalDateTime audience_date;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "idClient")
    private Client idClient;
    
    @ToString.Exclude
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "idLawyer")
    private LawyerProfile idLawyer;

}
