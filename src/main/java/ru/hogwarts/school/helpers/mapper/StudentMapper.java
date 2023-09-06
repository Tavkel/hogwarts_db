package ru.hogwarts.school.helpers.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.hogwarts.school.models.domain.Student;
import ru.hogwarts.school.models.dto.StudentDto;

@Mapper(uses = {FacultyMapper.class})
public interface StudentMapper {
    StudentMapper MAPPER = Mappers.getMapper(StudentMapper.class);

    @Mapping(source = "facultyId", target = "faculty.id")
    @Mapping(target = "deleted", ignore = true)
    Student toStudent(StudentDto studentDto);

    @InheritInverseConfiguration
    @Mapping(source = "faculty.name", target = "facultyName")
    StudentDto fromStudent(Student student);
}
