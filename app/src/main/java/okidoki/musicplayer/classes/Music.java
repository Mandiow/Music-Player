package okidoki.musicplayer.classes;

import android.net.Uri;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

public class Music implements Serializable {
    //Every media file MUST have this fields
    private String id;
    private String filePath;
    //The ID one is a unique id that android have for each mediaFile existent in the system

    //Metadata and others
    public String title;
    private Artist artist;
    private Album album;
    private Integer trackNumber;
    private long duration;
    private Boolean isAudioBook;
    private Boolean hearted;


    //Constructors
    public Music(String id, String filePath, String title, Artist artist, Album album, Integer trackNumber, Boolean isAudioBook, long duration,Boolean hearted){
        this.id = id;
        this.title = title;
        this.filePath = filePath;
        this.artist = artist;
        this.album = album;
        this.trackNumber = trackNumber;
        this.isAudioBook = isAudioBook;
        this.duration = duration;
        this.hearted = hearted;
    }
    public Music(String id, String filePath){
        this.id = id;
        this.filePath = filePath;
    }


    //Getters and setters for metadata of media files (because users will be able to edit them);
    //NOTE: THEY MIGHT RETURN NULL VALUES SINCE METADATA CAN BE NULL

    public String getFilePath(){
        return this.filePath;
    }

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public Artist getArtist(){
        return this.artist;
    }
    public void setArtist(Artist artist){
        this.artist = artist;
    }

    public Album getAlbum(){
        return this.album;
    }
    public void setAlbum(Album album){
        this.album = album;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }
    private void setTrackNumber(int trackNumber){
        this.trackNumber = trackNumber;
    }

    public Boolean getIsAudioBook() {
        return isAudioBook;
    }
    public void setIsAudioBook(Boolean isAudioBook) {
        this.isAudioBook = isAudioBook;
    }

    public Boolean getIsHearted(){return  hearted;}
    public void setIsHearted(Boolean hearted){this.hearted = hearted;}

    public long getDuration() {
        return duration;
    }

    public String getId() {
        return id;
    }

}
