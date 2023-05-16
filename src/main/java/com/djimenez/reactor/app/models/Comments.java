package com.djimenez.reactor.app.models;

import java.util.ArrayList;
import java.util.List;

public class Comments {
	
	private List<String> commentList;

	public Comments() {
		this.commentList = new ArrayList<>();
	}

	public void addComment(String comment) {
		this.commentList.add(comment);
	}

	@Override
	public String toString() {
		return "Comments = " + commentList;
	}
	
	
	
	
	
}
