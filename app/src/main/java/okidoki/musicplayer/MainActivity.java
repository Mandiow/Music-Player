package okidoki.musicplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
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
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okidoki.musicplayer.classes.Album;
import okidoki.musicplayer.classes.AlbumList;
import okidoki.musicplayer.classes.Artist;
import okidoki.musicplayer.classes.ArtistList;
import okidoki.musicplayer.classes.MusicList;
import okidoki.musicplayer.classes.MusicPlayer;
import okidoki.musicplayer.fragments.ArtistFragment;
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
        PlaylistsFragment.OnPlaylistFragmentInteractionListener {
    public static MusicList playmusiclist;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private SlidingUpPanelLayout slidingFragment;
    public static ArtistList globalArtistList = new ArtistList();
    public static AlbumList globalAlbumList = new AlbumList();
    public static AlbumList ArtistAlbums = new AlbumList();
    public static List<Long> playLists = new ArrayList<>();
    public static List<MusicList> userPlaylists = new ArrayList<>();
    public static Artist localartist;
    public static Album localalbum;
    public static MusicList globalMusicList = new MusicList();
    public final int SCREEN_ARTIST = 1;
    public final int SCREEN_MUSICD = 2;
    public final int SCREEN_ALBUM = 3;
    public final int SCREEN_PLAYLIST = 4;
    public final int SCREEN_FAVORITES = 5;
    public final int SCREEN_RECENTLYADDED = 6;
    public final int SCREEN_OTHERS = 7;


    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    public static boolean slidingShowing = false;
    private CharSequence mTitle;
    public static MainActivity mainActivity;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = this;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_main);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        for (int i = 0; i < userPlaylists.size(); i++)
            userPlaylists.get(i).reloadMusicListfromPlayList();
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        slidingFragment = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .replace(R.id.MiniPlayerSongInfoContainer, miniPlayerFragment.newInstance())
                .commit();
        slidingFragment.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                FragmentManager fragmentManager = getFragmentManager();
                ActionBar ab = getSupportActionBar();
                if (ab != null)
                    if (v >= 0.4) {
                        if (!slidingShowing && ab.isShowing()) {
                            slidingShowing = true;

                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                                    .replace(R.id.MiniPlayerSongInfoContainer, SongInfoFragment.newInstance())
                                    .commit();
                        }
                        ab.hide();
                        PlayerFragment.updateSongInfo(PlayerFragment.musicPlayer.currentSong);
                    } else if (v <= 0.6) {
                        if (!ab.isShowing() && slidingShowing) {
                            slidingShowing = false;
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
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
        doBindService();
        globalMusicList.run();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            PlayerFragment.musicPlayer = ((MusicPlayer.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            PlayerFragment.musicPlayer = null;
        }
    };

    private void doBindService() {
        startService(new Intent(this, MusicPlayer.class));
        getApplicationContext().bindService(new Intent(MainActivity.mainActivity.getApplicationContext(), MusicPlayer.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void OnSelectedFragmentInteraction(Album albumFromList) {
        localalbum = albumFromList;
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right)
                .replace(R.id.container, MusicFragment.newInstance("ALBUM", SCREEN_MUSICD))
                .addToBackStack("MusicListFragment")
                .commit();


    }

    @Override
    public void OnArtistFragmentInteraction(Artist artist) {
        ArtistAlbums = artist.getAlbumList();
        localartist = artist;
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right)
                .replace(R.id.container, SelectedArtistFragment.newInstance())
                .addToBackStack("AlbumListFragment")
                .commit();


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();

        switch (position) {
            case 0:
                MainActivity.ArtistAlbums = null;
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right)
                        .replace(R.id.container, ArtistFragment.newInstance(SCREEN_ARTIST))
                        .addToBackStack("ArtistsFragment")
                        .commit();
                //actionBar.setTitle(getResources().getStringArray(R.array.Screens)[position]);
                break;
            case 1:
                MainActivity.localalbum = null;
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right)
                        .replace(R.id.container, MusicFragment.newInstance("GLOBAL", SCREEN_MUSICD))
                        .addToBackStack("MusicListFragment")
                        .commit();
                break;
            case 2:
                MainActivity.localartist = null;
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right)
                        .replace(R.id.container, SelectedArtistFragment.newInstance(SCREEN_ALBUM))
                        .addToBackStack("AlbumListFragment")
                        .commit();
                break;
            case 3:
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right)
                        .replace(R.id.container, PlaylistsFragment.newInstance(SCREEN_PLAYLIST))
                        .addToBackStack("PlaylistsFragment")
                        .commit();
                break;
            case 4:
                playmusiclist = MainActivity.userPlaylists.get(MusicList.findplaylist("Favorites"));
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right)
                        .replace(R.id.container, MusicFragment.newInstance("LIST", SCREEN_FAVORITES))
                        .addToBackStack("FavoritesFragment")
                        .commit();
                break;
            case 5:
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right)
                        .replace(R.id.container, RecentlyAddedFragment.newInstance(SCREEN_RECENTLYADDED))
                        .addToBackStack("RecentlyAddedFragment")
                        .commit();
                break;
            case 6:
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right)
                        .replace(R.id.container, OtherFragment.newInstance(SCREEN_OTHERS))
                        .addToBackStack("OthersFragment")
                        .commit();
                //actionBar.setTitle(getResources().getStringArray(R.array.Screens)[position]);
                break;

        }


    }

    @Override
    public void onBackPressed() {
        if (!getFragmentManager().popBackStackImmediate())
            super.onBackPressed();
    }

    public void onSectionAttached(int number) {
        String[] tempArray;
        tempArray = getResources().getStringArray(R.array.Screens);
        mTitle = tempArray[number - 1];
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.mainActivity);
                builder.setTitle("Settings Options").setItems(R.array.Settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        switch (pos) {
                            case 0:
                                File file = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + File.separator + "OkiDokAlbum.srl");
                                if (file.delete())
                                    file = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + File.separator + "OkiDokArtist.srl");
                                if (file.delete())
                                    file = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + File.separator + "OkiDokMusics3.srl");
                                if (file.delete())
                                    Toast.makeText(getApplicationContext(), "DELETED", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(getApplicationContext(), "OTHER", Toast.LENGTH_SHORT).show();
                                break;

                        }

                    }
                });
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnPlaylistFragmentInteraction(int position) {

        playmusiclist = userPlaylists.get(position);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right)
                .replace(R.id.container, MusicFragment.newInstance("LIST", position + 1))
                .addToBackStack("MusicListFragment")
                .commit();

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://okidoki.musicplayer/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://okidoki.musicplayer/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
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
            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MusicPlayer.class);
        getApplicationContext().unbindService(mConnection);
        getApplicationContext().stopService(intent);
        super.onDestroy();
    }

}