package com.bronko.backend.controller;

import com.bronko.backend.DTO.PlayerDTO;
import com.bronko.backend.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping("/player/{playerId}")
    public PlayerDTO getPlayer(@PathVariable int playerId) throws IOException, InterruptedException {
        return playerService.getPlayerIframe(playerId);
    }

    @GetMapping("/series/{seriesId}/episode/{episodeId}/players")
    public List<PlayerDTO> getPlayersForEpisode(@PathVariable int seriesId, @PathVariable int episodeId) throws IOException, JSONException {
        return playerService.getPlayersForEpisode(seriesId, episodeId);
    }
}
