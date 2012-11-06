package com.parse.videomore.list_activities;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.parse.videomore.R;

public class BaseListActivity extends ListActivity {
    // Основополагающий класс активности для активности списков категорий, проектов и роликов
	
    protected static final int PROGRESS_DIALOG = 0;
	
	protected Context mContext;
	protected ProgressDialog mProgressDialog;
	
	protected ListView mListView;
	protected ListAdapter mAdapter;
	
	public Handler mHandler = new Handler();
	
    // Выполняется при создании активности после конструктора
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        adjustLayout();	        
    }
    
    // Выполняется при изменении ориентации экрана
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	adjustLayout();
    }
    
    protected void adjustLayout() {	
		mListView = getListView(); 
    }
    
	@Override
	protected Dialog onCreateDialog(int id) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getText(R.string.loading));
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
		    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		        if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
		            return true; // Pretend we processed it
		        }
		        return false; // Any other keys are still processed as normal
		    }
		});
		return mProgressDialog;
	}
	
	// Метод показывает ProgressBar
    public void onShowProgress() {
    	if (mProgressDialog == null) {
    		showDialog(PROGRESS_DIALOG);
    	}
    }

    // Метод скрывает ProgressBar
    public void onHideProgress() {
    	if (mProgressDialog != null) {
	    	if (mProgressDialog.isShowing()) {
	    		removeDialog(PROGRESS_DIALOG);
	    		mProgressDialog = null;
	    	}
    	}
    }
}