package com.arnautou.pictrip.service;

import com.arnautou.pictrip.domain.Journey;
import com.arnautou.pictrip.service.dto.JourneyDTO;
import java.util.List;

/**
 * Service Interface for managing Journey.
 */
public interface JourneyService {

    /**
     * Create a journey between two steps
     *
     * @param journeyDTO the entity to save
     * @param stepFrom the departure step
     * @param stepTo the arrival step
     */
    Journey create(JourneyDTO journeyDTO, Long stepFrom, Long stepTo);

    /**
     * Remove a journey between two steps
     *
     * @param stepFrom the departure step
     * @param stepTo the arrival step
     * @return a copy of the removed step
     */
    JourneyDTO remove(Long stepFrom, Long stepTo);

   /**
    * Update the journey that arrives to a given step
    * @param stepToId : the ID of arrival step
    * @param journey : the updated journey information DTO
    */
    void updateByStepTo(Long stepToId, JourneyDTO journey);


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
     * Search for the journey corresponding to the query.
     *
     *  @param query the query of the search
     *
     *  @return the list of entities
     */
    List<JourneyDTO> search(String query);
}
