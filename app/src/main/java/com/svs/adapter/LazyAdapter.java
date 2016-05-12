package com.svs.adapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.svs.ImageLoader;
import com.svs.MainActivity;
import com.svs.R;
import com.svs.fragments.HomeFragment;

public class LazyAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;

	private Context mContext;
	private ImageView thumb_image;
	private ProgressDialog mProgressDialog;

	public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.gridrow, null);

		TextView title = (TextView) vi.findViewById(R.id.item_text); // title
		TextView id = (TextView) vi.findViewById(R.id.item_id); // artist
																	// name
		//TextView duration = (TextView) vi.findViewById(R.id.duration); // duration
		thumb_image = (ImageView) vi.findViewById(R.id.item_image); // thumb
																				// image

		HashMap<String, String> song = new HashMap<String, String>();
		song = data.get(position);
		// Setting all values in listview
		title.setText(song.get(HomeFragment.KEY_NAME));
		id.setText(song.get(HomeFragment.KEY_ID));
		//artist.setText("");
		//duration.setText("");
		//imageLoader.DisplayImage(song.get(HomeFragment.KEY_IMAGE), thumb_image);
		//new DownloadImage().execute(song.get(HomeFragment.KEY_IMAGE));
		Picasso.with(activity).load(song.get(HomeFragment.KEY_IMAGE)).into(thumb_image);


		return vi;
	}



}