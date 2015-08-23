package okidoki.musicplayer.classes;


import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

public class AlbumList {
    ArrayList<Album> albumList = new ArrayList<>();

    public AlbumList(){

    }
    public ArrayList<Album> getAlbumList(){
        return this.albumList;
    }
    public void appendAlbum(Album album){
        this.albumList.add(album);
    }
    public void removeArtist(long artistId){
        this.albumList.remove(artistId);
    }

    public void sortArtistList(int sortBy){
        //TODO
    }

    public Album searchAlbum(String albumName) {
        for (Album curAlbum : this.albumList) {
            if (curAlbum.getName().equals(albumName))
                return curAlbum;
        }
        return null;
    }

    public void getAlbumNameFromArtist(String artist)
    {
        for(Album curAlbum : this.albumList)
        {
            Log.w("Album:", curAlbum.getName());
            if (curAlbum.getName().equals(artist))
                Log.w("Album:", curAlbum.getName());
        }

    }

    public Album getAlbumFromList(int id){
        return this.albumList.get(id);
    }
    public int AlbumListSize(){return this.albumList.size();}

}
