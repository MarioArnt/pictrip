package com.arnautou.pictrip.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Picture.
 */
@Entity
@Table(name = "picture")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "picture")
public class Picture implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "src", nullable = false)
    private String src;

    @Column(name = "caption")
    private String caption;

    @NotNull
    @Column(name = "jhi_size", nullable = false)
    private Long size;

    @Column(name = "views")
    private Long views;

    @ManyToOne
    private Place place;

    @ManyToOne
    private Step step;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSrc() {
        return src;
    }

    public Picture src(String src) {
        this.src = src;
        return this;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getCaption() {
        return caption;
    }

    public Picture caption(String caption) {
        this.caption = caption;
        return this;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Long getSize() {
        return size;
    }

    public Picture size(Long size) {
        this.size = size;
        return this;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getViews() {
        return views;
    }

    public Picture views(Long views) {
        this.views = views;
        return this;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Place getPlace() {
        return place;
    }

    public Picture place(Place place) {
        this.place = place;
        return this;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public Step getStep() {
        return step;
    }

    public Picture step(Step step) {
        this.step = step;
        return this;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Picture picture = (Picture) o;
        if (picture.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), picture.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Picture{" +
            "id=" + getId() +
            ", src='" + getSrc() + "'" +
            ", caption='" + getCaption() + "'" +
            ", size='" + getSize() + "'" +
            ", views='" + getViews() + "'" +
            "}";
    }
}
