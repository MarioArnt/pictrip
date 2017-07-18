package com.arnautou.pictrip.service;

import com.arnautou.pictrip.service.dto.PictureDTO;
import java.util.List;

/**
 * Service Interface for managing Picture.
 */
public interface PictureService {

    /**
     * Save a picture.
     *
     * @param pictureDTO the entity to save
     * @return the persisted entity
     */
    PictureDTO save(PictureDTO pictureDTO);

    /**
     *  Get all the pictures.
     *
     *  @return the list of entities
     */
    List<PictureDTO> findAll();

    /**
     *  Get the "id" picture.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    PictureDTO findOne(Long id);

    /**
     *  Delete the "id" picture.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the picture corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @return the list of entities
     */
    List<PictureDTO> search(String query);
}
