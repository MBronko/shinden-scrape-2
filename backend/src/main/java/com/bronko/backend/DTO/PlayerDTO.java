package com.bronko.backend.DTO;

import com.bronko.backend.model.Player;
import com.bronko.backend.model.PlayerIframe;
import com.bronko.backend.model.PlayerInfo;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PlayerDTO {
    public PlayerDTO(Player player) {
        playerId = player.getPlayerId();

        PlayerIframe playerIframe = player.getIframe();
        if (playerIframe != null) {
            iframe = playerIframe.getIframe();
        }

        PlayerInfo playerInfo = player.getInfo();

        if (playerInfo != null) {
            service = playerInfo.getService();

            resolution = playerInfo.getResolution();

            langAudio = playerInfo.getLangAudio();

            langSub = playerInfo.getLangSub();

            subsFavicon = playerInfo.getSubsFavicon();

            subsAuthors = playerInfo.getSubsAuthors();

            source = playerInfo.getSource();

            added = playerInfo.getAdded();
        }
    }

    private int playerId;

    private String iframe;

    private String service;

    private String resolution;

    private String langAudio;

    private String langSub;

    private String subsFavicon = "";

    private String subsAuthors = "";

    private String source;

    private Timestamp added;
}
