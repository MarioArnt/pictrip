package com.arnautou.pictrip.service.mapper;

import com.arnautou.pictrip.domain.*;
import com.arnautou.pictrip.service.dto.PlaceDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Place and its DTO PlaceDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PlaceMapper extends EntityMapper <PlaceDTO, Place> {
    
    
    default Place fromId(Long id) {
        if (id == null) {
            return null;
        }
        Place place = new Place();
        place.setId(id);
        return place;
    }
}
