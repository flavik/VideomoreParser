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
import com.parse.videomore.containers.Project;
import com.parse.videomore.list_adapters.ProjectsListAdapter;

public class ProjectsListActivity extends BaseListActivity {
	
	private Category mCategory;
	
	// Обработчик клика на проекте
	protected class ListItemClickListener implements OnItemClickListener {
	    public ListItemClickListener() {
		}

		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    	int p = position - mListView.getHeaderViewsCount();
	    	if ((p >=0)&&(p < mAdapter.getCount())) {
	    		// Вызов новой активности с роликами для кликнутого проекта
		    	Intent intent = new Intent(mContext, MoviesListActivity.class);
		    	intent.putExtra(Constants.projectColumn, (Project)mAdapter.getItem(p));
		    	startActivity(intent);
	    	}
	    }	
	}
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent(); 
        mCategory = (Category)intent.getParcelableExtra(Constants.categoryColumn);
    	LoadTask mTask = new LoadTask(this, mHandler, Constants.projectIndex, mCategory.name);
    	mTask.execute();
    }
           
	@Override
    protected void adjustLayout() {
		setContentView(R.layout.projects);	
		super.adjustLayout();
		mListView.setOnItemClickListener(new ListItemClickListener());
    }
    
	// Метод используется для передачи распарсеных результатов из загрузчика
    public void setProjects(ArrayList<Project> projects) {
    	mAdapter = new ProjectsListAdapter(this, projects);
    	setListAdapter(mAdapter);
    }
}