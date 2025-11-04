package Development.Model;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="Lawyer_Profile")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LawyerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true, nullable = false)
    private Long identification;

    private String fullName;
    @Enumerated(EnumType.STRING)
    private LawyerStatus status; //ACTIVE - INACTIVE
    
    @OneToOne
    @JoinColumn(name = "idUser")
    private User idUser;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "idLawyer", fetch = FetchType.LAZY)
    private List<ClientLawyer> clients = new ArrayList<>();

    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "idLawyer", fetch = FetchType.LAZY)
    private List<Process> processes = new ArrayList<>();


}
