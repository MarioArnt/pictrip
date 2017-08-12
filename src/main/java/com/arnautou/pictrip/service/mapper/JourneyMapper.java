package com.arnautou.pictrip.service.mapper;

import com.arnautou.pictrip.domain.*;
import com.arnautou.pictrip.service.dto.JourneyDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Journey and its DTO JourneyDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface JourneyMapper extends EntityMapper <JourneyDTO, Journey> {
    
    
    default Journey fromId(Long id) {
        if (id == null) {
            return null;
        }
        Journey journey = new Journey();
        journey.setId(id);
        return journey;
    }
}
