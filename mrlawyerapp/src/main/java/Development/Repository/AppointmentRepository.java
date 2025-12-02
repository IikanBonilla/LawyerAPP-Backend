package Development.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Development.DTOs.AppointmentDTO;
import Development.Model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, String>{
    
    @Query("""
        SELECT DISTINCT NEW Development.DTOs.AppointmentDTO(
            a.id,
            a.name,
            a.description,
            a.dateTime
        )
        FROM Appointment a
        JOIN a.idLawyer l
        WHERE l.idUser.id = :idUser
        ORDER BY a.dateTime desc          
            """)
    public List<AppointmentDTO> findByUserId(@Param("idUser") String idUser);

    public void deleteByDateTimeBefore(LocalDateTime dateTime);
}
