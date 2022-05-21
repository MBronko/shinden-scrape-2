package com.bronko.backend.controller;

import com.bronko.backend.model.Episode;
import com.bronko.backend.model.Player;
import com.bronko.backend.service.EpisodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EpisodeController {
    private final EpisodeService episodeService;

    @GetMapping("/series/{seriesId}/episodes")
    public List<Episode> getEpisodesForSeries(@PathVariable int seriesId) throws IOException {
        return episodeService.getEpisodesForSeries(seriesId);
    }
}
