package Development.Services;

import Development.Model.Client;
import java.util.List;
import Development.Repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientServices implements IClientServices{
    @Autowired
    private ClientRepository clientRepository;

    @Override
    public List<Client> listClient() {
        // Retorna todos los clientes de la tabla
        return clientRepository.findAll();
    }

    @Override
    public Client searchByIdClient(String id) {
        // Busca un cliente por su id, si no lo encuentra retorna null
        return this.clientRepository.findById(id).orElse(null);
    }

    @Override
    public Client saveClient(Client client) {
        // Guarda o actualiza un cliente
        return this.clientRepository.save(client);
    }

    @Override
    public void deleteClient(String id) {
        // Elimina un cliente por su id
        this.clientRepository.deleteById(id);
    }

}
