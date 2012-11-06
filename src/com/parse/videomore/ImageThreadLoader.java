package com.parse.videomore;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.State;
import java.lang.ref.SoftReference;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

/**
 * @author Viacheslav Poluektov
 */
public class ImageThreadLoader {
	// Это класс предназначенный для загрузки и кэширования изображений

	private static final String TAG = "ImageThreadLoader";
	
	private static final int HARD_CACHE_CAPACITY = 256;
	// Global cache of images.
	// Using SoftReference to allow garbage collector to clean cache if needed
	private final ConcurrentHashMap<String, SoftReference<Bitmap>> Cache = 
			new ConcurrentHashMap<String,  SoftReference<Bitmap>>(HARD_CACHE_CAPACITY); 
	private final class QueueItem {
		public URL url;
		public ImageLoadedListener listener;
	}
	private final ArrayList<QueueItem> Queue = new ArrayList<QueueItem>();

	private final Handler handler = new Handler();	// Assumes that this is started from the main (UI) thread
	private QueueRunner runner = new QueueRunner();
	private Thread thread = new Thread(runner);
	
	/** Creates a new instance of the ImageThreadLoader */
	private ImageThreadLoader() {
	}

// Реализация singleton (единственный класс для всего приложения) - общий кэш изображений 
// begin     
	private static class ImageThreadLoaderHolder {  
		public static ImageThreadLoader instance = new ImageThreadLoader();  
	}  
		 
	public synchronized static ImageThreadLoader getInstance() {  
		return ImageThreadLoaderHolder.instance;  
	}  
//  end	
	/**
	 * Defines an interface for a callback that will handle
	 * responses from the thread loader when an image is done
	 * being loaded.
	 */
	public interface ImageLoadedListener {
		public void imageLoaded(Bitmap imageBitmap, String url);
	}

	/**
	 * Provides a Runnable class to handle loading
	 * the image from the URL and settings the
	 * ImageView on the UI thread.
	 */
	
// Извлечение из очереди запроса на загрузку, его обратка,
//	и обратный вызов интерфейса запроса (установка изображение в графический компонент) 
	
	
// Все действия происходят в отдельном одном потоке
	private class QueueRunner implements Runnable {
		public void run() {
			synchronized(this) {
				while(Queue.size() > 0) {
					final QueueItem item = Queue.remove(0);

					if (item != null) {			
						if ((Cache.containsKey(item.url.toString())) && (Cache.get(item.url.toString()).get() != null)) {
							// Use a handler to get back onto the UI thread for the update
				
							handler.post(new Runnable() {
								public void run() {
									if (item.listener != null) {
										// NB: There's a potential race condition here where the cache item could get
										//     garbage collected between when we post the runnable and it's executed.
										//     Ideally we would re-run the network load or something.
										SoftReference<Bitmap> ref = Cache.get(item.url.toString());
										if (ref != null ) {
											item.listener.imageLoaded(ref.get(), item.url.toString());
										}
									}
								}
							});
							//Log.d(TAG,"Load from cache: "+item.url.toString());
							
						} else {
							final Bitmap loadedBmp = readBitmapFromNetwork(item.url);
							if (loadedBmp != null) {
								// Use a handler to get back onto the UI thread for the update
								handler.post(new Runnable() {
									public void run() {
										if (item.listener != null) {
											item.listener.imageLoaded(loadedBmp, item.url.toString());
										}
									}
								});
								
								Cache.put(item.url.toString(), new SoftReference<Bitmap>(loadedBmp));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Queues up a URI to load an image from for a given image view.
	 *
	 * @param uri	The URI source of the image
	 * @param callback	The listener class to call when the image is loaded
	 * @throws MalformedURLException If the provided uri cannot be parsed
	 * @return A Bitmap image if the image is in the cache, else null.
	 */
	
// Попытка получить изображение сначала из кэша, если там нету, то ставим запрос в очередь
	public Bitmap loadImage(final String url, final ImageLoadedListener listener) 
			throws MalformedURLException {
		// If it's in the cache, just get it and quit it
		//Log.i(TAG, "Attempt load: "+url);
		if (Cache.containsKey(url)) {
			SoftReference<Bitmap> ref = Cache.get(url);
			if (ref.get() != null) {
				return ref.get();
			}
		}

		QueueItem item = new QueueItem();
		item.url = new URL(url);
		item.listener = listener;
		Queue.add(item);

		// start the thread if needed
		if (thread.getState() == State.NEW) {
			thread.start();
		} else if (thread.getState() == State.TERMINATED) {
			thread = new Thread(runner);
			thread.start();
		}
		return null;
	}	
	/**
	 * Convenience method to retrieve a bitmap image from
	 * a URL over the network. The built-in methods do
	 * not seem to work, as they return a FileNotFound
	 * exception.
	 *
	 * Note that this does not perform any threading --
	 * it blocks the call while retrieving the data.
	 *
	 * @param url The URL to read the bitmap from.
	 * @return A Bitmap image or null if an error occurs.
	 */
// Прямая загрузка и декодирование изображения по ссылке
	public static Bitmap readBitmapFromNetwork (URL url) {
		InputStream is = null;
		BufferedInputStream bis = null;
		Bitmap bmp = null;
		try {
			Log.d(TAG, "Load from internet: " + url);
			URLConnection conn = url.openConnection();
			try {
				conn.connect();
				is = conn.getInputStream();
				if (is == null) {
					Log.w(TAG, "Got null on getInputStream method");
				}
				bis = Build.VERSION.SDK_INT < 9 ? new BufferedInputStream(new FlushedInputStream(is)) : new BufferedInputStream(is, 8192);
				bmp = BitmapFactory.decodeStream(bis);
				if (bmp == null) {
					Log.w(TAG, "Couldn't decode stream - bitmap is null");
				}
			} catch (ConnectException e) {
				Log.e(TAG,"Connect not established");
				return null;
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, "Bad URL", e);
			return null;
		} catch (IOException e) {
			Log.e(TAG, "Could not get remote an image", e);
			return null;
		} finally {
			try {
				if( is != null )
					is.close();
				if( bis != null )
					bis.close();
			} catch (IOException e) {
				Log.w(TAG, "Error closing stream.");
			}
		}
		return bmp;
	}

	// Вспомогательный класс для чтения потока из сети
	private static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}
		
		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int bbyte = read();
					if (bbyte < 0) {
						break;  // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}
}