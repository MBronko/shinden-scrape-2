package com.bronko.backend.controller;

import com.bronko.backend.model.Player;
import com.bronko.backend.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping("/player/{playerId}")
    public Player getPlayer(@PathVariable int playerId) throws IOException, InterruptedException {
        return playerService.getPlayer(playerId);
    }
}
