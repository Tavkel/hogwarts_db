package ru.hogwarts.school.models.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "faculties")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Faculty extends SafeDeleted {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "colour")
    private String colour;
    @OneToMany(mappedBy = "faculty")
    private List<Student> students;


    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Faculty() {
    }

    public Faculty(Long id) {
        this.id = id;
    }

    public Faculty(String name, String colour) {
        this.name = name;
        this.colour = colour;
    }

    public Faculty(Long id, String name, String colour, boolean deleted) {
        super(deleted);
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
        Faculty faculty = (Faculty) o;
        return Objects.equals(id, faculty.id)
                && Objects.equals(name, faculty.name)
                && Objects.equals(colour, faculty.colour)
                && Objects.equals(getDeleted(), faculty.getDeleted());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, colour, getDeleted());
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", colour='" + colour + '\'' +
                '}';
    }
}
