package com.phongbm.music;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayActivity extends Activity implements View.OnClickListener {
	public static final String TAG = "MainActivity";
	private static final int UPDATE_SEEKBAR = 0;
	private static final int SET_ORIGIN_STATE = 1;

	private MediaPlayer mediaPlayer;
	private ImageView imgPlay, imgSpeaker;
	private TextView txtTotalDuration, txtCurrenDuration, txtSongTitle,
			txtArtist;
	private SeekBar seekBar;
	private boolean isPlaying = false;
	private int countClickButtonPlay = 0;
	private String currentDuration;
	private Thread updateSeekBarThread;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_SEEKBAR:
				txtCurrenDuration.setText(currentDuration);
				seekBar.setProgress((int) TimeUnit.MILLISECONDS
						.toSeconds(mediaPlayer.getCurrentPosition()));
				break;
			case SET_ORIGIN_STATE:
				txtCurrenDuration.setText("00:00");
				txtTotalDuration.setText("00:00");
				seekBar.setProgress(0);
				imgPlay.setImageResource(R.drawable.ic_media_play);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_play);
		initializeComponent();
		Intent intent = PlayActivity.this.getIntent();
		String data = intent.getStringExtra("MEDIA_DATA");
		txtSongTitle.setText(intent.getStringExtra("MEDIA_TITLE"));
		txtArtist.setText(intent.getStringExtra("MEDIA_ARTIST"));
		txtTotalDuration.setText(intent.getStringExtra("MEDIA_DURATION"));
		mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(data);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException | SecurityException
				| IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.imgPlay:
			if (countClickButtonPlay % 2 == 0) {
				if (!mediaPlayer.isPlaying()) {
					seekBar.setMax((int) TimeUnit.MILLISECONDS
							.toSeconds(mediaPlayer.getDuration()));
					mediaPlayer.start();
					isPlaying = true;
					updateSeekBarThread = new Thread(updateSeekBar);
					updateSeekBarThread.start();
					imgPlay.setImageResource(R.drawable.ic_media_pause);
				}
			} else {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					imgPlay.setImageResource(R.drawable.ic_media_play);
				}
			}
			countClickButtonPlay++;
			break;
		case R.id.imgSpeaker:
			AudioManager audioManager = (AudioManager) PlayActivity.this
					.getSystemService(Context.AUDIO_SERVICE);
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
			break;
		}
	}

	private void initializeComponent() {
		imgPlay = (ImageView) findViewById(R.id.imgPlay);
		imgPlay.setOnClickListener(PlayActivity.this);
		imgSpeaker = (ImageView) findViewById(R.id.imgSpeaker);
		imgSpeaker.setOnClickListener(PlayActivity.this);
		txtCurrenDuration = (TextView) findViewById(R.id.txtCurrentDuration);
		txtTotalDuration = (TextView) findViewById(R.id.txtTotalDuration);
		txtSongTitle = (TextView) findViewById(R.id.txtSongTitle);
		txtSongTitle.setSelected(true);
		txtArtist = (TextView) findViewById(R.id.txtArtist);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					if (isPlaying) {
						mediaPlayer.seekTo(progress * 1000);
					}
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	}

	Runnable updateSeekBar = new Runnable() {
		@Override
		public void run() {
			while (isPlaying) {
				long currentPosition = mediaPlayer.getCurrentPosition();
				int minutes = (int) TimeUnit.MILLISECONDS
						.toMinutes(currentPosition);
				int seconds = (int) TimeUnit.MILLISECONDS
						.toSeconds(currentPosition) - minutes * 60;
				currentDuration = (minutes < 10 ? "0" + minutes : "" + minutes)
						+ ":" + (seconds < 10 ? "0" + seconds : "" + seconds);
				handler.sendEmptyMessage(UPDATE_SEEKBAR);
				if (currentDuration.equals(txtTotalDuration.getText()
						.toString())) {
					isPlaying = false;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			countClickButtonPlay = 0;
			handler.sendEmptyMessage(SET_ORIGIN_STATE);
		}
	};

	protected void onDestroy() {
		mediaPlayer.stop();
		super.onDestroy();
	};

}