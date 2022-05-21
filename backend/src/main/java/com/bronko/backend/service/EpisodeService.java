package com.bronko.backend.service;

import com.bronko.backend.model.Episode;
import com.bronko.backend.model.Player;
import com.bronko.backend.model.Series;
import com.bronko.backend.repository.EpisodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EpisodeService {
    private final EpisodeRepository episodeRepository;
    private final ShindenScrapeService shindenScrapeService;
    private final SeriesService seriesService;

    public List<Episode> getEpisodesForSeries(int seriesId) throws IOException {
        Series series = seriesService.getSeries(seriesId);

//        Map<Integer, String> iframes = new HashMap<>();


        List<Episode> episodes = shindenScrapeService.getEpisodes(seriesId);

        List<Episode> resultEpisodes = new ArrayList<>();
        List<Integer> episodeIds = new ArrayList<>();

        for (Episode episode : episodes) {
//            series.add
            resultEpisodes.add(createEpisode(episode));
            episodeIds.add(episode.getEpisodeId());
        }


        List<Integer> toDelete = new ArrayList<>();
        for (Episode episode : series.getEpisodes()) {
            if(!episodeIds.contains(episode.getEpisodeId())){
                toDelete.add(episode.getEpisodeId());
            }
        }

        return resultEpisodes;
    }


    public Episode createEpisode(Episode episode) {
        return episodeRepository.save(episode);
    }
}
