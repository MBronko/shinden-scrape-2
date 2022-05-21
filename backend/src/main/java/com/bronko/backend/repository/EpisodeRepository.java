package com.bronko.backend.repository;

import com.bronko.backend.model.Episode;
import com.bronko.backend.model.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Integer> {
    public void deleteAllBySeries(Series series);
}
