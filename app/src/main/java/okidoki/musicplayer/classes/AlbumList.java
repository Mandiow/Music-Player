package okidoki.musicplayer.classes;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import okidoki.musicplayer.MainActivity;
import okidoki.musicplayer.R;

public class AlbumList implements Serializable {
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
            {
                return curAlbum;
            }
        }
        return null;
    }

    public Album getAlbumNameFromArtist(String artist)
    {
        for(Album curAlbum : this.albumList)
        {
            if (curAlbum.getArtist().equals(artist))
                return curAlbum;
        }
        return null;

    }
    //FILL THE DAMN BITMAPS I FORGOT, FOR GOD"S SAKE =.=
    public void FillBitmaps(AlbumList global)
    {
        ContentResolver cr = MainActivity.mainActivity.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;
        if(cur != null) {
            count = cur.getCount();

            if (count > 0) {
                while (cur.moveToNext()) {
                    Album curAlbum = global.searchAlbum((cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM))));
                    Long albumId = cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                    Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
                    try {

                        MainActivity.globalAlbumList.getAlbumFromList(MainActivity.globalAlbumList.AlbumPos(curAlbum.getName())).setBitmap(MediaStore.Images.Media.getBitmap(
                                MainActivity.mainActivity.getContentResolver(), albumArtUri));

                        MainActivity.globalAlbumList.getAlbumFromList(MainActivity.globalAlbumList.AlbumPos(curAlbum.getName())).setHasBitmap(true);
                    } catch (FileNotFoundException exception) {
                        MainActivity.globalAlbumList.getAlbumFromList(MainActivity.globalAlbumList.AlbumPos(curAlbum.getName()))
                                .setBitmap(BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(), R.drawable.ic_album));
                        MainActivity.globalAlbumList.getAlbumFromList(MainActivity.globalAlbumList.AlbumPos(curAlbum.getName())).setHasBitmap(false);

                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public Album getAlbumFromList(int id){
        return this.albumList.get(id);
    }
    public int AlbumListSize(){return this.albumList.size();}

    public int AlbumPos(String album)
    {
        int index = 0;
        for(Album curAlbum : this.albumList)
        {
            if (curAlbum.getName().equals(album))
                return index;
            index++;
        }
        return 0;

    }

}
