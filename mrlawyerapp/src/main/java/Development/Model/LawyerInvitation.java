package Development.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
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
@Table(name = "lawyer_invitation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LawyerInvitation {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private String identification;
    private String email;

    @ToString.Exclude
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="idLawFirm")
    private LawFirm idLawFirm;

    private boolean used = false;
}
