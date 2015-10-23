package com.phongbm.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemClickListener,
		OnClickListener {
	public static final String TAG = "MainActivity";

	private InputMethodManager inputMethodManager;
	private ListView listViewMedia;
	private AudioAdapter audioAdapter;
	private TextView txtAppName;
	private ImageView imgSearch, imgDeleteSign;
	private EditText edtSearch;
	private TextWatcher textWatcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initializeComponent();
		audioAdapter = new AudioAdapter(MainActivity.this);
		listViewMedia.setAdapter(audioAdapter);
	}

	private void initializeComponent() {
		inputMethodManager = (InputMethodManager) MainActivity.this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		listViewMedia = (ListView) findViewById(R.id.listViewMedia);
		listViewMedia.setOnItemClickListener(MainActivity.this);
		txtAppName = (TextView) findViewById(R.id.txtAppName);
		imgSearch = (ImageView) findViewById(R.id.imgSearch);
		imgSearch.setOnClickListener(MainActivity.this);
		imgDeleteSign = (ImageView) findViewById(R.id.imgDeleteSign);
		imgDeleteSign.setOnClickListener(MainActivity.this);
		textWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				audioAdapter.getFilter().filter(s);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};
		edtSearch = (EditText) findViewById(R.id.edtSearch);
		edtSearch.addTextChangedListener(textWatcher);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String data = audioAdapter.getAudios().get(position).getData();
		String title = audioAdapter.getAudios().get(position).getTitle();
		String artist = audioAdapter.getAudios().get(position).getArtist();
		String duration = audioAdapter.getAudios().get(position).getDuration();
		Intent intent = new Intent(MainActivity.this, PlayActivity.class);
		intent.putExtra("MEDIA_DATA", data);
		intent.putExtra("MEDIA_TITLE", title);
		intent.putExtra("MEDIA_ARTIST", artist);
		intent.putExtra("MEDIA_DURATION", duration);
		MainActivity.this.startActivity(intent);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.imgSearch:
			txtAppName.setVisibility(RelativeLayout.GONE);
			imgSearch.setVisibility(RelativeLayout.GONE);
			imgDeleteSign.setVisibility(RelativeLayout.VISIBLE);
			edtSearch.setVisibility(RelativeLayout.VISIBLE);
			edtSearch.requestFocus();
			inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
					0);
			break;
		case R.id.imgDeleteSign:
			imgDeleteSign.setVisibility(RelativeLayout.GONE);
			edtSearch.setVisibility(RelativeLayout.GONE);
			imgSearch.setVisibility(RelativeLayout.VISIBLE);
			txtAppName.setVisibility(RelativeLayout.VISIBLE);
			edtSearch.setText("");
			inputMethodManager.hideSoftInputFromWindow(
					imgDeleteSign.getWindowToken(), 0);
			break;
		}
	}

}