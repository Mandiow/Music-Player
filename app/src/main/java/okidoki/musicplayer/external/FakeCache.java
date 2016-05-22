package okidoki.musicplayer.external;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import okidoki.musicplayer.classes.AlbumList;
import okidoki.musicplayer.classes.ArtistList;
import okidoki.musicplayer.classes.MusicList;

/**
 * Created by Mandiow on 01/02/2016.
 */
public class FakeCache implements Serializable {

    public  ObjectOutputStream ObjectAlbumOut;
    public FileOutputStream FileAlbumOut;
    public void writeAlbum(Context context,AlbumList Album) {


        String filename = "OkiDokAlbum.srl";

        try {
            // create a file in downloads directory
            FileAlbumOut =
                    new FileOutputStream(
                            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),filename)
                    );
            ObjectAlbumOut = new ObjectOutputStream(FileAlbumOut);
            ObjectAlbumOut.writeObject(Album);
            ObjectAlbumOut.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            Log.v("MyApp", "File didn't write");
        }
    }

    public FileInputStream FileAlbumIn;
    public ObjectInputStream ObjectAlbumIn;
    public AlbumList readAlbum(Context context) {
        AlbumList ReturnClass = null;
        String filename = "OkiDokAlbum.srl";

        try {
            // create a file in downloads directory
            FileAlbumIn =
                    new FileInputStream(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + File.separator + filename)
                    );
            ObjectAlbumIn = new ObjectInputStream(FileAlbumIn);
            ReturnClass = (AlbumList) ObjectAlbumIn.readObject();
            ObjectAlbumIn.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            Log.v("MyApp", "File didn't Read");
        }
        return ReturnClass;
    }
    public  ObjectOutputStream ObjectArtistOut;
    public FileOutputStream FileArtistOut;
    public void writeArtist(Context context,ArtistList Artist) {


        String filename = "OkiDokArtist.srl";

        try {
            // create a file in downloads directory
            FileArtistOut =
                    new FileOutputStream(
                            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename)
                    );
            ObjectArtistOut = new ObjectOutputStream(FileArtistOut);
            ObjectArtistOut.writeObject(Artist);
            ObjectArtistOut.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.v("MyApp", "File didn't write");
        }
    }
    public FileInputStream FileArtistIn;
    public ObjectInputStream ObjectArtistIn;
    public ArtistList readArtist(Context context) {
        ArtistList ReturnClass = null;
        String filename = "OkiDokArtist.srl";

        try {
            // create a file in downloads directory
            FileArtistIn =
                    new FileInputStream(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + File.separator + filename)
                    );
            ObjectArtistIn = new ObjectInputStream(FileArtistIn);
            ReturnClass = (ArtistList) ObjectArtistIn.readObject();
            ObjectArtistIn.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            Log.v("MyApp", "File OkiDokArtist didn't Read");
        }
        return ReturnClass;
    }

    public  ObjectOutputStream ObjectMusicOut;
    public FileOutputStream FileMusicOut;
    public void writeMusic(Context context,MusicList Music) {


        String filename = "OkiDokMusics3.srl";

        try {
            // create a file in downloads directory
            FileMusicOut =
                    new FileOutputStream(
                            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),filename)
                    );
            ObjectMusicOut = new ObjectOutputStream(FileMusicOut);
            ObjectMusicOut.writeObject(Music);
            ObjectMusicOut.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            Log.v("MyApp","File OkiDokMusics3 didn't write");
        }


        //Log.e("btry", String.valueOf(out));

    }

    public FileInputStream FileReadMusicIn;
    public ObjectInputStream ObjectMusicIn;
    public MusicList readMusic(Context context) {
        MusicList ReturnClass = null;
        String filename = "OkiDokMusics3.srl";

        try {
            // create a file in downloads directory
            FileReadMusicIn =
                    new FileInputStream(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + File.separator + filename)
                    );
            ObjectMusicIn = new ObjectInputStream(FileReadMusicIn);
            ReturnClass = (MusicList) ObjectMusicIn.readObject();
            Log.e("Valor da Classe:",ReturnClass.toString());
            ObjectMusicIn.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            Log.v("MyApp", "File OkiDokMusics3 didn't read");
        }
        return ReturnClass;
    }
    public static boolean Exists(){
        String filename = "OkiDokMusics3.srl";
        File file = new File(Environment.getExternalStorageDirectory(),Environment.DIRECTORY_DOWNLOADS + File.separator + filename);
        return file.exists();
    }

}
