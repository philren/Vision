package com.gpvision.ui;

import java.util.Formatter;
import java.util.Locale;
import com.gpvision.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MediaController extends FrameLayout {

	private static final int UPDATE_PROGRESS = 1417;
	private static final int CANCEL_UPDATE = 1418;
	private static final int DELAY = 1000;

	private MediaPlayerControl mPlayer;
	private View mRoot;
	private ImageButton mPauseButton;
	private SeekBar mSeekBar;
	private TextView mEndTime, mCurrentTime;

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

		mPauseButton = (ImageButton) mRoot
				.findViewById(R.id.media_controller_play_pause_btn);
		mPauseButton.setOnClickListener(mPauseListener);

		mSeekBar = (SeekBar) mRoot
				.findViewById(R.id.media_controller_play_seek_bar);
		mSeekBar.setMax(1000);
		mSeekBar.setOnSeekBarChangeListener(mSeekListener);

		mCurrentTime = (TextView) mRoot
				.findViewById(R.id.media_controller_current_time);
		mEndTime = (TextView) mRoot
				.findViewById(R.id.media_controller_end_time);
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

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
		if (mSeekBar != null) {
			if (duration > 0) {
				// use long to avoid overflow
				long pos = 1000L * position / duration;
				mSeekBar.setProgress((int) pos);
			}
			int percent = mPlayer.getBufferPercentage();
			mSeekBar.setSecondaryProgress(percent * 10);
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
		updatePausePlay();
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

	private View.OnClickListener mPauseListener = new View.OnClickListener() {
		public void onClick(View v) {
			doPauseResume();
		}
	};

	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
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
				sendEmptyMessageDelayed(UPDATE_PROGRESS, DELAY);
				break;
			case CANCEL_UPDATE:
				removeMessages(UPDATE_PROGRESS);
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
		// boolean canPause();
		// boolean canSeekBackward();
		// boolean canSeekForward();
	}
}
