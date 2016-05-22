package okidoki.musicplayer.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okidoki.musicplayer.MainActivity;
import okidoki.musicplayer.R;
import okidoki.musicplayer.classes.MusicList;
/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class MusicFragment extends Fragment implements AbsListView.OnItemClickListener  {


    public static MusicList musicList;
    public static musicListViewAdapter musicListAdapter;
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    // TODO: Rename and change types of parameters
    public static MusicFragment newInstance() {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static MusicFragment newInstance(String param1, int i) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_SECTION_NUMBER, i);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MusicFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicList = null;
        mParam1 = getArguments().getString(ARG_PARAM1);
        if(mParam1 == "GLOBAL")
            musicList = MainActivity.globalMusicList;
        if(mParam1 == "ALBUM"){
            musicList = MainActivity.localalbum.getMusicList();
            musicList.setName(MainActivity.localalbum.getArtist());
        }
        if(mParam1 == "LIST") {
            musicList = MainActivity.playmusiclist;
        }

        musicListAdapter = new musicListViewAdapter(MainActivity.mainActivity, musicList);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_music_list, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.frag_music_list_list);
        TextView playlistTitle = (TextView) rootView.findViewById(R.id.musicListName);
        playlistTitle.setText(musicList.getName());
        TextView playlistDuration = (TextView) rootView.findViewById(R.id.totalTimeMusicList);
        long duration = 0;
        for(int i = 0; i < musicList.musicListSize();i++)
            duration+= musicList.getMusicFromList(i).getDuration();

        playlistDuration.setText(String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));

        listView.setFastScrollEnabled(true);
        listView.setAdapter(musicListAdapter);
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select one Option")
                        .setItems(R.array.MusicOptions, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.mainActivity);
                                        String[] arrayOfPlaylists = new String[MainActivity.userPlaylists.size() + 1];
                                        for(int i = 0; i < MainActivity.userPlaylists.size(); i++)
                                            arrayOfPlaylists[i] = MainActivity.userPlaylists.get(i).getName();
                                        final int maxID = MainActivity.userPlaylists.size();
                                        arrayOfPlaylists[maxID] = "New Playlist";
                                        alert.setTitle("Add to Playlist")
                                                .setItems(arrayOfPlaylists, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //When one of the possible lists is selected;
                                                        if (which != maxID) {
                                                            MainActivity.userPlaylists.get(which).appendMusic(musicList.getMusicFromList(position));
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
                                                                            MainActivity.userPlaylists.get(i).appendMusic(musicList.getMusicFromList(position));
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
                                        File file = new File(musicList.getMusicFromList(position).getFilePath());
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
                                                        musicList.removeFromPlaylist(MainActivity.mainActivity.getContentResolver(),musicList.getMusicFromList(position));
                                                        musicList.removeMusic(musicList.getMusicFromList(position));
                                                        musicListAdapter.notifyDataSetChanged();
                                                        Toast.makeText(MainActivity.mainActivity,"Music was deleted ):",Toast.LENGTH_SHORT).show();
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
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                musicList = null;
                if (mParam1 == "GLOBAL")
                    musicList = MainActivity.globalMusicList;
                if (mParam1 == "ALBUM") {
                    musicList = MainActivity.localalbum.getMusicList();
                    musicList.setName(MainActivity.localalbum.getArtist());
                }
                if (mParam1 == "LIST") {
                    musicList = MainActivity.playmusiclist;
                    //Toast.makeText(MainActivity.mainActivity,MainActivity.playmusiclist.getMusicFromList(2).getTitle(),Toast.LENGTH_SHORT).show();
                }

                PlayerFragment.musicPlayer.playSong(position, true, false);
                PlayerFragment.playImageButton.setImageResource(R.drawable.ic_pause);
                miniPlayerFragment.updatePlayImage();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
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
        MainActivity.localalbum = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.

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
    }

    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MusicFragment newInstance(int sectionNumber) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
}

class musicListViewAdapter extends BaseAdapter {
    Context context;
    MusicList adapterMusicList;
    public musicListViewAdapter(Context context, MusicList musicList){
        this.context = context;
        adapterMusicList = musicList;
    }
    @Override
    public int getCount() {
        if(adapterMusicList != null)
            return adapterMusicList.getMusicList().size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return adapterMusicList.getMusicFromList(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.song_item,parent,false);
        }
        else{
            row = convertView;
        }
        TextView titleTextView = (TextView) row.findViewById(R.id.songTitleSongList);
        TextView artistTextView = (TextView) row.findViewById(R.id.songArtistSongList);
        ImageView imageView = (ImageView) row.findViewById(R.id.albumArtSongList);

        titleTextView.setText(adapterMusicList.getMusicFromList(position).getTitle());
        artistTextView.setText(adapterMusicList.getMusicFromList(position).getArtist().getName());

        imageView.setImageBitmap(MainActivity.globalAlbumList.searchAlbum(adapterMusicList.getMusicFromList(position).getAlbum().getName()).getBitmap());

        return row;
    }
}