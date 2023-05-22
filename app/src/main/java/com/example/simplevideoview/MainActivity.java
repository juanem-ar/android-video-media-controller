package com.example.simplevideoview;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.chip.Chip;

public class MainActivity extends AppCompatActivity {

    private static final String VIDEO_REMOTE = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";
    private static final String PLAYBACK_TIME = "play_time";

    private VideoView mVideoView;
    private int mCurrentPosition = 0;

    private boolean isMediaControllerShowing;
    MediaController mediaController;
    TextView textView;

    Chip chip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chip = findViewById(R.id.chip4);
        mVideoView = findViewById(R.id.videoView);
        textView = findViewById(R.id.buffering_textview);

        mediaController = new MediaController(this);
        mediaController.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(mediaController);


        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializePlayer();
                chip.setVisibility(Chip.GONE);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
        System.out.println("Se freno la app");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mVideoView.pause();
        }
    }
    private Uri getMedia(String nombre){
        if (URLUtil.isValidUrl(VIDEO_REMOTE)) {
// media name is an external URL
            return Uri.parse(VIDEO_REMOTE);
        } else { // media name is a raw resource embedded in the app
            return Uri.parse("android.resource://" + getPackageName() +
                    "/raw/video");
        }
    }

    private void initializePlayer() {
        textView.setVisibility(VideoView.VISIBLE);
        Uri videoUri = getMedia(VIDEO_REMOTE);
        mVideoView.setVideoURI(videoUri);

        mVideoView.start();

        mVideoView.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        textView.setVisibility(VideoView.INVISIBLE);
                        if (mCurrentPosition > 0) {
                            mVideoView.seekTo(mCurrentPosition);
                        } else {
                            mVideoView.seekTo(1);
                        }
                        mVideoView.start();
                    }
                });

        mVideoView.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    Toast.makeText(MainActivity.this, "Playback completed",
                            Toast.LENGTH_SHORT).show();
                    mVideoView.seekTo(1);
            }
        });
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //
        if(mVideoView != null){
            mCurrentPosition = mVideoView.getCurrentPosition();
            isMediaControllerShowing = mediaController.isShowing();
            outState.putInt(PLAYBACK_TIME, mCurrentPosition);
            outState.putBoolean("media_controller_showing", isMediaControllerShowing);
        }
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
            isMediaControllerShowing = savedInstanceState.getBoolean("media_controller_showing");
            mVideoView.seekTo(mCurrentPosition);
            if (isMediaControllerShowing) {
                mediaController.show();
            }
        }
    }
    private void releasePlayer(){
        mVideoView.stopPlayback();
    }

}