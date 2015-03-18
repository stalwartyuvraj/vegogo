package com.models;

import java.io.Serializable;

public class Criteria implements Serializable {

	int criteria_id;
	String criteria_name;
	public int getCriteria_id() {
		return criteria_id;
	}
	public void setCriteria_id(int criteria_id) {
		this.criteria_id = criteria_id;
	}
	public String getCriteria_name() {
		return criteria_name;
	}
	public void setCriteria_name(String criteria_name) {
		this.criteria_name = criteria_name;
	}


}
