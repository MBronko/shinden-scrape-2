package com.bronko.backend.service;

import com.bronko.backend.model.Episode;
import com.bronko.backend.model.Series;
import com.bronko.backend.repository.EpisodeRepository;
import com.bronko.backend.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EpisodeService {
    private final SeriesRepository seriesRepository;
    private final EpisodeRepository episodeRepository;

    private final ShindenScrapeService shindenScrapeService;
    private final SeriesService seriesService;

    public List<Episode> getEpisodesForSeries(int seriesId) throws IOException {
        Series series = seriesService.getSeries(seriesId);

        List<Episode> toDelete = new ArrayList<>(series.getEpisodes());

        Map<Integer, Episode> cachedEpisodes = new HashMap<>();
        for (Episode episode : series.getEpisodes()) {
            cachedEpisodes.put(episode.getEpisodeId(), episode);
        }

        List<Episode> episodes = shindenScrapeService.getEpisodes(seriesId);

        for (Episode newEpisode : episodes) {
            Episode episode = cachedEpisodes.get(newEpisode.getEpisodeId());
            if (episode == null) {
                episode = new Episode();
                episode.setEpisodeId(newEpisode.getEpisodeId());

                series.addEpisode(episode);
            } else {
                toDelete.remove(episode);
            }

            episode.update(newEpisode);
        }

        series.getEpisodes().removeAll(toDelete);

        return seriesRepository.save(series).getEpisodes();
    }

    public Episode getEpisode(int seriesId, int episodeId) throws IOException {
        Optional<Episode> episode = episodeRepository.findById(episodeId);
        if (episode.isPresent()) {
            return episode.get();
        }
        getEpisodesForSeries(seriesId);

        episode = episodeRepository.findById(episodeId);
        if (episode.isPresent()) {
            return episode.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid series_id or episode_id");
    }
}
