package ru.hogwarts.school.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.spel.EvaluationContextProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.models.domain.Avatar;
import ru.hogwarts.school.services.interfaces.AvatarService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    private final AvatarService avatarService;
    @Value("${upload-file-size-limit}")
    private int fileSizeLimit;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable long studentId,
                                               @RequestParam MultipartFile file) throws IOException {
        if (file.getSize() > fileSizeLimit * 1024L) {
            return new ResponseEntity<>("File too big.", HttpStatus.BAD_REQUEST);
        }

        avatarService.saveAvatar(studentId, file);
        return new ResponseEntity<>("File saved", HttpStatus.OK);
    }

    @GetMapping(value = "/{studentId}")
    public ResponseEntity<byte[]> getAvatarPreview(@PathVariable long studentId) {
        Avatar avatar = avatarService.getAvatarPreview(studentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return new ResponseEntity<>(avatar.getData(), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<Avatar>> getAvatarsPaged(@RequestParam int pageN, @RequestParam(required = false) int pageSize) {
        if (pageSize < 1) {
            pageSize = 5; // set to default or throw?
            //throw new IllegalArgumentException("Page size must be greater than zero.");
        }
        if (pageN < 1) {
            pageN = 1; //same question
            throw new IllegalArgumentException("PageNumber must be greater than zero.");
        }

        return new ResponseEntity<>(avatarService.getAvatarsPage(pageN, pageSize), HttpStatus.OK);
    }

    @GetMapping(value = "/{studentId}/download")
    public void downloadAvatar(@PathVariable long studentId, HttpServletResponse response) throws IOException {
        var avatar = avatarService.getAvatarPreview(studentId);
        Path path = Path.of(avatar.getFilePath());
        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream();
        ) {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(avatar.getMediaType());
            response.setContentLengthLong(avatar.getFileSize());
            is.transferTo(os);
        }
    }
}
