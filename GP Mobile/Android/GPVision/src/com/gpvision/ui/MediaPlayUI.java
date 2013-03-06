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
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MediaPlayUI extends FrameLayout implements MediaPlayerControl {
	private SurfaceView mSurfaceView;
	private MediaPlayer mPlayer;
	private MediaController mController;
	private int mCurrentPosition = 0;
	private FullScreenModelListener fullScreenModel;

	public enum Model {
		Normal, FullScreen
	}

	public MediaPlayUI(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MediaPlayUI(Context context) {
		super(context);
	}

	public void setOnFullScreenModelListener(
			FullScreenModelListener fullScreenModel) {
		this.fullScreenModel = fullScreenModel;
	}

	public void setVideo(final Uri uri, final Model model, final int position) {

		mSurfaceView = new SurfaceView(getContext());
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				mPlayer.stop();
				mPlayer.reset();
				mPlayer.release();
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				startPlay(uri, holder, position);
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				if (model == Model.Normal) {
					mSurfaceView.setLayoutParams(new LinearLayout.LayoutParams(
							width, width * 9 / 16));
				}
			}
		});
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mController = new MediaController(getContext());
		mController.setMediaPlayer(this);
		mController.setEnabled(true);

		if (model == Model.Normal) {
			LinearLayout linearLayout = new LinearLayout(getContext());
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			linearLayout.addView(mSurfaceView);
			linearLayout.addView(mController);
			addView(linearLayout);
		} else {
			addView(mSurfaceView);
			addView(mController, new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.FILL_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
		}
	}

	private void startPlay(Uri uri, SurfaceHolder holder, final int position) {
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
					seekTo(position);
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
			try {
				return mPlayer.isPlaying();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void pause() {
		if (mPlayer != null) {
			try {
				if (mPlayer.isPlaying()) {
					mPlayer.pause();
					mCurrentPosition = mPlayer.getCurrentPosition();
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void seekTo(int pos) {
		if (mPlayer != null) {
			try {
				mPlayer.seekTo(pos);
				mCurrentPosition = mPlayer.getCurrentPosition();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void start() {
		if (mPlayer != null) {
			try {
				if (!mPlayer.isPlaying()) {
					mPlayer.start();
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void fullScreenModel() {
		pause();
		fullScreenModel.onFullScreenModel();
	}

	public interface FullScreenModelListener {
		void onFullScreenModel();
	}
}
