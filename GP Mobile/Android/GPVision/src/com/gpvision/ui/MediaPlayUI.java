package com.gpvision.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.gpvision.datamodel.Index;
import com.gpvision.datamodel.Location;
import com.gpvision.datamodel.Video;
import com.gpvision.ui.MediaController.MediaPlayerControl;
import com.gpvision.utils.Environment;
import com.gpvision.utils.LocalDataBuffer;
import com.gpvision.utils.LogUtil;

public class MediaPlayUI extends FrameLayout implements MediaPlayerControl {

	// private static final int BUFFER_PERCENT = 5;

	private SurfaceView mSurfaceView;
	private MediaPlayer mPlayer;
	private MediaController mController;
	private int mCurrentPosition = 0;
	private FullScreenModelListener fullScreenModel;
	private FaceBox mFaceBox;
	private HashMap<Integer, ArrayList<Index>> indexMap;
	private int width, heigth;
	private float scaleWidth, scaleHeigth;
	private Video video;
	private com.gpvision.ui.MediaController.Callback callback;
	private static boolean prepared = false;

	// private boolean onSeek = true;
	// private int soughtPercent = 0;

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

	public void setIndexMap(HashMap<Integer, ArrayList<Index>> indexMap) {
		this.indexMap = indexMap;
	}

	public void setCallback(com.gpvision.ui.MediaController.Callback callback) {
		this.callback = callback;
	}

	public boolean isPrepared() {
		return prepared;
	}

	public void setVideo(Video video, final Model model, final int position) {
		this.video = video;
		mSurfaceView = new SurfaceView(getContext());
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (mPlayer != null) {
					prepared = false;
					mPlayer.stop();
					mPlayer.reset();
					mPlayer.release();
					mPlayer = null;
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				preparePlayer();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				if (model == Model.Normal) {
					View view = (View) mSurfaceView.getParent();
					int newHeigth = (int) (1.0f * width
							* MediaPlayUI.this.video.getHeight() / MediaPlayUI.this.video
							.getWidth());
					view.setLayoutParams(new LinearLayout.LayoutParams(width,
							newHeigth));
					MediaPlayUI.this.width = width;
					MediaPlayUI.this.heigth = newHeigth;
				} else {
					MediaPlayUI.this.width = width;
					MediaPlayUI.this.heigth = height;
				}
			}
		});
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mController = new MediaController(getContext());
		mController.setMediaPlayer(this);
		mController.setEnabled(true);
		mController.setCallback(callback);

		mFaceBox = new FaceBox(getContext());

		if (model == Model.Normal) {
			LinearLayout linearLayout = new LinearLayout(getContext());
			linearLayout.setOrientation(LinearLayout.VERTICAL);

			FrameLayout frameLayout = new FrameLayout(getContext());
			frameLayout.addView(mSurfaceView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			frameLayout.addView(mFaceBox, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

			linearLayout.addView(frameLayout);
			linearLayout.addView(mController);
			addView(linearLayout);
		} else {
			addView(mSurfaceView);
			addView(mFaceBox);
			addView(mController, new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.FILL_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
		}
	}

	public void preparePlayer() {
		try {
			prepared = false;
			if (mPlayer == null)
				mPlayer = new MediaPlayer();
			mPlayer.reset();
			mPlayer.setDataSource(getContext(),
					getVideoUri(video.getStoreName()));
			mPlayer.setDisplay(mSurfaceView.getHolder());
			mPlayer.prepareAsync();
			mPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					prepared = true;
					int videoHeigth = video.getHeight();
					int videoWidth = video.getWidth();
					scaleWidth = width / (videoWidth * 1.0f);
					scaleHeigth = heigth / (videoHeigth * 1.0f);
				}
			});
			mPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					mPlayer.pause();
					mController.updatePausePlay();
				}
			});
			mPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					mp.pause();
					preparePlayer();
					return false;
				}
			});
			mPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

				@Override
				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					LogUtil.logI("buffered:" + percent);
					// if (onSeek) {
					// if (mp.isPlaying())
					// mp.pause();
					// if (percent - soughtPercent > BUFFER_PERCENT) {
					// if (!mp.isPlaying())
					// mp.start();
					// onSeek = false;
					// }
					// }
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

	private void updateFaceBox(int position) {
		if (indexMap == null)
			return;
		int indexKey = (int) Math.floor(position / (250 * 1.0f));
		if (indexMap.containsKey(indexKey)) {
			ArrayList<Index> indexs = indexMap.get(indexKey);
			ArrayList<Rect> rects = new ArrayList<Rect>();

			for (Index index : indexs) {
				Rect rect = new Rect();
				Location location = index.getLocation();
				rect.set(
						(int) (location.getLeft() * scaleWidth),
						(int) (location.getTop() * scaleHeigth),
						(int) ((location.getLeft() + location.getWidth()) * scaleWidth),
						(int) ((location.getTop() + location.getHeight()) * scaleHeigth));
				rects.add(rect);
			}
			mFaceBox.setAreas(rects);
		} else {
			mFaceBox.setAreas(null);
		}
	}

	private Uri getVideoUri(String storeName) {
		Uri.Builder builder = new Uri.Builder();
		Environment environment = LocalDataBuffer.getInstance()
				.getVideoEnvironment();
		builder.encodedPath(String.format("%s://%s", "http",
				environment.getHost()));
		if (environment.getBasePath() != null) {
			builder.appendEncodedPath(environment.getBasePath());
		}
		builder.appendEncodedPath("public");
		builder.appendEncodedPath("getvideo");
		builder.appendEncodedPath(storeName);
		return builder.build();
		// return Uri.parse("http://192.168.1.100:8080/video/test2.mp4");
	}

	@Override
	public int getBufferPercentage() {
		if (mPlayer != null && prepared) {
			try {
				return (mPlayer.getCurrentPosition() * 100)
						/ mPlayer.getDuration();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public int getPosition() {
		return mCurrentPosition;
	}

	@Override
	public int getCurrentPosition() {
		if (mPlayer != null) {
			try {
				mCurrentPosition = mPlayer.getCurrentPosition();
				updateFaceBox(mCurrentPosition);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
		return mCurrentPosition;
	}

	@Override
	public int getDuration() {
		if (mPlayer != null && prepared) {
			try {
				return mPlayer.getDuration();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
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
			} finally {
				mController.updatePausePlay();
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
			} finally {
				mController.updatePausePlay();
			}
		}
	}

	@Override
	public void fullScreenModel() {
		fullScreenModel.onFullScreenModel();
	}

	@Override
	public boolean prepared() {
		return isPrepared();
	}

	public interface FullScreenModelListener {
		void onFullScreenModel();
	}
}
