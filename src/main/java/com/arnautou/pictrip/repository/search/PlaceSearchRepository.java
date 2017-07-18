package com.arnautou.pictrip.repository.search;

import com.arnautou.pictrip.domain.Place;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Place entity.
 */
public interface PlaceSearchRepository extends ElasticsearchRepository<Place, Long> {
}
