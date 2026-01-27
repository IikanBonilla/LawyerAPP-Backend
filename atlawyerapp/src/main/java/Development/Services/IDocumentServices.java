package Development.Services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import Development.DTOs.DocumentMetadataDTO;
import Development.DTOs.DocumentResponseDTO;
import Development.Model.Document;
public interface IDocumentServices{
    public DocumentResponseDTO saveDocumentForProcess(MultipartFile file, String idProcess) throws IOException;
    public DocumentResponseDTO saveDocumentForClient(MultipartFile file, String idClient) throws IOException;
    public Document findByIdDocument(String id);
    public void deleteDocument(String id);
    public Document downloadDocument(String id);
    public Long countByIdClientId(String clientId);
    public Long countByIdProcessId(String idProcess);
    public List<DocumentMetadataDTO> getDocumentsByIdProcess(String idProcess);
    public List<DocumentMetadataDTO> getDocumentsByIdClient(String idClient);

}
