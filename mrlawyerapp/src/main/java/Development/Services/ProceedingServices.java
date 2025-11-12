package Development.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Development.DTOs.CreateProceedingDTO;
import Development.DTOs.GetProceedingDTO;
import Development.Model.Proceeding;
import Development.Model.Process;
import Development.Repository.ProceedingRepository;
import Development.Repository.ProcessRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ProceedingServices implements IProceedingServices{

    @Autowired
    private ProceedingRepository proceedingRepository;

    @Autowired
    private ProcessRepository processRepository;
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
    public Proceeding save(CreateProceedingDTO proceedingDTO) {
        Process process = processRepository.findById(proceedingDTO.getIdProcess()).orElseThrow(
        () -> new EntityNotFoundException("Proceso no encontrado: " + proceedingDTO.getIdProcess())
        );
        
        Proceeding proceeding = new Proceeding();
        proceeding.setProceeding(proceedingDTO.getProceeding());
        proceeding.setAnotation(proceedingDTO.getAnotation());
        proceeding.setProceedingDate(proceedingDTO.getProceedingDate());
        proceeding.setStartTermDate(proceedingDTO.getStartTermDate());
        proceeding.setEndTermDate(proceedingDTO.getEndTermDate());
        proceeding.setRegisterDate(proceedingDTO.getRegisterDate());
        proceeding.setIdProcess(process);

        return proceedingRepository.save(proceeding);
    }

    @Override
    public Proceeding findById(String id) {
        return proceedingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Actuacion no encontrada con id: " + id)
        );
    }

    @Override
    public void delete(String id) {
        if(!proceedingRepository.existsById(id)) 
        throw new EntityNotFoundException("No existe una Actuacion con el id: " + id);
        proceedingRepository.deleteById(id);
    }
    
}
