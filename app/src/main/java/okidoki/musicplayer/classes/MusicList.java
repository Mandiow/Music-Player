package okidoki.musicplayer.classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import okidoki.musicplayer.MainActivity;
import okidoki.musicplayer.R;


public class MusicList extends Object implements Serializable, Cloneable{
    ArrayList<Music> musicsList = new ArrayList<>();
    private String name;
    private long id; // Used by android media provider to search for playlists on it's contents
    public MusicList(String musicListName, long id){
        this.name = musicListName;
        this.id = id;
    }
    public MusicList(){
        this.name = "dummyList";
    }

    /**
     * Function to read all mp3 files from sdcard
     * and store the details in ArrayList
     * */
    public ArrayList<Music> searchForMusics(){
        ContentResolver cr = MainActivity.mainActivity.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;
        this.name = "All Songs";
        if(cur != null)
        {
            count = cur.getCount();

            if(count > 0)
            {
                while(cur.moveToNext())
                {
                    //Verify if we already have the Artist of the current element
                    Artist curArtist = MainActivity.globalArtistList.searchArtist(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)));

                    //If our curArtist doesn't exist in the global artist list, we need to add it in there
                    if(curArtist == null)
                    {
                        curArtist = new Artist(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)), new AlbumList(), new MusicList());
                        MainActivity.globalArtistList.appendArtist(curArtist);
                    }

                    //If it already exists, we need to add the reference to the album in the artist, which will be done latter on since we don't have the artist right now

                    //Verify if we already have the album for the current element
                    Album curAlbum = MainActivity.globalAlbumList.searchAlbum((cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM))));
                    if(curAlbum == null)
                    {
                        Long albumId = cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                        Uri sArtworkUri = Uri
                                .parse("content://media/external/audio/albumart");
                        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
                        Bitmap bitmap = null;
                        boolean hasBitmap = false;
                        try {

                            bitmap = MediaStore.Images.Media.getBitmap(
                                    MainActivity.mainActivity.getContentResolver(), albumArtUri);
                            hasBitmap = true;
                        } catch (FileNotFoundException exception) {
                            exception.printStackTrace();
                            bitmap = BitmapFactory.decodeResource(MainActivity.mainActivity.getResources(),R.drawable.ic_album);
                            hasBitmap = false;

                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                        int year = -1;
                        if(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)) != null)
                        {
                            year = Integer.valueOf(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)));
                        }
                        curAlbum = new Album(albumId,
                                cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                                year,
                                curArtist.getName(),
                                bitmap,
                                hasBitmap,
                                albumArtUri,
                                new MusicList()
                        );
                        curArtist.getAlbumList().appendAlbum(curAlbum);
                        MainActivity.globalAlbumList.appendAlbum(curAlbum);
                    }
                    //Adding the reference of the album to the artist
                    //Updating the globalAlbumList with the new album

                    //Add the music with it's album and artist (Artist and Album might be non-existent)
                    //but that isn't going to be a problem

                    Music musicElem =
                            new Music(
                                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID)),
                                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                                    curArtist,
                                    curAlbum,
                                    Integer.valueOf(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TRACK))),
                                    false,
                                    Long.valueOf(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION))),
                                    false
                                    );
                    curAlbum.getMusicList().appendMusic(musicElem);
                    curArtist.getMusicList().appendMusic(musicElem);

                    musicsList.add(musicElem);
                }

            }
        }
        cur.close();
        this.id = createPlaylist(cr, "All Songs");
        for(int i = 0; i < MainActivity.globalMusicList.musicListSize(); i++)
        {
            addMusicToPlaylist(cr,id,MainActivity.globalMusicList.musicsList.get(i));
        }

        return musicsList;
    }

    public static int queryPlaylists(ContentResolver resolver)
    {
        int count = 0;
        Uri media = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME };
        String sort = MediaStore.Audio.Playlists.NAME;
        Cursor cur = resolver.query(media, projection, null, null, sort);
        count = cur.getCount();
        if(cur != null)
        {
            while(cur.moveToNext())
            {
                count = cur.getCount();
                //Adiciona coisas misticas
                MainActivity.userPlaylists.add(new MusicList(
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Playlists.NAME)),
                        cur.getLong(cur.getColumnIndex(MediaStore.Audio.Playlists._ID))));
                MainActivity.playLists.add(cur.getLong(cur.getColumnIndex(MediaStore.Audio.Playlists._ID)));
            }
        }
        long id2 = -1;
        boolean found = false;
        for(int i = 0; i < MainActivity.userPlaylists.size(); i++)
        {
            if(MainActivity.userPlaylists.get(i).getName().equals("Favorites"))
            {
                found = true;
                break;
            }
        }
        if(found == false)
        {
            id2 = createPlaylist(resolver, "Favorites");
            MainActivity.userPlaylists.add(new MusicList("Favorites", id2));
            MainActivity.playLists.add(id2);
        }

        cur.close();
        return count;
    }

    public void reloadMusicListfromPlayList() {
        this.musicsList.clear();

        String[] proj =
                {
                        MediaStore.Audio.Playlists.Members.AUDIO_ID,
                        MediaStore.Audio.Playlists.Members.ARTIST,
                        MediaStore.Audio.Playlists.Members.TITLE,
                        MediaStore.Audio.Playlists.Members._ID
                };

        Cursor mCursor = MainActivity.mainActivity.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", id),
                proj, null, null, null);

        if(mCursor != null)
        {
            mCursor.moveToFirst();
            while(mCursor.moveToNext())
            {
                for(int i = 0; i < MainActivity.globalMusicList.musicListSize(); i++)
                {
                    if(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)) == Long.valueOf(MainActivity.globalMusicList.getMusicFromList(i).getId()))
                    {
                        this.musicsList.add(MainActivity.globalMusicList.getMusicFromList(i));
                    }
                }
            }
        }

    }



    public static long getPlaylist(ContentResolver resolver, String name)
    {
        long id = -1;

        Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Playlists._ID},
                MediaStore.Audio.Playlists.NAME + "=?",
                new String[]{name}, null);

        if (cursor != null) {
            if (cursor.moveToNext())
                id = cursor.getLong(0);
            cursor.close();
        }

        return id;
    }


    public static long createPlaylist(ContentResolver resolver, String name)
    {
        long id = getPlaylist(resolver, name);

        if (id == -1) {
            // We need to create a new playlist.
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.NAME, name);
            Uri uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
            MainActivity.userPlaylists.add(new MusicList(name,Long.parseLong(uri.getLastPathSegment())));
			/* Creating the playlist may fail due to race conditions or silly
			 * android bugs (i am looking at you, kitkat!). In this case, id will stay -1
			 */
            if (uri != null) {
                id = Long.parseLong(uri.getLastPathSegment());
            }
        } else {
            // We are overwriting an existing playlist. Clear existing songs.
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
            resolver.delete(uri, null, null);
        }


        return id;
    }

    public int addMusicToPlaylist(ContentResolver resolver, long playlistId, Music music)
    {
        if (playlistId == -1)
            return -1;

        // Find the greatest PLAY_ORDER in the playlist
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        String[] projection = new String[] { MediaStore.Audio.Playlists.Members.PLAY_ORDER };
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        int base = 0;
        if (cursor.moveToLast())
            base = cursor.getInt(0) + 1;
        cursor.close();

        ContentValues value = new ContentValues(2);
        value.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base));
        value.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, Long.valueOf(music.getId()));
        resolver.insert(uri, value);
        return 0;
    }

    public static int findplaylist(String name)
    {
        for(int i = 0; i < MainActivity.userPlaylists.size(); i++)
            if(MainActivity.userPlaylists.get(i).getName().equals(name))
                return i;
        return -1;
    }

    /**
     * Delete the playlist with the given id.
     *
     * @param resolver A ContentResolver to use.
     * @param id The Media.Audio.Playlists id of the playlist.
     */
    public static void deletePlaylist(ContentResolver resolver, long id)
    {
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
        resolver.delete(uri, null, null);
    }

    /**
     * Rename the playlist with the given id.
     *
     * @param resolver A ContentResolver to use.
     * @param id The Media.Audio.Playlists id of the playlist.
     * @param newName The new name for the playlist.
     */
    public static void renamePlaylist(ContentResolver resolver, long id, String newName)
    {
        long existingId = getPlaylist(resolver, newName);
        // We are already called the requested name; nothing to do.
        if (existingId == id)
            return;
        // There is already a playlist with this name. Kill it.
        if (existingId != -1)
            deletePlaylist(resolver, existingId);

        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Audio.Playlists.NAME, newName);
        resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values, "_id=" + id, null);
    }

    public void removeFromPlaylist(ContentResolver resolver, Music music) {
        String[] cols = new String[] {
                "count(*)"
        };
        Log.w("ID",String.valueOf(this.id));
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", this.id);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();

        resolver.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID + " = " + music.getId(), null);
    }

    public ArrayList<Music> getMusicList(){
        return this.musicsList;
    }

    public Music getMusicFromList(int id){
        return this.musicsList.get(id);
    }

    public String getName() {
        return name;
    }

    public long getID()
    {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * Class to filter files which are having .mp3 extension
     * */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }

    public int musicListSize () { return this.musicsList.size(); }
    public void appendMusic(Music music){
        ContentResolver cr = MainActivity.mainActivity.getContentResolver();
        addMusicToPlaylist(cr, this.id, music);
        this.musicsList.add(music);
    }
    public void removeMusic(Music music){
        ContentResolver cr = MainActivity.mainActivity.getContentResolver();
        removeFromPlaylist(cr,music);
        this.musicsList.remove(music);
    }

    public void sortMusicList(int sortBy){
        //TODO
    }
}
