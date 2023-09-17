package ru.hogwarts.school.services.interfaces;

import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.models.domain.Avatar;

import java.io.IOException;
import java.util.List;

public interface AvatarService {
    Avatar getAvatarPreview(long studentId);

    List<Avatar> getAvatarsPage(int pageN, int pageSize);

    void saveAvatar(long studentId, MultipartFile file) throws IOException;
}
