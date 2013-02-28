package com.gpvision.ui;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

public class MediaPlayUI extends SurfaceView implements MediaPlayerControl {
	private MediaPlayer mPlayer;
	private MediaController mController;
	private int mCurrentPosition = 0;

	public MediaPlayUI(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MediaPlayUI(Context context) {
		super(context);
		init();
	}

	private void init() {
		mController = new MediaController(getContext());
		mController.setMediaPlayer(this);
		mController.setAnchorView(this);
		mController.setEnabled(true);
	}

	public void setVideo(final Uri uri) {

		SurfaceHolder holder = getHolder();
		holder.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				mPlayer.release();
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				startPlay(uri, holder);
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}
		});
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.setSizeFromLayout();

	}

	private void startPlay(Uri uri, SurfaceHolder holder) {
		try {
			mPlayer = new MediaPlayer();
			mPlayer.setDataSource(getContext(), uri);
			mPlayer.setDisplay(holder);
			mPlayer.prepareAsync();
			mPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.start();
				}
			});
			mPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// mPlayer.stop();
				}
			});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mController.show();
		return super.onTouchEvent(event);
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getBufferPercentage() {
		if (mPlayer != null) {
			return (mPlayer.getCurrentPosition() * 100) / mPlayer.getDuration();
		}
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		if (mPlayer != null) {
			mCurrentPosition = mPlayer.getCurrentPosition();
		}
		return mCurrentPosition;
	}

	@Override
	public int getDuration() {
		if (mPlayer != null) {
			return mPlayer.getDuration();
		}
		return 0;
	}

	@Override
	public boolean isPlaying() {
		if (mPlayer != null) {
			return mPlayer.isPlaying();
		}
		return false;
	}

	@Override
	public void pause() {
		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {
				mPlayer.pause();
				mCurrentPosition = getCurrentPosition();
			}
		}
	}

	@Override
	public void seekTo(int pos) {
		if (mPlayer != null) {
			mPlayer.seekTo(pos);
			mCurrentPosition = getCurrentPosition();
		}

	}

	@Override
	public void start() {
		if (mPlayer != null) {
			if (!mPlayer.isPlaying()) {
				mPlayer.start();
			}
		}
	}

}
