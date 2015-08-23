package okidoki.musicplayer.classes;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.Date;

/**
 * Created by Administrator on 5/30/2015.
 */
public class Album {
    private String name;
    private int year;
    private String artist;
    private Bitmap albumArt;
    private boolean hasBitmap;
    private Uri albumArtUri;
    private long id;
    private MusicList musicList = new MusicList();

    public Album(long id, String name, int year, String artist, Bitmap albumArt, boolean hasBitmap, Uri albumArtUri, MusicList musicList){
        this.id = id;
        this.name = name;
        this.year = year;
        this.artist = artist;
        this.albumArt = albumArt;
        this.hasBitmap = hasBitmap;
        this.albumArtUri = albumArtUri;
        this.musicList = musicList;

    }

    public Album(long id, String name, int year, String artist, MusicList musicList){
        this.id = id;
        this.name = name;
        this.year = year;
        this.artist = artist;
        this.musicList = musicList;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getArtist() {
        return artist;
    }

    public MusicList getMusicList() {
        return musicList;
    }

    public int getDate() {
        return this.year;
    }

    public void setDate(int year) {
        this.year = year;
    }

    public Bitmap getBitmap() {
        return this.albumArt;
    }

    public long getId() {
        return this.id;
    }

    public Uri getAlbumArtUri() {
        return albumArtUri;
    }

    public boolean getHasBitmap() {
        return hasBitmap;
    }
}
