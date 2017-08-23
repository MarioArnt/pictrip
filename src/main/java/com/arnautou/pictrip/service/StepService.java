package com.arnautou.pictrip.service;

import com.arnautou.pictrip.domain.Step;
import com.arnautou.pictrip.service.dto.StepDTO;
import com.arnautou.pictrip.web.rest.errors.ErrorDetails;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Step.
 */
public interface StepService {

    /**
     *  Get all the steps.
     *
     *  @return the list of entities
     */
    List<StepDTO> findAll();

    /**
     * Check if the step creation is possible (i.e. valid trip ID and step number
     * and user has the right to create steps)
     *
     * @param step : the step DTO to create
     * @return an error details object if there is any error or null
     */
    ErrorDetails checkStepCreationPrerequisites(StepDTO step);

    /**
     * Create a new step for a given trip. The step can be
     * inserted anywhere in the trip, the creation of the journeys
     * will be handled.
     *
     * @param step the DTO of the step to create.
     * @return the DTO of the newly created step
     */
    StepDTO createStep(StepDTO step);

    /**
     * Update a step. The others steps are automatically reordered if
     * the the updated step number changes.
     *
     * @param step the DTO of the step to update
     * @return the updated step DTO
     */
    StepDTO updateStep(StepDTO step);

    /**
     * Delete a step from a trip. Will automatically delete the associated
     * journey
     *
     * @param stepId the ID of the step to delete
     */
    void deleteStep(Long stepId);
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
