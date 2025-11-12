package Development.Services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import Development.Model.Document;
public interface IDocumentServices{
    public Document saveDocumentForProcess(MultipartFile file, String idProcess) throws IOException;
    public Document saveDocumentForClient(MultipartFile file, String idClient) throws IOException;
    public Document findByIdDocument(String id);
    public void deleteDocument(String id);
    public Document downloadDocument(String id);
    public Long countByIdClientId(String clientId);
    public Long countByIdProcessId(String idProcess);
    public List<Document> getDocumentsByIdProcess(String idProcess);
    public List<Document> getDocumentsByIdClient(String idClient);

}
