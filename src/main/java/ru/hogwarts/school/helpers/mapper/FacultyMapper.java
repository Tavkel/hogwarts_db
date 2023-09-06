package ru.hogwarts.school.helpers.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.hogwarts.school.models.domain.Faculty;
import ru.hogwarts.school.models.dto.FacultyDto;

@Mapper
public interface FacultyMapper {
    FacultyMapper MAPPER = Mappers.getMapper(FacultyMapper.class);

    @Mapping(target = "students", ignore = true)
    Faculty toFaculty(FacultyDto facultyDto);

    @InheritInverseConfiguration
    FacultyDto fromFaculty(Faculty faculty);
}
