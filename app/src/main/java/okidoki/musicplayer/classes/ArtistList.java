package okidoki.musicplayer.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class ArtistList implements Serializable {
    ArrayList<Artist> artistList = new ArrayList<>();

    public ArtistList(){
    }

    public ArrayList<Artist> getArtistList(){
        return this.artistList;
    }
    public Artist getArtistFromList(int id){
        return this.artistList.get(id);
    }


    public void appendArtist(Artist artist){
        this.artistList.add(artist);
    }
    public void removeArtist(long artistId){
        this.artistList.remove(artistId);
    }

    public void sortArtistList(int sortBy){
        //TODO
    }
    public Artist searchArtist(String artistName){
        for(Artist curArtist : artistList)
        {
            if(curArtist.getName().equals(artistName))
                return curArtist;
        }
        return null;
    }

}
