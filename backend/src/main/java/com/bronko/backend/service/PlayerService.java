package com.bronko.backend.service;

import com.bronko.backend.DTO.PlayerDTO;
import com.bronko.backend.model.*;
import com.bronko.backend.repository.EpisodeRepository;
import com.bronko.backend.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final EpisodeRepository episodeRepository;

    private final ShindenScrapeService shindenScrapeService;
    private final EpisodeService episodeService;

    public PlayerDTO getPlayerIframe(int playerId) throws IOException, InterruptedException {
        Optional<Player> optPlayer = playerRepository.findById(playerId);
        Player player = optPlayer.orElseGet(Player::new);
        player.setPlayerId(playerId);

        PlayerIframe iframe = player.getIframe();

        if (iframe != null) {
            return new PlayerDTO(player);
        }

        PlayerIframe newIframe = shindenScrapeService.getPlayerIframe(playerId);
        player.addIframe(newIframe);

        return new PlayerDTO(playerRepository.save(player));
    }

    public List<PlayerDTO> getPlayersForEpisode(int seriesId, int episodeId) throws IOException, JSONException {
        Episode episode = episodeService.getEpisode(seriesId, episodeId);

        List<Player> toRemove = new ArrayList<>(episode.getPlayers());

        Map<Integer, Player> cachedPlayers = new HashMap<>();
        for (Player player : episode.getPlayers()) {
            cachedPlayers.put(player.getPlayerId(), player);
        }

        List<PlayerInfo> players = shindenScrapeService.getPlayersInfo(seriesId, episodeId);

        for (PlayerInfo playerInfo : players) {
            Player player = cachedPlayers.get(playerInfo.getId());
            if (player == null) {
                player = new Player();
                player.setPlayerId(playerInfo.getId());

                episode.addPlayer(player);
            } else {
                toRemove.remove(player);
            }

            player.addInfo(playerInfo);
        }

        episode.getPlayers().removeAll(toRemove);

        List<PlayerDTO> result = new ArrayList<>();
        for (Player player : episodeRepository.save(episode).getPlayers()) {
            result.add(new PlayerDTO(player));
        }

        return result;
    }
}
