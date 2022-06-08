package com.bronko.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "series")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Series {
    @Id
    @Column(name = "series_id")
    private int seriesId;

    @Column(length = 512)
    private String title = "";

    @Column(length = 2048)
    private String description = "";

    private String imgUrl = "";

    private double rating = 0;

    private int votes = 0;

    private String distributionType = "";

    private String status = "";

    private String emissionDate = "";

    private int episodeCount = 0;

    private Instant updateTimestamp = Instant.now();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "relatedTo", orphanRemoval = true)
    private List<RelatedSeries> relatedSeries = new ArrayList<>();

    public void addRelatedSeries(RelatedSeries relatedSeries) {
        this.relatedSeries.add(relatedSeries);
        relatedSeries.setRelatedTo(this);
    }

    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "series", orphanRemoval = true)
    private List<Episode> episodes = new ArrayList<>();

    public void addEpisode(Episode episode) {
        this.episodes.add(episode);
        episode.setSeries(this);
    }

    public void update(Series series) {
        title = series.title;
        description = series.description;
        imgUrl = series.imgUrl;
        rating = series.rating;
        votes = series.votes;
        distributionType = series.distributionType;
        status = series.status;
        emissionDate = series.emissionDate;
        episodeCount = series.episodeCount;
        updateTimestamp = series.updateTimestamp;
    }
}
