package com.daasuu.sample.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.VideoSize;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

@SuppressLint("ViewConstructor")
public class PlayerTextureView extends TextureView implements TextureView.SurfaceTextureListener, Player.Listener {

    private final static String TAG = PlayerTextureView.class.getSimpleName();

    protected static final float DEFAULT_ASPECT = -1f;
    private final SimpleExoPlayer player;
    protected float videoAspect = DEFAULT_ASPECT;

    public PlayerTextureView(Context context, String path) {
        super(context, null, 0);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "yourApplicationName"));

        // This is the MediaSource representing the media to be played.
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(path));
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem);

        // SimpleExoPlayer
        player = new SimpleExoPlayer.Builder(context).build();
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        // Prepare the player with the source.
        player.setMediaSource(videoSource);
        player.prepare();
        player.addListener(this);

        setSurfaceTextureListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (videoAspect == DEFAULT_ASPECT) return;

        int measuredWidth = getMeasuredWidth();
        int viewHeight = (int) (measuredWidth / videoAspect);
        Log.d(TAG, "onMeasure videoAspect = " + videoAspect);
        Log.d(TAG, "onMeasure viewWidth = " + measuredWidth + " viewHeight = " + viewHeight);

        setMeasuredDimension(measuredWidth, viewHeight);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable width = " + width + " height = " + height);

        //3. bind the player to the view
        player.setVideoSurface(new Surface(surface));
        player.setPlayWhenReady(true);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onVideoSizeChanged(VideoSize videoSize) {
        int width = videoSize.width;
        int height = videoSize.height;
        float pixelWidthHeightRatio = videoSize.pixelWidthHeightRatio;
        Log.d(TAG, "width = " + width + " height = " + height + " pixelWidthHeightRatio = " + pixelWidthHeightRatio);
        videoAspect = ((float) width / height) * pixelWidthHeightRatio;
        Log.d(TAG, "videoAspect = " + videoAspect);
        requestLayout();
    }

    @Override
    public void onSurfaceSizeChanged(int width, int height) {

    }

    @Override
    public void onRenderedFirstFrame() {

    }

    public void play() {
        player.setPlayWhenReady(true);
    }

    public void pause() {
        player.setPlayWhenReady(false);
    }

}
