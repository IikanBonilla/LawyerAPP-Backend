package Development.Services;

import java.time.LocalDateTime;
import java.util.List;

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

        return appointmentRepository.save(appointment);
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

        return appointmentRepository.save(appointment);
    }

    @Override
    public void delete(String id) {
        if(!appointmentRepository.existsById(id))
        throw new IllegalStateException("Ya existe una cita con id: ");
       try{
            appointmentRepository.deleteById(id);
       }catch(Exception ex){
            throw new RuntimeException("Error al eliminar cita" + ex);
       }
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
