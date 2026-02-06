package Development.Services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Development.Config.EmailConfig;
import Development.DTOs.CreateProceedingDTO;
import Development.DTOs.EmailBodyDTO;
import Development.DTOs.GetClientFullNameDTO;
import Development.DTOs.GetProceedingDTO;
import Development.Model.ClientProcess;
import Development.Model.LawyerProfile;
import Development.Model.Proceeding;
import Development.Model.Process;
import Development.Repository.ClientProcessRepository;
import Development.Repository.ClientRepository;
import Development.Repository.ProceedingRepository;
import Development.Repository.ProcessRepository;
import Development.Events.ProceedingCreatedEvent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NonUniqueResultException;
@Service
public class ProceedingServices implements IProceedingServices{

    private static final Logger log = LoggerFactory.getLogger(ProceedingServices.class);
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProceedingRepository proceedingRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    private NotificationServices notificationService;

    @Autowired
    private ClientProcessRepository clientProcessRepository;

    

    @Override
    public List<GetProceedingDTO> findByProcess(String idProcess) {
        if(!processRepository.existsById(idProcess))
        throw new EntityNotFoundException("No existe un processo con el id: " + idProcess);
        try{
            return proceedingRepository.findByIdProcess(idProcess);
        }catch(Exception ex){
            throw new RuntimeException("Error al obtener actuaciones para el proceso: " + idProcess, ex);
        }
    }

    @Override
    @Async
    public void sendProceedingEmail(Proceeding proceeding, Process process, String userEmail) {
        try {
            String fullName = getFullNameClientSafe(process.getId());
        EmailBodyDTO emailBody = buildSimpleHtmlEmail(process, proceeding, fullName, userEmail);
        eventPublisher.publishEvent(new ProceedingCreatedEvent(this, proceeding, emailBody));
            log.info("Email enviado asíncronamente para la actuación: {}", proceeding.getId());
        } catch (Exception e) {
            log.error("Error enviando email para la actuación {}: {}", proceeding.getId(), e.getMessage());
            // No relanzar la excepción para no interrumpir el flujo principal
        }
    }


    @Override
    public Proceeding createProceedingForProcess(String idProcess, CreateProceedingDTO proceedingDTO, String userEmail) {
        Process process = processRepository.findById(idProcess).orElseThrow(
        () -> new EntityNotFoundException("Proceso no encontrado: " + idProcess)
        );
        
        Proceeding proceeding = new Proceeding();
        proceeding.setProceeding(proceedingDTO.getProceeding());
        proceeding.setAnotation(proceedingDTO.getAnotation());
        proceeding.setProceedingDate(proceedingDTO.getProceedingDate());
        proceeding.setStartTermDate(proceedingDTO.getStartTermDate());
        proceeding.setEndTermDate(proceedingDTO.getEndTermDate());
        proceeding.setRegisterDate(proceedingDTO.getRegisterDate());
        proceeding.setIdProcess(process);

        Proceeding savedProceeding = proceedingRepository.save(proceeding);

        ClientProcess cp = clientProcessRepository.findByIdProcessId(idProcess);

        LawyerProfile lawyer = cp.getIdClient().getIdLawyer();

        notificationService.sendNotificationAccount(lawyer.getId(), "PROCEEDING_CREATED", savedProceeding);
        notificationService.sendNotificationAdmin(lawyer.getIdLawFirm().getId(), "PROCEEDING_CREATED", savedProceeding);
        

        if(emailConfig.isEmailEnabled()){
        sendProceedingEmail(savedProceeding, process, userEmail);
        }

        return savedProceeding;
    }

    /**
     * Método seguro para obtener nombre del cliente (no lanza excepciones)
     */
    private String getFullNameClientSafe(String idProcess) {
        if (idProcess == null || idProcess.trim().isEmpty()) {
            return "Cliente";
        }
        
        try {
            Optional<GetClientFullNameDTO> dto = clientRepository.findNameByIdProcess(idProcess);
            
            if (dto.isPresent()) {
                return dto.get().getFullName();
            }else{
                return "No se encontró cliente para proceso: " + idProcess;
            }   
        } catch (NonUniqueResultException e) {
            // Si hay múltiples, tomar el primero
            List<GetClientFullNameDTO> allClients = clientRepository.findAllNamesByIdProcess(idProcess);
            if (!allClients.isEmpty()) {
                return allClients.get(0).getFullName() + " (entre otros)";
            }else{
                return "No se encontró cliente para proceso: " + idProcess;
            }
            
        } catch (Exception e) {
            // Cualquier otro error, devolver algo seguro
            return "No se encontró cliente para proceso: " + idProcess;
        }
    }
    @Override
    public Proceeding findById(String id) {
        return proceedingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Actuacion no encontrada con id: " + id)
        );
    }

    @Transactional
    @Override
    public void delete(String id) {
        Proceeding proceeding = proceedingRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("No existe actuación con id: " + id)
        );
        proceedingRepository.deleteById(id);

        ClientProcess cp = clientProcessRepository.findByIdProcessId(proceeding.getIdProcess().getId());

        LawyerProfile lawyer = cp.getIdClient().getIdLawyer();

        Map<String, String> payload = Map.of("idProceeding", id);

        notificationService.sendNotificationAccount(lawyer.getId(), "PROCEEDING_DELETED", payload);
        notificationService.sendNotificationAdmin(lawyer.getIdLawFirm().getId(), "PROCEEDING_DELETED", payload);
    }
    
    private EmailBodyDTO buildSimpleHtmlEmail(Process process, Proceeding proceeding, 
                                            String clientFullName, String userEmail) {
        
        // HTML minimalista corregido para LocalDate
        String htmlContent = String.format("""
            <div style="
                font-family: 'Segoe UI', Arial, sans-serif;
                max-width: 600px;
                margin: 0 auto;
                background: #f8f9fa;
                padding: 20px;
                border-radius: 8px;
            ">
                <div style="
                    background: white;
                    padding: 25px;
                    border-radius: 6px;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                ">
                    <!-- Encabezado -->
                    <div style="text-align: center; margin-bottom: 20px;">
                        <div style="
                            background: #2c3e50;
                            color: white;
                            padding: 12px;
                            border-radius: 5px;
                            display: inline-block;
                            margin-bottom: 15px;
                        ">
                            <h2 style="margin: 0; font-size: 18px;">📋 Nueva Actuación</h2>
                        </div>
                        <p style="color: #666; margin: 0;">
                            Sistema de Gestión Legal - MR Asociados
                        </p>
                    </div>
                    
                    <!-- Información principal -->
                    <div style="
                        background: #f8f9fa;
                        padding: 15px;
                        border-radius: 5px;
                        margin-bottom: 20px;
                        border-left: 4px solid #4a6491;
                    ">
                        <h3 style="color: #2c3e50; margin-top: 0;">Información del Proceso</h3>
                        
                        <table style="width: 100%%; border-collapse: collapse;">
                            <tr>
                                <td style="padding: 8px 0; color: #555; width: 120px;"><strong>Proceso:</strong></td>
                                <td style="padding: 8px 0;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #555;"><strong>Radicado:</strong></td>
                                <td style="padding: 8px 0;"><strong>%s</strong></td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #555;"><strong>Cliente:</strong></td>
                                <td style="padding: 8px 0;">%s</td>
                            </tr>
                        </table>
                    </div>
                    
                    <!-- Detalles de la actuación -->
                    <div style="margin-bottom: 25px;">
                        <h3 style="color: #2c3e50; margin-bottom: 15px;">Detalles de la Actuación</h3>
                        
                        <div style="
                            background: white;
                            border: 1px solid #e9ecef;
                            border-radius: 5px;
                            padding: 15px;
                        ">
                            <p style="margin: 10px 0;">
                                <span style="color: #555; font-weight: 500;">Actuación:</span><br>
                                <span style="color: #222;">%s</span>
                            </p>
                            
                            %s  <!-- Anotación (condicional) -->
                            
                            <p style="margin: 10px 0;">
                                <span style="color: #555; font-weight: 500;">Fecha de actuación:</span><br>
                                <span style="color: #222;">%s</span>
                            </p>
                            
                            <p style="margin: 10px 0;">
                                <span style="color: #555; font-weight: 500;">Fecha de registro:</span><br>
                                <span style="color: #222;">%s</span>
                            </p>
                            
                            <p style="margin: 10px 0;">
                                <span style="color: #555; font-weight: 500;">Término inicia:</span><br>
                                <span style="color: #222;">%s</span>
                            </p>
                            
                            <p style="margin: 10px 0;">
                                <span style="color: #555; font-weight: 500;">Término finaliza:</span><br>
                                <span style="color: #222;">%s</span>
                            </p>
                        </div>
                    </div>
                    
                    <!-- Pie de página -->
                    <div style="
                        border-top: 1px solid #dee2e6;
                        padding-top: 15px;
                        text-align: center;
                        color: #6c757d;
                        font-size: 13px;
                    ">
                        <p style="margin: 5px 0;">
                            <strong>MR Asociados</strong><br>
                            Sistema de Gestión Legal
                        </p>
                        <p style="margin: 5px 0; font-size: 12px;">
                            Este es un mensaje automático. No responda a este correo.<br>
                        </p>
                    </div>
                </div>
            </div>
            """,
            // Parámetros para String.format()
            escapeHtml(process.getContenidoDeRadicacion()),
            escapeHtml(process.getIdentification()),
            escapeHtml(clientFullName),
            escapeHtml(proceeding.getProceeding()),
            // Anotación (condicional)
            proceeding.getAnotation() != null && !proceeding.getAnotation().isEmpty() 
                ? String.format("""
                    <p style="margin: 10px 0;">
                        <span style="color: #555; font-weight: 500;">Anotación:</span><br>
                        <span style="color: #222;">%s</span>
                    </p>
                    """, escapeHtml(proceeding.getAnotation()))
                : "",
            // Fechas (todas como LocalDate)
            formatDate(proceeding.getProceedingDate()),
            formatDate(proceeding.getRegisterDate()),
            formatDate(proceeding.getStartTermDate()),
            formatDate(proceeding.getEndTermDate())
        );
        
        // Crear el DTO del email
        EmailBodyDTO emailBody = new EmailBodyDTO();
        emailBody.setTo(userEmail);
        emailBody.setSubject("Nueva actuación registrada");
        emailBody.setText(htmlContent);
        
        return emailBody;
    }
    
    /**
     * Método para escapar caracteres HTML (seguridad)
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    /**
     * Formatear fechas de manera legible
     */
    private String formatDate(LocalDate date) {
        if (date == null) {
            return "<span style=\"color: #6c757d; font-style: italic;\">No definida</span>";
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", 
                                                                Locale.of("es", "ES"));
        return date.format(formatter);
    }
}
