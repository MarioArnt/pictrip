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
