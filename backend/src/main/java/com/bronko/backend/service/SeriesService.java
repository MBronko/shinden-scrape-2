package com.bronko.backend.service;

import com.bronko.backend.model.Series;
import com.bronko.backend.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeriesService {
    private final SeriesRepository seriesRepository;
    private final ShindenScrapeService shindenScrapeService;

    public Series getSeries(int id) throws IOException {
        Optional<Series> optSeries = seriesRepository.findById(id);
        if (optSeries.isEmpty()) {
            Series newSeriesData = shindenScrapeService.getSeries(id);
            return seriesRepository.save(newSeriesData);
        }
        Series series = optSeries.get();

        Instant toCompare = Instant.now();
        toCompare = toCompare.minus(3, ChronoUnit.HOURS);

        if (series.getUpdateTimestamp().isBefore(toCompare)) {
            Series newSeriesData = shindenScrapeService.getSeries(id);

            series.update(newSeriesData);
            return seriesRepository.save(series);
        }

        return series;
    }
}
