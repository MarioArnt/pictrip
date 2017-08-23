package com.arnautou.pictrip.service.impl;

import com.arnautou.pictrip.domain.Journey;
import com.arnautou.pictrip.domain.Trip;
import com.arnautou.pictrip.service.*;
import com.arnautou.pictrip.domain.Step;
import com.arnautou.pictrip.repository.StepRepository;
import com.arnautou.pictrip.repository.search.StepSearchRepository;
import com.arnautou.pictrip.service.dto.JourneyDTO;
import com.arnautou.pictrip.service.dto.PlaceDTO;
import com.arnautou.pictrip.service.dto.StepDTO;
import com.arnautou.pictrip.service.mapper.StepMapper;
import com.arnautou.pictrip.web.rest.errors.ErrorDetails;
import com.arnautou.pictrip.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Step.
 */
@Service
@Transactional
public class StepServiceImpl implements StepService{

    private final Logger log = LoggerFactory.getLogger(StepServiceImpl.class);

    private final StepRepository stepRepository;

    private final StepMapper stepMapper;

    private final StepSearchRepository stepSearchRepository;

    private final PlaceService placeService;

    private final JourneyService journeyService;

    private final TripService tripService;

    private final UserService userService;

    public StepServiceImpl(StepRepository stepRepository, StepMapper stepMapper, StepSearchRepository stepSearchRepository, PlaceService placeService, JourneyService journeyService, TripService tripService, UserService userService) {
        this.stepRepository = stepRepository;
        this.stepMapper = stepMapper;
        this.stepSearchRepository = stepSearchRepository;
        this.placeService = placeService;
        this.journeyService = journeyService;
        this.tripService = tripService;
        this.userService = userService;
    }

    /**
     * Save a step.
     *
     * @param stepDTO the entity to save
     * @return the persisted entity
     */
    private Step save(StepDTO stepDTO) {
        log.debug("Request to save Step DTO: {}", stepDTO);
        Step step = stepMapper.toEntity(stepDTO);
        return save(step);
    }
    private Step save(Step step) {
        log.debug("Request to save Step : {}", step);
        step = stepRepository.save(step);
        stepSearchRepository.save(step);
        return step;
    }

    /**
     * Count the number of step in a given trip
     * @param tripId : the trip ID
     * @return the number of steps in this trip.
     */
    @Override
    public Integer countStepsByTripId(Long tripId) {
        return stepRepository.countByTripId(tripId);
    }

    /**
     *  Get all the steps.
     *
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<StepDTO> findAll() {
        log.debug("Request to get all Steps");
        return stepRepository.findAll().stream()
            .map(stepMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Check if the step creation is possible (i.e. valid trip ID and step number
     * and user has the right to create steps)
     *
     * @param step : the step DTO to create
     * @return an error details object if there is any error or null
     */
    @Override
    public ErrorDetails checkStepCreationPrerequisites(StepDTO step) {

        // Fetch trip
        if(step.getTripId() == null) {
            return new ErrorDetails(HttpStatus.BAD_REQUEST, "notripid", "The step you're trying to create is not linked to a Trip");
        }

        Trip trip = this.tripService.findTripById(step.getTripId());
        if(trip == null) {
            return new ErrorDetails(HttpStatus.BAD_REQUEST, "tripnotfound", "The Trip linked to this step does not exists");
        }

        // Check if user has right to post the step
        if(!trip.getOwner().getId().equals(this.userService.getCurrentLoggedUserId())) {
            return new ErrorDetails(HttpStatus.FORBIDDEN, "stepcreationforbidden", "You are not allowed to create a step for this Trip");
        }

        // Check that a Step with the same ID does not exist
        if (step.getId() != null) {
            return new ErrorDetails(HttpStatus.CONFLICT, "idexists", "A new step cannot already have an ID");
        }

        Integer numberSteps = this.countStepsByTripId(step.getTripId());
        if(step.getNumber()== null || step.getNumber() < 1 || step.getNumber() > numberSteps + 1) {
            return new ErrorDetails(HttpStatus.BAD_REQUEST, "invalidstepnumber", "The step number is not valid");
        }

        return null;
    }

    /**
     * Create a new step for a given trip. The step can be
     * inserted anywhere in the trip, the creation of the journeys
     * will be handled.
     *
     * @param step the DTO of the step to create.
     * @return the DTO of the newly created step
     */
    @Override
    public StepDTO createStep(StepDTO step) {

        Integer numberSteps = this.countStepsByTripId(step.getTripId());

        // If the step is not the last step (i.e. step is inserted between two steps)
        Step oldArrivalStep = null;
        JourneyDTO oldJourney = null;
        if(step.getNumber() < numberSteps + 1) {
            Step oldDepartureStep = this.findByTripIdAndNumber(step.getTripId(), step.getNumber() - 1);
            oldArrivalStep = this.findByTripIdAndNumber(step.getTripId(), step.getNumber());
            oldJourney = removeJourney(oldDepartureStep, oldArrivalStep);
            this.findAll().stream().filter(s -> s.getNumber() >= step.getNumber()).forEach(s -> {
                s.setNumber(s.getNumber() + 1);
                save(s);
            });
        }

        // Save the place of the step
        PlaceDTO place = new PlaceDTO();
        place.setLat(step.getPlaceLat());
        place.setLon(step.getPlaceLng());
        place.setName(step.getPlaceName());
        place = placeService.save(place);

        // Save the new step
        step.setPlaceId(place.getId());
        Step newStep = save(step);

        // Save the journey
        if(step.getNumber() > 1) {
            JourneyDTO journeyDTO = new JourneyDTO();
            journeyDTO.setTransportation(step.getArrivalTransportation());
            Step previousStep = this.findByTripIdAndNumber(step.getTripId(), step.getNumber() - 1);
            createJourney(journeyDTO, previousStep, newStep);
        }

        // Recreate the deleted journey
        if(step.getNumber() < numberSteps + 1) {
            createJourney(oldJourney, newStep, oldArrivalStep);
        }

        return stepMapper.toDto(newStep);
    }

    /**
     * Update a step. The others steps are automatically reordered if
     * the the updated step number changes.
     *
     * @param step the DTO of the step to update
     * @return the updated step DTO
     */
    @Override
    public StepDTO updateStep(StepDTO step) {

        Step oldStep = this.stepRepository.findOne(step.getId());
        Integer oldStepNumber = oldStep.getNumber();
        Integer newStepNumber = step.getNumber();
        step.setNumber(oldStepNumber);

        // Update place
        PlaceDTO place = new PlaceDTO();
        place.setId(oldStep.getPlace().getId());
        place.setLat(step.getPlaceLat());
        place.setLon(step.getPlaceLng());
        place.setName(step.getPlaceName());
        placeService.save(place);

        // Update step
        Step updatedStep = save(step);

        // Update journey
        if(step.getNumber() > 1) {
            JourneyDTO journeyDTO = new JourneyDTO();
            journeyDTO.setTransportation(step.getArrivalTransportation());
            this.journeyService.updateByStepTo(step.getId(), journeyDTO);
        }

        // Reorder steps if needed
        if(oldStepNumber != newStepNumber) {
            reorderSteps(step.getTripId(), oldStepNumber, newStepNumber);
        }

        return stepMapper.toDto(updatedStep);
    }

    /**
     * Reorder the trip steps
     * @param tripId : the trip ID
     * @param oldStepNumber : the step to move current number
     * @param newStepNumber : the step to move target number
     */
    private void reorderSteps(Long tripId, Integer oldStepNumber, Integer newStepNumber) {

        Integer numberSteps = this.countStepsByTripId(tripId);
        List<Step> tripSteps = getByTripId(tripId);

        Optional<Step> stepToMove = findStepInTripByNumber(tripSteps, oldStepNumber);
        Optional<Step> stepBeforeStepToMove = findStepInTripByNumber(tripSteps, oldStepNumber - 1);
        Optional<Step> stepAfterStepToMove = findStepInTripByNumber(tripSteps, oldStepNumber + 1);

        Optional<Step> stepDestination = findStepInTripByNumber(tripSteps, newStepNumber);
        Optional<Step> stepBeforeStepDestination = findStepInTripByNumber(tripSteps, newStepNumber - 1);
        Optional<Step> stepAfterStepDestination = findStepInTripByNumber(tripSteps, newStepNumber + 1);

        Optional<JourneyDTO> oldJourneyBetweenStepToMoveAndStepBefore = Optional.empty();
        Optional<JourneyDTO> oldJourneyBetweenStepToMoveAndStepAfter = Optional.empty();
        Optional<JourneyDTO> oldJourneyBetweenStepDestinationAndStepBefore = Optional.empty();
        Optional<JourneyDTO> oldJourneyBetweenStepDestinationAndStepAfter = Optional.empty();

        // Remove links between the step to move and the step before and after if any
        if (oldStepNumber > 1) {
            oldJourneyBetweenStepToMoveAndStepBefore = Optional.of(removeJourney(stepBeforeStepToMove.get(), stepToMove.get()));
        }
        if (oldStepNumber < numberSteps) {
            oldJourneyBetweenStepToMoveAndStepAfter = Optional.of(removeJourney(stepToMove.get(), stepAfterStepToMove.get()));
        }
        // Remove link between the two steps between which we want to insert the moved step
        if (newStepNumber < numberSteps && oldStepNumber < newStepNumber) {
            oldJourneyBetweenStepDestinationAndStepAfter = Optional.of(removeJourney(stepDestination.get(), stepAfterStepDestination.get()));
        }
        if (newStepNumber > 1 && oldStepNumber > newStepNumber) {
            oldJourneyBetweenStepDestinationAndStepBefore = Optional.of(removeJourney(stepBeforeStepDestination.get(), stepDestination.get()));
        }

        // Update other steps numbers (decrement or increment according to the moving direction)
        if (oldStepNumber < newStepNumber) {
            tripSteps.stream()
                .filter(step -> step.getNumber() > oldStepNumber && step.getNumber() <= newStepNumber)
                .forEach(step -> {
                    step.setNumber(step.getNumber() - 1);
                    save(step);
                });
        }
        else if (oldStepNumber > newStepNumber) {
            tripSteps.stream()
                .filter(step -> step.getNumber() >= newStepNumber && step.getNumber() < oldStepNumber)
                .forEach(step -> {
                    step.setNumber(step.getNumber() + 1);
                    save(step);
                });
        }

        // Update step to move number
        stepToMove.get().setNumber(newStepNumber);

        // Recreate linked around the newly moved steps
        if (newStepNumber > 1) {
            createJourney(
                oldJourneyBetweenStepToMoveAndStepBefore.get(),
                findStepInTripByNumber(tripSteps, newStepNumber - 1).get(),
                findStepInTripByNumber(tripSteps, newStepNumber).get()
            );
        }
        if (newStepNumber < numberSteps) {
            if( oldStepNumber < newStepNumber) {
                createJourney(
                    oldJourneyBetweenStepDestinationAndStepAfter.get(),
                    findStepInTripByNumber(tripSteps, newStepNumber).get(),
                    findStepInTripByNumber(tripSteps, newStepNumber + 1).get()
                );
            } else {
                createJourney(
                    oldJourneyBetweenStepDestinationAndStepBefore.get(),
                    findStepInTripByNumber(tripSteps, newStepNumber).get(),
                    findStepInTripByNumber(tripSteps, newStepNumber + 1).get()
                );
            }
        }

        // Recreate link between the two steps that used to be before and after the moved step.
        if(oldStepNumber > 1 && oldStepNumber < newStepNumber) {
            createJourney(
                oldJourneyBetweenStepToMoveAndStepAfter.get(),
                findStepInTripByNumber(tripSteps, oldStepNumber - 1).get(),
                findStepInTripByNumber(tripSteps, oldStepNumber).get()
            );
        }
        if(oldStepNumber < numberSteps && oldStepNumber > newStepNumber) {
            createJourney(
                oldJourneyBetweenStepToMoveAndStepAfter.get(),
                findStepInTripByNumber(tripSteps, oldStepNumber).get(),
                findStepInTripByNumber(tripSteps, oldStepNumber + 1).get()
            );
        }

        this.stepRepository.save(tripSteps);
    }

    /**
     * Delete a step from a trip. Will automatically delete the associated
     * journey
     *
     * @param stepId the ID of the step to delete
     */
    @Override
    public void deleteStep(Long stepId){
        // Fetch step to delete
        Step stepToDelete = this.stepRepository.findOne(stepId);
        Long tripId = stepToDelete.getTrip().getId();
        Integer stepNumber = stepToDelete.getNumber();
        List<Step> tripSteps = getByTripId(tripId);
        Integer numberSteps = tripSteps.size();

        // Remove linked journeys
        Optional<JourneyDTO> oldJourney = Optional.empty();
        if(stepNumber > 1) {
            removeJourney(findStepInTripByNumber(tripSteps, stepNumber - 1).get(), stepToDelete);
        }
        if(stepNumber < numberSteps) {
            oldJourney = Optional.of(removeJourney(stepToDelete, findStepInTripByNumber(tripSteps, stepNumber +1).get()));
        }

        // Remove step
        this.stepRepository.delete(stepId);

        // Reindex other steps
        tripSteps.stream().filter(step -> step.getNumber() > stepNumber).forEach(step -> {
            step.setNumber(step.getNumber() - 1);
        });

        this.stepRepository.save(tripSteps);

        //Recreate link if step was delete between two others steps
        if(stepNumber > 1 && stepNumber < numberSteps) {
            createJourney(
                oldJourney.get(),
                findStepInTripByNumber(tripSteps, stepNumber - 1).get(),
                findStepInTripByNumber(tripSteps, stepNumber).get()
            );
        }
    }

    private Optional<Step> findStepInTripByNumber(List<Step> tripSteps, Integer number) {
        return tripSteps.stream().filter(step -> step.getNumber().equals(number)).findAny();
    }

    /**
     * Create a journey between two step and update steps arrival and departure
     * information
     *
     * @param journeyDTO : the journey to create
     * @param stepFrom : the step of departure
     * @param stepTo : the step of arrival
     */
    private void createJourney(JourneyDTO journeyDTO, Step stepFrom, Step stepTo) {
        Journey journey = this.journeyService.create(journeyDTO, stepFrom.getId(), stepTo.getId());
        stepFrom.setDeparture(journey);
        stepTo.setArrival(journey);
        save(stepFrom);
        save(stepTo);
    }

    /**
     * Remove a journey between two steps, also update arrival and departure journeys
     * of impacted steps.
     *
     * @param stepFrom the departure step
     * @param stepTo the arrival step
     * @return the DTO of the deleted step
     */
    private JourneyDTO removeJourney(Step stepFrom, Step stepTo) {
        JourneyDTO oldJourney = this.journeyService.remove(stepFrom.getId(), stepTo.getId());
        stepFrom.setDeparture(null);
        stepTo.setArrival(null);
        save(stepFrom);
        save(stepTo);
        return oldJourney;
    }

    /**
     *  Get all the steps of a given trip.
     *
     *  @param tripId : the ID of the trip
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<StepDTO> findByTripId(Long tripId) {
        log.debug("Request to get all Steps of a given trip");
        return stepRepository.findByTripId(tripId).stream()
            .map(stepMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    private List<Step> getByTripId(Long tripId) {
        return stepRepository.findByTripId(tripId);
    }

    /**
     * Find a step by trip ID and step number
     *
     * @param tripId : the trip ID
     * @param stepNumber : the number of the step in this trip
     * @return : the matching Step if exists, null otherwise
     */
    private Step findByTripIdAndNumber(Long tripId, Integer stepNumber) {
        log.debug("Request to get Step number # of a given trip");
        Optional<Step> result = stepRepository.findOneByTripIdAndNumber(tripId, stepNumber);
        if(result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /**
     *  Get one step by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public StepDTO findOne(Long id) {
        log.debug("Request to get Step : {}", id);
        Step step = stepRepository.findOne(id);
        return stepMapper.toDto(step);
    }

    /**
     *  Delete the  step by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Step : {}", id);
        stepRepository.delete(id);
        stepSearchRepository.delete(id);
    }

    /**
     * Search for the step corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<StepDTO> search(String query) {
        log.debug("Request to search Steps for query {}", query);
        return StreamSupport
            .stream(stepSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(stepMapper::toDto)
            .collect(Collectors.toList());
    }
}
