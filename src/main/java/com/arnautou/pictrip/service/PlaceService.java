package com.arnautou.pictrip.service;

import com.arnautou.pictrip.service.dto.PlaceDTO;
import java.util.List;

/**
 * Service Interface for managing Place.
 */
public interface PlaceService {

    /**
     * Save a place.
     *
     * @param placeDTO the entity to save
     * @return the persisted entity
     */
    PlaceDTO save(PlaceDTO placeDTO);

    /**
     *  Get all the places.
     *
     *  @return the list of entities
     */
    List<PlaceDTO> findAll();

    /**
     *  Get the "id" place.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    PlaceDTO findOne(Long id);

    /**
     *  Delete the "id" place.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the place corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @return the list of entities
     */
    List<PlaceDTO> search(String query);
}
