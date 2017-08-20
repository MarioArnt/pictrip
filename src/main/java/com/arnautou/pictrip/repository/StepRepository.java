package com.arnautou.pictrip.repository;

import com.arnautou.pictrip.domain.Step;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Step entity.
 */
@Repository
public interface StepRepository extends JpaRepository<Step,Long> {
    Integer countByTripId(Long tripId);
    List<Step> findByTripId(Long tripId);
    Optional<Step> findOneByTripIdAndNumber(Long tripId, Integer stepNumber);
}
