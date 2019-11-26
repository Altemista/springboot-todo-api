package com.nttdata.nge.example.server.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class Resource<T> {

	private String url;
	private T content;

	public Resource(T content, String url) {
		this.content = content;
		this.url = url;
	}

	@JsonUnwrapped
	public T getContent() {
		return content;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return "Resource{" + "url='" + url + '\'' + ", content=" + content + '}';
	}
}
