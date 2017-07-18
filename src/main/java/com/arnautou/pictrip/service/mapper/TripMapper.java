package com.arnautou.pictrip.service.mapper;

import com.arnautou.pictrip.domain.*;
import com.arnautou.pictrip.service.dto.TripDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Trip and its DTO TripDTO.
 */
@Mapper(componentModel = "spring", uses = {PictureMapper.class, UserMapper.class, })
public interface TripMapper extends EntityMapper <TripDTO, Trip> {

    @Mapping(source = "cover.id", target = "coverId")
    @Mapping(source = "cover.src", target = "coverSrc")

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "owner.login", target = "ownerLogin")
    TripDTO toDto(Trip trip); 

    @Mapping(source = "coverId", target = "cover")
    @Mapping(target = "steps", ignore = true)

    @Mapping(source = "ownerId", target = "owner")
    Trip toEntity(TripDTO tripDTO); 
    default Trip fromId(Long id) {
        if (id == null) {
            return null;
        }
        Trip trip = new Trip();
        trip.setId(id);
        return trip;
    }
}
