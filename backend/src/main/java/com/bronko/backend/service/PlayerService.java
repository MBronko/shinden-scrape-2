package com.bronko.backend.service;

import com.bronko.backend.DTO.PlayerDTO;
import com.bronko.backend.model.Episode;
import com.bronko.backend.model.Player;
import com.bronko.backend.model.PlayerIframe;
import com.bronko.backend.model.PlayerInfo;
import com.bronko.backend.repository.EpisodeRepository;
import com.bronko.backend.repository.PlayerIframeRepository;
import com.bronko.backend.repository.PlayerInfoRepository;
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
        Episode episode = episodeRepository.findById(episodeId).orElse(null);

        List<PlayerInfo> players = shindenScrapeService.getPlayersInfo(seriesId, episodeId);
        List<PlayerDTO> result = new ArrayList<>();

        List<Integer> playerIds = new ArrayList<>();

        for (PlayerInfo playerInfo : players) {
            int playerId = playerInfo.getId();
            playerIds.add(playerId);

            Player player = playerRepository.findById(playerId).orElseGet(Player::new);
            player.setPlayerId(playerId);
            player.addInfo(playerInfo);

            if (episode != null) {
                episode.addPlayer(player);
            }

            player = playerRepository.save(player);
            result.add(new PlayerDTO(player));
        }

        if (episode != null) {
            episodeRepository.save(episode);
            playerRepository.deleteAllByEpisodeAndPlayerIdNotIn(episode, playerIds);
        }

        return result;
    }
}
