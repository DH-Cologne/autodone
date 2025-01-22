package de.uoc.dh.idh.autodone.services;

import de.uoc.dh.idh.autodone.entities.MediaEntity;
import de.uoc.dh.idh.autodone.entities.StatusEntity;

public class MediaDownloadTask implements Comparable<MediaDownloadTask> {
    private final MediaEntity media;
    private final StatusEntity status;

    public MediaDownloadTask(MediaEntity media, StatusEntity status) {
        this.media = media;
        this.status = status;
    }

    public MediaEntity getMedia() {
        return media;
    }

    public StatusEntity getStatus() {
        return status;
    }

    @Override
    public int compareTo(MediaDownloadTask other) {
        return this.status.getDate().compareTo(other.getStatus().getDate());
    }
}