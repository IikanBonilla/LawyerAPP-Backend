package Development.Services;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Development.Config.EmailConfig;
import Development.DTOs.CreateLawyerInvitationDTO;
import Development.DTOs.EmailBodyDTO;
import Development.DTOs.LawyerInvitationDTO;
import Development.Model.LawFirm;
import Development.Model.LawyerInvitation;
import Development.Repository.LawFirmRepository;
import Development.Repository.LawyerInvitationRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class LawyerInvitationServices implements ILawyerInvitationServices{
    @Autowired
    private LawFirmRepository lawFirmRepository;

    @Autowired
    private LawyerInvitationRepository invitationRepository;

    @Autowired
    private NotificationServices notificationService;

    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    private EmailServices emailService;

    @Override
    public LawyerInvitation createInvitation(String idFirm, CreateLawyerInvitationDTO invitationDTO) {
        LawFirm firm = lawFirmRepository.findById(idFirm)
         .orElseThrow(() -> new EntityNotFoundException("No existe firma con id: " + idFirm)
        );

        boolean existsByEmail= invitationRepository.existsByEmailAndIdLawFirmAndUsedFalse(invitationDTO.getEmail(), firm);
        boolean existsByIdentification = invitationRepository.existsByIdentificationAndIdLawFirmAndUsedFalse(invitationDTO.getIdentification(), firm);
        if(existsByEmail || existsByIdentification){
            throw new IllegalArgumentException("Ya existe una invitación con ese email o identificación en esta firma");
        }
        try{
        LawyerInvitation invitation = new LawyerInvitation();
        invitation.setIdentification(invitationDTO.getIdentification());
        invitation.setEmail(invitationDTO.getEmail());
        invitation.setIdLawFirm(firm);

        LawyerInvitation savedInvitation = invitationRepository.save(invitation);

        if(emailConfig.isEmailEnabled()){
        EmailBodyDTO bodyDTO = new EmailBodyDTO();
        bodyDTO.setSubject("CREDENCIALES DE INGRESO MA LAWYER APP");
        bodyDTO.setText("Estas son tus credenciales para registrar tu usuario en MA LAWYER APP aplicación para abogados \n\n"
            + "EMAIL: " + invitationDTO.getEmail() 
            + "\nNÚMERO DE IDENTIFICACIÓN: " + invitationDTO.getIdentification() 
            + "\n\nEstos datos deberás ingresarlos en \'Email\' y \'Número de identificación\'"
        );
        bodyDTO.setTo(invitationDTO.getEmail());

        emailService.sendSimpleMessage(bodyDTO);
        }

        notificationService.sendNotificationAdmin(invitation.getIdLawFirm().getId(), "INVITATION_CREATED", savedInvitation);

        return savedInvitation;
        }catch(Exception ex){
            throw new RuntimeException("Error al ingresar invitación");
        }
    }

    @Override
    public void deleteInvitation(String id) {
        LawyerInvitation invitation = invitationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("No existe invitación con id: " + id)
            );
        if(invitation.getIdLawFirm() != null){
            invitation.setIdLawFirm(null);
            invitationRepository.delete(invitation);
        }else{
            invitationRepository.delete(invitation);
        }

        Map<String, String> payload = Map.of("idInvitation", id);

        notificationService.sendNotificationAdmin(invitation.getIdLawFirm().getId(), "INVITATION_DELETED", payload);
    }

    @Override
    public List<LawyerInvitationDTO> findByIdFirm(String idFirm) {
        if(!lawFirmRepository.existsById(idFirm))
            throw new IllegalArgumentException("No existe una firma con id: " + idFirm);
        try{
            return invitationRepository.findByIdLawFirm(idFirm);
        }catch(Exception ex){
            throw new RuntimeException("Error inesperado al buscar invitaciones");
        }
    }
    
}
