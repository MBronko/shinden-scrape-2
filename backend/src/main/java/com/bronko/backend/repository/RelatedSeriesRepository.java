package com.bronko.backend.repository;

import com.bronko.backend.model.RelatedSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatedSeriesRepository extends JpaRepository<RelatedSeries, Integer> {
}
