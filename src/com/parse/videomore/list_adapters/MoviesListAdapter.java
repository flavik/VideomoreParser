package com.parse.videomore.list_adapters;

import java.net.MalformedURLException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.videomore.ImageThreadLoader;
import com.parse.videomore.ImageThreadLoader.ImageLoadedListener;
import com.parse.videomore.R;
import com.parse.videomore.containers.Movie;

public class MoviesListAdapter extends BaseAdapter {
	// Адаптер списка роликов (используется для упраления списком, служит как бы оболочкой для доступа к элементу)

//	private static final String TAG = "MoviesListAdapter";
	
	private ArrayList<Movie> mMovies;
	
	// Загрузчик изображений
	private static final ImageThreadLoader imageLoader = ImageThreadLoader.getInstance();
    private LayoutInflater mLayoutInflater;
    
	public static final class ViewHolder {
        public final TextView title;
        public final TextView views;
        public final ImageView thumb;
    
        public ViewHolder(TextView title, TextView views, ImageView thumb) {
        	this.title = title;
        	this.views = views;
        	this.thumb = thumb;
        }
	}
	
    public MoviesListAdapter(final Context context, final ArrayList<Movie> movies) {
    	super();
        this.mMovies = movies;  
        mLayoutInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return mMovies.size();
    }

    public Object getItem(int position) {
        return mMovies.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, final ViewGroup parent) {         
    	if (convertView == null) {
    		convertView = (View)mLayoutInflater.inflate(R.layout.movie_list_item, parent, false);
            TextView title = (TextView)convertView.findViewById(R.id.title);
            TextView views = (TextView)convertView.findViewById(R.id.views);
            ImageView thumb = (ImageView)convertView.findViewById(R.id.thumb);
            ViewHolder holder = new ViewHolder(title, views, thumb);
            convertView.setTag(holder);
    	}
    	ViewHolder holder = (ViewHolder) convertView.getTag();
    	
    	if (holder != null) {
	    	holder.title.setText(((Movie)getItem(position)).title);
	    	holder.views.setText("Количество просмотров: "+((Movie)getItem(position)).views);
	    	holder.thumb.setTag(((Movie)getItem(position)).thumb);
	
			try {	
				Bitmap cachedImage = imageLoader.loadImage(((Movie)getItem(position)).thumb, new ImageLoadedListener() {
					public void imageLoaded(Bitmap imageBitmap, String url) {
						ImageView thumb = (ImageView) parent.findViewWithTag(url);
						if (thumb != null)
							thumb.setImageBitmap(imageBitmap);
					}
				});
				
				if (cachedImage != null) {
					holder.thumb.setImageBitmap(cachedImage);
				} else {
					holder.thumb.setImageResource(R.drawable.ic_launcher);
				}
			} 
			catch (MalformedURLException e) {
//				Log.e(TAG, "Bad remote image URL: " + thumbnail, e);
			}
    	}
        return convertView;
    }
}
