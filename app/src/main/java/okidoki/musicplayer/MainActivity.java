package okidoki.musicplayer;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.app.FragmentManager;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import java.util.ArrayList;
import java.util.List;

import okidoki.musicplayer.classes.Album;
import okidoki.musicplayer.classes.AlbumList;
import okidoki.musicplayer.classes.Artist;
import okidoki.musicplayer.classes.ArtistList;
import okidoki.musicplayer.classes.MusicList;
import okidoki.musicplayer.fragments.ArtistFragment;
import okidoki.musicplayer.fragments.FavoritesFragment;
import okidoki.musicplayer.fragments.MusicFragment;
import okidoki.musicplayer.fragments.NavigationDrawerFragment;
import okidoki.musicplayer.fragments.OtherFragment;
import okidoki.musicplayer.fragments.PlayerFragment;
import okidoki.musicplayer.fragments.PlaylistsFragment;
import okidoki.musicplayer.fragments.RecentlyAddedFragment;
import okidoki.musicplayer.fragments.SelectedArtistFragment;
import okidoki.musicplayer.fragments.SongInfoFragment;
import okidoki.musicplayer.fragments.miniPlayerFragment;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        ArtistFragment.OnArtistFragmentInteractionListener,
        SelectedArtistFragment.OnSelectedFragmentInteractionListener,
        PlaylistsFragment.OnPlaylistFragmentInteractionListener
{
    public static MusicList playmusiclist ;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private SlidingUpPanelLayout slidingFragment;
    public static MusicList globalMusicList = new MusicList();
    public static ArtistList globalArtistList = new ArtistList();
    public static AlbumList globalAlbumList = new AlbumList();
    public static AlbumList ArtistAlbums = new AlbumList();
    public static List<Long> playLists = new ArrayList<>();
    public static List<MusicList> userPlaylists = new ArrayList<>();
    public static Artist localartist;
    public static Album  localalbum;
    public static final int SCRREN_ARTIST = 1;
    public static final int SCREEN_MUSICD = 2;
    public static final int SCREEN_ALBUM = 3;
    public static final int SCREEN_PLAYLIST = 4;
    public static final int SCREEN_FAVORITES = 5;
    public static final int SCREEN_RECENTLYADDED = 6;
    public static final int SCREEN_OTHERS = 7;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    public static boolean slidingShowing = false;
    private CharSequence mTitle;
    public static MainActivity mainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_main);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        if(globalMusicList.musicListSize() == 0) {
            globalMusicList.searchForMusics();
            MusicList.queryPlaylists(getContentResolver());
            Log.w("Playlist size",String.valueOf(playLists.size()));
        }
        for (int i = 0; i < userPlaylists.size(); i++)
            userPlaylists.get(i).reloadMusicListfromPlayList();
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        slidingFragment = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.MiniPlayerSongInfoContainer, miniPlayerFragment.newInstance())
                .commit();
        slidingFragment.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                FragmentManager fragmentManager = getFragmentManager();
                ActionBar ab = getSupportActionBar();

                if (v >= 0.4) {
                    if (ab.isShowing() && slidingShowing == false) {
                        slidingShowing = true;

                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                                .replace(R.id.MiniPlayerSongInfoContainer, SongInfoFragment.newInstance())
                                .commit();
                    }
                    ab.hide();
                    PlayerFragment.updateSongInfo(PlayerFragment.musicPlayer.currentSong);
                } else if (v <= 0.6) {
                    if (!ab.isShowing() && slidingShowing == true) {
                        slidingShowing = false;
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                                .replace(R.id.MiniPlayerSongInfoContainer, miniPlayerFragment.newInstance())
                                .commit();
                    }
                    ab.show();
                    PlayerFragment.updateSongInfo(PlayerFragment.musicPlayer.currentSong);
                }
            }

            @Override
            public void onPanelCollapsed(View view) {

            }

            @Override
            public void onPanelExpanded(View view) {

            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {
                slidingFragment.setAnchorPoint(10);

            }
        });

        PlayerFragment.updateSongInfo(PlayerFragment.musicPlayer.currentSong);

    }

    @Override
    public void OnSelectedFragmentInteraction(Album albumFromList){
        localalbum = albumFromList;
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.container, MusicFragment.newInstance("ALBUM", 2))
                .addToBackStack("MusicListFragment")
                .commit();


    }
    @Override
    public void OnArtistFragmentInteraction(Artist artist){
        ArtistAlbums = artist.getAlbumList();
        localartist = artist;
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.container, SelectedArtistFragment.newInstance())
                .addToBackStack("AlbumListFragment")
                .commit();



    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        switch (position)
        {
            case 0:
                MainActivity.ArtistAlbums = null;
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.container, ArtistFragment.newInstance(position + 1))
                        .addToBackStack("ArtistsFragment")
                        .commit();
                //actionBar.setTitle(getResources().getStringArray(R.array.Screens)[position]);
                break;
            case 1:
                MainActivity.localalbum = null;
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.container, MusicFragment.newInstance("GLOBAL", position + 1))
                        .addToBackStack("MusicListFragment")
                        .commit();
                break;
            case 2:
                MainActivity.localartist = null;
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.container, SelectedArtistFragment.newInstance(position + 1))
                        .addToBackStack("AlbumListFragment")
                        .commit();
                break;
            case 3:
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.container, PlaylistsFragment.newInstance(position + 1))
                        .addToBackStack("PlaylistsFragment")
                        .commit();
                break;
            case 4:
                playmusiclist = MainActivity.userPlaylists.get(MusicList.findplaylist("Favorites"));
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.container, MusicFragment.newInstance("LIST",position+1))
                        .addToBackStack("FavoritesFragment")
                        .commit();
                break;
            case 5:
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.container, RecentlyAddedFragment.newInstance(position + 1))
                        .addToBackStack("RecentlyAddedFragment")
                        .commit();
                break;
            case 6:
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.container, OtherFragment.newInstance(position + 1))
                        .addToBackStack("OthersFragment")
                        .commit();
                //actionBar.setTitle(getResources().getStringArray(R.array.Screens)[position]);
                break;

        }


    }

    @Override
    public void onBackPressed()
    {
        if(getFragmentManager().popBackStackImmediate())
            return;
        else
            super.onBackPressed();
    }

    public void onSectionAttached(int number) {
        String [] tempArray;
        tempArray = getResources().getStringArray(R.array.Screens);
        mTitle = tempArray[number-1];
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);

            restoreActionBar();
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                //openSearch();
                return true;
            case R.id.action_settings:
                //openSettings();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnPlaylistFragmentInteraction(int position) {

        playmusiclist = userPlaylists.get(position);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.container, MusicFragment.newInstance("LIST", position + 1))
                .addToBackStack("MusicListFragment")
                .commit();

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}