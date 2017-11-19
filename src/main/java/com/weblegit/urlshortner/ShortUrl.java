package com.weblegit.urlshortner;

import java.io.Serializable;

public class ShortUrl implements Serializable {

	private static final long serialVersionUID = 3230219612676759605L;

	public ShortUrl(String url, String shortCode) {
		this.url = url;
		this.shortCode = shortCode;
	}

	private String url;
	private String shortCode;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

}
