package ru.hogwarts.school.models.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public abstract class SafeDeleted {
    @Column(name = "deleted")
    private Boolean deleted = false;

    public SafeDeleted() {
    }

    public SafeDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SafeDeleted that = (SafeDeleted) o;
        return Objects.equals(deleted, that.deleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deleted);
    }

    @Override
    public String toString() {
        return "SafeDeleted{" +
                "deleted=" + deleted +
                '}';
    }
}
