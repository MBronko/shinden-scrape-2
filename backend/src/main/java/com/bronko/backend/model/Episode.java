package com.bronko.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "episodes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Episode {
    @Id
    private int episodeId;

    private boolean online;

    private String title;

    private int episodeNumber;

    private String langs;

    private Timestamp emissionDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "episode", orphanRemoval = true)
    private List<Player> players = new ArrayList<>();

    public void addPlayer(Player player) {
        this.players.add(player);
        player.setEpisode(this);
    }
}
