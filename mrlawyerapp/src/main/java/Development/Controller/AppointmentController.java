package Development.Controller;

import Development.DTOs.AppointmentDTO;
import Development.DTOs.CreateAppointmentDTO;
import Development.DTOs.UpAppointmentDTO;
import Development.Model.Appointment;
import Development.Services.AppointmentServices;
import jakarta.persistence.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;



@RestController

@RequestMapping("/api/appointments")
public class AppointmentController {
    private Logger logger = LoggerFactory.getLogger(Appointment.class);
    @Autowired
    private AppointmentServices appointmentServices;

    @GetMapping("/user/{idUser}")
    public ResponseEntity<?> getAppointmentByUserId(@PathVariable String idUser) {
        try{
            List<AppointmentDTO> appointments = appointmentServices.findAppointmentByUserId(idUser);
            logger.info("Citas encontradas: {}", appointments.size());
            return ResponseEntity.ok(appointments);
        }catch(IllegalArgumentException ex){
            // Manejo de error de validación (400 Bad Request)
            return ResponseEntity.badRequest().body(ex.getMessage());
        }catch(RuntimeException ex){
            // Manejo de error interno (500 Internal Server Error)
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.internalServerError().body("Error inesperado al obtener citas");
        }
    }
    
    
    // Crear cita para un abogado
    @PostMapping("/lawyer/{idLawyer}")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<?> createAppointmentForLawyer(@PathVariable String idLawyer, @RequestBody CreateAppointmentDTO appointmentDTO) {
        try {
            Appointment appointment = appointmentServices.saveForLaywer(idLawyer, appointmentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la cita: " + ex.getMessage());
        }
    }

    
    // Actualizar cita
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<?> updateAppointment(@PathVariable String id, @RequestBody UpAppointmentDTO appointmentDTO) {
        try {
            Appointment appointment = appointmentServices.update(id, appointmentDTO);
            return ResponseEntity.ok(appointment);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la cita: " + ex.getMessage());
        }
    }

   
    // Eliminar cita
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<?> deleteAppointment(@PathVariable String id) {
        try {
            appointmentServices.delete(id);
            return ResponseEntity.ok("Cita eliminada exitosamente");
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
