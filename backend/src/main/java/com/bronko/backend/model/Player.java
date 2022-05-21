package com.bronko.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "players")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    @Id
    private int playerId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "episode_id")
    private Episode episode;

    private String service;

    private String resolution;

    private String langAudio;

    private String langSub;

    private String subsFavicon = "";

    private String subsAuthors = "";

    private String source;

    private Timestamp added;

    private String iframe;
}
