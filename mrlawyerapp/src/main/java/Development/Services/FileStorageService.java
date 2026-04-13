package Development.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService implements IFileStorageService{

    @Value("${app.storage.path}")
    private String BASE_PATH;

    @Override
    public String saveFile(MultipartFile file, String folder) throws IOException {
        String cleanName = System.currentTimeMillis() + "_" +
                StringUtils.cleanPath(file.getOriginalFilename());

        Path dirPath = Paths.get(BASE_PATH, folder);
        Files.createDirectories(dirPath);

        Path filePath = dirPath.resolve(cleanName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

    @Override
    public byte[] loadFile(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    @Override
    public void deleteFile(String path) throws IOException {
        Files.delete(Paths.get(path));
    }

    @Override
    public String saveBytes(byte[] data, String fileName, String folder) throws IOException {
        String cleanName = System.currentTimeMillis() + "_" + fileName;

        Path dirPath = Paths.get(BASE_PATH, folder);
        Files.createDirectories(dirPath);

        Path filePath = dirPath.resolve(cleanName);

        Files.write(filePath, data);

        return filePath.toString();
    }
    
}
