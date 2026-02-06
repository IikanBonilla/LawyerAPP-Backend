package Development.Model;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="lawyer_profile")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LawyerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    
    private String identification;

    private String fullName;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE; //ACTIVE - INACTIVE
    
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="idLawFirm", nullable = false)
    private LawFirm idLawFirm;

    @OneToOne
    @JoinColumn(name = "idUser")
    private User idUser;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "idLawyer", fetch = FetchType.LAZY)
    private List<Client> clients = new ArrayList<>();

    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "idLawyer", fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "idLawyer", fetch = FetchType.LAZY)
    private List<Audience> audiences = new ArrayList<>();


}
