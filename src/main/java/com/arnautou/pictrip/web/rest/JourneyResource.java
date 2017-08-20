package com.arnautou.pictrip.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.arnautou.pictrip.service.JourneyService;
import com.arnautou.pictrip.web.rest.util.HeaderUtil;
import com.arnautou.pictrip.service.dto.JourneyDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Journey.
 */
@RestController
@RequestMapping("/api")
public class JourneyResource {

    private final Logger log = LoggerFactory.getLogger(JourneyResource.class);

    private static final String ENTITY_NAME = "journey";

    private final JourneyService journeyService;

    public JourneyResource(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    /**
     * POST  /journeys : Create a new journey.
     *
     * @param journeyDTO the journeyDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new journeyDTO, or with status 400 (Bad Request) if the journey has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/journeys")
    @Timed
    public ResponseEntity<JourneyDTO> createJourney(@Valid @RequestBody JourneyDTO journeyDTO) throws URISyntaxException {
        log.debug("REST request to save Journey : {}", journeyDTO);
        if (journeyDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new journey cannot already have an ID")).body(null);
        }
        JourneyDTO result = journeyService.save(journeyDTO);
        return ResponseEntity.created(new URI("/api/journeys/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /journeys : Updates an existing journey.
     *
     * @param journeyDTO the journeyDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated journeyDTO,
     * or with status 400 (Bad Request) if the journeyDTO is not valid,
     * or with status 500 (Internal Server Error) if the journeyDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/journeys")
    @Timed
    public ResponseEntity<JourneyDTO> updateJourney(@Valid @RequestBody JourneyDTO journeyDTO) throws URISyntaxException {
        log.debug("REST request to update Journey : {}", journeyDTO);
        if (journeyDTO.getId() == null) {
            return createJourney(journeyDTO);
        }
        JourneyDTO result = journeyService.save(journeyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, journeyDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /journeys : get all the journeys.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of journeys in body
     */
    @GetMapping("/journeys")
    @Timed
    public List<JourneyDTO> getAllJourneys() {
        log.debug("REST request to get all Journeys");
        return journeyService.findAll();
    }

    /**
     * GET  /journeys/:id : get the "id" journey.
     *
     * @param id the id of the journeyDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the journeyDTO, or with status 404 (Not Found)
     */
    @GetMapping("/journeys/{id}")
    @Timed
    public ResponseEntity<JourneyDTO> getJourney(@PathVariable Long id) {
        log.debug("REST request to get Journey : {}", id);
        JourneyDTO journeyDTO = journeyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(journeyDTO));
    }

    /**
     * DELETE  /journeys/:id : delete the "id" journey.
     *
     * @param id the id of the journeyDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/journeys/{id}")
    @Timed
    public ResponseEntity<Void> deleteJourney(@PathVariable Long id) {
        log.debug("REST request to delete Journey : {}", id);
        journeyService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/journeys?query=:query : search for the journey corresponding
     * to the query.
     *
     * @param query the query of the journey search
     * @return the result of the search
     */
    @GetMapping("/_search/journeys")
    @Timed
    public List<JourneyDTO> searchJourneys(@RequestParam String query) {
        log.debug("REST request to search Journeys for query {}", query);
        return journeyService.search(query);
    }

}
