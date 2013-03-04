package com.gpvision.ui;

import java.io.IOException;

import com.gpvision.ui.MediaController.MediaPlayerControl;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MediaPlayUI extends LinearLayout {
	private SurfaceView mSurfaceView;
	private MediaPlayer mPlayer;
	private MediaController mController;
	private int mCurrentPosition = 0;

	public enum Model {
		Normal, FullScreen
	}

	public MediaPlayUI(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MediaPlayUI(Context context) {
		super(context);
		init();
	}

	private void init() {
		setOrientation(LinearLayout.VERTICAL);
	}

	public void setVideo(final Uri uri, Model model) {

		mSurfaceView = new SurfaceView(getContext());
		SurfaceHolder holder = mSurfaceView.getHolder();
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
				mSurfaceView.setLayoutParams(new LayoutParams(width,
						width * 9 / 16));
			}
		});
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.setSizeFromLayout();

		mController = new MediaController(getContext());
		mController.setMediaPlayer(mediaPlayerControl);
		mController.setEnabled(true);

		if (model == Model.Normal) {
			addView(mSurfaceView);
			addView(mController);
		} else {
			FrameLayout frameLayout = new FrameLayout(getContext());
			frameLayout.addView(mSurfaceView);
			frameLayout.addView(mController, new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.FILL_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
			addView(frameLayout);
		}
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
					mController.updatePausePlay();
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
		if (mController != null) {
			// mController.show();
		}
		return super.onTouchEvent(event);
	}

	private MediaPlayerControl mediaPlayerControl = new MediaPlayerControl() {

		@Override
		public int getBufferPercentage() {
			if (mPlayer != null) {
				return (mPlayer.getCurrentPosition() * 100)
						/ mPlayer.getDuration();
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
	};

}
