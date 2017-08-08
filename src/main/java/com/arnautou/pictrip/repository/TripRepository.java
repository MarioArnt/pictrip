package com.arnautou.pictrip.repository;

import com.arnautou.pictrip.domain.Trip;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Spring Data JPA repository for the Trip entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TripRepository extends JpaRepository<Trip,Long> {

    @Query("select trip from Trip trip where trip.owner.login = ?#{principal.username}")
    List<Trip> findByOwnerIsCurrentUser();

    @Query("select distinct trip from Trip trip left join fetch trip.members")
    List<Trip> findAllWithEagerRelationships();

    @Query("select distinct trip from Trip trip left join fetch trip.members where trip.owner.id =:ownerId")
    List<Trip> findByCurrentUserWithEagerRelationships(@Param("ownerId") Long ownerId);

    @Query("select trip from Trip trip left join fetch trip.members where trip.id =:id")
    Trip findOneWithEagerRelationships(@Param("id") Long id);

    Trip findOne(Long id);

}
