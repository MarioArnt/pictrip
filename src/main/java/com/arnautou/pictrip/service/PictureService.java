package com.arnautou.pictrip.service;

import com.arnautou.pictrip.domain.Picture;
import com.arnautou.pictrip.service.dto.PictureDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Picture.
 */
public interface PictureService {

    /**
     * Save a picture.
     *
     * @param pictureDTO the entity to save
     * @return the persisted entity
     */
    PictureDTO save(PictureDTO pictureDTO);

    /**
     * Save a picture.
     *
     * @param picture the entity to save
     * @return the persisted entity
     */
    Picture save(Picture picture);

    /**
     *  Get all the pictures.
     *
     *  @return the list of entities
     */
    List<PictureDTO> findAll();

    /**
     *  Get the "id" picture.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    PictureDTO findOne(Long id);

    /**
     *  Get the "id" picture.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Picture getOne(Long id);

    /**
     *  Delete the "id" picture.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the picture corresponding to the query.
     *
     *  @param query the query of the search
     *
     *  @return the list of entities
     */
    List<PictureDTO> search(String query);

    /**
     * Uploads a picture in the correct directory, save path
     * in database by creating entity and returns it.
     *
     * @param request : the HTTP servlet request
     * @param file : the multipart file to upload
     * @param userId : the ID of the user making the request
     * @param tripId : the trip for which the picture was uploaded for (optional).
     * @param stepId : the step for which the picture was uploaded for (optional)
     * @return : the DTO of entity created in base that is linked to the newly
     * uploaded picture
     * @throws IOException : if something wrong happens when writing on disk
     */
    PictureDTO upload(HttpServletRequest request, MultipartFile file, Long userId, Optional<Long> tripId, Optional<Long> stepId) throws IOException;

}
