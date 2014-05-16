package models;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.mongodb.BasicDBList;
import com.mongodb.CommandResult;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;
import com.mongodb.util.JSON;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.ArrayList ; 

import org.bson.types.ObjectId;
public class mongoDBHandler {
	
	public static MongoClient mongoClient ;
	public static DB db ;
	public static Set<String> colls ;
	public static final String DBNAME = "seekrdb" ;
	public static final String dbUser = "arpitg1991" ; 
	public static final String dbPassword = "arpit1" ; 
	public mongoDBHandler() throws UnknownHostException{
        
		MongoClientURI uri  = new MongoClientURI("mongodb://" + dbUser +":" + dbPassword + "@ds045089.mongolab.com:45089/" + DBNAME); 
        
		mongoClient = new MongoClient( uri);
		mongoClient.setWriteConcern(WriteConcern.JOURNALED);
		db = mongoClient.getDB(  DBNAME );
		colls = db.getCollectionNames();
		
	}
	public void addPost(JSONObject post) throws JSONException{
		DBCollection coll = db.getCollection("posts");
		ObjectId postId = new ObjectId () ;
		//postId.getTimeSecond();
		//System.out.println("TIME");
		//System.out.println(postId.getTimeSecond());
		String userId = post.getJSONObject("post").getString("userId") ;
		String text = post.getJSONObject("post").getString("text") ;
		String catId = post.getJSONObject("post").getString("catId") ;
		catId = "E";
		String lat = post.getJSONObject("post").getString("lat") ;
		String lon = post.getJSONObject("post").getString("lon") ;
		String expireT  = post.getJSONObject("post").getString("exp") ;
		long expireDur = Long.parseLong(expireT);
		
		double lonD = Double.parseDouble(lon);
		double latD = Double.parseDouble(lat);
		List point = new ArrayList() ; 
		point.add(lonD);
		point.add(latD) ; 
		
		java.util.Date postDate= new java.util.Date();
		Timestamp postTime = new Timestamp(postDate.getTime());
		long expireSec = postTime.getTime();
		expireSec += expireDur ; 
		java.util.Date expireDate= new java.util.Date();
		expireDate.setTime(expireSec);
		
		int likes = 0 ; 
		
		BasicDBObject postDoc = new BasicDBObject("_id", postId.toString()).
                append("userId", userId).
                append("text", text).
                append("location", new BasicDBObject("type", "Point").append("coordinates", point)).
                append("postTime",postDate).
                append("expireTime",expireDate).
                append("likes", likes ).
				append("catId",catId) ;
		coll.insert(postDoc);
	}
	public void addComment(JSONObject comment) throws JSONException{
		
		String userId = comment.getJSONObject("comment").getString("userId") ;
		String text = comment.getJSONObject("comment").getString("text") ;
		String postId = comment.getJSONObject("comment").getString("postId") ; 
		DBCollection coll = db.getCollection("comments");
		ObjectId commentId = new ObjectId () ;
		java.util.Date commentDate= new java.util.Date();
		
		
		BasicDBObject commentDoc = new BasicDBObject("_id", commentId.toString()).
				append("postId",postId).
                append("userId", userId).
                append("text", text).
                append("commentTime",commentDate) ;
                
		coll.insert(commentDoc);
		
		updateUniquePpl(postId) ;
		
		
	}
	
	public void updateUniquePpl(String postId) throws JSONException{
		DBCollection coll1 = db.getCollection("posts") ; 
		DBCollection coll = db.getCollection("comments");
		int maxPpl = -1 ; 
		JSONObject commentList = new JSONObject() ; 
		List<JSONObject> commentItems = new ArrayList<JSONObject>() ; 
		
		BasicDBObject query = new BasicDBObject("postId",postId) ;  
		
		DBCursor cursor1 = coll1.find(query) ;
		try {
			   while(cursor1.hasNext()) {
			       DBObject queryItem = cursor1.next() ;
			       JSONObject postObj = new JSONObject(queryItem.toMap() );
			        maxPpl = postObj.getInt("maxPpl") ;
				   
			   }
			} finally {
			   cursor1.close();
			}
		
		
		DBCursor cursor = coll.find(query);
		
		try {
			   while(cursor.hasNext()) {
			       DBObject queryItem = cursor.next() ;
			       JSONObject commentObj = new JSONObject(queryItem.toMap() );
			       commentItems.add(commentObj);
				   System.out.println(queryItem);
			   }
			} finally {
			   cursor.close();
			}
		commentList.put("comment",commentItems);
		
	}
	public JSONObject getCommentForPost(String postId ) throws JSONException{
		DBCollection coll = db.getCollection("comments");
		JSONObject commentList = new JSONObject() ; 
		List<JSONObject> commentItems = new ArrayList<JSONObject>() ; 
		BasicDBObject query = new BasicDBObject("postId",postId) ;  
		DBCursor cursor = coll.find(query);
		
		try {
			   while(cursor.hasNext()) {
			       DBObject queryItem = cursor.next() ;
			       JSONObject commentObj = new JSONObject(queryItem.toMap() );
			       commentItems.add(commentObj);
				   System.out.println(queryItem);
			   }
			} finally {
			   cursor.close();
			}
		commentList.put("comment",commentItems);
		return commentList ; 
		
	}
	
	public JSONObject getPost (JSONObject jQuery) throws JSONException{
		DBCollection coll = db.getCollection("posts");
		JSONObject postList = new JSONObject() ; 
		List<JSONObject> postItems = new ArrayList<JSONObject>() ; 
		String lat = jQuery.getJSONObject("query").getString("lat") ;
		String lon = jQuery.getJSONObject("query").getString("lon") ;
		String dist = jQuery.getJSONObject("query").getString("dist") ;
		int maxDis = Integer.parseInt(dist);
		double lonD = Double.parseDouble(lat);
		double latD = Double.parseDouble(lon);
		List point = new ArrayList() ; 
		point.add(lonD);
		point.add(latD) ; 
		BasicDBObject query = new BasicDBObject("location", 
				new BasicDBObject("$near", 
						new BasicDBObject("$geometry",
								new BasicDBObject("type","Point").
								append("coordinates",point)).
								append("$maxDistance",maxDis))); 
		System.out.println(query.toString()) ;
		DBCursor cursor = coll.find(query);
		
		try {
			   while(cursor.hasNext()) {
			       DBObject queryItem = cursor.next() ;
			       //queryItem.get("");
			       JSONObject postObj = new JSONObject(queryItem.toMap() );
			       
			       postItems.add(postObj);
				   //System.out.println(queryItem);
			   }
			} finally {
			   cursor.close();
			}
		postList.put("post",postItems);
		return postList ; 
	}
	public JSONObject searchPosts(String searchText ){
		final DBObject textSearchCommand = new BasicDBObject();
		String collectionName = "posts" ;
	    textSearchCommand.put("text", collectionName);
	    textSearchCommand.put("search", searchText);
	    JSONObject postList = new JSONObject() ; 
		List<JSONObject> postItems = new ArrayList<JSONObject>() ; 
		
	    final CommandResult commandResult = db.command(textSearchCommand);
	    System.out.println(searchText);
	    BasicDBList postsDbObjectList = new BasicDBList() ; 
	    
	    postsDbObjectList = (BasicDBList) commandResult.get("results");
	    
	    Iterator<Object> it = postsDbObjectList.iterator();
	    
	    while ( it.hasNext()) {
	    	BasicDBObject queryItem = (BasicDBObject) it.next();
	    	JSONObject postObj = new JSONObject(queryItem.toMap() );
		    postItems.add(postObj);
			   
	     
	    }
	    try {
			postList.put("post",postItems);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return postList;
	}

}
