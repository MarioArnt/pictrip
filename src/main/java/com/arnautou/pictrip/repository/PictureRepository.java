package com.arnautou.pictrip.repository;

import com.arnautou.pictrip.domain.Picture;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Picture entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PictureRepository extends JpaRepository<Picture,Long> {
    
}
