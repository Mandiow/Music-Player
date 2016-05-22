package okidoki.musicplayer.classes;

import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
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
import okidoki.musicplayer.external.BroadCastReciever;
import okidoki.musicplayer.fragments.MusicFragment;
import okidoki.musicplayer.fragments.PlayerFragment;


public class MusicPlayer extends Service{


    public MediaPlayer mediaPlayer = new MediaPlayer();
    private AudioManager audioManager;
    public static MusicList musicQueue;
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

    private NotificationManager mNotificationManager;
    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        repeatState = RepeatStates.RepeatAll;
        musicQueue = MainActivity.globalMusicList;
        //alreadyPlayedMusics = new boolean[musicQueue.getMusicList().size()];


    }
    NotificationCompat.Builder bBuilder;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

// here to show that your service is running foreground
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent bIntent = new Intent(this, MainActivity.class);

        //Creation of the notifications Intents
        PendingIntent pbIntent = PendingIntent.getActivity(this, 0, bIntent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent rewindIntent = BroadCastReciever.buildReceiverPendingIntent(
                this,
                BroadCastReciever.ACTION_TYPE.REWIND
        );
        PendingIntent pauseIntent = BroadCastReciever.buildReceiverPendingIntent(
                this,
                BroadCastReciever.ACTION_TYPE.PAUSE_RESUME
        );
        PendingIntent forwardIntent = BroadCastReciever.buildReceiverPendingIntent(
                this,
                BroadCastReciever.ACTION_TYPE.FORWARD
        );
        bBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon512)
                        .setContentTitle("OkDok Player")
                        .setContentText(songTitle)
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setContentIntent(pbIntent)
                        .addAction(R.drawable.ic_rewind, " ", rewindIntent)
                        .addAction(R.drawable.ic_pause," ",pauseIntent)
                        .addAction(R.drawable.ic_forward, " ", forwardIntent)
        ;
        if (MusicFragment.musicList != null){
            bBuilder.setLargeIcon(MusicFragment.musicList.getMusicFromList(currentSong).getAlbum().getBitmap());
        }

        this.startForeground(1,  bBuilder.build());
        //Toast.makeText(MainActivity.mainActivity.getApplicationContext(), "this is my Toast message!!! =)",
        //      Toast.LENGTH_LONG).show();

// here the body of your service where you can arrange your reminders and send alerts
        return START_STICKY;

    }


    public void  playSong(int songIndex, boolean user,boolean calledbyOnCompletion){
        if(MusicFragment.musicList != null && user){
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
            Intent intent =  new Intent(MainActivity.mainActivity.getApplicationContext(), MainActivity.class);
            if (intent != null) {
                PendingIntent.getActivity(MainActivity.mainActivity.getApplicationContext(), 0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

            }
            // Changing Button Image to pause image
            currentSong = songIndex;
            String Artist = MusicFragment.musicList.getMusicFromList(currentSong).getAlbum().getArtist();
            bBuilder.setContentText(songTitle).setLargeIcon(MainActivity.globalAlbumList.getAlbumNameFromArtist(Artist).getBitmap());

            mNotificationManager.notify(1, bBuilder.build());


           } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    public class NoisyAudioStreamReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w("AQUI:", "ERA PRA TER IDO");
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                if (mediaPlayer.isPlaying()) {

                    mediaPlayer().pause();
                }
            }
        }}

    public void UpdateCurrentSong(int update)
    {
        currentSong = update;
    }
    public class LocalBinder extends Binder {
        public MusicPlayer getService() {
            return MusicPlayer.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public MediaPlayer mediaPlayer() {
        return mediaPlayer;
    }
}
