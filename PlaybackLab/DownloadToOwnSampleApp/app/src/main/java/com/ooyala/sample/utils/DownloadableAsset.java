package com.ooyala.sample.utils;

import static com.ooyala.sample.utils.DownloadState.NOT_DOWNLOADED;

public class DownloadableAsset {

    private DownloadState status = NOT_DOWNLOADED;
    private String name;
    private PlayerSelectionOption playerSelectionOption;

    public DownloadableAsset(String name, PlayerSelectionOption playerSelectionOption) {
        this.name = name;
        this.playerSelectionOption = playerSelectionOption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DownloadState getStatus() {
        return status;
    }

    public void setStatus(DownloadState status) {
        this.status = status;
    }

    public PlayerSelectionOption getPlayerSelectionOption() {
        return playerSelectionOption;
    }

    public void setPlayerSelectionOption(PlayerSelectionOption playerSelectionOption) {
        this.playerSelectionOption = playerSelectionOption;
    }
}
