package com.parse.videomore.list_activities;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;

import com.parse.videomore.Constants;
import com.parse.videomore.LoadTask;
import com.parse.videomore.R;
import com.parse.videomore.containers.Movie;
import com.parse.videomore.containers.Project;
import com.parse.videomore.list_adapters.MoviesListAdapter;

public class MoviesListActivity extends BaseListActivity {
	
	private Project mProject;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent(); 
        mProject = (Project)intent.getParcelableExtra(Constants.projectColumn);
    	LoadTask mTask = new LoadTask(this, mHandler, Constants.movieIndex, mProject.name);
    	mTask.execute();
    }
        
	@Override
    protected void adjustLayout() {
		setContentView(R.layout.movies);	
		super.adjustLayout();
    }
    
	// Метод используется для передачи распарсеных результатов из загрузчика
    public void setMovies(ArrayList<Movie> movies) {
    	mAdapter = new MoviesListAdapter(this, movies);
    	setListAdapter(mAdapter);
    }
}