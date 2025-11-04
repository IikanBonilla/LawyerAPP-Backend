package Development.Services;

import java.util.List;


import Development.Model.Client;
public interface IClientServices{
    public List<Client> listClient();
    public Client searchByIdClient(String id);
    public Client saveClient(Client client);
    public void deleteClient(String id);
    
}
