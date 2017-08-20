package com.arnautou.pictrip.repository;

import com.arnautou.pictrip.domain.Journey;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.Optional;


/**
 * Spring Data JPA repository for the Journey entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JourneyRepository extends JpaRepository<Journey,Long> {
    Optional<Journey> findOneByStepToId(Long stepId);
}
