package com.parse.videomore.list_adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.videomore.R;
import com.parse.videomore.containers.Category;

public class CategoriesListAdapter extends BaseAdapter {
	// Адаптер списка категорий (используется для упраления списком, служит как бы оболочкой для доступа к элементу)
	
//	private static final String TAG = "CategoriesListAdapter";
	
	private ArrayList<Category> mCategories;
	
    private LayoutInflater mLayoutInflater;
	
    public CategoriesListAdapter(final Context context, ArrayList<Category> categories) {
    	super();
        this.mCategories = categories;  
        mLayoutInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return mCategories.size();
    }

    public Object getItem(int position) {
        return mCategories.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, final ViewGroup parent) {         
    	if (convertView == null) {
    		convertView = (View)mLayoutInflater.inflate(R.layout.category_list_item, parent, false);
    	}
    	
    	TextView mTextView = (TextView)convertView.findViewById(R.id.category_title);
    	mTextView.setText(((Category)getItem(position)).title);
    	
        return convertView;
    }
}
