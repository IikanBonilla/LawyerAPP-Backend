package Development.Services;

import java.io.IOException;
import java.util.stream.Stream;

import org.springframework.web.multipart.MultipartFile;

import Development.Model.Document;
public interface IDocumentServices{
    public Stream<Document> listDocument();
    public Document searchByIdDocument(String id);
    public Document saveDocument(MultipartFile document, String idProcess) throws IOException;
    public void deleteDocument(String id);
    public Document downloadDocument(String id);
}
