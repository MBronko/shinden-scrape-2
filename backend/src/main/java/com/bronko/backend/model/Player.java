package com.bronko.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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

    @OneToOne(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private PlayerIframe iframe;

    @OneToOne(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private PlayerInfo info;

    public void addIframe(PlayerIframe iframe) {
        this.iframe = iframe;
        iframe.setPlayer(this);
    }

    public void addInfo(PlayerInfo info) {
        this.info = info;
        info.setPlayer(this);
    }
}
