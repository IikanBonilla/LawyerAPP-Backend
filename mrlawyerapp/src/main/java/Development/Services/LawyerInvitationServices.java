package Development.Services;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Development.DTOs.CreateLawyerInvitationDTO;
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
        return invitationRepository.save(invitation);
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
