package ru.hogwarts.school.services.interfaces;

import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.models.domain.Avatar;

import java.io.IOException;

public interface AvatarService {
    Avatar getAvatarPreview(long studentId);

    void saveAvatar(long studentId, MultipartFile file) throws IOException;
}
