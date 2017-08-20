package com.arnautou.pictrip.service;

import com.arnautou.pictrip.service.dto.JourneyDTO;
import java.util.List;

/**
 * Service Interface for managing Journey.
 */
public interface JourneyService {

    /**
     * Save a journey.
     *
     * @param journeyDTO the entity to save
     * @return the persisted entity
     */
    JourneyDTO save(JourneyDTO journeyDTO);

    /**
     *  Get all the journeys.
     *
     *  @return the list of entities
     */
    List<JourneyDTO> findAll();

    /**
     *  Get the "id" journey.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    JourneyDTO findOne(Long id);

    /**
     *  Delete the "id" journey.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Update the journey that arrives to a given step
     * @param stepToId : the ID of arrival step
     * @param journey : the updated journey information DTO
     */
    void updateByStepTo(Long stepToId, JourneyDTO journey);

    /**
     * Search for the journey corresponding to the query.
     *
     *  @param query the query of the search
     *
     *  @return the list of entities
     */
    List<JourneyDTO> search(String query);
}
