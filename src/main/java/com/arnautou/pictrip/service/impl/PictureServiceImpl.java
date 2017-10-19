package com.arnautou.pictrip.service.impl;

import com.arnautou.pictrip.service.PictureService;
import com.arnautou.pictrip.domain.Picture;
import com.arnautou.pictrip.repository.PictureRepository;
import com.arnautou.pictrip.repository.search.PictureSearchRepository;
import com.arnautou.pictrip.service.dto.PictureDTO;
import com.arnautou.pictrip.service.mapper.PictureMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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
        save(picture);
        PictureDTO result = pictureMapper.toDto(picture);
        return result;
    }

    /**
     * Save a picture.
     *
     * @param picture the entity to save
     * @return the persisted entity
     */
    @Override
    public Picture save(Picture picture) {
        picture = pictureRepository.save(picture);
        pictureSearchRepository.save(picture);
        return picture;
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
     *  Get one picture by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Picture getOne(Long id) {
        return pictureRepository.findOne(id);
    }


    /**
     *  Delete the  picture by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) throws IOException {
        log.debug("Request to delete Picture : {}", id);
        Picture toDelete = pictureRepository.findOne(id);
        Path path = FileSystems.getDefault().getPath(toDelete.getSrc());
        Files.deleteIfExists(path);
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

    /**
     * Uploads a picture in the correct directory, save path
     * in database by creating entity and returns it.
     *
     * @param request : the HTTP servlet request
     * @param file : the multipart file to upload
     * @param userId : the ID of the user making the request
     * @param tripId : the trip for which the picture was uploaded for.
     * @param stepId : the step for which the picture was uploaded for (optional)
     * @return : the DTO of entity created in base that is linked to the newly
     * uploaded picture
     * @throws IOException : if something wrong happens when writing on disk
     */
    @Override
    public PictureDTO upload(HttpServletRequest request, MultipartFile file, Long userId, Optional<Long> tripId, Optional<Long> stepId) throws IOException {
        String realPathToUploads = createFoldersForUploadedFile(request, userId, tripId, stepId);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss:n");
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append("IMG-");
        fileNameBuilder.append(formatter.format(now));
        if(file.getContentType().equals("image/jpeg")) {
            fileNameBuilder.append(".jpeg");
        } else if(file.getContentType().equals("image/png")) {
            fileNameBuilder.append(".png");
        } else {
            return null;
        }
        String filePath = realPathToUploads + File.separator + fileNameBuilder.toString();
        log.info("Upload picture in = {}", filePath);
        File dest = new File(filePath);
        dest.createNewFile();
        FileOutputStream fos = new FileOutputStream(dest);
        fos.write(file.getBytes());
        fos.close();
        Picture newPicture = new Picture();
        newPicture.setCaption("");
        newPicture.setSize(file.getSize());
        newPicture.setSrc(dest.getAbsolutePath());
        newPicture.setViews(0l);
        PictureDTO result = pictureMapper.toDto(save(newPicture));
        return result;
    }

    private String createFoldersForUploadedFile(HttpServletRequest request, Long userId, Optional<Long> tripId, Optional<Long> stepId) {
        StringBuilder uploadDirectory = new StringBuilder();
        uploadDirectory.append(File.separator);
        uploadDirectory.append("uploads");
        uploadDirectory.append(File.separator);
        uploadDirectory.append("user-");
        uploadDirectory.append(userId);
        uploadDirectory.append(File.separator);
        if(tripId.isPresent()) {
            uploadDirectory.append("trip-");
            uploadDirectory.append(tripId.get());
            uploadDirectory.append(File.separator);
        }
        if(stepId.isPresent()) {
            uploadDirectory.append("step-");
            uploadDirectory.append(stepId.get());
            uploadDirectory.append(File.separator);
        }
        String realPathToUploads = request.getServletContext().getRealPath(uploadDirectory.toString());
        if (!new File(realPathToUploads).exists()) {
            new File(realPathToUploads).mkdirs();
        }
        return realPathToUploads;
    }

    @Override
    public void movePictureToStepFolder(Picture picture, Long stepId) throws IOException {
        File oldPicture = new File(picture.getSrc());
        String fileName = oldPicture.getName();
        String oldDirectory = oldPicture.getParent();
        StringBuilder newDirectory = new StringBuilder();
        newDirectory.append(oldDirectory);
        newDirectory.append(File.separator);
        newDirectory.append("step-");
        newDirectory.append(stepId);
        newDirectory.append(File.separator);
        if (!new File(newDirectory.toString()).exists()) {
            new File(newDirectory.toString()).mkdirs();
        }
        StringBuilder newFilePath = new StringBuilder();
        newFilePath.append(newDirectory.toString());
        newFilePath.append(File.separator);
        newFilePath.append(fileName);
        Path oldPath = FileSystems.getDefault().getPath(picture.getSrc());
        Path newPath = FileSystems.getDefault().getPath(newFilePath.toString());
        Files.move(oldPath, newPath);
        picture.setSrc(newFilePath.toString());
    }
}
