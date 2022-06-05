package com.bronko.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "episodes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Episode {
    @Id
    private int episodeId;

    private boolean online = false;

    private String title = "";

    private int episodeNumber = 0;

    private String langs = "";

    private Timestamp emissionDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;

    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "episode", orphanRemoval = true)
    private List<Player> players = new ArrayList<>();

    public void addPlayer(Player player) {
        this.players.add(player);
        player.setEpisode(this);
    }

    public void update(Episode episode) {
        this.online = episode.online;
        this.title = episode.title;
        this.episodeNumber = episode.episodeNumber;
        this.langs = episode.langs;
        this.emissionDate = episode.emissionDate;
    }
}
