package com.arnautou.pictrip.web.rest;

import com.arnautou.pictrip.PictripApp;

import com.arnautou.pictrip.domain.Journey;
import com.arnautou.pictrip.domain.Step;
import com.arnautou.pictrip.domain.Step;
import com.arnautou.pictrip.repository.JourneyRepository;
import com.arnautou.pictrip.service.JourneyService;
import com.arnautou.pictrip.repository.search.JourneySearchRepository;
import com.arnautou.pictrip.service.dto.JourneyDTO;
import com.arnautou.pictrip.service.mapper.JourneyMapper;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.arnautou.pictrip.domain.enumeration.Transportation;
/**
 * Test class for the JourneyResource REST controller.
 *
 * @see JourneyResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PictripApp.class)
public class JourneyResourceIntTest {

    private static final Transportation DEFAULT_TRANSPORTATION = Transportation.PLANE;
    private static final Transportation UPDATED_TRANSPORTATION = Transportation.TRAIN;

    private static final Long DEFAULT_DURATION = 1L;
    private static final Long UPDATED_DURATION = 2L;

    @Autowired
    private JourneyRepository journeyRepository;

    @Autowired
    private JourneyMapper journeyMapper;

    @Autowired
    private JourneyService journeyService;

    @Autowired
    private JourneySearchRepository journeySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restJourneyMockMvc;

    private Journey journey;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        JourneyResource journeyResource = new JourneyResource(journeyService);
        this.restJourneyMockMvc = MockMvcBuilders.standaloneSetup(journeyResource)
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
    public static Journey createEntity(EntityManager em) {
        Journey journey = new Journey()
            .transportation(DEFAULT_TRANSPORTATION)
            .duration(DEFAULT_DURATION);
        // Add required entity
        Step stepFrom = StepResourceIntTest.createEntity(em);
        em.persist(stepFrom);
        em.flush();
        journey.setStepFrom(stepFrom);
        // Add required entity
        Step stepTo = StepResourceIntTest.createEntity(em);
        em.persist(stepTo);
        em.flush();
        journey.setStepTo(stepTo);
        return journey;
    }

    @Before
    public void initTest() {
        journeySearchRepository.deleteAll();
        journey = createEntity(em);
    }

    @Test
    @Transactional
    public void createJourney() throws Exception {
        int databaseSizeBeforeCreate = journeyRepository.findAll().size();

        // Create the Journey
        JourneyDTO journeyDTO = journeyMapper.toDto(journey);
        restJourneyMockMvc.perform(post("/api/journeys")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(journeyDTO)))
            .andExpect(status().isCreated());

        // Validate the Journey in the database
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeCreate + 1);
        Journey testJourney = journeyList.get(journeyList.size() - 1);
        assertThat(testJourney.getTransportation()).isEqualTo(DEFAULT_TRANSPORTATION);
        assertThat(testJourney.getDuration()).isEqualTo(DEFAULT_DURATION);

        // Validate the Journey in Elasticsearch
        Journey journeyEs = journeySearchRepository.findOne(testJourney.getId());
        assertThat(journeyEs).isEqualToComparingFieldByField(testJourney);
    }

    @Test
    @Transactional
    public void createJourneyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = journeyRepository.findAll().size();

        // Create the Journey with an existing ID
        journey.setId(1L);
        JourneyDTO journeyDTO = journeyMapper.toDto(journey);

        // An entity with an existing ID cannot be created, so this API call must fail
        restJourneyMockMvc.perform(post("/api/journeys")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(journeyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllJourneys() throws Exception {
        // Initialize the database
        journeyRepository.saveAndFlush(journey);

        // Get all the journeyList
        restJourneyMockMvc.perform(get("/api/journeys?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(journey.getId().intValue())))
            .andExpect(jsonPath("$.[*].transportation").value(hasItem(DEFAULT_TRANSPORTATION.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.intValue())));
    }

    @Test
    @Transactional
    public void getJourney() throws Exception {
        // Initialize the database
        journeyRepository.saveAndFlush(journey);

        // Get the journey
        restJourneyMockMvc.perform(get("/api/journeys/{id}", journey.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(journey.getId().intValue()))
            .andExpect(jsonPath("$.transportation").value(DEFAULT_TRANSPORTATION.toString()))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingJourney() throws Exception {
        // Get the journey
        restJourneyMockMvc.perform(get("/api/journeys/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJourney() throws Exception {
        // Initialize the database
        journeyRepository.saveAndFlush(journey);
        journeySearchRepository.save(journey);
        int databaseSizeBeforeUpdate = journeyRepository.findAll().size();

        // Update the journey
        Journey updatedJourney = journeyRepository.findOne(journey.getId());
        updatedJourney
            .transportation(UPDATED_TRANSPORTATION)
            .duration(UPDATED_DURATION);
        JourneyDTO journeyDTO = journeyMapper.toDto(updatedJourney);

        restJourneyMockMvc.perform(put("/api/journeys")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(journeyDTO)))
            .andExpect(status().isOk());

        // Validate the Journey in the database
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeUpdate);
        Journey testJourney = journeyList.get(journeyList.size() - 1);
        assertThat(testJourney.getTransportation()).isEqualTo(UPDATED_TRANSPORTATION);
        assertThat(testJourney.getDuration()).isEqualTo(UPDATED_DURATION);

        // Validate the Journey in Elasticsearch
        Journey journeyEs = journeySearchRepository.findOne(testJourney.getId());
        assertThat(journeyEs).isEqualToComparingFieldByField(testJourney);
    }

    @Test
    @Transactional
    public void updateNonExistingJourney() throws Exception {
        int databaseSizeBeforeUpdate = journeyRepository.findAll().size();

        // Create the Journey
        JourneyDTO journeyDTO = journeyMapper.toDto(journey);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restJourneyMockMvc.perform(put("/api/journeys")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(journeyDTO)))
            .andExpect(status().isCreated());

        // Validate the Journey in the database
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteJourney() throws Exception {
        // Initialize the database
        journeyRepository.saveAndFlush(journey);
        journeySearchRepository.save(journey);
        int databaseSizeBeforeDelete = journeyRepository.findAll().size();

        // Get the journey
        restJourneyMockMvc.perform(delete("/api/journeys/{id}", journey.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean journeyExistsInEs = journeySearchRepository.exists(journey.getId());
        assertThat(journeyExistsInEs).isFalse();

        // Validate the database is empty
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchJourney() throws Exception {
        // Initialize the database
        journeyRepository.saveAndFlush(journey);
        journeySearchRepository.save(journey);

        // Search the journey
        restJourneyMockMvc.perform(get("/api/_search/journeys?query=id:" + journey.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(journey.getId().intValue())))
            .andExpect(jsonPath("$.[*].transportation").value(hasItem(DEFAULT_TRANSPORTATION.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Journey.class);
        Journey journey1 = new Journey();
        journey1.setId(1L);
        Journey journey2 = new Journey();
        journey2.setId(journey1.getId());
        assertThat(journey1).isEqualTo(journey2);
        journey2.setId(2L);
        assertThat(journey1).isNotEqualTo(journey2);
        journey1.setId(null);
        assertThat(journey1).isNotEqualTo(journey2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(JourneyDTO.class);
        JourneyDTO journeyDTO1 = new JourneyDTO();
        journeyDTO1.setId(1L);
        JourneyDTO journeyDTO2 = new JourneyDTO();
        assertThat(journeyDTO1).isNotEqualTo(journeyDTO2);
        journeyDTO2.setId(journeyDTO1.getId());
        assertThat(journeyDTO1).isEqualTo(journeyDTO2);
        journeyDTO2.setId(2L);
        assertThat(journeyDTO1).isNotEqualTo(journeyDTO2);
        journeyDTO1.setId(null);
        assertThat(journeyDTO1).isNotEqualTo(journeyDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(journeyMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(journeyMapper.fromId(null)).isNull();
    }
}
