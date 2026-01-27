package Development.Repository;

import org.springframework.data.jpa.repository.JpaRepository;


import Development.Model.Document;
import java.util.List;


public interface DocumentRepository extends JpaRepository<Document, String>{
    // Documentos por cliente
    List<Document> findByIdClientId(String idClient);
    
    // Documentos por proceso
    List<Document> findByIdProcessId(String idProcess);

    //Documentos por id proceso y id client
    List<Document> findByIdClient_IdAndIdProcess_Id(String idClient, String idProcess);

    // Contar documentos de un cliente
    Long countByIdClientId(String clientId);
    // Contar documentos de un proceso
    Long countByIdProcessId(String processId);
    // Buscar por nombre de archivo
    List<Document> findByIdClientIdAndOriginalNameContainingIgnoreCase(String clientId, String filename);
}
