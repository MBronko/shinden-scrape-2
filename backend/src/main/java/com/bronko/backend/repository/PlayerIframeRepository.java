package com.bronko.backend.repository;

import com.bronko.backend.model.PlayerIframe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerIframeRepository extends JpaRepository<PlayerIframe, Integer> {
}
