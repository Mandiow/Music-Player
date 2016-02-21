package okidoki.musicplayer.external;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import okidoki.musicplayer.fragments.PlayerFragment;

/**
 * Created by Mandiow on 01/02/2016.
 */
public class BroadCastReciever extends BroadcastReceiver {


    private static final String TYPE = "ACTION_TYPE";

    public enum ACTION_TYPE{
        REWIND,
        PAUSE_RESUME,
        FORWARD
    }

    public static PendingIntent buildReceiverPendingIntent(Context context,ACTION_TYPE type){
        Intent intent = new Intent(context.getApplicationContext(),BroadCastReciever.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(TYPE, type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,type.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        ACTION_TYPE type =(ACTION_TYPE)intent.getExtras().getSerializable(TYPE);
        Log.v("AQUI:", type.name());
        switch (type){
            case REWIND:
                PlayerFragment.musicPlayer.playSong(PlayerFragment.musicPlayer.currentSong - 1, false, false);

                break;
            case FORWARD:
                PlayerFragment.musicPlayer.playSong(PlayerFragment.musicPlayer.currentSong + 1, false, false);
                break;
            case PAUSE_RESUME:
                if (PlayerFragment.musicPlayer.mediaPlayer.isPlaying()) {

                    PlayerFragment.musicPlayer.mediaPlayer().pause();
                    // Changing button image to play button
                } else {
                    if (PlayerFragment.musicPlayer.got == false) {
                        PlayerFragment.musicPlayer.playSong(0, false, false);
                    } else {
                        // Resume song

                        PlayerFragment.musicPlayer.mediaPlayer().start();
                    }

                }
                PlayerFragment.updateSongInfo(PlayerFragment.musicPlayer.currentSong);
                break;
        }

    }
}
