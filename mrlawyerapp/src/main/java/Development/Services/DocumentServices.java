package Development.Services;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import Development.DTOs.DocumentMetadataDTO;
import Development.DTOs.DocumentResponseDTO;
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

    /**
     * Convierte entidad Document a DocumentResponseDTO
     */
    private DocumentResponseDTO convertToResponseDTO(Document document) {
        DocumentResponseDTO dto = new DocumentResponseDTO();
        dto.setId(document.getId());
        dto.setType(document.getType());
        dto.setOriginalName(document.getOriginalName());
        return dto;
    }

    /**
     * Convierte entidad Document a DocumentMetadataDTO  
     */
    private DocumentMetadataDTO convertToMetadataDTO(Document document) {
        DocumentMetadataDTO dto = new DocumentMetadataDTO();
        dto.setId(document.getId());
        dto.setType(document.getType());
        dto.setOriginalName(document.getOriginalName());
        
        return dto;
    }
    @SuppressWarnings("null")
    @Override
    @Transactional
    public DocumentResponseDTO saveDocumentForClient(MultipartFile file, String idClient) throws IOException{
    
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
    
    Document savedDocument = documentRepository.save(document);
    return convertToResponseDTO(savedDocument);
    }

     /**
     * Guardar documento asociado a un Proceso
     */
    @SuppressWarnings("null")
    @Override
    @Transactional
    public DocumentResponseDTO saveDocumentForProcess(MultipartFile file, String idProcess) throws IOException {
        
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
        
        Document savedDocument = documentRepository.save(document);

        // Convertir a DTO para respuesta
        return convertToResponseDTO(savedDocument);
    }

    @Override
    public Document downloadDocument(String id){
        return documentRepository.findById(id).get();
    }
    @Override
    public void deleteDocument(String id) {
        Document document = this.documentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado con ID: " + id));

        document.setIdClient(null);
        document.setIdProcess(null);

        this.documentRepository.deleteById(document.getId());
    }

    @Override
    public Document findByIdDocument(String id) {
        return this.documentRepository.findById(id).orElse(null);
    }
    @Override
    public List<DocumentMetadataDTO> getDocumentsByIdClient(String idClient) {
          List<Document> documents = documentRepository.findByIdClientId(idClient);
        // ✅ Convertir lista de entidades a lista de DTOs
        return documents.stream()
        .map(this::convertToMetadataDTO)
        .collect(Collectors.toList());
    }
    @Override
    public List<DocumentMetadataDTO> getDocumentsByIdProcess(String idProcess) {
        List<Document> documents = documentRepository.findByIdProcessId(idProcess);
        // ✅ Convertir lista de entidades a lista de DTOs
        return documents.stream()
        .map(this::convertToMetadataDTO)
        .collect(Collectors.toList());
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
