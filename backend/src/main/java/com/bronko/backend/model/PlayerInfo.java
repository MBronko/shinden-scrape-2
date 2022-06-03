package com.bronko.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "player_info")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerInfo {
    @Id
    private int id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    private String service;

    private String resolution;

    private String langAudio;

    private String langSub;

    private String subsFavicon = "";

    private String subsAuthors = "";

    private String source;

    private Timestamp added;
}
