package com.bronko.backend.repository;

import com.bronko.backend.model.Episode;
import com.bronko.backend.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    void deleteAllByEpisode(Episode episode);

    Optional<Player> findByPlayerIdAndIframeIsNotNull(int playerId);

    List<Player> findAllByEpisodeAndPlayerIdIn(Episode episode, List<Integer> playerId);
    void deleteAllByEpisodeAndPlayerIdNotIn(Episode episode, List<Integer> playerId);
}
