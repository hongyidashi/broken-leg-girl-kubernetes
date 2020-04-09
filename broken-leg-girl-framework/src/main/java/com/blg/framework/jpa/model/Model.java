package com.blg.framework.jpa.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.blg.framework.jpa.support.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 模型基类
 * @author lujijiang
 */
@MappedSuperclass
public class Model implements Serializable, Cloneable{
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Comment("创建时间")
    private Date timestampCreated;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Comment("更新时间")
    private Date timestampUpdated;
    @Column(columnDefinition = "decimal(1,0)")
    @NotNull
    @Comment("无效标志")
    protected boolean disabled;
    @Column
    @Version
    @NotNull
    @Comment("版本号")
    protected int version;

    public Date getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(Date timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public Date getTimestampUpdated() {
        return timestampUpdated;
    }

    public void setTimestampUpdated(Date timestampUpdated) {
        this.timestampUpdated = timestampUpdated;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @PrePersist
    public void init() {
        if (timestampUpdated == null)
            this.timestampUpdated = new Date();
        if (timestampCreated == null)
            this.timestampCreated = this.timestampUpdated;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this,SerializerFeature.WriteClassName,SerializerFeature.PrettyFormat);
    }
}
