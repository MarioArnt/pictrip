package com.arnautou.pictrip.service.dto;


import java.time.LocalDate;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import com.arnautou.pictrip.domain.enumeration.Privacy;
import com.arnautou.pictrip.domain.enumeration.Color;

/**
 * A DTO for the Trip entity.
 */
public class TripDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String name;

    private String description;

    private LocalDate dateFrom;

    private LocalDate dateTo;

    @NotNull
    private Privacy privacy;

    @NotNull
    private Color color;

    private Long coverId;

    private String coverSrc;

    private Set<UserDTO> members = new HashSet<>();

    private Long ownerId;

    private String ownerLogin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Privacy getPrivacy() {
        return privacy;
    }

    public void setPrivacy(Privacy privacy) {
        this.privacy = privacy;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Long getCoverId() {
        return coverId;
    }

    public void setCoverId(Long pictureId) {
        this.coverId = pictureId;
    }

    public String getCoverSrc() {
        return coverSrc;
    }

    public void setCoverSrc(String pictureSrc) {
        this.coverSrc = pictureSrc;
    }

    public Set<UserDTO> getMembers() {
        return members;
    }

    public void setMembers(Set<UserDTO> users) {
        this.members = users;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long userId) {
        this.ownerId = userId;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String userLogin) {
        this.ownerLogin = userLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TripDTO tripDTO = (TripDTO) o;
        if(tripDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), tripDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TripDTO{" +
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
