package com.gpvision.ui;

import java.util.Formatter;
import java.util.Locale;
import com.gpvision.R;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MediaController extends FrameLayout {

	private static final int UPDATE_PROGRESS = 1417;
	private static final int CANCEL_UPDATE = 1418;
	private static final int CANCEL_VOLUME_BAR = 1419;
	private static final int UPDATE_PROGRESS_DELAY = 250;
	private static final int VOLUME_BAR_SHOW_TIME = 1000 * 10;

	private MediaPlayerControl mPlayer;
	private View mRoot;
	private ImageButton mPauseButton;
	private ImageButton mVolumeButton;
	private ImageButton mFullScreenButton;
	private SeekBar mPlaySeekBar;
	private SeekBar mVolumeSeekBar;
	private TextView mEndTime, mCurrentTime;
	private LinearLayout mPlayLayout, mVolumeLayout;

	private StringBuilder mFormatBuilder;
	private Formatter mFormatter;

	public MediaController(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MediaController(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		mRoot = inflater.inflate(R.layout.layout_media_controller, null);
		mPlayLayout = (LinearLayout) mRoot
				.findViewById(R.id.media_controller_play_layout);
		mVolumeLayout = (LinearLayout) mRoot
				.findViewById(R.id.media_controller_volume_layout);

		mPauseButton = (ImageButton) mRoot
				.findViewById(R.id.media_controller_play_pause_btn);
		mPauseButton.setOnClickListener(mPauseListener);

		mPlaySeekBar = (SeekBar) mRoot
				.findViewById(R.id.media_controller_play_seek_bar);
		mPlaySeekBar.setMax(1000);
		mPlaySeekBar.setOnSeekBarChangeListener(mPlaySeekListener);

		mCurrentTime = (TextView) mRoot
				.findViewById(R.id.media_controller_current_time);
		mEndTime = (TextView) mRoot
				.findViewById(R.id.media_controller_end_time);
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

		mVolumeButton = (ImageButton) mRoot
				.findViewById(R.id.media_controller_volume_btn);
		mVolumeButton.setOnClickListener(mVolumeListener);
		mVolumeSeekBar = (SeekBar) mRoot
				.findViewById(R.id.media_controller_volume_seekBar);
		mVolumeSeekBar.setMax(1000);
		mVolumeSeekBar.setOnSeekBarChangeListener(mVolumeSeekListener);

		mFullScreenButton = (ImageButton) mRoot
				.findViewById(R.id.media_controller_full_screen_btn);
		mFullScreenButton.setOnClickListener(mFullScreenListener);
		addView(mRoot);
	}

	public void setMediaPlayer(MediaPlayerControl player) {
		mPlayer = player;
	}

	public void updatePausePlay() {
		if (mRoot == null || mPauseButton == null)
			return;

		if (mPlayer.isPlaying()) {
			mPauseButton
					.setImageResource(R.drawable.icon_media_controller_button_pause);
			handler.removeMessages(UPDATE_PROGRESS);
			handler.sendEmptyMessage(UPDATE_PROGRESS);
		} else {
			mPauseButton
					.setImageResource(R.drawable.icon_media_controller_button_play);
			handler.sendEmptyMessage(CANCEL_UPDATE);
		}
	}

	private int setProgress() {
		if (mPlayer == null) {
			return 0;
		}
		int position = mPlayer.getCurrentPosition();
		int duration = mPlayer.getDuration();
		if (mPlaySeekBar != null) {
			if (duration > 0) {
				// use long to avoid overflow
				long pos = 1000L * position / duration;
				mPlaySeekBar.setProgress((int) pos);
			}
			int percent = mPlayer.getBufferPercentage();
			mPlaySeekBar.setSecondaryProgress(percent * 10);
		}

		if (mEndTime != null)
			mEndTime.setText(stringForTime(duration));
		if (mCurrentTime != null)
			mCurrentTime.setText(stringForTime(position));

		return position;
	}

	private void doPauseResume() {
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
		} else {
			mPlayer.start();
		}
	}

	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	private void setVolume(float precent) {
		AudioManager audioManager = (AudioManager) getContext()
				.getSystemService(Service.AUDIO_SERVICE);
		audioManager
				.setStreamVolume(
						AudioManager.STREAM_MUSIC,
						(int) (audioManager
								.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * precent),
						AudioManager.FLAG_VIBRATE);
	}

	private float getVolume() {
		AudioManager audioManager = (AudioManager) getContext()
				.getSystemService(Service.AUDIO_SERVICE);
		return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 1.0f
				/ audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	private View.OnClickListener mPauseListener = new View.OnClickListener() {
		public void onClick(View v) {
			doPauseResume();
		}
	};

	private View.OnClickListener mVolumeListener = new View.OnClickListener() {
		public void onClick(View v) {
			mPlayLayout.setVisibility(View.GONE);
			mVolumeLayout.setVisibility(View.VISIBLE);

			mVolumeSeekBar.setProgress((int) (getVolume() * 1000));
			handler.sendEmptyMessageDelayed(CANCEL_VOLUME_BAR,
					VOLUME_BAR_SHOW_TIME);
		}
	};

	private View.OnClickListener mFullScreenListener = new View.OnClickListener() {
		public void onClick(View v) {
			mPlayer.fullScreenModel();
		}
	};

	private OnSeekBarChangeListener mVolumeSeekListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (!fromUser) {
				return;
			}
			setVolume(progress * 1.0f / 1000);
		}
	};

	private OnSeekBarChangeListener mPlaySeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {

		}

		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (!fromuser) {
				// We're not interested in programmatically generated changes to
				// the progress bar's position.
				return;
			}

			long duration = mPlayer.getDuration();
			long newposition = (duration * progress) / 1000L;
			mPlayer.seekTo((int) newposition);
			if (mCurrentTime != null)
				mCurrentTime.setText(stringForTime((int) newposition));
		}

		public void onStopTrackingTouch(SeekBar bar) {
			updatePausePlay();

		}
	};

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_PROGRESS:
				setProgress();
				sendEmptyMessageDelayed(UPDATE_PROGRESS, UPDATE_PROGRESS_DELAY);
				break;
			case CANCEL_UPDATE:
				removeMessages(UPDATE_PROGRESS);
				break;
			case CANCEL_VOLUME_BAR:
				mPlayLayout.setVisibility(View.VISIBLE);
				mVolumeLayout.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}

	};

	public interface MediaPlayerControl {
		void start();

		void pause();

		int getDuration();

		int getCurrentPosition();

		void seekTo(int pos);

		boolean isPlaying();

		int getBufferPercentage();

		void fullScreenModel();
		// boolean canPause();
		// boolean canSeekBackward();
		// boolean canSeekForward();
	}
}
