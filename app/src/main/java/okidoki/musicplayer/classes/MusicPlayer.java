package okidoki.musicplayer.classes;

import android.app.FragmentManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.nio.channels.AlreadyConnectedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.Toast;

import okidoki.musicplayer.MainActivity;
import okidoki.musicplayer.R;
import okidoki.musicplayer.fragments.MusicFragment;
import okidoki.musicplayer.fragments.PlayerFragment;


public class MusicPlayer extends Service{
    public MediaPlayer mediaPlayer;
    public MusicList musicQueue;
    public int currentSong = 0;
    public boolean[] alreadyPlayedMusics;
    public Boolean got = false;
    public String songTitle;

    public enum RepeatStates {
        DoNotRepeat("Do Not Repeat", 0),
        RepeatOne("Reapeat One", 1),
        RepeatAll("Repeat All", 2);

        private String stringValue;
        private int intValue;
        RepeatStates(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    public RepeatStates repeatState;
    public Boolean isShuffle = false;


    public MusicPlayer() {
        super();
        repeatState = RepeatStates.RepeatAll;
        musicQueue = MainActivity.globalMusicList;
        alreadyPlayedMusics = new boolean[musicQueue.getMusicList().size()];
        mediaPlayer = new MediaPlayer();
    }

    public void  playSong(int songIndex, boolean user,boolean calledbyOnCompletion){
        if(MusicFragment.musicList != null && user){
            musicQueue = null;
            musicQueue = MusicFragment.musicList;
            alreadyPlayedMusics = new boolean[musicQueue.musicListSize()];
        }
        if(!calledbyOnCompletion) {
            if (isShuffle) {
                Random rand = new Random();
                songIndex = rand.nextInt(musicQueue.musicListSize() - 1);
            } else {
                if (songIndex < 0)
                    songIndex = musicQueue.musicListSize() - 1;
                if (songIndex > musicQueue.musicListSize() - 1)
                    songIndex = 0;
            }
        }
        try {

            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    alreadyPlayedMusics[currentSong] = true;

                    switch(repeatState)
                    {
                        case DoNotRepeat:
                            if(isShuffle)
                            {
                                List<Integer> possibleNextMusic = new ArrayList<Integer>();
                                for(int i = 0; i < musicQueue.musicListSize(); i++)
                                {
                                    possibleNextMusic.add(i);
                                }
                                Random rand = new Random();
                                currentSong = rand.nextInt(possibleNextMusic.size());
                                playSong(currentSong,false, true);
                            }
                            else {
                                if (currentSong < (musicQueue.musicListSize() - 1))
                                {
                                    playSong(currentSong + 1,false, true);
                                }
                            }
                            break;
                        case RepeatAll:
                            if(isShuffle)
                            {
                                // shuffle is on - play a random song
                                Random rand = new Random();
                                currentSong = rand.nextInt((musicQueue.musicListSize() - 1) - 0 + 1) + 0;
                                playSong(currentSong,false, true);
                            }
                            else
                            {
                                if (currentSong < (musicQueue.musicListSize() - 1))
                                {
                                    playSong(currentSong + 1,false, true);
                                }
                                else
                                {
                                    // play first song
                                    playSong(0,false, true);
                                }
                            }
                            break;
                        case RepeatOne:
                            playSong(currentSong,false, true);
                            break;
                    }
                }
            });
            got = true;
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicQueue.getMusicFromList(songIndex).getFilePath());

            mediaPlayer.prepare();
            mediaPlayer.start();
            // Displaying Song title
            songTitle = musicQueue.getMusicFromList(songIndex).getTitle();
            PlayerFragment.updateSongInfo(songIndex);


            // Changing Button Image to pause image
            currentSong = songIndex;

            // set Progress bar values
            //songProgressBar.setProgress(0);
            //songProgressBar.setMax(100);

            // Updating progress bar
            //updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void UpdateCurrentSong(int update)
    {
        currentSong = update;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public MediaPlayer mediaPlayer() {
        return mediaPlayer;
    }
}
