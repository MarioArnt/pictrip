package com.arnautou.pictrip.web.rest;

import com.arnautou.pictrip.PictripApp;

import com.arnautou.pictrip.domain.Picture;
import com.arnautou.pictrip.repository.PictureRepository;
import com.arnautou.pictrip.service.PictureService;
import com.arnautou.pictrip.repository.search.PictureSearchRepository;
import com.arnautou.pictrip.service.dto.PictureDTO;
import com.arnautou.pictrip.service.mapper.PictureMapper;
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

/**
 * Test class for the PictureResource REST controller.
 *
 * @see PictureResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PictripApp.class)
public class PictureResourceIntTest {

    private static final String DEFAULT_SRC = "AAAAAAAAAA";
    private static final String UPDATED_SRC = "BBBBBBBBBB";

    private static final String DEFAULT_CAPTION = "AAAAAAAAAA";
    private static final String UPDATED_CAPTION = "BBBBBBBBBB";

    private static final Long DEFAULT_SIZE = 1L;
    private static final Long UPDATED_SIZE = 2L;

    private static final Long DEFAULT_VIEWS = 1L;
    private static final Long UPDATED_VIEWS = 2L;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private PictureMapper pictureMapper;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private PictureSearchRepository pictureSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPictureMockMvc;

    private Picture picture;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PictureResource pictureResource = new PictureResource(pictureService);
        this.restPictureMockMvc = MockMvcBuilders.standaloneSetup(pictureResource)
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
    public static Picture createEntity(EntityManager em) {
        Picture picture = new Picture()
            .src(DEFAULT_SRC)
            .caption(DEFAULT_CAPTION)
            .size(DEFAULT_SIZE)
            .views(DEFAULT_VIEWS);
        return picture;
    }

    @Before
    public void initTest() {
        pictureSearchRepository.deleteAll();
        picture = createEntity(em);
    }

    @Test
    @Transactional
    public void createPicture() throws Exception {
        int databaseSizeBeforeCreate = pictureRepository.findAll().size();

        // Create the Picture
        PictureDTO pictureDTO = pictureMapper.toDto(picture);
        restPictureMockMvc.perform(post("/api/pictures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pictureDTO)))
            .andExpect(status().isCreated());

        // Validate the Picture in the database
        List<Picture> pictureList = pictureRepository.findAll();
        assertThat(pictureList).hasSize(databaseSizeBeforeCreate + 1);
        Picture testPicture = pictureList.get(pictureList.size() - 1);
        assertThat(testPicture.getSrc()).isEqualTo(DEFAULT_SRC);
        assertThat(testPicture.getCaption()).isEqualTo(DEFAULT_CAPTION);
        assertThat(testPicture.getSize()).isEqualTo(DEFAULT_SIZE);
        assertThat(testPicture.getViews()).isEqualTo(DEFAULT_VIEWS);

        // Validate the Picture in Elasticsearch
        Picture pictureEs = pictureSearchRepository.findOne(testPicture.getId());
        assertThat(pictureEs).isEqualToComparingFieldByField(testPicture);
    }

    @Test
    @Transactional
    public void createPictureWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = pictureRepository.findAll().size();

        // Create the Picture with an existing ID
        picture.setId(1L);
        PictureDTO pictureDTO = pictureMapper.toDto(picture);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPictureMockMvc.perform(post("/api/pictures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pictureDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Picture> pictureList = pictureRepository.findAll();
        assertThat(pictureList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkSrcIsRequired() throws Exception {
        int databaseSizeBeforeTest = pictureRepository.findAll().size();
        // set the field null
        picture.setSrc(null);

        // Create the Picture, which fails.
        PictureDTO pictureDTO = pictureMapper.toDto(picture);

        restPictureMockMvc.perform(post("/api/pictures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pictureDTO)))
            .andExpect(status().isBadRequest());

        List<Picture> pictureList = pictureRepository.findAll();
        assertThat(pictureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSizeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pictureRepository.findAll().size();
        // set the field null
        picture.setSize(null);

        // Create the Picture, which fails.
        PictureDTO pictureDTO = pictureMapper.toDto(picture);

        restPictureMockMvc.perform(post("/api/pictures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pictureDTO)))
            .andExpect(status().isBadRequest());

        List<Picture> pictureList = pictureRepository.findAll();
        assertThat(pictureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPictures() throws Exception {
        // Initialize the database
        pictureRepository.saveAndFlush(picture);

        // Get all the pictureList
        restPictureMockMvc.perform(get("/api/pictures?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(picture.getId().intValue())))
            .andExpect(jsonPath("$.[*].src").value(hasItem(DEFAULT_SRC.toString())))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION.toString())))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].views").value(hasItem(DEFAULT_VIEWS.intValue())));
    }

    @Test
    @Transactional
    public void getPicture() throws Exception {
        // Initialize the database
        pictureRepository.saveAndFlush(picture);

        // Get the picture
        restPictureMockMvc.perform(get("/api/pictures/{id}", picture.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(picture.getId().intValue()))
            .andExpect(jsonPath("$.src").value(DEFAULT_SRC.toString()))
            .andExpect(jsonPath("$.caption").value(DEFAULT_CAPTION.toString()))
            .andExpect(jsonPath("$.size").value(DEFAULT_SIZE.intValue()))
            .andExpect(jsonPath("$.views").value(DEFAULT_VIEWS.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingPicture() throws Exception {
        // Get the picture
        restPictureMockMvc.perform(get("/api/pictures/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePicture() throws Exception {
        // Initialize the database
        pictureRepository.saveAndFlush(picture);
        pictureSearchRepository.save(picture);
        int databaseSizeBeforeUpdate = pictureRepository.findAll().size();

        // Update the picture
        Picture updatedPicture = pictureRepository.findOne(picture.getId());
        updatedPicture
            .src(UPDATED_SRC)
            .caption(UPDATED_CAPTION)
            .size(UPDATED_SIZE)
            .views(UPDATED_VIEWS);
        PictureDTO pictureDTO = pictureMapper.toDto(updatedPicture);

        restPictureMockMvc.perform(put("/api/pictures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pictureDTO)))
            .andExpect(status().isOk());

        // Validate the Picture in the database
        List<Picture> pictureList = pictureRepository.findAll();
        assertThat(pictureList).hasSize(databaseSizeBeforeUpdate);
        Picture testPicture = pictureList.get(pictureList.size() - 1);
        assertThat(testPicture.getSrc()).isEqualTo(UPDATED_SRC);
        assertThat(testPicture.getCaption()).isEqualTo(UPDATED_CAPTION);
        assertThat(testPicture.getSize()).isEqualTo(UPDATED_SIZE);
        assertThat(testPicture.getViews()).isEqualTo(UPDATED_VIEWS);

        // Validate the Picture in Elasticsearch
        Picture pictureEs = pictureSearchRepository.findOne(testPicture.getId());
        assertThat(pictureEs).isEqualToComparingFieldByField(testPicture);
    }

    @Test
    @Transactional
    public void updateNonExistingPicture() throws Exception {
        int databaseSizeBeforeUpdate = pictureRepository.findAll().size();

        // Create the Picture
        PictureDTO pictureDTO = pictureMapper.toDto(picture);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPictureMockMvc.perform(put("/api/pictures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pictureDTO)))
            .andExpect(status().isCreated());

        // Validate the Picture in the database
        List<Picture> pictureList = pictureRepository.findAll();
        assertThat(pictureList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePicture() throws Exception {
        // Initialize the database
        pictureRepository.saveAndFlush(picture);
        pictureSearchRepository.save(picture);
        int databaseSizeBeforeDelete = pictureRepository.findAll().size();

        // Get the picture
        restPictureMockMvc.perform(delete("/api/pictures/{id}", picture.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean pictureExistsInEs = pictureSearchRepository.exists(picture.getId());
        assertThat(pictureExistsInEs).isFalse();

        // Validate the database is empty
        List<Picture> pictureList = pictureRepository.findAll();
        assertThat(pictureList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPicture() throws Exception {
        // Initialize the database
        pictureRepository.saveAndFlush(picture);
        pictureSearchRepository.save(picture);

        // Search the picture
        restPictureMockMvc.perform(get("/api/_search/pictures?query=id:" + picture.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(picture.getId().intValue())))
            .andExpect(jsonPath("$.[*].src").value(hasItem(DEFAULT_SRC.toString())))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION.toString())))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].views").value(hasItem(DEFAULT_VIEWS.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Picture.class);
        Picture picture1 = new Picture();
        picture1.setId(1L);
        Picture picture2 = new Picture();
        picture2.setId(picture1.getId());
        assertThat(picture1).isEqualTo(picture2);
        picture2.setId(2L);
        assertThat(picture1).isNotEqualTo(picture2);
        picture1.setId(null);
        assertThat(picture1).isNotEqualTo(picture2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PictureDTO.class);
        PictureDTO pictureDTO1 = new PictureDTO();
        pictureDTO1.setId(1L);
        PictureDTO pictureDTO2 = new PictureDTO();
        assertThat(pictureDTO1).isNotEqualTo(pictureDTO2);
        pictureDTO2.setId(pictureDTO1.getId());
        assertThat(pictureDTO1).isEqualTo(pictureDTO2);
        pictureDTO2.setId(2L);
        assertThat(pictureDTO1).isNotEqualTo(pictureDTO2);
        pictureDTO1.setId(null);
        assertThat(pictureDTO1).isNotEqualTo(pictureDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(pictureMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(pictureMapper.fromId(null)).isNull();
    }
}
