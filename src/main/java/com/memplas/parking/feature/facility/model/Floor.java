package com.memplas.parking.feature.facility.model;

import com.memplas.parking.core.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "floors")
public class Floor extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "facility_id")
    private ParkingFacility facility;

    private Integer number;

    private String name;

    private Integer columns;

    private Integer rows;

    private Boolean isAccessible;

    private Double maxHeight;
//<editor-fold desc="Constructors">

    public Floor() {
    }

    public Floor(Long id) {
        super(id);
    }
//</editor-fold>

//<editor-fold desc="Getters">

    public ParkingFacility getFacility() {
        return facility;
    }

    public Integer getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public Integer getColumns() {
        return columns;
    }

    public Integer getRows() {
        return rows;
    }

    public Boolean getAccessible() {
        return isAccessible;
    }

    public Double getMaxHeight() {
        return maxHeight;
    }


//</editor-fold>

//<editor-fold desc="Setters">

    public void setFacility(ParkingFacility facility) {
        this.facility = facility;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public void setAccessible(Boolean accessible) {
        isAccessible = accessible;
    }

    public void setMaxHeight(Double maxHeight) {
        this.maxHeight = maxHeight;
    }


//</editor-fold>

//<editor-fold desc="toString">

    @Override
    public String toString() {
        return "Floor{" +
                ", number=" + number +
                ", name='" + name + '\'' +
                ", columns=" + columns +
                ", rows=" + rows +
                ", isAccessible=" + isAccessible +
                ", maxHeight=" + maxHeight +
                '}';
    }


//</editor-fold>

}
