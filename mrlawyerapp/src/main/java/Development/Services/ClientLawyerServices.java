package Development.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Development.DTOs.AssociateClientLawyerDTO;
import Development.Model.Client;
import Development.Model.ClientLawyer;
import Development.Model.LawyerProfile;
import Development.Repository.ClientLawyerRepository;
import Development.Repository.ClientRepository;
import Development.Repository.LawyerRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ClientLawyerServices implements IClientLawyerServices {

    @Autowired
    private ClientLawyerRepository clientLawyerRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LawyerRepository lawyerRepository;

    @Override
    public ClientLawyer associateClientToLawyer(AssociateClientLawyerDTO associationDTO) {
        // Verificar que el cliente existe
        Client client = clientRepository.findById(associationDTO.getClientId())
            .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado: " + associationDTO.getClientId()));

        // Verificar que el abogado existe
        LawyerProfile lawyer = lawyerRepository.findById(associationDTO.getLawyerId())
            .orElseThrow(() -> new EntityNotFoundException("Abogado no encontrado: " + associationDTO.getLawyerId()));

        // Verificar si ya existe la asociación
        if (clientLawyerRepository.existsByIdClientIdAndIdLawyerId(associationDTO.getClientId(), associationDTO.getLawyerId())) {
            throw new IllegalStateException("El cliente ya está asociado a este abogado");
        }

        // Crear y guardar la asociación
        ClientLawyer clientLawyer = new ClientLawyer();
        clientLawyer.setIdClient(client);
        clientLawyer.setIdLawyer(lawyer);

        return clientLawyerRepository.save(clientLawyer);
    }

    @Override
    public void removeClientFromLawyer(String clientLawyerId) {
        if (!clientLawyerRepository.existsById(clientLawyerId)) {
            throw new EntityNotFoundException("Asociación no encontrada: " + clientLawyerId);
        }
        clientLawyerRepository.deleteById(clientLawyerId);
    }

    @Override
    public boolean existsAssociation(String clientId, String lawyerId) {
        return clientLawyerRepository.existsByIdClientIdAndIdLawyerId(clientId, lawyerId);
    }
}