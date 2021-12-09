package com.infosys.lex.areaofwork.entities;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.Set;

@Table(value = "user_areaofwork")
public class AreaOfWork {
    public AreaOfWork() {
        super();
    }

    @PrimaryKey
    private AreaOfWorkKey areaOfWorkKey;

    @Column("area_of_work")
    private Set<String> areaOfWork;

    @Column("created_on")
    private Date createdOn;

    @Column("updated_on")
    private Date updatedOn;

    public AreaOfWork(AreaOfWorkKey areaOfWorkKey, Set<String> areaOfWork, Date createdOn, Date updatedOn) {
        super();
        this.areaOfWorkKey = areaOfWorkKey;
        this.areaOfWork = areaOfWork;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    public AreaOfWorkKey getAreaOfWorkKey() {
        return areaOfWorkKey;
    }

    public void setAreaOfWorkKey(AreaOfWorkKey areaOfWorkKey) {
        this.areaOfWorkKey = areaOfWorkKey;
    }

    public Set<String> getAreaOfWork() {
        return areaOfWork;
    }

    public void setAreaOfWork(Set<String> areaOfWork) {
        this.areaOfWork = areaOfWork;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public String toString() {
        return "AreaOfWork{" +
                "areaOfWorkKey=" + areaOfWorkKey +
                ", areaOfWork=" + areaOfWork +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                '}';
    }
}
