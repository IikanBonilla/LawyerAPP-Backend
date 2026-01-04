package Development.Controller;

// Spring Framework imports
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// Spring Content Disposition (para descargas)
import org.springframework.http.ContentDisposition;

// Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Anotaciones de Spring
import org.springframework.beans.factory.annotation.Autowired;

// Collections y utilidades
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// IOException
import java.io.IOException;

import Development.DTOs.DocumentMetadataDTO;
import Development.DTOs.DocumentResponseDTO;
import Development.Model.Document;
import Development.Services.DocumentServices;

@RestController

@RequestMapping("/api/document")
public class DocumentController {
    
    private final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    
    @Autowired
    private DocumentServices documentServices;

    // ✅ UPLOAD - Guardar documento para Cliente
    @PostMapping("/client/{idClient}/upload")
    @PreAuthorize("hasRole('LAWYER') and @LawyerStatusChecker.isActive()")
    public ResponseEntity<?> uploadDocumentForClient(
            @PathVariable String idClient,
            @RequestParam("file") MultipartFile file) {
        try {
            logger.info("Subiendo documento para cliente ID: {} - Archivo: {}", idClient, file.getOriginalFilename());
            
            DocumentResponseDTO document = documentServices.saveDocumentForClient(file, idClient);
            
            logger.info("Documento guardado exitosamente - ID: {}, Cliente: {}", document.getId(), idClient);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Documento subido exitosamente");
            response.put("documentId", document.getId());
            response.put("fileName", document.getOriginalName());
            response.put("fileType", document.getType());
            response.put("clientId", idClient);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException ex) {
            logger.warn("Error de validación: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
            
        } catch (IOException ex) {
            logger.error("Error de IO subiendo documento: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error al procesar el archivo");
            
        } catch (Exception ex) {
            logger.error("Error inesperado subiendo documento: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al subir documento");
        }
    }

    // ✅ UPLOAD - Guardar documento para Proceso
    @PostMapping("/process/{idProcess}/upload")
    @PreAuthorize("hasRole('LAWYER') and @LawyerStatusChecker.isActive()")
    public ResponseEntity<?> uploadDocumentForProcess(
            @PathVariable String idProcess,
            @RequestParam("file") MultipartFile file) {
        try {
            logger.info("Subiendo documento para proceso ID: {} - Archivo: {}", idProcess, file.getOriginalFilename());
            
            DocumentResponseDTO document = documentServices.saveDocumentForProcess(file, idProcess);
            
            logger.info("Documento guardado exitosamente - ID: {}, Proceso: {}", document.getId(), idProcess);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Documento subido exitosamente");
            response.put("documentId", document.getId());
            response.put("fileName", document.getOriginalName());
            response.put("fileType", document.getType());
            response.put("processId", idProcess);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException ex) {
            logger.warn("Error de validación: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
            
        } catch (IOException ex) {
            logger.error("Error de IO subiendo documento: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error al procesar el archivo");
            
        } catch (Exception ex) {
            logger.error("Error inesperado subiendo documento: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al subir documento");
        }
    }

    // ✅ DOWNLOAD - Descargar documento
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable String id) {
        try {
            logger.info("Descargando documento ID: {}", id);
            
            Document document = documentServices.downloadDocument(id);
            
            if (document == null) {
                return ResponseEntity.notFound().build();
            }
            
            logger.info("Documento encontrado - Nombre: {}, Tipo: {}", document.getOriginalName(), document.getType());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(document.getType()));
            headers.setContentDispositionFormData("attachment", document.getOriginalName());
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(document.getData(), headers, HttpStatus.OK);
            
        } catch (Exception ex) {
            logger.error("Error descargando documento: {}", ex.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ PREVIEW - Ver documento (inline)
    @GetMapping("/preview/{id}")
    public ResponseEntity<byte[]> previewDocument(@PathVariable String id) {
        try {
            logger.info("Vista previa de documento ID: {}", id);
            
            Document document = documentServices.downloadDocument(id);
            
            if (document == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Verificar si es un tipo de archivo que se puede previsualizar
            String contentType = document.getType();
            if (contentType != null && 
                (contentType.startsWith("image/") || 
                 contentType.equals("application/pdf") ||
                 contentType.startsWith("text/"))) {
                
                logger.info("Mostrando vista previa - Nombre: {}", document.getOriginalName());
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(contentType));
                headers.setContentDisposition(ContentDisposition.inline().filename(document.getOriginalName()).build());
                
                return new ResponseEntity<>(document.getData(), headers, HttpStatus.OK);
            } else {
                // Si no es previsualizable, forzar descarga
                return downloadDocument(id);
            }
            
        } catch (Exception ex) {
            logger.error("Error en vista previa de documento: {}", ex.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ DELETE - Eliminar documento
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('LAWYER') and @LawyerStatusChecker.isActive()")
    public ResponseEntity<?> deleteDocument(@PathVariable String id) {
        try {
            logger.info("Eliminando documento ID: {}", id);
            
            documentServices.deleteDocument(id);
            
            logger.info("Documento eliminado exitosamente - ID: {}", id);
            return ResponseEntity.ok("Documento eliminado exitosamente");
            
        } catch (IllegalArgumentException ex) {
            logger.warn("Documento no encontrado: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception ex) {
            logger.error("Error inesperado eliminando documento: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al eliminar documento");
        }
    }

    // ✅ GET BY ID - Obtener documento por ID (metadatos)
    @GetMapping("/{id}")
    public ResponseEntity<?> getDocumentById(@PathVariable String id) {
        try {
            logger.info("Buscando documento ID: {}", id);
            
            Document document = documentServices.findByIdDocument(id);
            
            if (document == null) {
                return ResponseEntity.notFound().build();
            }
            
            logger.info("Documento encontrado - ID: {}", id);
            
            // Devolver solo metadatos, no los datos binarios
            Map<String, Object> response = new HashMap<>();
            response.put("id", document.getId());
            response.put("originalName", document.getOriginalName());
            response.put("type", document.getType());
            response.put("clientId", document.getIdClient() != null ? document.getIdClient().getId() : null);
            response.put("processId", document.getIdProcess() != null ? document.getIdProcess().getId() : null);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error obteniendo documento: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al obtener documento");
        }
    }

    // ✅ GET ALL - Obtener documentos por Cliente
    @GetMapping("/client/{idClient}")
    public ResponseEntity<?> getDocumentsByClient(@PathVariable String idClient) {
        try {
            logger.info("Buscando documentos para cliente ID: {}", idClient);
            
            List<DocumentMetadataDTO> documents = documentServices.getDocumentsByIdClient(idClient);
            
            logger.info("Encontrados {} documentos para cliente {}", documents.size(), idClient);
            
            // Convertir a DTO sin los datos binarios
            List<Map<String, Object>> response = documents.stream()
                .map(doc -> {
                    Map<String, Object> docMap = new HashMap<>();
                    docMap.put("id", doc.getId());
                    docMap.put("originalName", doc.getOriginalName());
                    docMap.put("type", doc.getType());
                    return docMap;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error obteniendo documentos del cliente: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al obtener documentos");
        }
    }

    // ✅ GET ALL - Obtener documentos por Proceso
    @GetMapping("/process/{idProcess}")
    public ResponseEntity<?> getDocumentsByProcess(@PathVariable String idProcess) {
        try {
            logger.info("Buscando documentos para proceso ID: {}", idProcess);
            
            List<DocumentMetadataDTO> documents = documentServices.getDocumentsByIdProcess(idProcess);
            
            logger.info("Encontrados {} documentos para proceso {}", documents.size(), idProcess);
            
            // Convertir a DTO sin los datos binarios
            List<Map<String, Object>> response = documents.stream()
                .map(doc -> {
                    Map<String, Object> docMap = new HashMap<>();
                    docMap.put("id", doc.getId());
                    docMap.put("originalName", doc.getOriginalName());
                    docMap.put("type", doc.getType());
                    return docMap;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error obteniendo documentos del proceso: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al obtener documentos");
        }
    }

    // ✅ COUNT - Contar documentos por Cliente
    @GetMapping("/client/{idClient}/count")
    public ResponseEntity<?> countDocumentsByClient(@PathVariable String idClient) {
        try {
            logger.info("Contando documentos para cliente ID: {}", idClient);
            
            Long count = documentServices.countByIdClientId(idClient);
            
            Map<String, Object> response = new HashMap<>();
            response.put("clientId", idClient);
            response.put("documentCount", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error contando documentos: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al contar documentos");
        }
    }

    // ✅ COUNT - Contar documentos por Proceso
    @GetMapping("/process/{idProcess}/count")
    public ResponseEntity<?> countDocumentsByProcess(@PathVariable String idProcess) {
        try {
            logger.info("Contando documentos para proceso ID: {}", idProcess);
            
            Long count = documentServices.countByIdProcessId(idProcess);
            
            Map<String, Object> response = new HashMap<>();
            response.put("processId", idProcess);
            response.put("documentCount", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error contando documentos: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al contar documentos");
        }
    }
}