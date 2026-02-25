package Development.Services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import Development.DTOs.AppointmentDTO;
import Development.DTOs.CreateAppointmentDTO;
import Development.DTOs.UpAppointmentDTO;
import Development.Model.Appointment;
import Development.Model.LawyerProfile;
import Development.Repository.AppointmentRepository;
import Development.Repository.LawyerRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AppointmentServices implements IAppointmentServices{

    
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private LawyerRepository lawyerRepository;
    @Autowired
    private NotificationServices notificationService;

    @Override
    public Appointment saveForLaywer(String idLawyer, CreateAppointmentDTO appointmentDTO) {
        LawyerProfile lawyer = lawyerRepository.findById(idLawyer).orElseThrow(
            () -> new EntityNotFoundException("No existe un abogado con id: " + idLawyer)
            );
        Appointment appointment = new Appointment();
        appointment.setName(appointmentDTO.getName());
        appointment.setDescription(appointmentDTO.getDescription());
        appointment.setDateTime(appointmentDTO.getDateTime());
        appointment.setIdLawyer(lawyer);

        Appointment newAppointment = appointmentRepository.save(appointment);

        notificationService.sendNotificationAccount(idLawyer, "APPOINTMENT_CREATED", newAppointment);
        notificationService.sendNotificationAdmin(lawyer.getIdLawFirm().getId(), "APPOINTMENT_CREATED", newAppointment);

        return newAppointment;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredAppointments() {
        LocalDateTime now = LocalDateTime.now();
        appointmentRepository.deleteByDateTimeBefore(now);
        System.out.println("Citas antiguas eliminadas automaticamente a las " + now);
    }

    @Override
    public Appointment update(String id, UpAppointmentDTO appointmentDTO) {

        Appointment appointment = appointmentRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("No existe un abogado con id: " + id)
            );

        appointment.setDateTime(appointmentDTO.getDateTime());

        LawyerProfile lawyer = appointment.getIdLawyer();
        Appointment appointmentUpdated = appointmentRepository.save(appointment);

        notificationService.sendNotificationAccount(lawyer.getId(), "APPOINTMENT_UPDATED", appointmentUpdated);
        notificationService.sendNotificationAdmin(lawyer.getIdLawFirm().getId(), "APPOINTMENT_UPDATED", appointmentUpdated);

        return appointmentUpdated;
    }

    @Override
    public void delete(String id) {
        
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("No existe cita con ese id")
        );
        LawyerProfile lawyer = appointment.getIdLawyer();
        try{
                appointmentRepository.deleteById(id);
        }catch(Exception ex){
                throw new RuntimeException("Error al eliminar cita" + ex);
        }

        

        Map<String, String> payload = Map.of("idAppointment", id);

        notificationService.sendNotificationAccount(lawyer.getId(), "APPOINTMENT_DELETED", payload);
        notificationService.sendNotificationAdmin(lawyer.getIdLawFirm().getId(), "APPOINTMENT_DELETED", payload);
    }

    @Override
    public List<AppointmentDTO> findAppointmentByUserId(String idUser) {
        if (idUser == null || idUser.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }

        try{
            return appointmentRepository.findByUserId(idUser);
        }catch(Exception ex){
            throw new RuntimeException("Error al obtener citas", ex);
        }
    }

}
