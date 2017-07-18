package com.arnautou.pictrip.repository.search;

import com.arnautou.pictrip.domain.Picture;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Picture entity.
 */
public interface PictureSearchRepository extends ElasticsearchRepository<Picture, Long> {
}
