package ru.hogwarts.school.services.implementations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.models.domain.Avatar;
import ru.hogwarts.school.models.domain.Student;
import ru.hogwarts.school.services.interfaces.AvatarService;
import ru.hogwarts.school.services.interfaces.StudentService;
import ru.hogwarts.school.services.repositories.AvatarRepository;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarServiceImpl implements AvatarService {
    private final AvatarRepository avatarRepository;
    private final StudentService studentService;
    private final String avatarDirectory;

    private final String avatarNotFoundMessage = "Student does not have an avatar.";

    public AvatarServiceImpl(AvatarRepository avatarRepository,
                             StudentService studentService,
                             @Value("avatar-directory") String avatarDirectory) {
        this.avatarRepository = avatarRepository;
        this.studentService = studentService;
        this.avatarDirectory = avatarDirectory;
    }

    @Override
    public Avatar getAvatarPreview(long studentId) {
        studentService.getStudentById(studentId);
        return avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> new NoSuchElementException(avatarNotFoundMessage));
    }

    @Override
    public List<Avatar> getAvatarsPage(int pageN, int pageSize) {
        var pageRequest = PageRequest.of(pageN - 1, pageSize);
        return avatarRepository.findAll(pageRequest).getContent();
    }

    @Override
    public void saveAvatar(long studentId, MultipartFile file) throws IOException {
        Avatar avatar;
        try {
            avatar = getAvatarPreview(studentId);
        } catch (NoSuchElementException ex) {
            if (!ex.getMessage().equals(avatarNotFoundMessage)) {
                throw ex;
            }
            Student student = new Student();
            student.setId(studentId);
            avatar = new Avatar();
            avatar.setStudent(student);
        }


        var filePath = writeAvatarToFile(studentId, file);
        avatarSetUp(file, avatar, filePath);

        avatarRepository.saveAndFlush(avatar);
    }

    private String getExtension(String fileName) {
        if (fileName == null) return "";
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot == -1) return "";
        return fileName.substring(lastDot);
    }

    private void avatarSetUp(MultipartFile file, Avatar avatar, Path filePath) throws IOException{
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(generatePreview(filePath));
    }

    private Path writeAvatarToFile(long studentId, MultipartFile file) throws IOException {
        Path filePath = Path.of(avatarDirectory, studentId + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }
        return filePath;
    }

    private byte[] generatePreview(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            BufferedImage image = ImageIO.read(bis);

            int reduceFactor = Math.max(image.getHeight() / 60, image.getWidth() / 60);
            int pHeight = image.getHeight() / reduceFactor;
            int pWidth = image.getWidth() / reduceFactor;

            BufferedImage preview = new BufferedImage(pWidth, pHeight, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, pWidth, pHeight, null);
            graphics.dispose();

            ImageIO.write(preview, "JPG", baos);
            return baos.toByteArray();
        }
    }
}
