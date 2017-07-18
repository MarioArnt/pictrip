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

import com.arnautou.pictrip.domain.enumeration.Privacy;

import com.arnautou.pictrip.domain.enumeration.Color;

/**
 * A Trip.
 */
@Entity
@Table(name = "trip")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "trip")
public class Trip implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "name", length = 80, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "date_from")
    private LocalDate dateFrom;

    @Column(name = "date_to")
    private LocalDate dateTo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "privacy", nullable = false)
    private Privacy privacy;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "color", nullable = false)
    private Color color;

    @OneToOne
    @JoinColumn(unique = true)
    private Picture cover;

    @OneToMany(mappedBy = "trip")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Step> steps = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "trip_members",
               joinColumns = @JoinColumn(name="trips_id", referencedColumnName="id"),
               inverseJoinColumns = @JoinColumn(name="members_id", referencedColumnName="id"))
    private Set<User> members = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private User owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Trip name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Trip description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public Trip dateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
        return this;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public Trip dateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
        return this;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public Privacy getPrivacy() {
        return privacy;
    }

    public Trip privacy(Privacy privacy) {
        this.privacy = privacy;
        return this;
    }

    public void setPrivacy(Privacy privacy) {
        this.privacy = privacy;
    }

    public Color getColor() {
        return color;
    }

    public Trip color(Color color) {
        this.color = color;
        return this;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Picture getCover() {
        return cover;
    }

    public Trip cover(Picture picture) {
        this.cover = picture;
        return this;
    }

    public void setCover(Picture picture) {
        this.cover = picture;
    }

    public Set<Step> getSteps() {
        return steps;
    }

    public Trip steps(Set<Step> steps) {
        this.steps = steps;
        return this;
    }

    public Trip addSteps(Step step) {
        this.steps.add(step);
        step.setTrip(this);
        return this;
    }

    public Trip removeSteps(Step step) {
        this.steps.remove(step);
        step.setTrip(null);
        return this;
    }

    public void setSteps(Set<Step> steps) {
        this.steps = steps;
    }

    public Set<User> getMembers() {
        return members;
    }

    public Trip members(Set<User> users) {
        this.members = users;
        return this;
    }

    public Trip addMembers(User user) {
        this.members.add(user);
        user.getTrips().add(this);
        return this;
    }

    public Trip removeMembers(User user) {
        this.members.remove(user);
        user.getTrips().remove(this);
        return this;
    }

    public void setMembers(Set<User> users) {
        this.members = users;
    }

    public User getOwner() {
        return owner;
    }

    public Trip owner(User user) {
        this.owner = user;
        return this;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Trip trip = (Trip) o;
        if (trip.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), trip.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Trip{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", dateFrom='" + getDateFrom() + "'" +
            ", dateTo='" + getDateTo() + "'" +
            ", privacy='" + getPrivacy() + "'" +
            ", color='" + getColor() + "'" +
            "}";
    }
}
