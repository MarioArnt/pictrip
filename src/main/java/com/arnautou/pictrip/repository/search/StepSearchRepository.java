package com.arnautou.pictrip.repository.search;

import com.arnautou.pictrip.domain.Step;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Step entity.
 */
public interface StepSearchRepository extends ElasticsearchRepository<Step, Long> {
}
