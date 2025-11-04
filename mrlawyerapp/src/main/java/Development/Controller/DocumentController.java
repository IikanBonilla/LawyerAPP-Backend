package Development.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//import org.springframework.web.bind.annotation.PutMapping;
import Development.Model.Document;
import Development.Model.ResponseFile;
import Development.Model.ResponseMessage;
import Development.Services.DocumentServices;


//Controller
@RestController
//url + localhost
@RequestMapping("documentApi")
//Direccion de angular
@CrossOrigin(origins = "*")
public class DocumentController {
    private Logger logger = LoggerFactory.getLogger(Document.class);
    @Autowired
    private DocumentServices documentService;
    @GetMapping("/document/listall")
    public ResponseEntity<List<ResponseFile>> getAllDocument(){
        List<ResponseFile> documents = documentService.listDocument().map(Document ->{
            String documentDownloadUri = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/document/listall/")
            .path(Document.getId().toString())//Convertir long a String
            .toUriString();

            int size = (Document.getData() != null) ? Document.getData().length : 0;
            return new ResponseFile(
                Document.getOriginalName(),
                documentDownloadUri,
                Document.getType(),
                size
            );
        }).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(documents);
    }



    @PostMapping("/document/upload")
    public ResponseEntity<ResponseMessage> createDocument(@RequestParam ("document") MultipartFile document, @RequestParam String idProcess){
        String message = "";
        try{
        documentService.saveDocument(document, idProcess);
        message = "Upload the fyle successfully: " + document.getOriginalFilename();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        }catch (Exception e){
            message = "Could not upload the file" + document.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/document/download")
    public ResponseEntity<byte[]> getDocument(@RequestParam String id) {
        Document document = documentService.downloadDocument(id);

        if(document == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + document.getOriginalName() + "\"")
            .body(document.getData());
    }
    
    @DeleteMapping("/document/delete")
    public void deleteDocument(@RequestParam String id){
        documentService.deleteDocument(id);
        logger.info("Documento elimininado con ID: {}", id);
    }
}