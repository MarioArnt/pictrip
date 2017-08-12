package com.arnautou.pictrip.repository.search;

import com.arnautou.pictrip.domain.Journey;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Journey entity.
 */
public interface JourneySearchRepository extends ElasticsearchRepository<Journey, Long> {
}
