package com.arnautou.pictrip.service.dto;


import com.arnautou.pictrip.domain.enumeration.Transportation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the Step entity.
 */

public class StepDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 0)
    private Integer number;

    private String description;

    private LocalDate dateFrom;

    private LocalDate dateTo;

    private Long placeId;

    private String placeName;

    private Double placeLat;

    private Double placeLng;

    private Long tripId;

    private String tripName;

    private Long arrivalId;

    private Transportation arrivalTransportation;

    private Long departureId;

    private Transportation departureTransportation;

    private Set<PictureDTO> pictures;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public Double getPlaceLat() {
        return placeLat;
    }

    public void setPlaceLat(Double placeLat) {
        this.placeLat = placeLat;
    }

    public Double getPlaceLng() {
        return placeLng;
    }

    public void setPlaceLng(Double placeLng) {
        this.placeLng = placeLng;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public Long getArrivalId() {
        return arrivalId;
    }

    public void setArrivalId(Long arrivalId) {
        this.arrivalId = arrivalId;
    }

    public Transportation getArrivalTransportation() {
        return arrivalTransportation;
    }

    public void setArrivalTransportation(Transportation arrivalTransportation) {
        this.arrivalTransportation = arrivalTransportation;
    }

    public void setDepartureId(Long departureId) {
        this.departureId = departureId;
    }

    public Long getDepartureId() {
        return departureId;
    }

    public void setDepartureTransportation(Transportation departureTransportation) {
        this.departureTransportation = departureTransportation;
    }

    public Transportation getDepartureTransportation() {
        return departureTransportation;
    }

    public Set<PictureDTO> getPictures() {
        return pictures;
    }

    public void setPictures(Set<PictureDTO> pictures) {
        this.pictures = pictures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StepDTO stepDTO = (StepDTO) o;
        if(stepDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), stepDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "StepDTO{" +
            "id=" + getId() +
            ", number='" + getNumber() + "'" +
            ", description='" + getDescription() + "'" +
            ", dateFrom='" + getDateFrom() + "'" +
            ", dateTo='" + getDateTo() + "'" +
            "}";
    }
}
