package Development.Services;


import java.io.IOException;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import Development.Model.Document;
import Development.Repository.DocumentRepository;
import Development.Repository.ClientRepository;
import Development.Model.Client;

@Service
public class DocumentServices implements IDocumentServices{
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired ClientRepository clientRepository;

    @SuppressWarnings("null")
    @Override
    public Document saveDocument(MultipartFile file, String idClient) throws IOException{
    Client client = clientRepository.findById(idClient).orElse(null);
    
    Document document = new Document();
    document.setType(file.getContentType());
    document.setOriginalName(StringUtils.cleanPath(file.getOriginalFilename()));
    document.setData(file.getBytes());
    document.setIdClient(client); // Asignas el proceso completo
    
    return documentRepository.save(document);
    }

    @Override
    public Document downloadDocument(String id){
        return this.documentRepository.findById(id).get();
    }
    @Override
    public void deleteDocument(String id) {
        this.documentRepository.deleteById(id);
    }

    @Override
    public Document searchByIdDocument(String id) {
        return this.documentRepository.findById(id).orElse(null);
    }

    @Override
    public Stream<Document> listDocument() {
       return this.documentRepository.findAll().stream();
    }

    
    
}
