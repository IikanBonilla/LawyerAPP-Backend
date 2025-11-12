package Development.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Development.DTOs.AssociateClientProcessDTO;
import Development.Model.Client;
import Development.Model.ClientProcess;
import Development.Model.Process;
import Development.Repository.ClientProcessRepository;
import Development.Repository.ClientRepository;
import Development.Repository.ProcessRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ClientProcessServices implements IClientProcessServices {

    @Autowired
    private ClientProcessRepository clientProcessRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Override
    public ClientProcess associateClientToProcess(AssociateClientProcessDTO associationDTO) {
        // Verificar que el cliente existe
        Client client = clientRepository.findById(associationDTO.getClientId())
            .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado: " + associationDTO.getClientId()));

        // Verificar que el proceso existe
        Process process = processRepository.findById(associationDTO.getProcessId())
            .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado: " + associationDTO.getProcessId()));

        // Verificar si ya existe la asociación
        if (clientProcessRepository.existsByIdClientIdAndIdProcessId(associationDTO.getClientId(), associationDTO.getProcessId())) {
            throw new IllegalStateException("El cliente ya está asociado a este proceso");
        }

        // Crear y guardar la asociación
        ClientProcess clientProcess = new ClientProcess();
        clientProcess.setIdClient(client);
        clientProcess.setIdProcess(process);

        return clientProcessRepository.save(clientProcess);
    }

    @Override
    public void removeClientFromProcess(String clientProcessId) {
        if (!clientProcessRepository.existsById(clientProcessId)) {
            throw new EntityNotFoundException("Asociación no encontrada: " + clientProcessId);
        }
        clientProcessRepository.deleteById(clientProcessId);
    }

    @Override
    public boolean existsAssociation(String clientId, String processId) {
        return clientProcessRepository.existsByIdClientIdAndIdProcessId(clientId, processId);
    }
}