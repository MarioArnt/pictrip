package com.arnautou.pictrip.repository.search;

import com.arnautou.pictrip.domain.Trip;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Trip entity.
 */
public interface TripSearchRepository extends ElasticsearchRepository<Trip, Long> {
}
