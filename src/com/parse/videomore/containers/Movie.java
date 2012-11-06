/**
 * 
 */
package com.parse.videomore.containers;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * @author flavik
 *
 */
public class Movie implements Parcelable {
	// Класс описывающий требуемые поля для роликов videomore
	
	private static final String TAG = "Movie parser";
	
	public String title;
	public int episode;
	public int views;
	public String thumb;

// Методы используются для реализации интерфейса Parcelable (передача этого объекта между активностями (activity))
// begin
	public int describeContents() {
		// no op
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeInt(episode);
		dest.writeInt(views);
		dest.writeString(thumb);
	}
	
	public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
		public Movie createFromParcel(Parcel in) {
			return new Movie(in);
		}
		
		public Movie[] newArray(int size) {
			return new Movie[size];
		}
	};

	private Movie(Parcel in) {
		title = in.readString();
		episode = in.readInt();
		views = in.readInt();
		thumb = in.readString();
	}
// end
	public Movie() {
	}

// Метод для получения массива роликов из xml (на основе SAXParser xml)
	public static ArrayList<Movie> valuesOf(String resp) {
		try {
			Log.i(TAG, "Response: "+resp);
			/** Handling XML */
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			
			/** Create handler to handle XML Tags ( extends DefaultHandler ) */
			MoviesXMLHandler moviesXMLHandler = new MoviesXMLHandler();
			xr.setContentHandler(moviesXMLHandler);
			xr.parse(new InputSource(new StringReader(resp)));
			
			return moviesXMLHandler.mMoviesList;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String toString() {
		String st = new String();
		st = "title: "+this.title+"\n"
			+ "episode: "+this.episode+"\n"	
			+ "views: "+this.views+"\n"	
			+ "thumb: "+this.thumb;
		return st;
	}
	
// Обработчик xml тегов	
	private static class MoviesXMLHandler extends DefaultHandler {

		private Boolean currentElement = false;
		private String currentValue = null;
		public ArrayList<Movie> mMoviesList = null;


		/** Called when tag starts ( ex:- <name>AndroidPeople</name>
		* -- <name> )*/
		@Override
		public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
	
			currentElement = true;
			currentValue = null;
			
			if (localName.equalsIgnoreCase("tracks")) {
				mMoviesList = new ArrayList<Movie>();
			} else if (localName.equalsIgnoreCase("track")) {
				mMoviesList.add(new Movie());
			}

		}

		/** Called when tag closing ( ex:- <name>AndroidPeople</name>
		* -- </name> )*/
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {

			currentElement = false;
	
			/** set value */
			if (localName.equalsIgnoreCase("title")) {
				mMoviesList.get(mMoviesList.size()-1).title = currentValue;
			} else if (localName.equalsIgnoreCase("episode")) {
				if ((currentValue != null) && (!currentValue.equals(""))) {
					mMoviesList.get(mMoviesList.size()-1).episode = Integer.valueOf(currentValue);
				}
			}  else if (localName.equalsIgnoreCase("views")) {
				if ((currentValue != null) && (!currentValue.equals(""))) {
					mMoviesList.get(mMoviesList.size()-1).views = Integer.valueOf(currentValue);
				}
			} else if (localName.equalsIgnoreCase("normal-thumbnail-url")) {
				mMoviesList.get(mMoviesList.size()-1).thumb = currentValue;
			}
		}

		/** Called to get tag characters ( ex:- <name>AndroidPeople</name>
		* -- to get AndroidPeople Character ) */
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {

			if (currentElement) {
				currentValue = new String(ch, start, length);
				currentElement = false;
			}

		}

	}
}