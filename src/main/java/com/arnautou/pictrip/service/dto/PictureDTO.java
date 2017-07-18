package com.arnautou.pictrip.service.dto;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the Picture entity.
 */
public class PictureDTO implements Serializable {

    private Long id;

    @NotNull
    private String src;

    private String caption;

    @NotNull
    private Long size;

    private Long views;

    private Long placeId;

    private String placeName;

    private Long stepId;

    private String stepName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
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

    public Long getStepId() {
        return stepId;
    }

    public void setStepId(Long stepId) {
        this.stepId = stepId;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PictureDTO pictureDTO = (PictureDTO) o;
        if(pictureDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), pictureDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PictureDTO{" +
            "id=" + getId() +
            ", src='" + getSrc() + "'" +
            ", caption='" + getCaption() + "'" +
            ", size='" + getSize() + "'" +
            ", views='" + getViews() + "'" +
            "}";
    }
}
