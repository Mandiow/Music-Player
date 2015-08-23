package okidoki.musicplayer.fragments;

import okidoki.musicplayer.MainActivity;
import okidoki.musicplayer.R;
import okidoki.musicplayer.classes.Music;
import okidoki.musicplayer.classes.MusicList;
import okidoki.musicplayer.classes.MusicPlayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends android.support.v4.app.Fragment implements MediaPlayer.OnCompletionListener,SeekBar.OnSeekBarChangeListener, Runnable {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static MusicPlayer musicPlayer = new MusicPlayer();
    private ListView mDrawerListView;
    private static Bitmap bitmap;
    public static ImageButton playImageButton;
    private ImageButton pauseImageButton;
    private ImageButton forwardImageButton;
    private ImageButton rewindImageButton;
    private static ImageButton favoriteImageButton;
    private static ImageButton repeatImageButton;
    private static ImageButton shuffleImageButton;
    public static SeekBar progressBar;
    private MusicList musicList;
    private static View view;
    private Handler seekHandler = new Handler();
    private static int lastID;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerFragment newInstance() {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Creating lovely buttons and instances
        musicList = MainActivity.globalMusicList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_player, container, false);

        lastID = -1;
        Thread currentThread  = new Thread(this);
        currentThread.start();

        playImageButton = (ImageButton)view.findViewById(R.id.PlayButton);
        playImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (musicPlayer.mediaPlayer.isPlaying()) {

                    musicPlayer.mediaPlayer().pause();
                    // Changing button image to play button
                    playImageButton.setImageResource(R.drawable.ic_play);

                } else {
                    if (musicPlayer.got == false) {
                        musicPlayer.playSong(musicPlayer.currentSong, false, false);
                    } else {
                        // Resume song

                        musicPlayer.mediaPlayer().start();
                    }
                    // Changing button image to pause button
                    playImageButton.setImageResource(R.drawable.ic_pause);
                }
                updateSongInfo(musicPlayer.currentSong);
            }

        });


        rewindImageButton = (ImageButton)view.findViewById(R.id.rewindButton);
        rewindImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicPlayer.mediaPlayer.isPlaying()) {
                    musicPlayer.playSong(musicPlayer.currentSong - 1,false, false);
                    updateSongInfo(musicPlayer.currentSong);
                }else{
                    updateSongInfo(musicPlayer.currentSong - 1);
                    musicPlayer.UpdateCurrentSong(musicPlayer.currentSong - 1);
                    musicPlayer.got = false;

                }
            }
        });
        forwardImageButton = (ImageButton)view.findViewById(R.id.forwardButton);
        forwardImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicPlayer.mediaPlayer.isPlaying()) {
                    musicPlayer.playSong(musicPlayer.currentSong + 1,false, false);
                    updateSongInfo(musicPlayer.currentSong);
                }else{
                    updateSongInfo(musicPlayer.currentSong + 1);
                    musicPlayer.UpdateCurrentSong(musicPlayer.currentSong + 1);
                    musicPlayer.got = false;

                }

            }
        });
        favoriteImageButton = (ImageButton)view.findViewById(R.id.hearted);
        favoriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicPlayer.musicQueue.getMusicFromList(musicPlayer.currentSong).getIsHearted()){
                    favoriteImageButton.setImageResource(R.drawable.ic_notfavorite);
                    musicPlayer.musicQueue.getMusicFromList(musicPlayer.currentSong).setIsHearted(false);
                    MainActivity.userPlaylists.get(MusicList.findplaylist("Favorites")).removeMusic(
                            musicPlayer.musicQueue.getMusicFromList(musicPlayer.currentSong)
                    );

                }else{
                    favoriteImageButton.setImageResource(R.drawable.ic_favorite);
                    musicPlayer.musicQueue.getMusicFromList(musicPlayer.currentSong).setIsHearted(true);
                    MainActivity.userPlaylists.get(MusicList.findplaylist("Favorites")).appendMusic(
                            musicPlayer.musicQueue.getMusicFromList(musicPlayer.currentSong)
                    );

                }

            }
        });


        repeatImageButton = (ImageButton)view.findViewById(R.id.RepeatButton);
        repeatImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(musicPlayer.repeatState)
                {
                    case RepeatAll:
                            musicPlayer.repeatState = MusicPlayer.RepeatStates.RepeatOne;
                            musicPlayer.alreadyPlayedMusics = new boolean[musicPlayer.musicQueue.musicListSize()];
                            repeatImageButton.setImageResource(R.drawable.ic_repeatone);
                            Toast.makeText(MainActivity.mainActivity,"Repeating this music. :)",Toast.LENGTH_SHORT).show();
                        break;
                    case RepeatOne:
                            musicPlayer.repeatState = MusicPlayer.RepeatStates.DoNotRepeat;
                            repeatImageButton.setImageResource(R.drawable.ic_notrepeat);
                            Toast.makeText(MainActivity.mainActivity,"Repeating is now off.",Toast.LENGTH_SHORT).show();
                        break;
                    case DoNotRepeat:
                            musicPlayer.repeatState = MusicPlayer.RepeatStates.RepeatAll;
                            musicPlayer.alreadyPlayedMusics = new boolean[musicPlayer.musicQueue.musicListSize()];
                            repeatImageButton.setImageResource(R.drawable.ic_repeat);
                            Toast.makeText(MainActivity.mainActivity,"Repeating all musics.",Toast.LENGTH_SHORT).show();

                        break;
                }

            }
        });
        shuffleImageButton = (ImageButton)view.findViewById(R.id.shuffleButton);
        shuffleImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicPlayer.isShuffle)
                {
                    musicPlayer.isShuffle = false;
                    shuffleImageButton.setImageResource(R.drawable.ic_linear);
                    Toast.makeText(MainActivity.mainActivity,"Shuffle is off!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    musicPlayer.isShuffle = true;
                    shuffleImageButton.setImageResource(R.drawable.ic_random);
                    Toast.makeText(MainActivity.mainActivity,"Shuffle is on!",Toast.LENGTH_SHORT).show();
                }
            }
        });


        progressBar = (SeekBar) view.findViewById(R.id.seekBar);
        progressBar.setOnSeekBarChangeListener(this);
        return view;
    }

    public static void updateSongInfo(int id) {

        // Update for elements of song information fragment
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextInfo);
        TextView artistTextView = (TextView) view.findViewById(R.id.artistTextInfo);
        if (titleTextView != null && artistTextView != null) {
            titleTextView.setText(musicPlayer.musicQueue.getMusicFromList(id).getTitle());
            artistTextView.setText(musicPlayer.musicQueue.getMusicFromList(id).getArtist().getName());
        }
        // Update for mini-player in the app
        TextView titleTextView2 = (TextView) view.findViewById(R.id.titleMiniPlayer);
        TextView artistTextView2 = (TextView) view.findViewById(R.id.artistMiniPlayer);
        if (titleTextView2 != null && artistTextView2 != null) {
            titleTextView2.setText(musicPlayer.musicQueue.getMusicFromList(id).getTitle());
            artistTextView2.setText(musicPlayer.musicQueue.getMusicFromList(id).getArtist().getName());
            miniPlayerFragment.updatePlayImage();
        }
        if(musicPlayer.mediaPlayer.isPlaying())
            playImageButton.setImageResource(R.drawable.ic_pause);
        else
            playImageButton.setImageResource(R.drawable.ic_play);


        if (id != lastID) {
            if(bitmap != null)
                bitmap.recycle();
            bitmap = null;
            //Generates the high quality bitmap for the current song (it is expensive, but it is used just once)
            ImageView albumArtView = (ImageView) view.findViewById(R.id.AlbumButton);
            File musicFile = new File(musicPlayer.musicQueue.getMusicFromList(id).getFilePath());
            File musicFolder = new File(musicFile.getParent());
            File[] filesList = musicFolder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.toLowerCase().endsWith(".jpg");
                }
            });
            if (filesList.length != 0) {

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                bitmap = BitmapFactory.decodeFile(filesList[0].getAbsolutePath(), bmOptions);

                // Calculate inSampleSize
                int height_px = MainActivity.mainActivity.getResources().getDisplayMetrics().heightPixels;
                int width_px = MainActivity.mainActivity.getResources().getDisplayMetrics().widthPixels;
                int density = MainActivity.mainActivity.getResources().getDisplayMetrics().densityDpi;

                bmOptions.inSampleSize = calculateInSampleSize(bmOptions,
                        (height_px / 350) * density,
                        width_px);

                // Decode bitmap with inSampleSize set
                bmOptions.inJustDecodeBounds = false;
                Log.w("samplesize",Integer.toString(bmOptions.inSampleSize));


                bitmap = BitmapFactory.decodeFile(filesList[0].getAbsolutePath(), bmOptions);
                albumArtView.setImageBitmap(bitmap);
            } else
            {
                if(musicPlayer.musicQueue.getMusicFromList(id).getAlbum().getHasBitmap()){
                    albumArtView.setImageBitmap(musicPlayer.musicQueue.getMusicFromList(id).getAlbum().getBitmap());
                }
                else
                    albumArtView.setImageResource(R.drawable.album256);
            }

            lastID = id;

            progressBar.setProgress(0);
            progressBar.setMax(musicPlayer.mediaPlayer.getDuration());
            TextView totaltime = (TextView) view.findViewById(R.id.totalTimePlayerFragment);
            totaltime.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(musicPlayer.mediaPlayer.getDuration()) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(musicPlayer.mediaPlayer.getDuration())),
                    TimeUnit.MILLISECONDS.toSeconds(musicPlayer.mediaPlayer.getDuration()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(musicPlayer.mediaPlayer.getDuration()))));
            if (musicPlayer.musicQueue.getMusicFromList(id).getIsHearted()) {
                favoriteImageButton.setImageResource(R.drawable.ic_favorite);
            } else {

                favoriteImageButton.setImageResource(R.drawable.ic_notfavorite);
            }
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        Log.w("reqWitdh",Integer.toString(reqWidth));
        Log.w("reqHeight",Integer.toString(reqHeight));
        Log.w("width",Integer.toString(width));
        Log.w("height",Integer.toString(height));
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height;
            final int halfWidth = width;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = MainActivity.mainActivity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }
    Runnable run = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }};


    public void seekUpdation() {
        progressBar.setProgress(musicPlayer.mediaPlayer.getCurrentPosition());
        seekHandler.postDelayed(run, 1000);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int pos, boolean user) {
        if(user){
            musicPlayer.mediaPlayer.seekTo(pos);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        TextView titleTextView = (TextView) view.findViewById(R.id.currentTimePlayerFragment);
        titleTextView.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(musicPlayer.mediaPlayer.getCurrentPosition()) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(musicPlayer.mediaPlayer.getCurrentPosition())),
                TimeUnit.MILLISECONDS.toSeconds(musicPlayer.mediaPlayer.getCurrentPosition()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(musicPlayer.mediaPlayer.getCurrentPosition()))));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void run() {
        while (musicPlayer.mediaPlayer != null) {
            try {
                int currentPosition = musicPlayer.mediaPlayer.getCurrentPosition();
                Message msg = new Message();
                msg.what = currentPosition;
                if(musicPlayer.mediaPlayer().isPlaying())
                    threadHandler.sendMessage(msg);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private Handler threadHandler = new Handler(){
        public void handleMessage(Message msg){
            //super.handleMessage(msg);
            //txt.setText(Integer.toString(msg.what));
            progressBar.setProgress(msg.what);
            TextView titleTextView = (TextView) view.findViewById(R.id.currentTimePlayerFragment);
            titleTextView.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(msg.what) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(msg.what)),
                    TimeUnit.MILLISECONDS.toSeconds(msg.what) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(msg.what))));
        }


    };

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}