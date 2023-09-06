package ru.hogwarts.school.models.dto;

import ru.hogwarts.school.models.domain.Student;

import java.util.List;
import java.util.Objects;

public class FacultyDto {
    private Long id;
    private String name;
    private String colour;

    public FacultyDto() {
    }

    public FacultyDto(Long id, String name, String colour) {
        this.id = id;
        this.name = name;
        this.colour = colour;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacultyDto that = (FacultyDto) o;
        return Objects.equals(name, that.name) && Objects.equals(colour, that.colour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, colour);
    }

    @Override
    public String toString() {
        return "FacultyDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", colour='" + colour + '\'' +
                '}';
    }
}
