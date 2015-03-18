package com.db;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.models.Category;
import com.models.Criteria;
import com.models.Favorite;
import com.models.News;
import com.models.Photo;
import com.models.Store;
import com.models.StoreCriteria;

public class Queries {

	private DbHelper dbHelper;
	private  SQLiteDatabase db;

	private static Queries instance;

	public Queries(DbHelper dbHelper) {
		this.dbHelper = dbHelper;
		db=dbHelper.getDB();
	}

	public static synchronized Queries getInstance(DbHelper dbHelper)
	{
		if (instance == null)
			instance = new Queries(dbHelper);

		return instance;
	}

	public void deleteTable(String tableName) {
		if(db==null)
			db=dbHelper.getDB();

		try{
			db.delete(tableName, null, null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}

	public void insertNews(News entry) {
		if(db==null)
			db=dbHelper.getDB();

		ContentValues values = new ContentValues();
		values.put("news_content", entry.getNews_content());
		values.put("news_title", entry.getNews_title());
		values.put("news_url", entry.getNews_url());
		values.put("photo_url", entry.getPhoto_url());
		values.put("created_at", entry.getCreated_at());
		values.put("is_deleted", entry.getIs_deleted());
		values.put("news_id", entry.getNews_id());
		values.put("updated_at", entry.getUpdated_at());

		db.insert("news", null, values);

	}

	public void insertStore(Store entry) {

		if(db==null)
			db=dbHelper.getDB();
		ContentValues values = new ContentValues();
		values.put("email", entry.getEmail());
		values.put("phone_no", entry.getPhone_no());
		values.put("sms_no", entry.getSms_no());
		values.put("store_address", entry.getStore_address());
		values.put("store_desc", entry.getStore_desc());
		values.put("store_name", entry.getStore_name());
		values.put("website", entry.getWebsite());
		values.put("category_id", entry.getCategory_id());
		values.put("created_at", entry.getCreated_at());
		values.put("distance", entry.getDistance());

		values.put("featured", entry.getFeatured());
		values.put("icon_id", entry.getIcon_id());
		values.put("is_deleted", entry.getIs_deleted());
		values.put("lat", entry.getLat());
		values.put("lon", entry.getLon());
		values.put("rating_count", entry.getRating_count());
		values.put("rating_total", entry.getRating_total());
		values.put("store_id", entry.getStore_id());
		values.put("updated_at", entry.getUpdated_at());

		db.insert("stores", null, values);

	}

	public void updateStore(Store entry) {
		if(db==null)
			db=dbHelper.getDB();

		ContentValues values = new ContentValues();
		values.put("email", entry.getEmail());
		values.put("phone_no", entry.getPhone_no());
		values.put("sms_no", entry.getSms_no());
		values.put("store_address", entry.getStore_address());
		values.put("store_desc", entry.getStore_desc());
		values.put("store_name", entry.getStore_name());
		values.put("website", entry.getWebsite());
		values.put("category_id", entry.getCategory_id());
		values.put("created_at", entry.getCreated_at());
		values.put("distance", entry.getDistance());

		values.put("featured", entry.getFeatured());
		values.put("icon_id", entry.getIcon_id());
		values.put("is_deleted", entry.getIs_deleted());
		values.put("lat", entry.getLat());
		values.put("lon", entry.getLon());
		values.put("rating_count", entry.getRating_count());
		values.put("rating_total", entry.getRating_total());
		values.put("store_id", entry.getStore_id());
		values.put("updated_at", entry.getUpdated_at());
		values.put("updated_at", entry.getUpdated_at());

		db.update("stores", values, "store_id = " + entry.getStore_id(), null);

	}


	public void insertCriteria(Criteria entry) {

		if(db==null)
			db=dbHelper.getDB();

		ContentValues values = new ContentValues();
		values.put("criteria_id", entry.getCriteria_id());
		values.put("criteria_name", entry.getCriteria_name());

		db.insert("criteria", null, values);

	}

	public void insertStoreCriteria(StoreCriteria entry) {
		if(db==null)
			db=dbHelper.getDB();


		ContentValues values = new ContentValues();
		values.put("id", entry.getId());
		values.put("criteria_id", entry.getCriteria_id());
		values.put("store_id", entry.getStore_id());

		db.insert("store_criteria", null, values);

	}	


	public void insertCategory(Category entry) {

		if(db==null)
			db=dbHelper.getDB();

		ContentValues values = new ContentValues();
		values.put("category", entry.getCategory());
		values.put("category_icon", entry.getCategory_icon());
		values.put("category_id", entry.getCategory_id());
		values.put("created_at", entry.getCreated_at());
		values.put("is_deleted", entry.getIs_deleted());
		values.put("updated_at", entry.getUpdated_at());

		db.insert("categories", null, values);

	}

	public void insertPhoto(Photo entry) {
		if(db==null)
			db=dbHelper.getDB();

		ContentValues values = new ContentValues();
		values.put("photo_url", entry.getPhoto_url());
		values.put("thumb_url", entry.getThumb_url());
		values.put("created_at", entry.getCreated_at());
		values.put("is_deleted", entry.getIs_deleted());
		values.put("photo_id", entry.getPhoto_id());
		values.put("store_id", entry.getStore_id());
		values.put("updated_at", entry.getUpdated_at());

		db.insert("photos", null, values);

	}

	public void insertFavorite(Favorite entry) {

		if(db==null)
			db=dbHelper.getDB();

		ContentValues values = new ContentValues();
		values.put("store_id", entry.getStore_id());

		db.insert("favorites", null, values);

	}

	public void deleteFavorite(int store_id) {


		db.delete("favorites", "store_id = " + store_id, null);
	}

	public Favorite getFavoriteByStoreId(int storeId) {

		Favorite entry = null;
		String sql = String.format("SELECT * FROM favorites WHERE store_id = %d", storeId);

		Cursor mCursor = db.rawQuery(sql , null); 
		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {
				entry = new Favorite();
				entry.setFavorite_id( mCursor.getInt( mCursor.getColumnIndex("favorite_id")) );
				entry.setStore_id( mCursor.getInt( mCursor.getColumnIndex("store_id")) );
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return entry;
	}

	public ArrayList<News> getNews() {

		ArrayList<News> list = new ArrayList<News>();

		Cursor mCursor = db.rawQuery("SELECT * FROM news ORDER BY updated_at DESC", null); 
		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {

				News news = new News();
				news.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
				news.setIs_deleted( mCursor.getInt( mCursor.getColumnIndex("is_deleted")) );
				news.setNews_content( mCursor.getString( mCursor.getColumnIndex("news_content")) );
				news.setNews_id( mCursor.getInt( mCursor.getColumnIndex("news_id")) );
				news.setNews_title( mCursor.getString( mCursor.getColumnIndex("news_title")) );
				news.setNews_url( mCursor.getString( mCursor.getColumnIndex("news_url")) );
				news.setPhoto_url( mCursor.getString( mCursor.getColumnIndex("photo_url")) );
				news.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );

				list.add(news);
			} while (mCursor.moveToNext());
		}
		mCursor.close();

		return list;
	}

	public News getNewsByNewsId(int newsId) {

		News news = null;


		String sql = String.format("SELECT * FROM news WHERE news_id = %d", newsId);
		Cursor mCursor = db.rawQuery(sql, null); 
		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {

				news = new News();
				news.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
				news.setIs_deleted( mCursor.getInt( mCursor.getColumnIndex("is_deleted")) );
				news.setNews_content( mCursor.getString( mCursor.getColumnIndex("news_content")) );
				news.setNews_id( mCursor.getInt( mCursor.getColumnIndex("news_id")) );
				news.setNews_title( mCursor.getString( mCursor.getColumnIndex("news_title")) );
				news.setNews_url( mCursor.getString( mCursor.getColumnIndex("news_url")) );
				news.setPhoto_url( mCursor.getString( mCursor.getColumnIndex("photo_url")) );
				news.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );

			} while (mCursor.moveToNext());
		}
		mCursor.close();

		return news;
	}

	public ArrayList<Favorite> getFavorites() {

		ArrayList<Favorite> list = new ArrayList<Favorite>();

		Cursor mCursor = db.rawQuery("SELECT * FROM favorites", null); 
		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {

				Favorite fave = new Favorite();
				fave.setFavorite_id( mCursor.getInt( mCursor.getColumnIndex("favorite_id")) );
				fave.setStore_id( mCursor.getInt( mCursor.getColumnIndex("store_id")) );

				list.add(fave);
			} while (mCursor.moveToNext());
		}
		mCursor.close();

		return list;
	}


	public ArrayList<Store> getStores(int pos) {

		ArrayList<Store> list = new ArrayList<Store>();



		String q="SELECT * FROM stores ";

		if(pos!=0)
			q+=" where category_id="+pos;
		
		Cursor mCursor = db.rawQuery(q, null); 
		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {

				Store entry = new Store();
				entry.setCategory_id( mCursor.getInt( mCursor.getColumnIndex("category_id")) );
				entry.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
				entry.setDistance( mCursor.getDouble( mCursor.getColumnIndex("distance")) );
				entry.setEmail( mCursor.getString( mCursor.getColumnIndex("email")) );
				entry.setFeatured( mCursor.getInt( mCursor.getColumnIndex("featured")) );
				entry.setIcon_id( mCursor.getInt( mCursor.getColumnIndex("icon_id")) );
				entry.setIs_deleted( mCursor.getInt( mCursor.getColumnIndex("is_deleted")) );
				entry.setLat( mCursor.getDouble( mCursor.getColumnIndex("lat")) );
				entry.setLon( mCursor.getDouble( mCursor.getColumnIndex("lon")) );

				entry.setPhone_no( mCursor.getString( mCursor.getColumnIndex("phone_no")) );
				entry.setRating_count( mCursor.getInt( mCursor.getColumnIndex("rating_count")) );
				entry.setRating_total( mCursor.getInt( mCursor.getColumnIndex("rating_total")) );
				entry.setSms_no( mCursor.getString( mCursor.getColumnIndex("sms_no")) );

				entry.setStore_address( mCursor.getString( mCursor.getColumnIndex("store_address")) );
				entry.setStore_desc( mCursor.getString( mCursor.getColumnIndex("store_desc")) );
				entry.setStore_id( mCursor.getInt( mCursor.getColumnIndex("store_id")) );

				entry.setStore_name( mCursor.getString( mCursor.getColumnIndex("store_name")) );
				entry.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );
				entry.setWebsite( mCursor.getString( mCursor.getColumnIndex("website")) );

				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();

		return list;
	}

	public void  countCriteria()
	{

		Cursor mCursor = db.rawQuery("SELECT COUNT(*) FROM store_criteria", null); 
		mCursor.moveToFirst();
		Log.d("tag","Count store criteria "+mCursor.getInt(0));
		mCursor.close();

		Cursor mCursor2 = db.rawQuery("SELECT COUNT(*) FROM criteria", null); 
		mCursor2.moveToFirst();
		Log.d("tag","Count criteria"+mCursor2.getInt(0));
		mCursor2.close();	

		Cursor mCursor3 = db.rawQuery("SELECT COUNT(*) FROM stores", null); 
		mCursor3.moveToFirst();
		Log.d("tag","Count stores "+mCursor3.getInt(0));
		mCursor3.close();	
	}

	
	public ArrayList<Store> getStoresByCategoryId(int categoryId) {

		ArrayList<Store> list = new ArrayList<Store>();


		String sql = String.format("SELECT * FROM stores WHERE category_id = %d", categoryId);
		Cursor mCursor = db.rawQuery(sql, null); 
		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {

				Store entry = new Store();
				entry.setCategory_id( mCursor.getInt( mCursor.getColumnIndex("category_id")) );
				entry.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
				entry.setDistance( mCursor.getDouble( mCursor.getColumnIndex("distance")) );
				entry.setEmail( mCursor.getString( mCursor.getColumnIndex("email")) );
				entry.setFeatured( mCursor.getInt( mCursor.getColumnIndex("featured")) );
				entry.setIcon_id( mCursor.getInt( mCursor.getColumnIndex("icon_id")) );
				entry.setIs_deleted( mCursor.getInt( mCursor.getColumnIndex("is_deleted")) );
				entry.setLat( mCursor.getDouble( mCursor.getColumnIndex("lat")) );
				entry.setLon( mCursor.getDouble( mCursor.getColumnIndex("lon")) );

				entry.setPhone_no( mCursor.getString( mCursor.getColumnIndex("phone_no")) );
				entry.setRating_count( mCursor.getInt( mCursor.getColumnIndex("rating_count")) );
				entry.setRating_total( mCursor.getInt( mCursor.getColumnIndex("rating_total")) );
				entry.setSms_no( mCursor.getString( mCursor.getColumnIndex("sms_no")) );

				entry.setStore_address( mCursor.getString( mCursor.getColumnIndex("store_address")) );
				entry.setStore_desc( mCursor.getString( mCursor.getColumnIndex("store_desc")) );
				entry.setStore_id( mCursor.getInt( mCursor.getColumnIndex("store_id")) );

				entry.setStore_name( mCursor.getString( mCursor.getColumnIndex("store_name")) );
				entry.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );
				entry.setWebsite( mCursor.getString( mCursor.getColumnIndex("website")) );

				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();

		return list;
	}


	public Store getStoresByStoreId(int storeId) {

		Store entry = null;


		String sql = String.format("SELECT * FROM stores WHERE store_id = %d", storeId);
		Cursor mCursor = db.rawQuery(sql, null); 
		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {

				entry = new Store();
				entry.setCategory_id( mCursor.getInt( mCursor.getColumnIndex("category_id")) );
				entry.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
				entry.setDistance( mCursor.getDouble( mCursor.getColumnIndex("distance")) );
				entry.setEmail( mCursor.getString( mCursor.getColumnIndex("email")) );
				entry.setFeatured( mCursor.getInt( mCursor.getColumnIndex("featured")) );
				entry.setIcon_id( mCursor.getInt( mCursor.getColumnIndex("icon_id")) );
				entry.setIs_deleted( mCursor.getInt( mCursor.getColumnIndex("is_deleted")) );
				entry.setLat( mCursor.getDouble( mCursor.getColumnIndex("lat")) );
				entry.setLon( mCursor.getDouble( mCursor.getColumnIndex("lon")) );

				entry.setPhone_no( mCursor.getString( mCursor.getColumnIndex("phone_no")) );
				entry.setRating_count( mCursor.getInt( mCursor.getColumnIndex("rating_count")) );
				entry.setRating_total( mCursor.getInt( mCursor.getColumnIndex("rating_total")) );
				entry.setSms_no( mCursor.getString( mCursor.getColumnIndex("sms_no")) );

				entry.setStore_address( mCursor.getString( mCursor.getColumnIndex("store_address")) );
				entry.setStore_desc( mCursor.getString( mCursor.getColumnIndex("store_desc")) );
				entry.setStore_id( mCursor.getInt( mCursor.getColumnIndex("store_id")) );

				entry.setStore_name( mCursor.getString( mCursor.getColumnIndex("store_name")) );
				entry.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );
				entry.setWebsite( mCursor.getString( mCursor.getColumnIndex("website")) );

			} while (mCursor.moveToNext());
		}
		mCursor.close();

		return entry;
	}

	public ArrayList<Store> getStoresFeatured() {

		ArrayList<Store> list = new ArrayList<Store>();

		Cursor mCursor = db.rawQuery("SELECT * FROM stores WHERE featured = 1", null); 
		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {

				Store entry = new Store();
				entry.setCategory_id( mCursor.getInt( mCursor.getColumnIndex("category_id")) );
				entry.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
				entry.setDistance( mCursor.getDouble( mCursor.getColumnIndex("distance")) );
				entry.setEmail( mCursor.getString( mCursor.getColumnIndex("email")) );
				entry.setFeatured( mCursor.getInt( mCursor.getColumnIndex("featured")) );
				entry.setIcon_id( mCursor.getInt( mCursor.getColumnIndex("icon_id")) );
				entry.setIs_deleted( mCursor.getInt( mCursor.getColumnIndex("is_deleted")) );
				entry.setLat( mCursor.getDouble( mCursor.getColumnIndex("lat")) );
				entry.setLon( mCursor.getDouble( mCursor.getColumnIndex("lon")) );

				entry.setPhone_no( mCursor.getString( mCursor.getColumnIndex("phone_no")) );
				entry.setRating_count( mCursor.getInt( mCursor.getColumnIndex("rating_count")) );
				entry.setRating_total( mCursor.getInt( mCursor.getColumnIndex("rating_total")) );
				entry.setSms_no( mCursor.getString( mCursor.getColumnIndex("sms_no")) );

				entry.setStore_address( mCursor.getString( mCursor.getColumnIndex("store_address")) );
				entry.setStore_desc( mCursor.getString( mCursor.getColumnIndex("store_desc")) );
				entry.setStore_id( mCursor.getInt( mCursor.getColumnIndex("store_id")) );

				entry.setStore_name( mCursor.getString( mCursor.getColumnIndex("store_name")) );
				entry.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );
				entry.setWebsite( mCursor.getString( mCursor.getColumnIndex("website")) );

				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();

		return list;
	}

	public Photo getPhotoByStoreId(int storeId) {

		Photo entry = null;


		String sql = String.format("SELECT * FROM photos WHERE store_id = %d ORDER BY photo_id ASC", storeId);
		Cursor mCursor = db.rawQuery(sql, null); 
		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {

				entry = new Photo();
				entry.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
				entry.setIs_deleted( mCursor.getInt( mCursor.getColumnIndex("is_deleted")) );
				entry.setPhoto_id( mCursor.getInt( mCursor.getColumnIndex("photo_id")) );
				entry.setPhoto_url( mCursor.getString( mCursor.getColumnIndex("photo_url")) );
				entry.setStore_id( mCursor.getInt( mCursor.getColumnIndex("store_id")) );
				entry.setThumb_url( mCursor.getString( mCursor.getColumnIndex("thumb_url")) );
				entry.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );

			} while (mCursor.moveToNext());
		}
		mCursor.close();

		return entry;
	}

	public ArrayList<Photo> getPhotosByStoreId(int storeId) {

		ArrayList<Photo> list = new ArrayList<Photo>();


		String sql = String.format("SELECT * FROM photos WHERE store_id = %d", storeId);
		Cursor mCursor = db.rawQuery(sql, null);  
		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {

				Photo entry = new Photo();
				entry.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
				entry.setIs_deleted( mCursor.getInt( mCursor.getColumnIndex("is_deleted")) );
				entry.setPhoto_id( mCursor.getInt( mCursor.getColumnIndex("photo_id")) );
				entry.setPhoto_url( mCursor.getString( mCursor.getColumnIndex("photo_url")) );
				entry.setStore_id( mCursor.getInt( mCursor.getColumnIndex("store_id")) );
				entry.setThumb_url( mCursor.getString( mCursor.getColumnIndex("thumb_url")) );
				entry.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );

				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();

		return list;
	}


	public ArrayList<Category> getCategories() {

		ArrayList<Category> list = new ArrayList<Category>();

		Cursor mCursor = db.rawQuery("SELECT * FROM categories ORDER BY category ASC", null); 
		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {

				Category entry = new Category();
				entry.setCategory( mCursor.getString( mCursor.getColumnIndex("category")) );
				entry.setCategory_icon( mCursor.getString( mCursor.getColumnIndex("category_icon")) );
				entry.setCategory_id( mCursor.getInt( mCursor.getColumnIndex("category_id")) );
				entry.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
				entry.setIs_deleted( mCursor.getInt( mCursor.getColumnIndex("is_deleted")) );
				entry.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );

				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();

		return list;
	}

	public ArrayList<Store> getStoresFavorites() {

		ArrayList<Store> list = new ArrayList<Store>();

		Cursor mCursor = db.rawQuery("SELECT * FROM stores INNER JOIN favorites ON stores.store_id = favorites.store_id ORDER BY stores.store_name", null); 
		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {

				Store entry = new Store();
				
				entry.setCategory_id( mCursor.getInt( mCursor.getColumnIndex("category_id")) );
				entry.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
				entry.setDistance( mCursor.getDouble( mCursor.getColumnIndex("distance")) );
				entry.setEmail( mCursor.getString( mCursor.getColumnIndex("email")) );
				entry.setFeatured( mCursor.getInt( mCursor.getColumnIndex("featured")) );
				entry.setIcon_id( mCursor.getInt( mCursor.getColumnIndex("icon_id")) );
				entry.setIs_deleted( mCursor.getInt( mCursor.getColumnIndex("is_deleted")) );
				entry.setLat( mCursor.getDouble( mCursor.getColumnIndex("lat")) );
				entry.setLon( mCursor.getDouble( mCursor.getColumnIndex("lon")) );

				entry.setPhone_no( mCursor.getString( mCursor.getColumnIndex("phone_no")) );
				entry.setRating_count( mCursor.getInt( mCursor.getColumnIndex("rating_count")) );
				entry.setRating_total( mCursor.getInt( mCursor.getColumnIndex("rating_total")) );
				entry.setSms_no( mCursor.getString( mCursor.getColumnIndex("sms_no")) );

				entry.setStore_address( mCursor.getString( mCursor.getColumnIndex("store_address")) );
				entry.setStore_desc( mCursor.getString( mCursor.getColumnIndex("store_desc")) );
				entry.setStore_id( mCursor.getInt( mCursor.getColumnIndex("store_id")) );

				entry.setStore_name( mCursor.getString( mCursor.getColumnIndex("store_name")) );
				entry.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );
				entry.setWebsite( mCursor.getString( mCursor.getColumnIndex("website")) );

				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();

		return list;
	}


	public ArrayList<String> getCategoryNames() {


		ArrayList<String> list = new ArrayList<String>();
		db=dbHelper.getDB();

		Cursor mCursor = db.rawQuery("SELECT * FROM categories ORDER BY category ASC", null); 
		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {
				list.add(mCursor.getString( mCursor.getColumnIndex("category")));
			} while (mCursor.moveToNext());
		}
		mCursor.close();

		return list;
	}


	public Category getCategoryByCategory(String cat) {

		Category entry = null;


		String sql = String.format("SELECT * FROM categories WHERE category = '%s' ORDER BY category ASC", cat);
		Cursor mCursor = db.rawQuery(sql, null); 
		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {

				entry = new Category();
				entry.setCategory( mCursor.getString( mCursor.getColumnIndex("category")) );
				entry.setCategory_icon( mCursor.getString( mCursor.getColumnIndex("category_icon")) );
				entry.setCategory_id( mCursor.getInt( mCursor.getColumnIndex("category_id")) );
				entry.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
				entry.setIs_deleted( mCursor.getInt( mCursor.getColumnIndex("is_deleted")) );
				entry.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );

			} while (mCursor.moveToNext());
		}
		mCursor.close();

		return entry;
	}





	public Category getCategoryByCategoryId(int categoryId) {

		Category entry = null;


		String sql = String.format("SELECT * FROM categories WHERE category_id = %d", categoryId);
		Cursor mCursor = db.rawQuery(sql, null); 
		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {

				entry = new Category();
				entry.setCategory( mCursor.getString( mCursor.getColumnIndex("category")) );
				entry.setCategory_icon( mCursor.getString( mCursor.getColumnIndex("category_icon")) );
				entry.setCategory_id( mCursor.getInt( mCursor.getColumnIndex("category_id")) );
				entry.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
				entry.setIs_deleted( mCursor.getInt( mCursor.getColumnIndex("is_deleted")) );
				entry.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );

			} while (mCursor.moveToNext());
		}
		mCursor.close();

		return entry;
	}


	public ArrayList<Store> getStoresByCriteria2(ArrayList<Integer> arrSels,int catId) {

		ArrayList<Store> list = new ArrayList<Store>();
		ArrayList<Store> filterList=new ArrayList<Store>();

		String qu="";
		

		if(catId!=0)
			qu="SELECT * FROM stores S where category_id="+catId;
		else
			qu="SELECT * FROM stores S";


		Cursor mCursor = db.rawQuery(qu, null); 

		Log.d("tag","Fetch Stores by cat "+qu);

		mCursor.moveToFirst();

		if (!mCursor.isAfterLast()) {
			do {
				Store entry = new Store();
				entry.setCategory_id( mCursor.getInt( mCursor.getColumnIndex("category_id")) );
				entry.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
				entry.setDistance( mCursor.getDouble( mCursor.getColumnIndex("distance")) );
				entry.setEmail( mCursor.getString( mCursor.getColumnIndex("email")) );
				entry.setFeatured( mCursor.getInt( mCursor.getColumnIndex("featured")) );
				entry.setIcon_id( mCursor.getInt( mCursor.getColumnIndex("icon_id")) );
				entry.setIs_deleted( mCursor.getInt(mCursor.getColumnIndex("is_deleted")) );
				entry.setLat( mCursor.getDouble(mCursor.getColumnIndex("lat")) );
				entry.setLon( mCursor.getDouble(mCursor.getColumnIndex("lon")) );

				entry.setPhone_no( mCursor.getString( mCursor.getColumnIndex("phone_no")) );
				entry.setRating_count( mCursor.getInt( mCursor.getColumnIndex("rating_count")) );
				entry.setRating_total( mCursor.getInt( mCursor.getColumnIndex("rating_total")) );
				entry.setSms_no( mCursor.getString( mCursor.getColumnIndex("sms_no")) );

				entry.setStore_address( mCursor.getString( mCursor.getColumnIndex("store_address")) );
				entry.setStore_desc( mCursor.getString( mCursor.getColumnIndex("store_desc")) );
				entry.setStore_id( mCursor.getInt( mCursor.getColumnIndex("store_id")) );

				entry.setStore_name(mCursor.getString(mCursor.getColumnIndex("store_name")) );
				entry.setUpdated_at(mCursor.getInt(mCursor.getColumnIndex("updated_at")) );
				entry.setWebsite(mCursor.getString(mCursor.getColumnIndex("website")) );

				list.add(entry);

			} while (mCursor.moveToNext());
		}
		mCursor.close();


		for(int i=0;i<list.size();i++)
		{

			qu="select * from store_criteria where store_id="+list.get(i).getStore_id();

			Log.d("tag","Store Criterian Query  "+i+"  "+qu);

			Cursor mCursor1 = db.rawQuery(qu, null); 
			mCursor1.moveToFirst();

			ArrayList<Integer> scList=new ArrayList<Integer>();
			if (!mCursor1.isAfterLast()) {
				do {
					scList.add(mCursor1.getInt(mCursor1.getColumnIndex("criteria_id")));
				}while (mCursor1.moveToNext());
			}

			Log.d("tag", scList.size()+" : "+arrSels.size());

			if(scList.containsAll(arrSels))//doesExists(scList,arrSels))
			{
				Log.d("tag", scList.size()+" : "+arrSels.size());
				filterList.add(list.get(i));
			}

	

		}
		return filterList;
	}
	
	
	

	
}
