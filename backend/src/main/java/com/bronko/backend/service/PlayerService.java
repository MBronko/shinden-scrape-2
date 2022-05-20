package com.bronko.backend.service;

import com.bronko.backend.model.Player;
import com.bronko.backend.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final ShindenScrapeService shindenScrapeService;

    public Player getPlayer(int playerId) throws IOException, InterruptedException {
        Optional<Player> player = playerRepository.findById(playerId);

        if (player.isPresent()) {
            return player.get();
        } else {
            Player newPlayer = shindenScrapeService.getPlayer(playerId);
            return createPlayer(newPlayer);
        }
    }

    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }
}
