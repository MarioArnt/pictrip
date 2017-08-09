package com.arnautou.pictrip.service.mapper;

import com.arnautou.pictrip.domain.*;
import com.arnautou.pictrip.service.dto.StepDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Step and its DTO StepDTO.
 */
@Mapper(componentModel = "spring", uses = {PlaceMapper.class, TripMapper.class, })
public interface StepMapper extends EntityMapper <StepDTO, Step> {

    @Mapping(source = "place.id", target = "placeId")
    @Mapping(source = "place.name", target = "placeName")
    @Mapping(source = "place.lat", target = "placeLat")
    @Mapping(source = "place.lon", target = "placeLng")
    @Mapping(source = "trip.id", target = "tripId")
    @Mapping(source = "trip.name", target = "tripName")
    StepDTO toDto(Step step);

    @Mapping(source = "placeId", target = "place")
    @Mapping(target = "pictures", ignore = true)
    @Mapping(source = "tripId", target = "trip")
    Step toEntity(StepDTO stepDTO);
    default Step fromId(Long id) {
        if (id == null) {
            return null;
        }
        Step step = new Step();
        step.setId(id);
        return step;
    }
}
