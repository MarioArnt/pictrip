package com.arnautou.pictrip.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

import com.arnautou.pictrip.domain.enumeration.Transportation;

/**
 * A Journey.
 */
@Entity
@Table(name = "journey")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "journey")
public class Journey implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "transportation")
    private Transportation transportation;

    @Column(name = "duration")
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

    public Journey transportation(Transportation transportation) {
        this.transportation = transportation;
        return this;
    }

    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
    }

    public Long getDuration() {
        return duration;
    }

    public Journey duration(Long duration) {
        this.duration = duration;
        return this;
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
        Journey journey = (Journey) o;
        if (journey.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), journey.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Journey{" +
            "id=" + getId() +
            ", transportation='" + getTransportation() + "'" +
            ", duration='" + getDuration() + "'" +
            "}";
    }
}
