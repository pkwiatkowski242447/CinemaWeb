package pl.pas.gr3.cinema.services.implementations;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.pas.gr3.cinema.aspects.logging.LoggerInterceptor;
import pl.pas.gr3.cinema.exceptions.ApplicationInputOutputException;
import pl.pas.gr3.cinema.model.UserFile;
import pl.pas.gr3.cinema.services.interfaces.IFileSystemService;
import pl.pas.gr3.cinema.utils.I18n;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

@Service
@LoggerInterceptor
@RequiredArgsConstructor
public class FileSystemService implements IFileSystemService {

    @Value("${file.path}")
    private String avatarsDirectory;

    // Lifecycle methods

    @PostConstruct
    private void initializeServerState() {
        File avatarsDir = new File(avatarsDirectory.replace("/", File.separator));
        this.avatarsDirectory = avatarsDir.getPath();
        if (!avatarsDir.exists() && !avatarsDir.mkdirs()) {
            throw new ApplicationInputOutputException(I18n.APPLICATION_COMPONENT_INITIALIZATION_EXCEPTION);
        }
    }

    public UserFile performFileWrite(MultipartFile file) {
        String fileName = this.avatarsDirectory + File.separator + Instant.now().toEpochMilli();
        File createdFile = new File(fileName);

        try {
            if (createdFile.createNewFile()) {
                file.transferTo(createdFile);
            }
        } catch (IOException exception) {
            throw new ApplicationInputOutputException();
        }

        return new UserFile(fileName, file.getContentType(), file.getOriginalFilename());
    }

    public Resource performFileRead(UserFile file) {
        return null;
    }
}
