package com.blg.framework.jpa.model;


import com.blg.framework.jpa.support.Comment;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.UUID;

/**
 * 实体模型
 * @author lujijiang
 */
@MappedSuperclass
public class EntityModel extends Model {
    @Id
    @NotNull
    @Size(min = 36, max = 36)
    @Column
    @Comment("主键")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PrePersist
    public void init() {
        if (id == null) {
            this.id = UUID.randomUUID().toString();
        }
        super.init();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EntityModel that = (EntityModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}


