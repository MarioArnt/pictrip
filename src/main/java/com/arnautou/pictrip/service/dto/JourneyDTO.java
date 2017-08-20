package com.arnautou.pictrip.service.dto;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import com.arnautou.pictrip.domain.enumeration.Transportation;

/**
 * A DTO for the Journey entity.
 */
public class JourneyDTO implements Serializable {

    private Long id;

    private Transportation transportation;

    private Long duration;

    private Long stepFromId;
    private Double stepFromLat;
    private Double stepFromLng;


    private Long stepToId;
    private Double stepToLat;
    private Double stepToLng;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transportation getTransportation() {
        return transportation;
    }

    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getStepFromId() {
        return stepFromId;
    }

    public void setStepFromId(Long stepId) {
        this.stepFromId = stepId;
    }

    public Double getStepFromLat() {
        return stepFromLat;
    }

    public void setStepFromLat(Double stepLat) {
        this.stepFromLat = stepLat;
    }

    public Double getStepFromLng() {
        return stepFromLng;
    }

    public void setStepFromLng(Double stepLng) {
        this.stepFromLng = stepLng;
    }

    public Long getStepToId() {
        return stepToId;
    }

    public void setStepToId(Long stepId) {
        this.stepToId = stepId;
    }

    public Double getStepToLat() {
        return stepToLat;
    }

    public void setStepToLat(Double stepLat) {
        this.stepToLat = stepLat;
    }

    public Double getStepToLng() {
        return stepToLng;
    }

    public void setStepToLng(Double stepLng) {
        this.stepToLng = stepLng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JourneyDTO journeyDTO = (JourneyDTO) o;
        if(journeyDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), journeyDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "JourneyDTO{" +
            "id=" + getId() +
            ", transportation='" + getTransportation() + "'" +
            ", duration='" + getDuration() + "'" +
            "}";
    }
}
