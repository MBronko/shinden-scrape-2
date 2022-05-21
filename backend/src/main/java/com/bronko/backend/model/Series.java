package com.bronko.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
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
    private String title;

    @Column(length = 2048)
    private String description;

    private String imgUrl;

    private double rating;

    private int votes;

    private String distributionType;

    private String status;

    private String emissionDate;

    private int episodeCount;

    @UpdateTimestamp
    private Timestamp creationTimestamp;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "relatedTo", orphanRemoval = true)
    private List<RelatedSeries> relatedSeries = new ArrayList<>();

    public void addRelatedSeries(RelatedSeries relatedSeries){
        this.relatedSeries.add(relatedSeries);
        relatedSeries.setRelatedTo(this);
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "series", orphanRemoval = true)
    private List<Episode> episodes = new ArrayList<>();

    public void addEpisode(Episode episode){
        this.episodes.add(episode);
        episode.setSeries(this);
    }
}
