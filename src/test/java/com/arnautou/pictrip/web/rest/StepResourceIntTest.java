package com.arnautou.pictrip.web.rest;

import com.arnautou.pictrip.PictripApp;

import com.arnautou.pictrip.domain.Step;
import com.arnautou.pictrip.domain.Place;
import com.arnautou.pictrip.domain.Trip;
import com.arnautou.pictrip.repository.StepRepository;
import com.arnautou.pictrip.service.PlaceService;
import com.arnautou.pictrip.service.StepService;
import com.arnautou.pictrip.repository.search.StepSearchRepository;
import com.arnautou.pictrip.service.TripService;
import com.arnautou.pictrip.service.UserService;
import com.arnautou.pictrip.service.dto.StepDTO;
import com.arnautou.pictrip.service.mapper.StepMapper;
import com.arnautou.pictrip.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the StepResource REST controller.
 *
 * @see StepResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PictripApp.class)
public class StepResourceIntTest {

    private static final Integer DEFAULT_NUMBER = 0;
    private static final Integer UPDATED_NUMBER = 1;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_FROM = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_FROM = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATE_TO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_TO = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private StepRepository stepRepository;

    @Autowired
    private StepMapper stepMapper;

    @Autowired
    private StepService stepService;

    @Autowired
    private TripService tripService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private UserService userService;

    @Autowired
    private StepSearchRepository stepSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restStepMockMvc;

    private Step step;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StepResource stepResource = new StepResource(stepService, tripService, userService, placeService);
        this.restStepMockMvc = MockMvcBuilders.standaloneSetup(stepResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Step createEntity(EntityManager em) {
        Step step = new Step()
            .number(DEFAULT_NUMBER)
            .description(DEFAULT_DESCRIPTION)
            .dateFrom(DEFAULT_DATE_FROM)
            .dateTo(DEFAULT_DATE_TO);
        // Add required entity
        Place place = PlaceResourceIntTest.createEntity(em);
        em.persist(place);
        em.flush();
        step.setPlace(place);
        // Add required entity
        Trip trip = TripResourceIntTest.createEntity(em);
        em.persist(trip);
        em.flush();
        step.setTrip(trip);
        return step;
    }

    @Before
    public void initTest() {
        stepSearchRepository.deleteAll();
        step = createEntity(em);
    }

    @Test
    @Transactional
    public void createStep() throws Exception {
        int databaseSizeBeforeCreate = stepRepository.findAll().size();

        // Create the Step
        StepDTO stepDTO = stepMapper.toDto(step);
        restStepMockMvc.perform(post("/api/steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stepDTO)))
            .andExpect(status().isCreated());

        // Validate the Step in the database
        List<Step> stepList = stepRepository.findAll();
        assertThat(stepList).hasSize(databaseSizeBeforeCreate + 1);
        Step testStep = stepList.get(stepList.size() - 1);
        assertThat(testStep.getNumber()).isEqualTo(DEFAULT_NUMBER);
        assertThat(testStep.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testStep.getDateFrom()).isEqualTo(DEFAULT_DATE_FROM);
        assertThat(testStep.getDateTo()).isEqualTo(DEFAULT_DATE_TO);

        // Validate the Step in Elasticsearch
        Step stepEs = stepSearchRepository.findOne(testStep.getId());
        assertThat(stepEs).isEqualToComparingFieldByField(testStep);
    }

    @Test
    @Transactional
    public void createStepWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = stepRepository.findAll().size();

        // Create the Step with an existing ID
        step.setId(1L);
        StepDTO stepDTO = stepMapper.toDto(step);

        // An entity with an existing ID cannot be created, so this API call must fail
        restStepMockMvc.perform(post("/api/steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stepDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Step> stepList = stepRepository.findAll();
        assertThat(stepList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = stepRepository.findAll().size();
        // set the field null
        step.setNumber(null);

        // Create the Step, which fails.
        StepDTO stepDTO = stepMapper.toDto(step);

        restStepMockMvc.perform(post("/api/steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stepDTO)))
            .andExpect(status().isBadRequest());

        List<Step> stepList = stepRepository.findAll();
        assertThat(stepList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSteps() throws Exception {
        // Initialize the database
        stepRepository.saveAndFlush(step);

        // Get all the stepList
        restStepMockMvc.perform(get("/api/steps?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(step.getId().intValue())))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].dateFrom").value(hasItem(DEFAULT_DATE_FROM.toString())))
            .andExpect(jsonPath("$.[*].dateTo").value(hasItem(DEFAULT_DATE_TO.toString())));
    }

    @Test
    @Transactional
    public void getStep() throws Exception {
        // Initialize the database
        stepRepository.saveAndFlush(step);

        // Get the step
        restStepMockMvc.perform(get("/api/steps/{id}", step.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(step.getId().intValue()))
            .andExpect(jsonPath("$.number").value(DEFAULT_NUMBER))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.dateFrom").value(DEFAULT_DATE_FROM.toString()))
            .andExpect(jsonPath("$.dateTo").value(DEFAULT_DATE_TO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStep() throws Exception {
        // Get the step
        restStepMockMvc.perform(get("/api/steps/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStep() throws Exception {
        // Initialize the database
        stepRepository.saveAndFlush(step);
        stepSearchRepository.save(step);
        int databaseSizeBeforeUpdate = stepRepository.findAll().size();

        // Update the step
        Step updatedStep = stepRepository.findOne(step.getId());
        updatedStep
            .number(UPDATED_NUMBER)
            .description(UPDATED_DESCRIPTION)
            .dateFrom(UPDATED_DATE_FROM)
            .dateTo(UPDATED_DATE_TO);
        StepDTO stepDTO = stepMapper.toDto(updatedStep);

        restStepMockMvc.perform(put("/api/steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stepDTO)))
            .andExpect(status().isOk());

        // Validate the Step in the database
        List<Step> stepList = stepRepository.findAll();
        assertThat(stepList).hasSize(databaseSizeBeforeUpdate);
        Step testStep = stepList.get(stepList.size() - 1);
        assertThat(testStep.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testStep.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testStep.getDateFrom()).isEqualTo(UPDATED_DATE_FROM);
        assertThat(testStep.getDateTo()).isEqualTo(UPDATED_DATE_TO);

        // Validate the Step in Elasticsearch
        Step stepEs = stepSearchRepository.findOne(testStep.getId());
        assertThat(stepEs).isEqualToComparingFieldByField(testStep);
    }

    @Test
    @Transactional
    public void updateNonExistingStep() throws Exception {
        int databaseSizeBeforeUpdate = stepRepository.findAll().size();

        // Create the Step
        StepDTO stepDTO = stepMapper.toDto(step);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restStepMockMvc.perform(put("/api/steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stepDTO)))
            .andExpect(status().isCreated());

        // Validate the Step in the database
        List<Step> stepList = stepRepository.findAll();
        assertThat(stepList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteStep() throws Exception {
        // Initialize the database
        stepRepository.saveAndFlush(step);
        stepSearchRepository.save(step);
        int databaseSizeBeforeDelete = stepRepository.findAll().size();

        // Get the step
        restStepMockMvc.perform(delete("/api/steps/{id}", step.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean stepExistsInEs = stepSearchRepository.exists(step.getId());
        assertThat(stepExistsInEs).isFalse();

        // Validate the database is empty
        List<Step> stepList = stepRepository.findAll();
        assertThat(stepList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchStep() throws Exception {
        // Initialize the database
        stepRepository.saveAndFlush(step);
        stepSearchRepository.save(step);

        // Search the step
        restStepMockMvc.perform(get("/api/_search/steps?query=id:" + step.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(step.getId().intValue())))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].dateFrom").value(hasItem(DEFAULT_DATE_FROM.toString())))
            .andExpect(jsonPath("$.[*].dateTo").value(hasItem(DEFAULT_DATE_TO.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Step.class);
        Step step1 = new Step();
        step1.setId(1L);
        Step step2 = new Step();
        step2.setId(step1.getId());
        assertThat(step1).isEqualTo(step2);
        step2.setId(2L);
        assertThat(step1).isNotEqualTo(step2);
        step1.setId(null);
        assertThat(step1).isNotEqualTo(step2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StepDTO.class);
        StepDTO stepDTO1 = new StepDTO();
        stepDTO1.setId(1L);
        StepDTO stepDTO2 = new StepDTO();
        assertThat(stepDTO1).isNotEqualTo(stepDTO2);
        stepDTO2.setId(stepDTO1.getId());
        assertThat(stepDTO1).isEqualTo(stepDTO2);
        stepDTO2.setId(2L);
        assertThat(stepDTO1).isNotEqualTo(stepDTO2);
        stepDTO1.setId(null);
        assertThat(stepDTO1).isNotEqualTo(stepDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(stepMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(stepMapper.fromId(null)).isNull();
    }
}
