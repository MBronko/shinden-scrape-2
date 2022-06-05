package com.bronko.backend.service;

import com.bronko.backend.model.Series;
import com.bronko.backend.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeriesService {
    private final SeriesRepository seriesRepository;
    private final ShindenScrapeService shindenScrapeService;

    public Series getSeries(int id) throws IOException {
        Optional<Series> series = seriesRepository.findById(id);
        if (series.isPresent()) {
            return series.get();
        } else {
            Series newSeriesData = shindenScrapeService.getSeries(id);
            return createSeries(newSeriesData);
        }
    }

    public Series createSeries(Series series) {
        return seriesRepository.save(series);
    }

    public void deleteSeries(int id) {
        seriesRepository.deleteById(id);
    }
}
