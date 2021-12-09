package com.infosys.lex.areaofexpertise.entities;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.Set;

@Table(value = "user_areaofexpertise")
public class AreaOfExpertise {
    public AreaOfExpertise() {
        super();
    }

    @PrimaryKey
    private AreaOfExpertiseKey areaOfExpertiseKey;

    @Column("area_of_expertise")
    private Set<String> areaOfExpertise;

    @Column("created_on")
    private Date createdOn;

    @Column("updated_on")
    private Date updatedOn;

    public AreaOfExpertise(AreaOfExpertiseKey areaOfExpertiseKey, Set<String> areaOfExpertise, Date createdOn, Date updatedOn) {
        super();
        this.areaOfExpertiseKey = areaOfExpertiseKey;
        this.areaOfExpertise = areaOfExpertise;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    public AreaOfExpertiseKey getAreaOfExpertiseKey() {
        return areaOfExpertiseKey;
    }

    public void setAreaOfExpertiseKey(AreaOfExpertiseKey areaOfExpertiseKey) {
        this.areaOfExpertiseKey = areaOfExpertiseKey;
    }

    public Set<String> getAreaOfExpertise() {
        return areaOfExpertise;
    }

    public void setAreaOfExpertise(Set<String> areaOfExpertise) {
        this.areaOfExpertise = areaOfExpertise;
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
        return "AreaOfExpertise{" +
                "areaOfExpertiseKey=" + areaOfExpertiseKey +
                ", areaOfExpertise=" + areaOfExpertise +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                '}';
    }
}
