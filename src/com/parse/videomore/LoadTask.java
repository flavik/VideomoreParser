package com.parse.videomore;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.parse.videomore.containers.Category;
import com.parse.videomore.containers.Movie;
import com.parse.videomore.containers.Project;
import com.parse.videomore.list_activities.BaseListActivity;
import com.parse.videomore.list_activities.CategoriesListActivity;
import com.parse.videomore.list_activities.MoviesListActivity;
import com.parse.videomore.list_activities.ProjectsListActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

public final class LoadTask extends AsyncTask<Integer, Integer, Long> {  
//  Реализация класса для загрузки xml, на основе стандартного класса android  AsyncTask

//	private static final String TAG = "LoadTask";
	
	private Context mContext;
	private Handler mHandler;
	
	public static final String USER_AGENT = "LoaderService/1.0";
	public static final int REQUEST_TIMEOUT_MS = 20 * 1000; // ms
	private static HttpClient mHttpClient;
	
	private int index;
	private String st;
	
	private ArrayList<Category> categories;
	private ArrayList<Project> projects;
	private ArrayList<Movie> movies;

	/**
	 * Configures the httpClient to connect to the URL provided.
	 */
	public static void maybeCreateHttpClient() {
		if (mHttpClient == null) {
			mHttpClient = new DefaultHttpClient();
			final HttpParams params = mHttpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, REQUEST_TIMEOUT_MS);
			HttpConnectionParams.setSoTimeout(params, REQUEST_TIMEOUT_MS);
			ConnManagerParams.setTimeout(params, REQUEST_TIMEOUT_MS);
		}
	}

	public LoadTask(final Context context, final Handler handler, final int index, final String st) {
		this.mContext = context;
		this.mHandler = handler;
		this.index = index;
		this.st = st;
	}
        
    /* Главный поток */
    @Override
    protected void onPreExecute() {
    	// Устанавливаем ProgressBar
    	mHandler.post(new Runnable() {
			public void run() {
				((BaseListActivity)mContext).onShowProgress();
			}
    	});
    }
    
    /* Отдельный поток */
    @Override
    protected Long doInBackground(Integer... arg0) {
		try {		
			switch (index) {
				// Полученные строки передаём соответствующим парсерам
				case Constants.categoryIndex: categories = Category.valuesOf(loadInf(Constants.CATEGORIES_URL)); break;
				case Constants.projectIndex: projects = Project.valuesOf(loadInf(Constants.PROJECT_URL+st+".xml")); break;
				case Constants.movieIndex: movies = Movie.valuesOf(loadInf(Constants.MOVIE_URL+st+".xml")); break;
				default: break;
			}
		} catch (SocketException e) {
			return (long)-1;
		} catch (IOException e) {
			return (long)-1;
		}
    	return (long)0;
    } 

    /* Главный поток */
    @Override
    protected void onPostExecute(Long result) {
	    // Посылаем результаты (если возможно) в наш контекст, из которого вызывали
    	if (result < 0) {
    		Toast.makeText(mContext.getApplicationContext(), "Ошибка сетевого соединения", Toast.LENGTH_LONG).show();
    	} else {
			switch (index) {
				case Constants.categoryIndex: 
			    	mHandler.post(new Runnable() {
						public void run() {
							((CategoriesListActivity)mContext).setCategories(categories);
						}
			    	});
					break;
				case Constants.projectIndex: 
			    	mHandler.post(new Runnable() {
						public void run() {
							((ProjectsListActivity)mContext).setProjects(projects);
						}
			    	});
			    	break;
				case Constants.movieIndex: 
			    	mHandler.post(new Runnable() {
						public void run() {
							((MoviesListActivity)mContext).setMovies(movies);
						}
			    	});
			    	break;
				default: break;
			}
    	}
    	// Убираем ProgressBar
    	mHandler.post(new Runnable() {
			public void run() {
				((BaseListActivity)mContext).onHideProgress();
			}
    	});
    }
    
    // Метод запрашивающий Get запросом по строке url, и возвращающий принятый результат в виде строки
    protected static String loadInf(final String st) throws SocketException, IOException {
		final HttpGet get = new HttpGet(st);
		
		maybeCreateHttpClient();
		
		try {
			final HttpResponse resp = mHttpClient.execute(get);
			final String response = EntityUtils.toString(resp.getEntity());
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				
				return response;
				
			} else {
				//Log.e(TAG, "Server error in fetching remote categories: " + resp.getStatusLine());
				throw new IOException();
			}
			
		} catch (SocketException e) {
			throw new SocketException();
		}
    }
}