package pl.pas.gr3.cinema.services.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import pl.pas.gr3.cinema.model.UserFile;

public interface IFileSystemService {

    UserFile performFileWrite(MultipartFile file);
    Resource performFileRead(UserFile file);
}
