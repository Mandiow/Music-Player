package okidoki.musicplayer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

import okidoki.musicplayer.MainActivity;
import okidoki.musicplayer.R;
import okidoki.musicplayer.classes.MusicList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SongInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SongInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static ImageButton optionsImageButton;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SongInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SongInfoFragment newInstance() {
        SongInfoFragment fragment = new SongInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SongInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songinfo,container,false);
        optionsImageButton = (ImageButton)view.findViewById(R.id.optionsButton);
        optionsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select one Option")
                        .setItems(R.array.MusicOptions, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.mainActivity);
                                        String[] arrayOfPlaylists = new String[MainActivity.userPlaylists.size() + 1];
                                        for (int i = 0; i < MainActivity.userPlaylists.size(); i++)
                                            arrayOfPlaylists[i] = MainActivity.userPlaylists.get(i).getName();
                                        final int maxID = MainActivity.userPlaylists.size();
                                        arrayOfPlaylists[maxID] = "New Playlist";
                                        alert.setTitle("Add to Playlist")
                                                .setItems(arrayOfPlaylists, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //When one of the possible lists is selected;
                                                        if (which != maxID) {
                                                            MainActivity.userPlaylists.get(which).appendMusic(PlayerFragment.musicPlayer.musicQueue.getMusicFromList(PlayerFragment.musicPlayer.currentSong));
                                                            // Add to the designed playlist
                                                        } else {
                                                            //Show's up the create playlist thing and add the element to this new playlist
                                                            AlertDialog.Builder alert2 = new AlertDialog.Builder(MainActivity.mainActivity);

                                                            alert2.setTitle("Creating new Playlist");
                                                            alert2.setMessage("Enter the name of the new playlist:");

                                                            // Set an EditText view to get user input
                                                            final EditText input = new EditText(MainActivity.mainActivity);
                                                            alert2.setView(input);

                                                            alert2.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                                    String value = input.getText().toString();
                                                                    long plID = MusicList.createPlaylist(MainActivity.mainActivity.getContentResolver(), value);
                                                                    //Now add the music to this playlist
                                                                    for (int i = 0; i < MainActivity.userPlaylists.size(); i++) {
                                                                        if (MainActivity.userPlaylists.get(i).getID() == plID) {
                                                                            MainActivity.userPlaylists.get(i).appendMusic(PlayerFragment.musicPlayer.musicQueue.getMusicFromList(PlayerFragment.musicPlayer.currentSong));
                                                                            break;
                                                                        }
                                                                    }

                                                                }
                                                            });
                                                            alert2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                                    // Canceled.
                                                                }
                                                            });
                                                            alert2.show();
                                                        }
                                                    }
                                                });
                                        alert.show();
                                        break;
                                    case 1:
                                        Toast.makeText(MainActivity.mainActivity, "Edit Info ZZZ", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 2:
                                        Intent intent = new Intent();
                                        intent.setAction(android.content.Intent.ACTION_SEND);
                                        File file = new File(PlayerFragment.musicPlayer.musicQueue.getMusicFromList(PlayerFragment.musicPlayer.currentSong).getFilePath());
                                        intent.setDataAndType(Uri.fromFile(file), "audio/*");
                                        startActivity(intent);
                                        break;
                                    case 3:
                                        // Launches thing to delete playlist
                                        // Use the Builder class for convenient dialog construction
                                        AlertDialog.Builder alert3 = new AlertDialog.Builder(getActivity());
                                        alert3.setMessage("Are you sure that you want to delete the music?")
                                                .setPositiveButton("Delete Music", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        PlayerFragment.musicPlayer.musicQueue.removeFromPlaylist(MainActivity.mainActivity.getContentResolver(), PlayerFragment.musicPlayer.musicQueue.getMusicFromList(PlayerFragment.musicPlayer.currentSong));
                                                        PlayerFragment.musicPlayer.musicQueue.removeMusic(PlayerFragment.musicPlayer.musicQueue.getMusicFromList(PlayerFragment.musicPlayer.currentSong));
                                                        MusicFragment.musicListAdapter.notifyDataSetChanged();
                                                        Toast.makeText(MainActivity.mainActivity, "Music was deleted ):", Toast.LENGTH_SHORT);
                                                    }
                                                })
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // User cancelled the dialog
                                                    }
                                                });
                                        // Create the AlertDialog object and return it
                                        alert3.show();
                                        break;
                                }
                            }
                        });
                builder.show();
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
