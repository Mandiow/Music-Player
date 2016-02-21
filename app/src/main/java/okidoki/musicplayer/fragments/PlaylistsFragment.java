package okidoki.musicplayer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import okidoki.musicplayer.MainActivity;
import okidoki.musicplayer.R;
import okidoki.musicplayer.classes.MusicList;
import okidoki.musicplayer.fragments.dummy.DummyContent;

import static android.widget.AdapterView.*;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class PlaylistsFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private PlaylistViewAdapter playlistViewAdapter;
    private LinearLayout AddplayListButton;

    private OnPlaylistFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static PlaylistsFragment newInstance(String param1, String param2) {
        PlaylistsFragment fragment = new PlaylistsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlaylistsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        playlistViewAdapter = new PlaylistViewAdapter(MainActivity.mainActivity, MainActivity.userPlaylists);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists_list, container, false);

        // Set the adapter
        ListView listView = (ListView) view.findViewById(R.id.frag_playlist_list_list);
        listView.setFastScrollEnabled(true);
        listView.setAdapter((ListAdapter) playlistViewAdapter);
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(MainActivity.userPlaylists.get(position).getName()+" Playlist Options")
                        .setItems(R.array.PlaylistOptions, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.mainActivity);

                                        alert.setTitle("Editing Playlist Name");
                                        alert.setMessage("Enter the new name for the playlist:");

                                        // Set an EditText view to get user input
                                        final EditText input = new EditText(MainActivity.mainActivity);
                                        alert.setView(input);

                                        alert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                String value = input.getText().toString();
                                                MusicList.renamePlaylist(MainActivity.mainActivity.getContentResolver(), MainActivity.userPlaylists.get(position).getID(), value);
                                                MainActivity.userPlaylists.get(position).setName(value);
                                                playlistViewAdapter.notifyDataSetChanged();
                                            }
                                        });

                                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                // Canceled.
                                            }
                                        });

                                        alert.show();
                                        break;
                                    case 1:
                                        // Launches thing to delete playlist
                                        // Use the Builder class for convenient dialog construction
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                                        builder2.setMessage("Are you sure that you want to delete the playlist?")
                                                .setPositiveButton("Delete Playlist", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        MusicList.deletePlaylist(MainActivity.mainActivity.getContentResolver(), MainActivity.userPlaylists.get(position).getID());
                                                        MainActivity.userPlaylists.remove(position);
                                                        playlistViewAdapter.notifyDataSetChanged();
                                                        Toast.makeText(MainActivity.mainActivity,"Playlist was deleted ):",Toast.LENGTH_SHORT);

                                                    }
                                                })
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // User cancelled the dialog
                                                    }
                                                });
                                        // Create the AlertDialog object and return it
                                        builder2.show();
                                        break;
                                }
                            }
                        });
                builder.show();
                return true;
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                mListener.OnPlaylistFragmentInteraction(position);
            }
        });
        AddplayListButton = (LinearLayout) view.findViewById(R.id.plus_btn);

        AddplayListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.mainActivity);

                alert.setTitle("Creating new Playlist");
                alert.setMessage("Enter the name of the new playlist:");

                // Set an EditText view to get user input
                final EditText input = new EditText(MainActivity.mainActivity);
                alert.setView(input);

                alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        MusicList.createPlaylist(MainActivity.mainActivity.getContentResolver(), value);
                        playlistViewAdapter.notifyDataSetChanged();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();


            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).onSectionAttached(4);
        try {
            mListener = (OnPlaylistFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            //
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.

        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
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
    public interface OnPlaylistFragmentInteractionListener {
        // TODO: Update argument type and name
        public void OnPlaylistFragmentInteraction(int pos);
    }
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaylistsFragment newInstance(int sectionNumber) {
        PlaylistsFragment fragment = new PlaylistsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

}

class PlaylistViewAdapter extends BaseAdapter {
    Context context;
    List<MusicList> adapterPlayList;
    public PlaylistViewAdapter(Context context, List<MusicList> musicList){
        this.context = context;
        this.adapterPlayList = musicList;
    }
    @Override
    public int getCount() {
        if(adapterPlayList != null)
            return adapterPlayList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return adapterPlayList.get(position);
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
            row = inflater.inflate(R.layout.playlist_item,parent,false);
        }
        else{
            row = convertView;
        }
        TextView titleTextView = (TextView) row.findViewById(R.id.Playlistname);
        TextView attrib = (TextView) row.findViewById(R.id.Playlistattrib);

        titleTextView.setText(adapterPlayList.get(position).getName());
        //attrib.setText(adapterPlayList.get(position).);

        return row;
    }
}
