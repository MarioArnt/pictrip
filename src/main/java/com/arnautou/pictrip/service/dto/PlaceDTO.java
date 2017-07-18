package com.arnautou.pictrip.service.dto;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Place entity.
 */
public class PlaceDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "-85.05115")
    @DecimalMax(value = "85.05115")
    private Double lat;

    @NotNull
    @DecimalMin(value = "-180")
    @DecimalMax(value = "180")
    private Double lon;

    @NotNull
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PlaceDTO placeDTO = (PlaceDTO) o;
        if(placeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), placeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PlaceDTO{" +
            "id=" + getId() +
            ", lat='" + getLat() + "'" +
            ", lon='" + getLon() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
}
