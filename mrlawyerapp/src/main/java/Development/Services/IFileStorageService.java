package Development.Services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface IFileStorageService {

    public String saveFile(MultipartFile file, String folder) throws IOException;
    public byte[] loadFile(String path) throws IOException;
    public void deleteFile(String path) throws IOException;
    //Migración de datos 
    public String saveBytes(byte[]  data, String fileName, String folder) throws IOException;
    
} 