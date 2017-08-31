package com.arnautou.pictrip.service.impl;

import com.arnautou.pictrip.service.JourneyService;
import com.arnautou.pictrip.domain.Journey;
import com.arnautou.pictrip.repository.JourneyRepository;
import com.arnautou.pictrip.repository.search.JourneySearchRepository;
import com.arnautou.pictrip.service.dto.JourneyDTO;
import com.arnautou.pictrip.service.mapper.JourneyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Journey.
 */
@Service
@Transactional
public class JourneyServiceImpl implements JourneyService{

    private final Logger log = LoggerFactory.getLogger(JourneyServiceImpl.class);

    private final JourneyRepository journeyRepository;

    private final JourneyMapper journeyMapper;

    private final JourneySearchRepository journeySearchRepository;

    public JourneyServiceImpl(JourneyRepository journeyRepository, JourneyMapper journeyMapper, JourneySearchRepository journeySearchRepository) {
        this.journeyRepository = journeyRepository;
        this.journeyMapper = journeyMapper;
        this.journeySearchRepository = journeySearchRepository;
    }

    /**
     * Save a journey.
     *
     * @param journeyDTO the entity to save
     * @return the persisted entity
     */
    private Journey save(JourneyDTO journeyDTO) {
        log.debug("Request to save Journey : {}", journeyDTO);
        Journey journey = journeyMapper.toEntity(journeyDTO);
        journey = journeyRepository.save(journey);
        journeySearchRepository.save(journey);
        return journey;
    }

    /**
     * Create a journey between two steps
     *
     * @param journeyDTO the entity to save
     * @param stepFrom the departure step
     * @param stepTo the arrival step
     */
    @Override
    public Journey create(JourneyDTO journeyDTO, Long stepFrom, Long stepTo) {
        journeyDTO.setStepFromId(stepFrom);
        journeyDTO.setStepToId(stepTo);
        return save(journeyDTO);
    }

    /**
     * Remove a journey between two steps
     *
     * @param stepFrom the departure step
     * @param stepTo the arrival step
     * @return a copy of the removed step
     */
    @Override
    public JourneyDTO remove(Long stepFrom, Long stepTo) {
        Journey journeyToDelete = this.journeyRepository.findOneByStepFromIdAndStepToId(stepFrom, stepTo).get();
        JourneyDTO journeyBackup = journeyMapper.toDto(journeyToDelete);
        this.journeyRepository.delete(journeyToDelete);
        return journeyBackup;
    }

    /**
    * Update the journey that arrives to a given step
    * @param stepToId : the ID of arrival step
    * @param journey : the updated journey information DTO
    */
    @Override
    public void updateByStepTo(Long stepToId, JourneyDTO journey) {
        Journey arrival = this.journeyRepository.findOneByStepToId(stepToId).get();
        arrival.setTransportation(journey.getTransportation());
        this.journeyRepository.save(arrival);
    }


    /**
     *  Get all the journeys.
     *
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<JourneyDTO> findAll() {
        log.debug("Request to get all Journeys");
        return journeyRepository.findAll().stream()
            .map(journeyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get one journey by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public JourneyDTO findOne(Long id) {
        log.debug("Request to get Journey : {}", id);
        Journey journey = journeyRepository.findOne(id);
        return journeyMapper.toDto(journey);
    }

    /**
     *  Delete the  journey by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Journey : {}", id);
        journeyRepository.delete(id);
        journeySearchRepository.delete(id);
    }

    /**
     * Search for the journey corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<JourneyDTO> search(String query) {
        log.debug("Request to search Journeys for query {}", query);
        return StreamSupport
            .stream(journeySearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(journeyMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<JourneyDTO> findByTripId(Long tripId) {
        return this.journeyRepository.findByStepToTripId(tripId).stream()
            .map(journeyMapper::toDto)
            .collect(Collectors.toList());
    }

}
