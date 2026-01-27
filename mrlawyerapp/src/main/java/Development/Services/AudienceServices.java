package Development.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Development.DTOs.CreateAudienceDTO;
import Development.DTOs.GetAudienceDTO;
import Development.DTOs.GetClientFullNameDTO;
import Development.DTOs.UpdateAudienceDTO;
import Development.Model.Audience;
import Development.Model.Client;
import Development.Model.LawyerProfile;
import Development.Repository.AudienceRepository;
import Development.Repository.ClientRepository;
import Development.Repository.LawyerRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AudienceServices implements IAudienceServices{
    @Autowired
    private AudienceRepository audienceRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private LawyerRepository lawyerRepository;


    private GetAudienceDTO convertToAudienceDTO(Audience audience) {
    GetAudienceDTO dto = new GetAudienceDTO();
    dto.setAddress(audience.getAddress());
    dto.setMeetingLink(audience.getMeetingLink());
    dto.setAudience_date(audience.getAudience_date());
    return dto;
    }

    @Override
    public List<GetAudienceDTO> findByClient(String idClient) {
        if(!clientRepository.existsById(idClient)){
            throw new EntityNotFoundException("No exite un cliente con ese id"+ idClient);
        }
        try{
            return audienceRepository.findByClientId(idClient);
        }catch(Exception ex){

            throw new RuntimeException("Error al obtener audiencia para el cliente: " + idClient, ex);
        }

    }
        @Override
    public List<GetAudienceDTO> findByUser(String idUser) {
        if (idUser == null || idUser.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        try{
            return audienceRepository.findByUserId(idUser);
        }catch(Exception ex){

            throw new RuntimeException("Error al obtener audiencia para el abogado: " + idUser, ex);
        }

    }


    @Override
    public Audience saveForClient(String idLawyer, String idClient, CreateAudienceDTO audienceDTO) {
        // 1. Buscar el proceso
        Client client = clientRepository.findById(idClient)
            .orElseThrow(() -> new EntityNotFoundException("No existe un proceso con este id: " + idClient));
        LawyerProfile lawyer = lawyerRepository.findById(idLawyer)
            .orElseThrow(() -> new EntityNotFoundException("No existe un proceso con este id: " + idLawyer));
        // 2. Crear la entidad Audience a partir del DTO
        Audience audience = new Audience();
        audience.setAddress(audienceDTO.getAddress());
        audience.setMeetingLink(audienceDTO.getMeetingLink());
        audience.setAudience_date(audienceDTO.getAudience_date());
        audience.setIdClient(client); 
        audience.setIdLawyer(lawyer);
        
        // 3. Guardar y retornar
        return audienceRepository.save(audience);
    }

    @Override
    public Audience update(String id, UpdateAudienceDTO updateDTO) {
        Audience existingAudience = audienceRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("No existe una audiencia con id: " + id));

            // 2. Actualizar solo los campos permitidos por el DTO
        if (updateDTO.getMeetingLink() != null) {
            existingAudience.setMeetingLink(updateDTO.getMeetingLink());
        }
        
        if (updateDTO.getAudience_date() != null) {
            existingAudience.setAudience_date(updateDTO.getAudience_date());
        }
        // 3. Guardar y retornar
        return audienceRepository.save(existingAudience);
    }

    @Override
    public GetAudienceDTO findById(String id) {
        Audience audience = audienceRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Audiencia no encontrada"));
        return convertToAudienceDTO(audience);
    }

    @Override
    public void delete(String id) {
        if(!audienceRepository.existsById(id)) throw new EntityNotFoundException("No existe una audiencia con el id: " + id);
        audienceRepository.deleteById(id);
    }

    @Override
    public String findClientName(String id, String idClient){
        audienceRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Audiencia no encontrada"));

        Optional<GetClientFullNameDTO> clientName = clientRepository.findNameById(idClient);

            if(!clientName.isPresent()){
                throw new EntityNotFoundException("No se encontró el nombre del cliente con el id: " + idClient);
            }

        return clientName.get().getFullName();

    }
    

}