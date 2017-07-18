package com.arnautou.pictrip.service.impl;

import com.arnautou.pictrip.service.StepService;
import com.arnautou.pictrip.domain.Step;
import com.arnautou.pictrip.repository.StepRepository;
import com.arnautou.pictrip.repository.search.StepSearchRepository;
import com.arnautou.pictrip.service.dto.StepDTO;
import com.arnautou.pictrip.service.mapper.StepMapper;
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
 * Service Implementation for managing Step.
 */
@Service
@Transactional
public class StepServiceImpl implements StepService{

    private final Logger log = LoggerFactory.getLogger(StepServiceImpl.class);

    private final StepRepository stepRepository;

    private final StepMapper stepMapper;

    private final StepSearchRepository stepSearchRepository;

    public StepServiceImpl(StepRepository stepRepository, StepMapper stepMapper, StepSearchRepository stepSearchRepository) {
        this.stepRepository = stepRepository;
        this.stepMapper = stepMapper;
        this.stepSearchRepository = stepSearchRepository;
    }

    /**
     * Save a step.
     *
     * @param stepDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public StepDTO save(StepDTO stepDTO) {
        log.debug("Request to save Step : {}", stepDTO);
        Step step = stepMapper.toEntity(stepDTO);
        step = stepRepository.save(step);
        StepDTO result = stepMapper.toDto(step);
        stepSearchRepository.save(step);
        return result;
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
