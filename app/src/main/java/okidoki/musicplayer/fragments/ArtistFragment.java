package okidoki.musicplayer.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import okidoki.musicplayer.MainActivity;
import okidoki.musicplayer.R;
import okidoki.musicplayer.classes.Artist;
import okidoki.musicplayer.classes.ArtistList;
import okidoki.musicplayer.fragments.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ArtistFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArtistList artistList;
    private ArtistListViewAdapter artistListViewAdapter;

    private OnArtistFragmentInteractionListener mListener;

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
    public static ArtistFragment newInstance(String param1, String param2) {
        ArtistFragment fragment = new ArtistFragment();
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
    public ArtistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        artistList = MainActivity.globalArtistList;
        artistListViewAdapter = new ArtistListViewAdapter(MainActivity.mainActivity, artistList);

        // TODO: Change Adapter to display your content
       // mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
         //       android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_list, container, false);

        ListView listView = (ListView) view.findViewById(R.id.frag_artist_list_list);
        listView.setFastScrollEnabled(true);
        listView.setAdapter(artistListViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                mListener.OnArtistFragmentInteraction(artistList.getArtistFromList(position));
                artistList.getArtistFromList(position).getAlbumList().getAlbumNameFromArtist(artistList.getArtistFromList(position).getName());
                //Toast.makeText(MainActivity.mainActivity,artistList.getArtistFromList(position).getName(),Toast.LENGTH_SHORT).show();

            }
        });

        // Set the adapter

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        try {
            mListener = (OnArtistFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArtistFragmentInteractionListener");
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
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            Toast.makeText(MainActivity.mainActivity,"AAA",Toast.LENGTH_SHORT).show();
            //mListener.OnArtistFragmentInteractionListener(artistList.getArtistFromList(position));
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
    public interface OnArtistFragmentInteractionListener {
        // TODO: Update argument type and name
        public void OnArtistFragmentInteraction(Artist artist);
    }
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ArtistFragment newInstance(int sectionNumber) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

}

class ArtistListViewAdapter extends BaseAdapter {
    Context context;
    ArtistList adapterArtistList;
    public ArtistListViewAdapter(Context context, ArtistList artistList){
        this.context = context;
        adapterArtistList = artistList;
    }
    @Override
    public int getCount() {
        if(adapterArtistList != null)
            return adapterArtistList.getArtistList().size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return adapterArtistList.getArtistFromList(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        int albums=0;
        int songs=0;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.artist_item,parent,false);
        }
        else{
            row = convertView;
        }
        TextView artistTextView = (TextView) row.findViewById(R.id.Artistname);
        TextView musicandalbumnumber = (TextView) row.findViewById(R.id.musicandalbumnumber);
        ImageView imageView = (ImageView) row.findViewById(R.id.albumArtSongList);
        artistTextView.setText(adapterArtistList.getArtistFromList(position).getName());
        adapterArtistList.getArtistFromList(position).getAlbumList().AlbumListSize();
        songs = adapterArtistList.getArtistFromList(position).getMusicList().musicListSize();
        musicandalbumnumber.setText(adapterArtistList.getArtistFromList(position).getAlbumList().getAlbumList().size() + " Albums" + "," + songs + " musics");
        if(adapterArtistList.getArtistFromList(position).getAlbumList().AlbumListSize() > 0)
            imageView.setImageBitmap(adapterArtistList.getArtistFromList(position).getAlbumList().getAlbumFromList(0).getBitmap());

        return row;
    }
}
