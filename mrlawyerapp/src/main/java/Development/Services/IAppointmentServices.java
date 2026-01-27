package Development.Services;

import java.util.List;

import Development.DTOs.AppointmentDTO;
import Development.DTOs.CreateAppointmentDTO;
import Development.DTOs.UpAppointmentDTO;
import Development.Model.Appointment;

public interface IAppointmentServices {
    public List<AppointmentDTO> findAppointmentByUserId(String userId);
    public Appointment saveForLaywer(String idLawyer, CreateAppointmentDTO appointmentDTO);
    public void deleteExpiredAppointments();
    public Appointment update(String id, UpAppointmentDTO appointmentDTO);
    public void delete(String id);
}
