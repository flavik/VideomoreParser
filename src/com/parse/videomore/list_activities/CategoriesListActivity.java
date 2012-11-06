package com.parse.videomore.list_activities;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.parse.videomore.Constants;
import com.parse.videomore.LoadTask;
import com.parse.videomore.R;
import com.parse.videomore.containers.Category;
import com.parse.videomore.list_adapters.CategoriesListAdapter;

public class CategoriesListActivity extends BaseListActivity {
	// Этот класс-активность вызывается при запуске приложения
	
	// Обработчик клика на категории
	protected class ListItemClickListener implements OnItemClickListener {
	    public ListItemClickListener() {
		}

		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    	int p = position - mListView.getHeaderViewsCount();
	    	if ((p >=0)&&(p < mAdapter.getCount())) {
	    		// Вызов новой активности с проектами для кликнутой категории
		    	Intent intent = new Intent(mContext, ProjectsListActivity.class);
		    	intent.putExtra(Constants.categoryColumn, (Category)mAdapter.getItem(p));
		    	startActivity(intent);
	    	}
	    }	
	}

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	LoadTask mTask = new LoadTask(this, mHandler, Constants.categoryIndex, null);
    	mTask.execute();
    }

	@Override
    protected void adjustLayout() {
		setContentView(R.layout.categories);	
		super.adjustLayout();
		mListView.setOnItemClickListener(new ListItemClickListener());	
    }
    
	// Метод используется для передачи распарсеных результатов из загрузчика
    public void setCategories(ArrayList<Category> categories) {
    	mAdapter = new CategoriesListAdapter(this, categories);
    	setListAdapter(mAdapter);
    }
}