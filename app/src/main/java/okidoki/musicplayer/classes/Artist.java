package okidoki.musicplayer.classes;

import java.io.Serializable;

/**
 * Created by Administrator on 5/30/2015.
 */
public class Artist implements Comparable<Artist>,Serializable {
    private String name;
    private AlbumList albumList;
    private MusicList musicList;
    public Artist(String name, AlbumList albumList, MusicList musicList)
    {
        this.name = name;
        this.albumList = albumList;
        this.musicList = musicList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AlbumList getAlbumList() {
        return albumList;
    }

    @Override
    public int compareTo(Artist another) {
        return this.name.compareTo(another.name);

    }

    public MusicList getMusicList() {
        return musicList;
    }
}
