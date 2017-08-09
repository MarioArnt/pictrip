package com.arnautou.pictrip.service;

import com.arnautou.pictrip.service.dto.StepDTO;
import java.util.List;

/**
 * Service Interface for managing Step.
 */
public interface StepService {

    /**
     * Save a step.
     *
     * @param stepDTO the entity to save
     * @return the persisted entity
     */
    StepDTO save(StepDTO stepDTO);

    /**
     *  Get all the steps.
     *
     *  @return the list of entities
     */
    List<StepDTO> findAll();

    /**
     *  Get all the steps of a given trip.
     *
     *  @return the list of entities
     */
    List<StepDTO> findByTripId(Long tripId);

    /**
     *  Get the "id" step.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    StepDTO findOne(Long id);

    /**
     *  Delete the "id" step.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    Integer countStepsByTripId(Long tripId);

    /**
     * Search for the step corresponding to the query.
     *
     *  @param query the query of the search
     *
     *  @return the list of entities
     */
    List<StepDTO> search(String query);
}
