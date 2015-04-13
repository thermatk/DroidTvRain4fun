package com.thermatk.android.tvrain;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class LiveStream extends Activity {
    private VideoView mVideoView;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        setContentView(R.layout.stream);
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        String path = getIntent().getExtras().getString("url");
        if (path == "") {
            Toast.makeText(LiveStream.this, "ERROR: set path variable", Toast.LENGTH_LONG).show();
            return;
        } else {
            //Alternative mVideoView.setVideoURI(Uri.parse(URLstring));
            mVideoView.setVideoPath(path);
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setPlaybackSpeed(1.0f);
                }
            });
        }

    }
}
