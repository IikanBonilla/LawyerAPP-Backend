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
@Table(name="Client")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private String id;

        private Long identification;

        private String firstName;
        private String lastName;
        private String email;
        private Long phoneNumber;
        @Enumerated(EnumType.STRING)
        private Status status = Status.ACTIVE;
        
        @ToString.Exclude
        @JsonIgnore
        @OneToMany(mappedBy = "idClient", fetch = FetchType.LAZY)
        private List<ClientProcess> processes = new ArrayList<>();
        
        @ToString.Exclude
        @JsonIgnore
        @ManyToOne
        @JoinColumn(name = "idLawyer")
        private LawyerProfile idLawyer;

        @ToString.Exclude
        @JsonManagedReference
        @OneToMany(mappedBy = "idClient", fetch = FetchType.LAZY)
        private List<Document> documents = new ArrayList<>();

        @ToString.Exclude
        @JsonManagedReference
        @OneToMany(mappedBy = "idClient", fetch = FetchType.LAZY)
        private List<Audience> audiences = new ArrayList<>();



}
