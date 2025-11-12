package Development.Services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Development.DTOs.CreateAudienceDTO;
import Development.DTOs.GetAudienceDTO;
import Development.DTOs.UpdateAudienceDTO;
import Development.Model.Audience;
import Development.Model.Status;
import Development.Repository.AudienceRepository;
import Development.Repository.ClientRepository;
import Development.Repository.ProcessRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AudienceServices implements IAudienceServices{
    @Autowired
    private AudienceRepository audienceRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ProcessRepository processRepository;
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
    public List<GetAudienceDTO> findByProcess(String idProcess) {
        if(!processRepository.existsById(idProcess)) throw new EntityNotFoundException("No existe un processo con el id: " + idProcess);
        try{
            return audienceRepository.findByProcessId(idProcess);
        }catch(Exception ex){
            throw new RuntimeException("Error al obtener Audiencia para proceso: " + idProcess, ex);
        }
    }

    @Override
    public Audience save(CreateAudienceDTO audienceDTO) {
        // 1. Buscar el proceso
        Development.Model.Process process = processRepository.findById(audienceDTO.getIdProcess())
            .orElseThrow(() -> new EntityNotFoundException("No existe un proceso con este id: " + audienceDTO.getIdProcess()));
        
        // 2. Crear la entidad Audience a partir del DTO
        Audience audience = new Audience();
        audience.setAddress(audienceDTO.getAddress());
        audience.setMeetingLink(audienceDTO.getMeetingLink());
        audience.setDate(audienceDTO.getDate());
        audience.setIdProcess(process); 
        audience.setStatus(Status.PENDING); // Valor por defecto
        
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
        
        if (updateDTO.getDate() != null) {
            existingAudience.setDate(updateDTO.getDate());
        }
        
        if (updateDTO.getStatus() != null) {
            existingAudience.setStatus(updateDTO.getStatus());
        }
        
        // 3. Guardar y retornar
        return audienceRepository.save(existingAudience);
    }

    @Override
    public Audience findById(String id) {
            return audienceRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("No existe audiencia con id: " + id));
    }

    @Override
    public void delete(String id) {
        if(!audienceRepository.existsById(id)) throw new EntityNotFoundException("No existe una audiencia con el id: " + id);
        audienceRepository.deleteById(id);
    }

    @Override
    public List<Audience> findByStatus(Status status) {
        return audienceRepository.findByStatus(status);
    }


}
