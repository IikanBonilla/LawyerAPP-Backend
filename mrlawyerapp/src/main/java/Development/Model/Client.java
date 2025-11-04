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

        @Column(unique = true, nullable = false)
        private Long identification;

        private String firstName;
        private String lastName;
        private String email;
        private Long phoneNumber;
        
        @ToString.Exclude
        @JsonIgnore
        @OneToMany(mappedBy = "idClient", fetch = FetchType.LAZY)
        private List<ClientProcess> processes = new ArrayList<>();
        
        @ToString.Exclude
        @JsonIgnore
        @OneToMany(mappedBy = "idClient", fetch = FetchType.LAZY)
        private List<ClientLawyer> lawyers = new ArrayList<>();

        @ToString.Exclude
        @JsonManagedReference
        @OneToMany(mappedBy = "idClient", fetch = FetchType.LAZY)
        private List<Document> documents = new ArrayList<>();


}
