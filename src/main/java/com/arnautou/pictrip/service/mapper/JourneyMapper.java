package com.arnautou.pictrip.service.mapper;

import com.arnautou.pictrip.domain.*;
import com.arnautou.pictrip.service.dto.JourneyDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Journey and its DTO JourneyDTO.
 */
@Mapper(componentModel = "spring", uses = {StepMapper.class, })
public interface JourneyMapper extends EntityMapper <JourneyDTO, Journey> {

    @Mapping(source = "stepFrom.id", target = "stepFromId")

    @Mapping(source = "stepTo.id", target = "stepToId")

    @Mapping(source = "stepFrom.place.lat", target = "stepFromLat")

    @Mapping(source = "stepTo.place.lat", target = "stepToLat")

    @Mapping(source = "stepFrom.place.lon", target = "stepFromLng")

    @Mapping(source = "stepTo.place.lon", target = "stepToLng")
    JourneyDTO toDto(Journey journey);

    @Mapping(source = "stepFromId", target = "stepFrom")

    @Mapping(source = "stepToId", target = "stepTo")
    Journey toEntity(JourneyDTO journeyDTO);
    default Journey fromId(Long id) {
        if (id == null) {
            return null;
        }
        Journey journey = new Journey();
        journey.setId(id);
        return journey;
    }
}
