package ru.hogwarts.school.services.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.models.domain.Avatar;
import ru.hogwarts.school.models.domain.Student;
import ru.hogwarts.school.models.dto.StudentDto;
import ru.hogwarts.school.services.repositories.AvatarRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvatarServiceImplTest {
    private AvatarRepository avatarRepository = mock(AvatarRepository.class);
    private StudentServiceImpl studentService = mock(StudentServiceImpl.class);
    private final String path = "./src/test/resources";
    private final AvatarServiceImpl sut;
    byte[] image;
    private final StudentDto studentDto = new StudentDto(1L, "Harry", 11, 1, "Gryffindoor");

    public AvatarServiceImplTest() throws IOException {
        sut = new AvatarServiceImpl(avatarRepository, studentService, path);

        try (InputStream is = Files.newInputStream(Path.of(path + "/test.jpg"));
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ) {
            bis.transferTo(baos);
            this.image = baos.toByteArray();
        }
    }

    @BeforeEach
    private void resetDirectory() throws IOException {
        Files.deleteIfExists(Path.of(path + "/1.jpg"));
    }

    @Test
    void getAvatarPreview_shouldReturnAvatar() {
        long studentId = 1L;
        when(studentService.getStudentById(studentDto.getId())).thenReturn(studentDto);
        when(avatarRepository.findByStudentId(studentDto.getId())).thenAnswer(p -> {
            var a = new Avatar();
            Student s = new Student();
            s.setId(studentId);
            a.setStudent(s);
            return Optional.of(a);
        });

        var actual = sut.getAvatarPreview(studentId);
        assertEquals(studentId, actual.getStudent().getId());
        verify(studentService, times(1)).getStudentById(studentId);
        verify(avatarRepository, times(1)).findByStudentId(studentId);
    }

    @Test
    void getAvatarPreview_NoStudent_shouldThrowNoSuchElementException() {
        when(studentService.getStudentById(anyLong())).thenThrow(new NoSuchElementException("Student not found."));

        var ex = assertThrows(NoSuchElementException.class, () -> sut.getAvatarPreview(22L));
        assertEquals("Student not found.", ex.getMessage());
        verify(studentService, times(1)).getStudentById(anyLong());
        verify(avatarRepository, never()).findByStudentId(anyLong());
    }

    @Test
    void getAvatarPreview_NoAvatar_shouldThrowNoSuchElementException() {
        when(studentService.getStudentById(anyLong())).thenReturn(studentDto);
        when(avatarRepository.findByStudentId(studentDto.getId())).thenThrow(new NoSuchElementException("Student does not have an avatar."));


        var ex = assertThrows(NoSuchElementException.class, () -> sut.getAvatarPreview(22L));
        assertEquals("Student does not have an avatar.", ex.getMessage());
        verify(studentService, times(1)).getStudentById(anyLong());
        verify(avatarRepository, times(1)).findByStudentId(anyLong());
    }

    @Test
    void saveAvatar_hadNoAvatar_shouldSaveAvatarToDbAndDisk() throws IOException {
        MultipartFile file = new MockMultipartFile("filename.jpg",
                "filename.jpg", "jpg", image);
        long studentId = 1L;
        when(studentService.getStudentById(studentId)).thenReturn(studentDto);
        when(avatarRepository.findByStudentId(studentId)).thenReturn(Optional.empty());
        sut.saveAvatar(studentId, file);

        verify(studentService, times(1)).getStudentById(studentId);
        verify(avatarRepository, times(1)).findByStudentId(studentId);
        verify(avatarRepository, times(1)).saveAndFlush(any());
        assertTrue(Files.isReadable(Path.of(path + "/" + studentId + ".jpg")));
    }

    @Test
    void saveAvatar_hadAvatar_shouldSaveAvatarToDbAndDisk() throws IOException {
        MultipartFile file = new MockMultipartFile("filename.jpg",
                "filename.jpg", "jpg", image);
        long studentId = 1L;
        Avatar oldAvatar = new Avatar();
        Student student = new Student();
        student.setId(studentId);
        oldAvatar.setStudent(student);

        when(studentService.getStudentById(studentId)).thenReturn(studentDto);
        when(avatarRepository.findByStudentId(studentId)).thenReturn(Optional.empty());
        sut.saveAvatar(studentId, file);

        verify(studentService, times(1)).getStudentById(studentId);
        verify(avatarRepository, times(1)).findByStudentId(studentId);
        verify(avatarRepository, times(1)).saveAndFlush(any());
        assertTrue(Files.isReadable(Path.of(path + "/" + studentId + ".jpg")));
    }

    @Test
    void saveAvatar_shouldThrowNoSuchElementExceptionIfNoStudentFound() throws IOException {
        when(studentService.getStudentById(anyLong())).thenThrow(new NoSuchElementException("Student not found."));
        MultipartFile file = new MockMultipartFile("filename.jpg",
                "filename.jpg", "jpg", image);

        var ex = assertThrows(NoSuchElementException.class, () -> sut.saveAvatar(1L, file));
        assertEquals("Student not found.", ex.getMessage());

        verify(avatarRepository, never()).saveAndFlush(any());
        verify(avatarRepository, never()).findByStudentId(anyLong());
    }
}