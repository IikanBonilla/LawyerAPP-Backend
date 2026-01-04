package Development.Model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="Law_Firm")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LawFirm {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String firmName;

    @Column(unique = true, nullable = false)
    private String email;

    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "idLawFirm", fetch = FetchType.LAZY)
    private List<LawyerProfile> lawyers = new ArrayList<>();

    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "idLawFirm", fetch = FetchType.LAZY)
    private List<LawyerInvitation> invitations = new ArrayList<>();

    @ToString.Exclude
    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "idAdmin")
    private User idAdmin;
}
