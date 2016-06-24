package com.csw.sax;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.csw.entity.AppInfoEntity;

public class XmlPaser extends DefaultHandler {

	private AppInfoEntity appInfoEntity;
	private List<AppInfoEntity> appInfoEntityList = new ArrayList<AppInfoEntity>();

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		
	}

	public XmlPaser(List<AppInfoEntity> appInfoEntityList) {
		this.appInfoEntityList = appInfoEntityList;
	}

	public XmlPaser() {
		super();
		
	}

	public List<AppInfoEntity> getAppInfoEntityList() {
		return appInfoEntityList;
	}

	public void setAppInfoEntityList(List<AppInfoEntity> appInfoEntityList) {
		this.appInfoEntityList = appInfoEntityList;
	}

	/*public AppInfoEntity getAppInfoEntity() {
		return appInfoEntity;
	}

	public void setAppInfoEntity(AppInfoEntity appInfoEntity) {
		this.appInfoEntity = appInfoEntity;
	}*/

	private String tagName = null;
	private String preTag = null;

	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (tagName != null) {

			if ("appName".equals(tagName)) {

				String appName = new String(ch, start, length);
				appInfoEntity.setAppName(appName);
			}else if ("appIconUrl".equals(tagName)) {
				String appIconUrl = new String(ch, start, length);

				Bitmap iconBitmap = returnBitMap(appIconUrl);
				appInfoEntity.setAppIconUrl(appIconUrl);
				appInfoEntity.setAppIcon(iconBitmap);
			}else if ("appUrl".equals(tagName)) {

				String appUrl = new String(ch, start, length);

				appInfoEntity.setAppUrl(appUrl);

			}else if ("appVersion".equals(tagName)) {

				String appVersion = new String(ch, start, length);

				appInfoEntity.setAppVersion(appVersion);

			}else if ("appSize".equals(tagName)) {
				String appSize = new String(ch, start, length);
				appInfoEntity.setAppSize(appSize);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		this.tagName=localName;
		if (tagName.equals("appinfo")) {
			appInfoEntityList.add(appInfoEntity);
			preTag = null;
		}
		tagName = null;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		this.tagName = localName;
		
		if ("appinfo".equals(tagName)) {
			appInfoEntity = new AppInfoEntity();
			String appId = attributes.getValue(0);
			appInfoEntity.setAppId(appId);
		}
		preTag = localName;// 将正在解析的节点名称赋给preTag=
	}

	public Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
