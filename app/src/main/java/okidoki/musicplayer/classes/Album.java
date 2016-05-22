package okidoki.musicplayer.classes;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 5/30/2015.
 */
public class Album implements Serializable {
    private String name;
    private int year;
    private String artist;
    transient private Bitmap albumArt;
    private boolean hasBitmap;
    transient private Uri albumArtUri;
    private long id;
    private MusicList musicList = new MusicList();
    private byte[] BitmapData;

    public Album(long id, String name, int year, String artist, Bitmap albumArt, boolean hasBitmap, Uri albumArtUri, MusicList musicList){
        this.id = id;
        this.name = name;
        this.year = year;
        this.artist = artist;
        this.albumArt = albumArt;
        this.hasBitmap = hasBitmap;
        this.albumArtUri = albumArtUri;
        this.musicList = musicList;
        BitmapData = getBytes(albumArt);

    }

    public Album(long id, String name, int year, String artist, MusicList musicList){
        this.id = id;
        this.name = name;
        this.year = year;
        this.artist = artist;
        this.musicList = musicList;

    }

    public byte[] getBytes(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
        return baos.toByteArray();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBitmap(Bitmap bitmap) {this.albumArt = bitmap;}

    public void setHasBitmap(boolean has) {this.hasBitmap = has;}

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
