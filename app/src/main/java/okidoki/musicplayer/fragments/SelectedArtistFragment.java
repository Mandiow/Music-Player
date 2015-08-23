package okidoki.musicplayer.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import okidoki.musicplayer.MainActivity;
import okidoki.musicplayer.R;
import okidoki.musicplayer.classes.Album;
import okidoki.musicplayer.classes.AlbumList;
import okidoki.musicplayer.classes.ArtistList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectedArtistFragment.OnSelectedFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectedArtistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectedArtistFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnSelectedFragmentInteractionListener mListener;
    private SelectedArtistListViewAdapter selectedArtistListViewAdapter;
    public AlbumList albumList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SelectedArtistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectedArtistFragment newInstance() {
        SelectedArtistFragment fragment = new SelectedArtistFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    public SelectedArtistFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumList = null;
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (MainActivity.ArtistAlbums == null)
            albumList = MainActivity.globalAlbumList;
        else
            albumList = MainActivity.ArtistAlbums;
        selectedArtistListViewAdapter = new SelectedArtistListViewAdapter(MainActivity.mainActivity, albumList);
        //albumList.getAlbumNameFromArtist(MainActivity.localartist.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_albums_grid, container, false);
        GridView listView = (GridView) view.findViewById(R.id.albumsgrid);
        listView.setFastScrollEnabled(true);
        listView.setAdapter(selectedArtistListViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                albumList = null;
                if (MainActivity.ArtistAlbums == null)
                    albumList = MainActivity.globalAlbumList;
                else
                    albumList = MainActivity.ArtistAlbums;
                mListener.OnSelectedFragmentInteraction(albumList.getAlbumFromList(position));
                //artistList.getArtistFromList(position).getAlbumList().getAlbumNameFromArtist(artistList.getArtistFromList(position).getName());
                //Toast.makeText(MainActivity.mainActivity,artistList.getArtistFromList(position).getName(),Toast.LENGTH_SHORT).show();

            }
        });
        return view ;
    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSelectedFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity.ArtistAlbums = null;
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
    public interface OnSelectedFragmentInteractionListener {
        // TODO: Update argument type and name
        public void OnSelectedFragmentInteraction(Album albumFromList);
    }
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static SelectedArtistFragment newInstance(int SectionNumber) {
        SelectedArtistFragment fragment = new SelectedArtistFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, SectionNumber);
        fragment.setArguments(args);
        return fragment;

    }
}

class SelectedArtistListViewAdapter extends BaseAdapter {
    Context context;
    AlbumList adapterSelectedArtistList;
    public SelectedArtistListViewAdapter(Context context, AlbumList albumList){
        this.context = context;
        adapterSelectedArtistList = albumList;
    }
    @Override
    public int getCount() {
        if(adapterSelectedArtistList != null)
            return adapterSelectedArtistList.getAlbumList().size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return adapterSelectedArtistList.getAlbumFromList(position);
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
            row = inflater.inflate(R.layout.album_item,parent,false);
        }
        else{
            row = convertView;
        }

        TextView artistTextView = (TextView) row.findViewById(R.id.ArtistTitleAlbumList);
        TextView AlbumTitleAlbumList = (TextView) row.findViewById(R.id.AlbumTitleAlbumList);
        ImageView imageView = (ImageView) row.findViewById(R.id.albumArtAlbumList);
        artistTextView.setText(adapterSelectedArtistList.getAlbumFromList(position).getArtist());
        AlbumTitleAlbumList.setText(adapterSelectedArtistList.getAlbumFromList(position).getName());
        imageView.setImageBitmap(adapterSelectedArtistList.getAlbumFromList(position).getBitmap());

        return row;
    }
}
