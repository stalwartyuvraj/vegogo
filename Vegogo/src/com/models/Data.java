package com.models;

import java.util.ArrayList;

public class Data {
	
	private ArrayList<Store> stores;
	private ArrayList<Category> categories;
	private ArrayList<Criteria> criteria;
	private ArrayList<StoreCriteria> storeCriterias;
	private ArrayList<Photo> photos;

	public ArrayList<StoreCriteria> getStoreCriterias() {
		return storeCriterias;
	}

	public void setStoreCriterias(ArrayList<StoreCriteria> s) {
		storeCriterias = s;
	}

	public ArrayList<Criteria> getCriteria() {
		return criteria;
	}

	public void setCriteria(ArrayList<Criteria> s) {
		criteria = s;
	}

	public void setStores(ArrayList<Store> s) {
	    stores = s;
	}
	
	public ArrayList<Store> getStores() {
	    return stores;
	}
	
	public void setCategories(ArrayList<Category> s) {
		categories = s;
	}
	
	public ArrayList<Category> getCategories() {
	    return categories;
	}
	
	public void setPhotos(ArrayList<Photo> s) {
	    photos = s;
	}
	
	public ArrayList<Photo> getPhotos() {
	    return photos;
	}
}
