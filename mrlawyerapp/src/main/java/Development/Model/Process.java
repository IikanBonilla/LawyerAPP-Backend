package Development.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
@Entity
@Table(name = "Process")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Process{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String identification;

    private LocalDate radicationDate;
    private String officeName;
    private String ponente;
    private String processType;
    private String processClass;
    private String subClassProcess;
    private String recurso;
    private String contenidoDeRadicacion;
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;


    
    @OneToMany(mappedBy = "idProcess", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private List<ClientProcess> clients = new ArrayList<>();



    @OneToMany(mappedBy = "idProcess", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonManagedReference
    private List<Proceeding> proceedings = new ArrayList<>();

    
    @OneToMany(mappedBy = "idProcess", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonManagedReference
    private List<Document> documents = new ArrayList<>();


    @ManyToOne
    @ToString.Exclude
    @JsonBackReference
    @JoinColumn(name = "idLawyer")
    private LawyerProfile idLawyer;

    private LocalDateTime startDate;

    @PrePersist
    protected void onCreate(){
        startDate =  LocalDateTime.now();
    }

}
