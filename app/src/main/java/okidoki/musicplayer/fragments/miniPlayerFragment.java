package okidoki.musicplayer.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import okidoki.musicplayer.MainActivity;
import okidoki.musicplayer.R;
import okidoki.musicplayer.classes.MusicList;
import okidoki.musicplayer.classes.MusicPlayer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link miniPlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link miniPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class miniPlayerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static ImageButton playImageButton;
    private ImageButton forwardImageButton;
    private ImageButton rewindImageButton;
    private static View view;
    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static miniPlayerFragment newInstance() {
        miniPlayerFragment fragment = new miniPlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public miniPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mini_player, container, false);

        playImageButton = (ImageButton)view.findViewById(R.id.playMiniPlayer);
        playImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.w("oi", "oi");
                if(PlayerFragment.musicPlayer.mediaPlayer.isPlaying()){

                        PlayerFragment.musicPlayer.mediaPlayer().pause();
                        // Changing button image to play button
                        playImageButton.setImageResource(R.drawable.ic_play);

                }else{
                    if(PlayerFragment.musicPlayer.got == false)
                    {
                        PlayerFragment.musicPlayer.playSong(PlayerFragment.musicPlayer.currentSong,false,false);
                    }else {
                        // Resume song

                        PlayerFragment.musicPlayer.mediaPlayer().start();
                    }
                    // Changing button image to pause button
                    playImageButton.setImageResource(R.drawable.ic_pause);
                }
                PlayerFragment.updateSongInfo(PlayerFragment.musicPlayer.currentSong);
            }

        });

        rewindImageButton = (ImageButton)view.findViewById(R.id.backMiniPlayer);
        rewindImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("oi", "oi");
                if(PlayerFragment.musicPlayer.mediaPlayer.isPlaying()) {
                    if (PlayerFragment.musicPlayer.currentSong == 0)
                        PlayerFragment.musicPlayer.playSong(MusicPlayer.musicQueue.musicListSize() - 1,false, true);
                    else
                        PlayerFragment.musicPlayer.playSong(PlayerFragment.musicPlayer.currentSong - 1,false, true);
                }else{
                    if (PlayerFragment.musicPlayer.currentSong == 0) {
                        PlayerFragment.updateSongInfo(MusicPlayer.musicQueue.musicListSize() - 1);
                        PlayerFragment.musicPlayer.UpdateCurrentSong(MusicPlayer.musicQueue.musicListSize() - 1);
                    }
                    else {
                        PlayerFragment.updateSongInfo(PlayerFragment.musicPlayer.currentSong - 1);
                        PlayerFragment.musicPlayer.UpdateCurrentSong(PlayerFragment.musicPlayer.currentSong - 1);
                    }
                    PlayerFragment.musicPlayer.got = false;

                }
            }
        });
        forwardImageButton = (ImageButton)view.findViewById(R.id.forwardMiniPlayer);
        forwardImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (PlayerFragment.musicPlayer.mediaPlayer.isPlaying()) {
                    PlayerFragment.musicPlayer.playSong(PlayerFragment.musicPlayer.currentSong + 1,false,true);
                    PlayerFragment.updateSongInfo(PlayerFragment.musicPlayer.currentSong);
                } else {
                    PlayerFragment.updateSongInfo(PlayerFragment.musicPlayer.currentSong + 1);
                    PlayerFragment.musicPlayer.UpdateCurrentSong(PlayerFragment.musicPlayer.currentSong + 1);
                    PlayerFragment.musicPlayer.got = false;
                }

            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public static void updatePlayImage()
    {
        playImageButton = (ImageButton)view.findViewById(R.id.playMiniPlayer);
        if(PlayerFragment.musicPlayer.mediaPlayer.isPlaying())
            playImageButton.setImageResource(R.drawable.ic_pause);
        else
            playImageButton.setImageResource(R.drawable.ic_play);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

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
