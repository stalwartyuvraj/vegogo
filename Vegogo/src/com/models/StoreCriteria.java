package com.models;

import java.io.Serializable;

public class StoreCriteria implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	int id,store_id,criteria_id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStore_id() {
		return store_id;
	}

	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}

	public int getCriteria_id() {
		return criteria_id;
	}

	public void setCriteria_id(int criteria_id) {
		this.criteria_id = criteria_id;
	}





}
