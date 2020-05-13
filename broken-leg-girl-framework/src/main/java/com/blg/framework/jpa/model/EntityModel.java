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

    @Override
    @PrePersist
    public void init() {
        if (id == null) {
            this.id = UUID.randomUUID().toString();
        }
        super.init();
    }

    @Override
    public boolean equals(Object o) {
        // 判断地址是否相等
        if (this == o) {
            return true;
        }
        // 为空，或不同类则返回false
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        // 通过ID来判断是否为同一个对象
        EntityModel that = (EntityModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}


