package com.arnautou.pictrip.service.mapper;

import com.arnautou.pictrip.domain.*;
import com.arnautou.pictrip.service.dto.PictureDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Picture and its DTO PictureDTO.
 */
@Mapper(componentModel = "spring", uses = {PlaceMapper.class, StepMapper.class, })
public interface PictureMapper extends EntityMapper <PictureDTO, Picture> {

    @Mapping(source = "place.id", target = "placeId")
    @Mapping(source = "place.name", target = "placeName")

    @Mapping(source = "step.id", target = "stepId")
    //@Mapping(source = "step.name", target = "stepName")
    PictureDTO toDto(Picture picture);

    @Mapping(source = "placeId", target = "place")

    @Mapping(source = "stepId", target = "step")
    Picture toEntity(PictureDTO pictureDTO);
    default Picture fromId(Long id) {
        if (id == null) {
            return null;
        }
        Picture picture = new Picture();
        picture.setId(id);
        return picture;
    }
}
