package com.phongbm.music;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class AudioAdapter extends BaseAdapter implements Filterable {
	public static final String TAG = "AudioAdapter";

	private LayoutInflater layoutInflater;
	private ArrayList<Audio> audios, audiosOrigin;
	private Cursor cursor;
	private Context context;
	private ValueFilter valueFilter;

	public AudioAdapter(Context context) {
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.valueFilter = new ValueFilter();
		this.getAllAudioExternal();
	}

	@Override
	public int getCount() {
		return audios.size();
	}

	@Override
	public Audio getItem(int position) {
		return audios.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.audio, null);
			viewHolder = new ViewHolder();
			viewHolder.txtTitle = (TextView) convertView
					.findViewById(R.id.txtTitle);
			viewHolder.txtArtist = (TextView) convertView
					.findViewById(R.id.txtArtist);
			viewHolder.txtDuration = (TextView) convertView
					.findViewById(R.id.txtDuration);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.txtTitle.setText(audios.get(position).getTitle());
		viewHolder.txtArtist.setText(audios.get(position).getArtist());
		viewHolder.txtDuration.setText(audios.get(position).getDuration());
		return convertView;
	}

	private class ViewHolder {
		TextView txtTitle, txtArtist, txtDuration;
	}

	private void getAllAudioExternal() {
		cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.TITLE + " ASC");
		cursor.moveToFirst();
		audiosOrigin = new ArrayList<Audio>();
		audios = new ArrayList<Audio>();
		int duration, minute, second;
		while (!cursor.isAfterLast()) {
			Audio audio = new Audio();
			audio.setData(cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA)));
			audio.setTitle(cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.TITLE)));
			audio.setArtist(cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
			duration = Integer.valueOf(cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DURATION)));
			minute = (int) TimeUnit.MILLISECONDS.toMinutes(duration);
			second = (int) TimeUnit.MILLISECONDS.toSeconds(duration) - minute
					* 60;
			audio.setDuration((minute < 10 ? "0" + minute : "" + minute) + ":"
					+ (second < 10 ? "0" + second : "" + second));
			audiosOrigin.add(audio);
			audios.add(audio);
			cursor.moveToNext();
		}
		cursor.close();
		return;
	}

	public ArrayList<Audio> getAudios() {
		return audios;
	}

	private class ValueFilter extends Filter {
		@SuppressLint("DefaultLocale")
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults filterResults = new FilterResults();
			if (constraint != null && constraint.length() > 0) {
				ArrayList<Audio> audiosResult = new ArrayList<Audio>();
				for (int i = 0; i < audiosOrigin.size(); i++) {
					if (audiosOrigin.get(i).getTitle().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						audiosResult.add(audiosOrigin.get(i));
					}
				}
				filterResults.count = audiosResult.size();
				filterResults.values = audiosResult;
			} else {
				audios = audiosOrigin;
				filterResults.count = audiosOrigin.size();
				filterResults.values = audiosOrigin;
			}
			return filterResults;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			audios = (ArrayList<Audio>) results.values;
			AudioAdapter.this.notifyDataSetChanged();
		}
	}

	@Override
	public Filter getFilter() {
		if (valueFilter == null)
			return new ValueFilter();
		return valueFilter;
	}

}