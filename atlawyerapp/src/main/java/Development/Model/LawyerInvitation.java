package Development.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "Lawyer_Invitation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LawyerInvitation {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Long identification;
    private String email;

    @ToString.Exclude
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="idLawFirm")
    private LawFirm idLawFirm;

    private boolean used = false;
}
