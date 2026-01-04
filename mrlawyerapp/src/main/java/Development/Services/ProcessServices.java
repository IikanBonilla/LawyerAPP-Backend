package Development.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import Development.DTOs.CreateProcessDTO;

import Development.DTOs.GetProcessDTO;
import Development.DTOs.GetProcessIdentificationDTO;
import Development.DTOs.UpdateProcessDTO;
import Development.Model.Client;
import Development.Model.ClientProcess;
import Development.Model.Document;
import Development.Model.Process;
import Development.Model.Status;
import Development.Repository.ClientProcessRepository;
import Development.Repository.ClientRepository;
import Development.Repository.DocumentRepository;

import Development.Repository.ProcessRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProcessServices implements IProcessServices{
    @Autowired
    private ProcessRepository processRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientProcessRepository clientProcessRepository;
    @Autowired 
    private DocumentRepository documentRepository;

    @Override
    public Process createProcessForClient(String idClient, CreateProcessDTO processDTO) {
        //Validar existencia del cliente
        Client client = clientRepository.findById(idClient).orElseThrow(
            () -> new EntityNotFoundException("No existe un cliente con id " + idClient)
            );

        // Validar que el identification sea único (opcional pero recomendado)
        if (processRepository.existsByIdentification(processDTO.getIdentification())) {
        throw new IllegalStateException("Ya existe un proceso con este identification: " + processDTO.getIdentification());
        }

        //Guardar el proceso con la info del dto
        Process process = new Process();
        process.setIdentification(processDTO.getIdentification());
        process.setRadicationDate(processDTO.getRadicationDate());
        process.setOfficeName(processDTO.getOfficeName());
        process.setPonente(processDTO.getPonente());
        process.setProcessType(processDTO.getProcessType());
        process.setProcessClass(processDTO.getProcessClass());
        process.setSubClassProcess(processDTO.getSubClassProcess());
        process.setRecurso(processDTO.getRecurso());
        process.setContenidoDeRadicacion(processDTO.getContenidoDeRadicacion());


        Process savedProcess = processRepository.save(process);

        ClientProcess clientProcess = new ClientProcess();
        clientProcess.setIdProcess(savedProcess);
        clientProcess.setIdClient(client);
        clientProcessRepository.save(clientProcess);

        return savedProcess;

    }

    @Override
    public Process updateProcess(String id, UpdateProcessDTO processDTO) {
        Process existingProcess = processRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado: " + id));

        // Validar identification único (si cambió)
        if (!existingProcess.getIdentification().equals(processDTO.getIdentification()) &&
            processRepository.existsByIdentification(processDTO.getIdentification())) {
            throw new IllegalStateException("Ya existe un proceso con este identification: " + processDTO.getIdentification());
        }

        // Actualizar campos
        existingProcess.setIdentification(processDTO.getIdentification());
        existingProcess.setRadicationDate(processDTO.getRadicationDate());
        existingProcess.setOfficeName(processDTO.getOfficeName());
        existingProcess.setPonente(processDTO.getPonente());
        existingProcess.setProcessType(processDTO.getProcessType());
        existingProcess.setProcessClass(processDTO.getProcessClass());
        existingProcess.setSubClassProcess(processDTO.getSubClassProcess());
        existingProcess.setRecurso(processDTO.getRecurso());
        existingProcess.setContenidoDeRadicacion(processDTO.getContenidoDeRadicacion());

        return processRepository.save(existingProcess);
    }

    @Override
    public void deleteProcess(String id) {
            try {
        processRepository.deleteById(id);
    } catch (EmptyResultDataAccessException ex) {
        throw new EntityNotFoundException("No existe un proceso con id: " + id);
    }
    }

    @Override
    public Process findById(String id) {
        return processRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("No existe un proceso con id: " + id));
    }

    @Override
    public List<GetProcessIdentificationDTO> radicadoByClientId(String idClient) {
        if(!clientRepository.existsById(idClient)) throw new EntityNotFoundException("No existe un cliente con id: " + idClient);
        try{
        return processRepository.findIdentificationByClientId(idClient);
        }catch(Exception ex){
            throw new RuntimeException("Error al obtener el Radicado", ex);
        }
    }

    @Override
    public GetProcessDTO findProcessById(String id) {
        return processRepository.findProcessById(id)
        .orElseThrow(() -> new EntityNotFoundException(
            "Proceso no encontrado con id: " + id
        ));
    }

    @Override
    public boolean associateDocumentToProcess(String idProcess, String idDocument) {
        if(!documentRepository.existsById(idDocument)) throw new EntityNotFoundException("No se encontró un documento con ese id: " + idDocument);
        Document objDoc = documentRepository.findById(idDocument).get();
        
        if(objDoc.getIdProcess() != null){
            throw new RuntimeException("El documento ya tiene un proceso asociado con id: " + objDoc.getIdProcess());
        }

        Process process = processRepository.findById(idProcess).get();

        objDoc.setIdProcess(process);
        documentRepository.save(objDoc);
        return true;
    }

    @Override
    public void associateClientToProcess(String idProcess, String idClient) {
        Process process = processRepository.findById(idProcess)
        .orElseThrow(() -> new EntityNotFoundException("No existe un proceso con id: " + idProcess));

        Client client = clientRepository.findById(idClient)
        .orElseThrow(() -> new EntityNotFoundException("No existe un cliente con id: " + idClient));

        boolean exists = clientProcessRepository.existsByIdClientIdAndIdProcessId(idClient, idProcess);
        if(exists){
            throw new IllegalStateException("El cliente ya está asociado al proceso");
        }else{
            ClientProcess cp = new ClientProcess();
            cp.setIdClient(client);
            cp.setIdProcess(process);
            clientProcessRepository.save(cp);
        }
    }

    @Override
    public Process changeProcessStatus(String id, String status) {
            Process process = processRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado: " + id));
    
        try {
            Status newStatus = Status.valueOf(status.toUpperCase());
            process.setStatus(newStatus);
            return processRepository.save(process);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Estado inválido: " + status);
        }
    }

    @Override
    public List<GetProcessIdentificationDTO> findProcessByStatus(String idLawyer, String status) {
        // Validaciones básicas
        if (idLawyer == null || idLawyer.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de abogado inválido");
        }
        
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado de proceso inválido");
        }
        
        try {
            
            List<GetProcessIdentificationDTO> processes = processRepository.findByStatus(status);
            
            return processes;
            
        } catch (Exception ex) {
          
            throw new RuntimeException("Error al buscar procesos por estado: " + ex.getMessage(), ex);
        }
    }
 
    
}
