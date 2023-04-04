package com.haodv.musiceat;

import android.graphics.Bitmap;

import java.io.Serializable;

public class SongDto implements Serializable {
    public Boolean isPlay = false;
    public long songId;
    public String songTitle;
    public String songArtist;
    public String path;
    public short genre;
    public long duration;
    public String album;
    public Bitmap albumArt;

    public Boolean getPlay() {
        return isPlay;
    }

    public void setPlay(Boolean play) {
        isPlay = play;
    }

    public String toString() {
        return String.format("songId: %d, Title: %s, Artist: %s, Path: %s, Genere: %d, Duration %s",
                songId, songTitle, songArtist, path, genre, duration);
    }
}
