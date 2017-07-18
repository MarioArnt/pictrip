package com.arnautou.pictrip.service;

import com.arnautou.pictrip.service.dto.TripDTO;
import java.util.List;

/**
 * Service Interface for managing Trip.
 */
public interface TripService {

    /**
     * Save a trip.
     *
     * @param tripDTO the entity to save
     * @return the persisted entity
     */
    TripDTO save(TripDTO tripDTO);

    /**
     *  Get all the trips.
     *
     *  @return the list of entities
     */
    List<TripDTO> findAll();

    /**
     *  Get the "id" trip.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    TripDTO findOne(Long id);

    /**
     *  Delete the "id" trip.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the trip corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @return the list of entities
     */
    List<TripDTO> search(String query);
}
