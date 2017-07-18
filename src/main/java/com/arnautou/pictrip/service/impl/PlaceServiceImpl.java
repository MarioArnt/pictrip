package com.arnautou.pictrip.service.impl;

import com.arnautou.pictrip.service.PlaceService;
import com.arnautou.pictrip.domain.Place;
import com.arnautou.pictrip.repository.PlaceRepository;
import com.arnautou.pictrip.repository.search.PlaceSearchRepository;
import com.arnautou.pictrip.service.dto.PlaceDTO;
import com.arnautou.pictrip.service.mapper.PlaceMapper;
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
 * Service Implementation for managing Place.
 */
@Service
@Transactional
public class PlaceServiceImpl implements PlaceService{

    private final Logger log = LoggerFactory.getLogger(PlaceServiceImpl.class);

    private final PlaceRepository placeRepository;

    private final PlaceMapper placeMapper;

    private final PlaceSearchRepository placeSearchRepository;

    public PlaceServiceImpl(PlaceRepository placeRepository, PlaceMapper placeMapper, PlaceSearchRepository placeSearchRepository) {
        this.placeRepository = placeRepository;
        this.placeMapper = placeMapper;
        this.placeSearchRepository = placeSearchRepository;
    }

    /**
     * Save a place.
     *
     * @param placeDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public PlaceDTO save(PlaceDTO placeDTO) {
        log.debug("Request to save Place : {}", placeDTO);
        Place place = placeMapper.toEntity(placeDTO);
        place = placeRepository.save(place);
        PlaceDTO result = placeMapper.toDto(place);
        placeSearchRepository.save(place);
        return result;
    }

    /**
     *  Get all the places.
     *
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<PlaceDTO> findAll() {
        log.debug("Request to get all Places");
        return placeRepository.findAll().stream()
            .map(placeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get one place by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public PlaceDTO findOne(Long id) {
        log.debug("Request to get Place : {}", id);
        Place place = placeRepository.findOne(id);
        return placeMapper.toDto(place);
    }

    /**
     *  Delete the  place by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Place : {}", id);
        placeRepository.delete(id);
        placeSearchRepository.delete(id);
    }

    /**
     * Search for the place corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<PlaceDTO> search(String query) {
        log.debug("Request to search Places for query {}", query);
        return StreamSupport
            .stream(placeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(placeMapper::toDto)
            .collect(Collectors.toList());
    }
}
