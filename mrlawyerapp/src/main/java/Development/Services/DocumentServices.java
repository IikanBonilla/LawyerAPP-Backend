package Development.Services;


import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import Development.Model.Client;
import Development.Model.Process;
import Development.Model.Document;

import Development.Repository.DocumentRepository;
import Development.Repository.ProcessRepository;
import jakarta.transaction.Transactional;
import Development.Repository.ClientRepository;


@Service
public class DocumentServices implements IDocumentServices{
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired 
    private ClientRepository clientRepository;
    @Autowired 
    private ProcessRepository processRepository;

    @SuppressWarnings("null")
    @Override
    @Transactional
    public Document saveDocumentForClient(MultipartFile file, String idClient) throws IOException{
    
    if(file.isEmpty()){
        throw new IllegalArgumentException("El archivo esta vacio");
    }

    Client client = clientRepository.findById(idClient).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + idClient));
    
    long maxSize = 10 * 1024 * 1024; // 10MB
    if (file.getSize() > maxSize) {
        throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido (10MB)");
    }

    Document document = new Document();
    document.setType(file.getContentType());
    document.setOriginalName(StringUtils.cleanPath(file.getOriginalFilename()));
    document.setData(file.getBytes());
    document.setIdClient(client); // Asignas el proceso completo
    
    return documentRepository.save(document);
    }

     /**
     * Guardar documento asociado a un Proceso
     */
    @SuppressWarnings("null")
    @Override
    @Transactional
    public Document saveDocumentForProcess(MultipartFile file, String idProcess) throws IOException {
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        
        Process process = processRepository.findById(idProcess)
            .orElseThrow(() -> new IllegalArgumentException("Proceso no encontrado con ID: " + idProcess));
        
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido (10MB)");
        }
        
        Document document = new Document();
        document.setType(file.getContentType());
        document.setOriginalName(StringUtils.cleanPath(file.getOriginalFilename()));
        document.setData(file.getBytes());
        document.setIdProcess(process);
        // idClient queda null
        
        return documentRepository.save(document);
    }

    @Override
    public Document downloadDocument(String id){
        return documentRepository.findById(id).get();
    }
    @Override
    public void deleteDocument(String id) {
        if (!documentRepository.existsById(id)) {
            throw new IllegalArgumentException("Documento no encontrado con ID: " + id);
        }
        this.documentRepository.deleteById(id);
    }

    @Override
    public Document findByIdDocument(String id) {
        return this.documentRepository.findById(id).orElse(null);
    }
    @Override
    public List<Document> getDocumentsByIdClient(String idClient) {
        return documentRepository.findByIdClientId(idClient);
    }
    @Override
    public List<Document> getDocumentsByIdProcess(String idProcess) {
        return documentRepository.findByIdProcessId(idProcess);
    }
    @Override
    public Long countByIdClientId(String idClient) {
       return documentRepository.countByIdClientId(idClient);
    }

    @Override
    public Long countByIdProcessId(String idProcess) {
        return documentRepository.countByIdProcessId(idProcess);
    }
    
    
}
