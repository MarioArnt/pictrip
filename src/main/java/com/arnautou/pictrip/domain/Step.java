package com.arnautou.pictrip.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Step.
 */
@Entity
@Table(name = "step")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "step")
public class Step implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Min(value = 0)
    @Column(name = "jhi_number", nullable = false)
    private Integer number;

    @Column(name = "description")
    private String description;

    @Column(name = "date_from")
    private LocalDate dateFrom;

    @Column(name = "date_to")
    private LocalDate dateTo;

    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Place place;

    @OneToMany(mappedBy = "step")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Picture> pictures = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private Trip trip;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public Step number(Integer number) {
        this.number = number;
        return this;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public Step description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public Step dateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
        return this;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public Step dateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
        return this;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public Place getPlace() {
        return place;
    }

    public Step place(Place place) {
        this.place = place;
        return this;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public Set<Picture> getPictures() {
        return pictures;
    }

    public Step pictures(Set<Picture> pictures) {
        this.pictures = pictures;
        return this;
    }

    public Step addPictures(Picture picture) {
        this.pictures.add(picture);
        picture.setStep(this);
        return this;
    }

    public Step removePictures(Picture picture) {
        this.pictures.remove(picture);
        picture.setStep(null);
        return this;
    }

    public void setPictures(Set<Picture> pictures) {
        this.pictures = pictures;
    }

    public Trip getTrip() {
        return trip;
    }

    public Step trip(Trip trip) {
        this.trip = trip;
        return this;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Step step = (Step) o;
        if (step.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), step.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Step{" +
            "id=" + getId() +
            ", number='" + getNumber() + "'" +
            ", description='" + getDescription() + "'" +
            ", dateFrom='" + getDateFrom() + "'" +
            ", dateTo='" + getDateTo() + "'" +
            "}";
    }
}
