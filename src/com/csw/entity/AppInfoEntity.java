package com.csw.entity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
/**
 * app信息
 * @author lgj
 *
 */
public class AppInfoEntity {

	/**
	 * appId
	 */
	private String appId;

	/**
	 * app名称
	 */
	private String appName;
	/**
	 * app图标
	 */
	private Bitmap appIcon;
	/**
	 * app图标地址
	 */
	private String appIconUrl;
	/**
	 * app地址
	 */
	private String appUrl;
	/**
	 * app版本
	 */
	private String appVersion;
	/**
	 * app大小
	 */
	private String appSize;

	public AppInfoEntity(String appName, Bitmap appIcon, String appIconUrl,
			String appUrl, String appVersion, String appSize) {
		super();
		this.appName = appName;
		this.appIcon = appIcon;
		this.appIconUrl = appIconUrl;
		this.appUrl = appUrl;
		this.appVersion = appVersion;
		this.appSize = appSize;
	}

	public AppInfoEntity(String appId, String appName, Bitmap appIcon,
			 String appUrl, String appVersion, String appSize) {
		super();
		this.appId = appId;
		this.appName = appName;
		this.appIcon = appIcon;
		this.appUrl = appUrl;
		this.appVersion = appVersion;
		this.appSize = appSize;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public AppInfoEntity() {
		super();
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Bitmap getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Bitmap appIcon) {
		this.appIcon = appIcon;
	}

	public String getAppIconUrl() {
		return appIconUrl;
	}

	public void setAppIconUrl(String appIconUrl) {
		this.appIconUrl = appIconUrl;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getAppSize() {
		return appSize;
	}

	public void setAppSize(String appSize) {
		this.appSize = appSize;
	}

}
