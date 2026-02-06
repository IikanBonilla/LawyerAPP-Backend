package Development.Model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "appointment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    
    private String name;
    private String description;
    private LocalDateTime dateTime;
   
    
    @ToString.Exclude
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "idLawyer", nullable = false)
    private LawyerProfile idLawyer;







}