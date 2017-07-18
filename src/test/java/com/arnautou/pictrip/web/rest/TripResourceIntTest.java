package com.arnautou.pictrip.web.rest;

import com.arnautou.pictrip.PictripApp;

import com.arnautou.pictrip.domain.Trip;
import com.arnautou.pictrip.domain.User;
import com.arnautou.pictrip.repository.TripRepository;
import com.arnautou.pictrip.service.TripService;
import com.arnautou.pictrip.repository.search.TripSearchRepository;
import com.arnautou.pictrip.service.dto.TripDTO;
import com.arnautou.pictrip.service.mapper.TripMapper;
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

import com.arnautou.pictrip.domain.enumeration.Privacy;
import com.arnautou.pictrip.domain.enumeration.Color;
/**
 * Test class for the TripResource REST controller.
 *
 * @see TripResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PictripApp.class)
public class TripResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_FROM = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_FROM = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATE_TO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_TO = LocalDate.now(ZoneId.systemDefault());

    private static final Privacy DEFAULT_PRIVACY = Privacy.PUBLIC;
    private static final Privacy UPDATED_PRIVACY = Privacy.ANYONE_WITH_LINK;

    private static final Color DEFAULT_COLOR = Color.RED;
    private static final Color UPDATED_COLOR = Color.PINK;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripMapper tripMapper;

    @Autowired
    private TripService tripService;

    @Autowired
    private TripSearchRepository tripSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTripMockMvc;

    private Trip trip;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TripResource tripResource = new TripResource(tripService);
        this.restTripMockMvc = MockMvcBuilders.standaloneSetup(tripResource)
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
    public static Trip createEntity(EntityManager em) {
        Trip trip = new Trip()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .dateFrom(DEFAULT_DATE_FROM)
            .dateTo(DEFAULT_DATE_TO)
            .privacy(DEFAULT_PRIVACY)
            .color(DEFAULT_COLOR);
        // Add required entity
        User owner = UserResourceIntTest.createEntity(em);
        em.persist(owner);
        em.flush();
        trip.setOwner(owner);
        return trip;
    }

    @Before
    public void initTest() {
        tripSearchRepository.deleteAll();
        trip = createEntity(em);
    }

    @Test
    @Transactional
    public void createTrip() throws Exception {
        int databaseSizeBeforeCreate = tripRepository.findAll().size();

        // Create the Trip
        TripDTO tripDTO = tripMapper.toDto(trip);
        restTripMockMvc.perform(post("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripDTO)))
            .andExpect(status().isCreated());

        // Validate the Trip in the database
        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeCreate + 1);
        Trip testTrip = tripList.get(tripList.size() - 1);
        assertThat(testTrip.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTrip.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTrip.getDateFrom()).isEqualTo(DEFAULT_DATE_FROM);
        assertThat(testTrip.getDateTo()).isEqualTo(DEFAULT_DATE_TO);
        assertThat(testTrip.getPrivacy()).isEqualTo(DEFAULT_PRIVACY);
        assertThat(testTrip.getColor()).isEqualTo(DEFAULT_COLOR);

        // Validate the Trip in Elasticsearch
        Trip tripEs = tripSearchRepository.findOne(testTrip.getId());
        assertThat(tripEs).isEqualToComparingFieldByField(testTrip);
    }

    @Test
    @Transactional
    public void createTripWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tripRepository.findAll().size();

        // Create the Trip with an existing ID
        trip.setId(1L);
        TripDTO tripDTO = tripMapper.toDto(trip);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTripMockMvc.perform(post("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = tripRepository.findAll().size();
        // set the field null
        trip.setName(null);

        // Create the Trip, which fails.
        TripDTO tripDTO = tripMapper.toDto(trip);

        restTripMockMvc.perform(post("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripDTO)))
            .andExpect(status().isBadRequest());

        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPrivacyIsRequired() throws Exception {
        int databaseSizeBeforeTest = tripRepository.findAll().size();
        // set the field null
        trip.setPrivacy(null);

        // Create the Trip, which fails.
        TripDTO tripDTO = tripMapper.toDto(trip);

        restTripMockMvc.perform(post("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripDTO)))
            .andExpect(status().isBadRequest());

        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkColorIsRequired() throws Exception {
        int databaseSizeBeforeTest = tripRepository.findAll().size();
        // set the field null
        trip.setColor(null);

        // Create the Trip, which fails.
        TripDTO tripDTO = tripMapper.toDto(trip);

        restTripMockMvc.perform(post("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripDTO)))
            .andExpect(status().isBadRequest());

        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTrips() throws Exception {
        // Initialize the database
        tripRepository.saveAndFlush(trip);

        // Get all the tripList
        restTripMockMvc.perform(get("/api/trips?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trip.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].dateFrom").value(hasItem(DEFAULT_DATE_FROM.toString())))
            .andExpect(jsonPath("$.[*].dateTo").value(hasItem(DEFAULT_DATE_TO.toString())))
            .andExpect(jsonPath("$.[*].privacy").value(hasItem(DEFAULT_PRIVACY.toString())))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR.toString())));
    }

    @Test
    @Transactional
    public void getTrip() throws Exception {
        // Initialize the database
        tripRepository.saveAndFlush(trip);

        // Get the trip
        restTripMockMvc.perform(get("/api/trips/{id}", trip.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(trip.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.dateFrom").value(DEFAULT_DATE_FROM.toString()))
            .andExpect(jsonPath("$.dateTo").value(DEFAULT_DATE_TO.toString()))
            .andExpect(jsonPath("$.privacy").value(DEFAULT_PRIVACY.toString()))
            .andExpect(jsonPath("$.color").value(DEFAULT_COLOR.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTrip() throws Exception {
        // Get the trip
        restTripMockMvc.perform(get("/api/trips/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTrip() throws Exception {
        // Initialize the database
        tripRepository.saveAndFlush(trip);
        tripSearchRepository.save(trip);
        int databaseSizeBeforeUpdate = tripRepository.findAll().size();

        // Update the trip
        Trip updatedTrip = tripRepository.findOne(trip.getId());
        updatedTrip
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .dateFrom(UPDATED_DATE_FROM)
            .dateTo(UPDATED_DATE_TO)
            .privacy(UPDATED_PRIVACY)
            .color(UPDATED_COLOR);
        TripDTO tripDTO = tripMapper.toDto(updatedTrip);

        restTripMockMvc.perform(put("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripDTO)))
            .andExpect(status().isOk());

        // Validate the Trip in the database
        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeUpdate);
        Trip testTrip = tripList.get(tripList.size() - 1);
        assertThat(testTrip.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTrip.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTrip.getDateFrom()).isEqualTo(UPDATED_DATE_FROM);
        assertThat(testTrip.getDateTo()).isEqualTo(UPDATED_DATE_TO);
        assertThat(testTrip.getPrivacy()).isEqualTo(UPDATED_PRIVACY);
        assertThat(testTrip.getColor()).isEqualTo(UPDATED_COLOR);

        // Validate the Trip in Elasticsearch
        Trip tripEs = tripSearchRepository.findOne(testTrip.getId());
        assertThat(tripEs).isEqualToComparingFieldByField(testTrip);
    }

    @Test
    @Transactional
    public void updateNonExistingTrip() throws Exception {
        int databaseSizeBeforeUpdate = tripRepository.findAll().size();

        // Create the Trip
        TripDTO tripDTO = tripMapper.toDto(trip);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTripMockMvc.perform(put("/api/trips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tripDTO)))
            .andExpect(status().isCreated());

        // Validate the Trip in the database
        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTrip() throws Exception {
        // Initialize the database
        tripRepository.saveAndFlush(trip);
        tripSearchRepository.save(trip);
        int databaseSizeBeforeDelete = tripRepository.findAll().size();

        // Get the trip
        restTripMockMvc.perform(delete("/api/trips/{id}", trip.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean tripExistsInEs = tripSearchRepository.exists(trip.getId());
        assertThat(tripExistsInEs).isFalse();

        // Validate the database is empty
        List<Trip> tripList = tripRepository.findAll();
        assertThat(tripList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTrip() throws Exception {
        // Initialize the database
        tripRepository.saveAndFlush(trip);
        tripSearchRepository.save(trip);

        // Search the trip
        restTripMockMvc.perform(get("/api/_search/trips?query=id:" + trip.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trip.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].dateFrom").value(hasItem(DEFAULT_DATE_FROM.toString())))
            .andExpect(jsonPath("$.[*].dateTo").value(hasItem(DEFAULT_DATE_TO.toString())))
            .andExpect(jsonPath("$.[*].privacy").value(hasItem(DEFAULT_PRIVACY.toString())))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Trip.class);
        Trip trip1 = new Trip();
        trip1.setId(1L);
        Trip trip2 = new Trip();
        trip2.setId(trip1.getId());
        assertThat(trip1).isEqualTo(trip2);
        trip2.setId(2L);
        assertThat(trip1).isNotEqualTo(trip2);
        trip1.setId(null);
        assertThat(trip1).isNotEqualTo(trip2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TripDTO.class);
        TripDTO tripDTO1 = new TripDTO();
        tripDTO1.setId(1L);
        TripDTO tripDTO2 = new TripDTO();
        assertThat(tripDTO1).isNotEqualTo(tripDTO2);
        tripDTO2.setId(tripDTO1.getId());
        assertThat(tripDTO1).isEqualTo(tripDTO2);
        tripDTO2.setId(2L);
        assertThat(tripDTO1).isNotEqualTo(tripDTO2);
        tripDTO1.setId(null);
        assertThat(tripDTO1).isNotEqualTo(tripDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(tripMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(tripMapper.fromId(null)).isNull();
    }
}
