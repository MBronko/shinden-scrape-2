package com.bronko.backend.controller;

import com.bronko.backend.model.Series;
import com.bronko.backend.service.SeriesService;
import com.bronko.backend.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class SeriesController {
    private final SeriesService seriesService;

    @GetMapping("/series/{id}")
    public Series getSeries(@PathVariable String id) throws IOException {
        return seriesService.getSeries(Utils.parseFirstDigitsInString(id));
    }
}
