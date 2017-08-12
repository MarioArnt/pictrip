package com.arnautou.pictrip.service.dto;


import java.io.Serializable;
import java.util.Objects;
import com.arnautou.pictrip.domain.enumeration.Transportation;

/**
 * A DTO for the Journey entity.
 */
public class JourneyDTO implements Serializable {

    private Long id;

    private Transportation transportation;

    private Long duration;

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
