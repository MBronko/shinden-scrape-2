package com.bronko.backend.service;

import com.bronko.backend.model.Episode;
import com.bronko.backend.model.Player;
import com.bronko.backend.repository.EpisodeRepository;
import com.bronko.backend.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final EpisodeRepository episodeRepository;
    private final ShindenScrapeService shindenScrapeService;

    public Player getPlayer(int playerId) throws IOException, InterruptedException {
        Optional<Player> player = playerRepository.findByPlayerIdAndIframeIsNotNull(playerId);

        if (player.isPresent() && player.get().getIframe() != null) {
            return player.get();
        }
        Player newPlayer = shindenScrapeService.getPlayer(playerId);
        return updateOrCreatePlayer(newPlayer);
    }

    public List<Player> getPlayersForEpisode(int seriesId, int episodeId) throws IOException, JSONException {
        Optional<Episode> optEpisode = episodeRepository.findById(episodeId);

        List<Player> players = shindenScrapeService.getPlayers(seriesId, episodeId);

        if (optEpisode.isPresent()) {
            Episode episode = optEpisode.get();
            for (Player player : players) {
                player.setEpisode(episode);
                episode.addPlayer(updateOrCreatePlayer(player));
            }

            return episodeRepository.save(episode).getPlayers();
        }

        return players;
    }

    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }

    public Player updateOrCreatePlayer(Player player) {
        Optional<Player> actualOptionalPlayer = playerRepository.findById(player.getPlayerId());

        if (actualOptionalPlayer.isPresent()) {
            Player actualPlayer = actualOptionalPlayer.get();
            if (player.getIframe() != null) {
                actualPlayer.setIframe(player.getIframe());
                player = actualPlayer;
            } else {
                player.setIframe(actualPlayer.getIframe());
            }
        }

        return createPlayer(player);
    }
}
