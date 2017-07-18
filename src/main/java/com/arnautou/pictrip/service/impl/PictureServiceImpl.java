package com.arnautou.pictrip.service.impl;

import com.arnautou.pictrip.service.PictureService;
import com.arnautou.pictrip.domain.Picture;
import com.arnautou.pictrip.repository.PictureRepository;
import com.arnautou.pictrip.repository.search.PictureSearchRepository;
import com.arnautou.pictrip.service.dto.PictureDTO;
import com.arnautou.pictrip.service.mapper.PictureMapper;
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
 * Service Implementation for managing Picture.
 */
@Service
@Transactional
public class PictureServiceImpl implements PictureService{

    private final Logger log = LoggerFactory.getLogger(PictureServiceImpl.class);

    private final PictureRepository pictureRepository;

    private final PictureMapper pictureMapper;

    private final PictureSearchRepository pictureSearchRepository;

    public PictureServiceImpl(PictureRepository pictureRepository, PictureMapper pictureMapper, PictureSearchRepository pictureSearchRepository) {
        this.pictureRepository = pictureRepository;
        this.pictureMapper = pictureMapper;
        this.pictureSearchRepository = pictureSearchRepository;
    }

    /**
     * Save a picture.
     *
     * @param pictureDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public PictureDTO save(PictureDTO pictureDTO) {
        log.debug("Request to save Picture : {}", pictureDTO);
        Picture picture = pictureMapper.toEntity(pictureDTO);
        picture = pictureRepository.save(picture);
        PictureDTO result = pictureMapper.toDto(picture);
        pictureSearchRepository.save(picture);
        return result;
    }

    /**
     *  Get all the pictures.
     *
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<PictureDTO> findAll() {
        log.debug("Request to get all Pictures");
        return pictureRepository.findAll().stream()
            .map(pictureMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get one picture by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public PictureDTO findOne(Long id) {
        log.debug("Request to get Picture : {}", id);
        Picture picture = pictureRepository.findOne(id);
        return pictureMapper.toDto(picture);
    }

    /**
     *  Delete the  picture by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Picture : {}", id);
        pictureRepository.delete(id);
        pictureSearchRepository.delete(id);
    }

    /**
     * Search for the picture corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<PictureDTO> search(String query) {
        log.debug("Request to search Pictures for query {}", query);
        return StreamSupport
            .stream(pictureSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(pictureMapper::toDto)
            .collect(Collectors.toList());
    }
}
