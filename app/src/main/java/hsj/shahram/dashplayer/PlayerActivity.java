package hsj.shahram.dashplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import hsj.shahram.dashplayer.databinding.ActivityPlayerBinding;

public class PlayerActivity extends AppCompatActivity {
    private SimpleExoPlayer player;

    private ActivityPlayerBinding binding;
    private String streamingType;
    private PlayerView playerView;
    private FrameLayout subtitleFrame;
    private TextView subtitleText;
    private DefaultTrackSelector trackSelector;

    private DataSource.Factory dataSourceFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullScreenMode();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player);


        init();
    }


    private void init() {


        streamingType = getIntent().getStringExtra(MainActivity.PLAYER_INTENT_EXTRA);
        initPlayer();
        subtitleFrame.setTag(Const.SUBTITLE_ON);
        clickListener();
    }

    private void initHls() {
        Uri uri1 = Uri.parse(Const.HLS_LINK_ONE);
        Uri uri2 = Uri.parse(Const.HLS_LINK_TWO);


        List<MediaSource> mediaSourceList = new ArrayList<>();

        MediaItem mediaItem2 = MediaItem.fromUri(uri2);
        MediaItem mediaItem1 = MediaItem.fromUri(uri1);


        MediaSource mediaSource2 = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem2);
        MediaSource mediaSource1 = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem1);


        mediaSourceList.add(mediaSource1);
        mediaSourceList.add(mediaSource2);


        player.setMediaSources(mediaSourceList);
        player.prepare();
        player.setPlayWhenReady(true);


    }


    private void initDash() {

        Uri uri1 = Uri.parse(Const.DASH_LINK_ONE);
        Uri uri2 = Uri.parse(Const.DASH_LINK_TWO);

        List<MediaSource> mediaSourceList = new ArrayList<>();

        MediaItem mediaItem1 = MediaItem.fromUri(uri1);
        MediaItem mediaItem2 = MediaItem.fromUri(uri2);

        MediaSource mediaSource1 = new DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem1);

        MediaSource mediaSource2 = new DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem2);


        mediaSourceList.add(mediaSource1);
        mediaSourceList.add(mediaSource2);


        player.setMediaSources(mediaSourceList);
        player.prepare();
        player.setPlayWhenReady(true);


    }

    private void initPlayer() {

        playerView = binding.playerView;
        subtitleFrame = playerView.findViewById(R.id.subtitle);
        subtitleText = playerView.findViewById(R.id.subtitle_text);

         trackSelector = new DefaultTrackSelector(this);
        trackSelector.setParameters(new DefaultTrackSelector.ParametersBuilder(this)
                .setRendererDisabled(C.TRACK_TYPE_VIDEO, false)
                .build());

        player = new SimpleExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .build();

        playerView.setPlayer(player);
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter.
                Builder(this)
                .build();

        dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "DashPlayer"), defaultBandwidthMeter);


        if (streamingType.equals(MainActivity.HLS_EXTRA))
            initHls();


        else
            initDash();


        player.addListener(new Player.EventListener() {


            @Override
            public void onPlaybackStateChanged(int state) {
                switch (state) {
                    case Player.STATE_BUFFERING:
                        binding.setInProgress(true);
                        break;


                    case Player.STATE_READY:
                        binding.setInProgress(false);

                        break;


                }
            }
        });

    }


    private void clickListener() {


        subtitleFrame.setOnClickListener(view ->

        {

            if (subtitleFrame.getTag().toString().equals(Const.SUBTITLE_ON)) {

                subtitleOff();


            } else {

                subtitleOn();

            }

        });

    }

    private void subtitleOff() {


        subtitleFrame.setTag(Const.SUBTITLE_OFF);
        subtitleText.setTextColor(ResourcesCompat.getColor(getResources(), R.color.gray, null));
        subtitleFrame.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.stroke_background, null));
        //releaseResource();
        //initPlayer(false);
        trackSelector.setParameters(new DefaultTrackSelector.ParametersBuilder(this)
                .setRendererDisabled(C.TRACK_TYPE_VIDEO, true)
                .build());


    }


    private void subtitleOn() {


        subtitleFrame.setTag(Const.SUBTITLE_ON);
        subtitleText.setTextColor(ResourcesCompat.getColor(getResources(), R.color.purple_500, null));
        subtitleFrame.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.stroke_background_purple, null));
        trackSelector.setParameters(new DefaultTrackSelector.ParametersBuilder(this)
                .setRendererDisabled(C.TRACK_TYPE_VIDEO, false)
                .build());


    }

    private void setupFullScreenMode() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void releaseResource() {

        if (player != null) {
            player.release();
            player = null;

        }

    }

    @Override
    protected void onDestroy() {
        releaseResource();
        super.onDestroy();
    }
}
