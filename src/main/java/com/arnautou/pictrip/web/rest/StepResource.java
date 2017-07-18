package com.arnautou.pictrip.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.arnautou.pictrip.service.StepService;
import com.arnautou.pictrip.web.rest.util.HeaderUtil;
import com.arnautou.pictrip.service.dto.StepDTO;
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
 * REST controller for managing Step.
 */
@RestController
@RequestMapping("/api")
public class StepResource {

    private final Logger log = LoggerFactory.getLogger(StepResource.class);

    private static final String ENTITY_NAME = "step";

    private final StepService stepService;

    public StepResource(StepService stepService) {
        this.stepService = stepService;
    }

    /**
     * POST  /steps : Create a new step.
     *
     * @param stepDTO the stepDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new stepDTO, or with status 400 (Bad Request) if the step has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/steps")
    @Timed
    public ResponseEntity<StepDTO> createStep(@Valid @RequestBody StepDTO stepDTO) throws URISyntaxException {
        log.debug("REST request to save Step : {}", stepDTO);
        if (stepDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new step cannot already have an ID")).body(null);
        }
        StepDTO result = stepService.save(stepDTO);
        return ResponseEntity.created(new URI("/api/steps/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /steps : Updates an existing step.
     *
     * @param stepDTO the stepDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated stepDTO,
     * or with status 400 (Bad Request) if the stepDTO is not valid,
     * or with status 500 (Internal Server Error) if the stepDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/steps")
    @Timed
    public ResponseEntity<StepDTO> updateStep(@Valid @RequestBody StepDTO stepDTO) throws URISyntaxException {
        log.debug("REST request to update Step : {}", stepDTO);
        if (stepDTO.getId() == null) {
            return createStep(stepDTO);
        }
        StepDTO result = stepService.save(stepDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, stepDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /steps : get all the steps.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of steps in body
     */
    @GetMapping("/steps")
    @Timed
    public List<StepDTO> getAllSteps() {
        log.debug("REST request to get all Steps");
        return stepService.findAll();
    }

    /**
     * GET  /steps/:id : get the "id" step.
     *
     * @param id the id of the stepDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the stepDTO, or with status 404 (Not Found)
     */
    @GetMapping("/steps/{id}")
    @Timed
    public ResponseEntity<StepDTO> getStep(@PathVariable Long id) {
        log.debug("REST request to get Step : {}", id);
        StepDTO stepDTO = stepService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(stepDTO));
    }

    /**
     * DELETE  /steps/:id : delete the "id" step.
     *
     * @param id the id of the stepDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/steps/{id}")
    @Timed
    public ResponseEntity<Void> deleteStep(@PathVariable Long id) {
        log.debug("REST request to delete Step : {}", id);
        stepService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/steps?query=:query : search for the step corresponding
     * to the query.
     *
     * @param query the query of the step search
     * @return the result of the search
     */
    @GetMapping("/_search/steps")
    @Timed
    public List<StepDTO> searchSteps(@RequestParam String query) {
        log.debug("REST request to search Steps for query {}", query);
        return stepService.search(query);
    }

}
