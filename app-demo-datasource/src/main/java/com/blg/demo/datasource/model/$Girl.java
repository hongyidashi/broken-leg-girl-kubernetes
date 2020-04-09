package com.blg.demo.datasource.model;

import com.blg.framework.jpa.model.EntityModel;
import com.blg.framework.jpa.support.Comment;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Auther: panhongtong
 * @Date: 2020/3/31 17:01
 * @Description:
 */
@BatchSize(size = 500)
@Entity
@Comment("女孩表")
@Table(name = "girl")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, include = "non-lazy")
@SelectBeforeUpdate
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
public class $Girl extends EntityModel {

    @Comment("名字")
    private String name;
    @Comment("年龄")
    private Integer age;
}
