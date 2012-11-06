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
public class Category implements Parcelable {
	// Класс описывающий требуемые поля для категорий videomore
	
	private static final String TAG = "Category parser";
	
	public String name;
	public String title;

// Методы используются для реализации интерфейса Parcelable (передача этого объекта между активностями (activity))
// begin
	public int describeContents() {
		// no op
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(title);
	}
	
	public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
		public Category createFromParcel(Parcel in) {
			return new Category(in);
		}
		
		public Category[] newArray(int size) {
			return new Category[size];
		}
	};
	
	private Category(Parcel in) {
		name = in.readString();
		title = in.readString();
	}
// end
	public Category() {
	}
	
// Метод для получения массива категорий из xml (на основе SAXParser xml)
	public static ArrayList<Category> valuesOf(String resp) {
		try {
			Log.i(TAG, "Response: "+resp);
			/** Handling XML */
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			
			/** Create handler to handle XML Tags ( extends DefaultHandler ) */
			CategoriesXMLHandler categoriesXMLHandler = new CategoriesXMLHandler();
			xr.setContentHandler(categoriesXMLHandler);
			xr.parse(new InputSource(new StringReader(resp)));
			
			return categoriesXMLHandler.mCategoriesList;
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
			+"title: "+this.title;
		return st;
	}
	
// Обработчик xml тегов
	private static class CategoriesXMLHandler extends DefaultHandler {

		private Boolean currentElement = false;
		private String currentValue = null;
		public ArrayList<Category> mCategoriesList = null;


		/** Called when tag starts ( ex:- <name>AndroidPeople</name>
		* -- <name> )*/
		@Override
		public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
	
			currentElement = true;
			currentValue = null;
	
			if ( (localName.equalsIgnoreCase("categories")) && 
					(attributes.getValue("type").equals("array"))) {
				mCategoriesList = new ArrayList<Category>();
			} else if (localName.equalsIgnoreCase("category")) {
				mCategoriesList.add(new Category());
			}

		}

		/** Called when tag closing ( ex:- <name>AndroidPeople</name>
		* -- </name> )*/
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {

			currentElement = false;
	
			/** set value */
			if (localName.equalsIgnoreCase("name")) {
				mCategoriesList.get(mCategoriesList.size()-1).name = currentValue;
			} else if (localName.equalsIgnoreCase("title")) {
				mCategoriesList.get(mCategoriesList.size()-1).title = currentValue;
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