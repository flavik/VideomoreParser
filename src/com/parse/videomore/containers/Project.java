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
public class Project implements Parcelable {
	// Класс описывающий требуемые поля для проектов videomore
	
	private static final String TAG = "Project parser";
	
	public String name;
	public String title;
	public String thumb;
	public String description;

// Методы используются для реализации интерфейса Parcelable (передача этого объекта между активностями (activity))
// begin
	public int describeContents() {
		// no op
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(title);
		dest.writeString(thumb);
		dest.writeString(description);
	}
	
	public static final Parcelable.Creator<Project> CREATOR = new Parcelable.Creator<Project>() {
		public Project createFromParcel(Parcel in) {
			return new Project(in);
		}
		
		public Project[] newArray(int size) {
			return new Project[size];
		}
	};

	private Project(Parcel in) {
		name = in.readString();
		title = in.readString();
		thumb = in.readString();
		description = in.readString();
	}
// end	
	public Project() {
	}

// Метод для получения массива проектов из xml (на основе SAXParser xml)
	public static ArrayList<Project> valuesOf(String resp) {
		try {
			Log.i(TAG, "Response: "+resp);
			/** Handling XML */
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			
			/** Create handler to handle XML Tags ( extends DefaultHandler ) */
			ProjectsXMLHandler projectsXMLHandler = new ProjectsXMLHandler();
			xr.setContentHandler(projectsXMLHandler);
			xr.parse(new InputSource(new StringReader(resp)));
			
			return projectsXMLHandler.mProjectsList;
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
		st = "name: "+this.name+"\n"
			+ "title: "+this.title+"\n"
			+ "thumb: "+this.thumb+"\n"
			+ "description: "+this.description;
		return st;
	}

// Обработчик xml тегов
	private static class ProjectsXMLHandler extends DefaultHandler {

		private Boolean currentElement = false;
		private String currentValue = null;
		public ArrayList<Project> mProjectsList = null;


		/** Called when tag starts ( ex:- <name>AndroidPeople</name>
		* -- <name> )*/
		@Override
		public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
	
			currentElement = true;
			currentValue = null;
	
			if (localName.equalsIgnoreCase("tracks")) {
				mProjectsList = new ArrayList<Project>();
			} else if (localName.equalsIgnoreCase("track")) {
				mProjectsList.add(new Project());
			}

		}

		/** Called when tag closing ( ex:- <name>AndroidPeople</name>
		* -- </name> )*/
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {

			currentElement = false;
	
			/** set value */
			if (localName.equalsIgnoreCase("name")) {
				mProjectsList.get(mProjectsList.size()-1).name = currentValue;
			} else if (localName.equalsIgnoreCase("title")) {
				mProjectsList.get(mProjectsList.size()-1).title = currentValue;
			} else if (localName.equalsIgnoreCase("small-thumbnail-url")) {
				mProjectsList.get(mProjectsList.size()-1).thumb = currentValue;
			} else if (localName.equalsIgnoreCase("description")) {
				mProjectsList.get(mProjectsList.size()-1).description = currentValue;
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