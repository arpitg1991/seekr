package models;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
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
import java.util.List;
import java.util.Set;
import java.util.ArrayList ; 

import org.bson.types.ObjectId;
public class mongoDBHandler {
	
	public static MongoClient mongoClient ;
	public static DB db ;
	public static Set<String> colls ;
	public mongoDBHandler() throws UnknownHostException{
        MongoClientURI uri  = new MongoClientURI("mongodb://arpitg1991:arpit1@ds047447.mongolab.com:47447/seekr"); 
        
		mongoClient = new MongoClient( uri);
		mongoClient.setWriteConcern(WriteConcern.JOURNALED);
		db = mongoClient.getDB( "seekr" );
		colls = db.getCollectionNames();
		for (String s : colls){
			System.out.println(s);
		}
	}
	public void addPost(JSONObject post) throws JSONException{
		DBCollection coll = db.getCollection("posts");
		ObjectId postId = new ObjectId () ;
		//postId.getTimeSecond();
		System.out.println("TIME");
		System.out.println(postId.getTimeSecond());
		String userId = post.getJSONObject("post").getString("userId") ;
		String text = post.getJSONObject("post").getString("text") ;
		String catId = post.getJSONObject("post").getString("catId") ;
		catId = "E";
		String lat = post.getJSONObject("post").getString("lat") ;
		String lon = post.getJSONObject("post").getString("lon") ;
		String expireT  = post.getJSONObject("post").getString("exp") ;
		long expireDur = Long.parseLong(expireT);
		
		double lonD = Double.parseDouble(lat);
		double latD = Double.parseDouble(lon);
		List point = new ArrayList() ; 
		point.add(new double[] {latD,lonD});
		
		java.util.Date postDate= new java.util.Date();
		Timestamp postTime = new Timestamp(postDate.getTime());
		long expireSec = postTime.getTime();
		expireSec += expireDur ; 
		java.util.Date expireDate= new java.util.Date();
		expireDate.setTime(expireSec);
		
		int likes = 0 ; 
		
		BasicDBObject postDoc = new BasicDBObject("_id", postId.toString()).
                //append("userId", userId).
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
		List point = new ArrayList() ; 
		DBCollection coll = db.getCollection("comments");
		ObjectId commentId = new ObjectId () ;
		java.util.Date commentDate= new java.util.Date();
		
		
		BasicDBObject commentDoc = new BasicDBObject("commentId", commentId.toString()).
				append("postId",postId).
                append("userId", userId).
                append("text", text).
                append("commentTime",commentDate) ;
                
		coll.insert(commentDoc);
	}
	public JSONObject getPost (JSONObject jQuery) throws JSONException{
		/*{
		    //"location": {
		    //    "$near": {
		            "$geometry": {
		                "type": "Point",
		                "coordinates": [
		                    40,
		                    5
		                ]
		            },
		            "$maxDistance": 500
		        }
		    }
		}*/
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
		point.add(new double[] {latD,lonD});
		
		BasicDBObject query = new BasicDBObject("location", 
				new BasicDBObject("$near", 
						new BasicDBObject("$geometry",
								new BasicDBObject("type","Point").
								append("coordinates",point))).
								append("$maxDistance",maxDis)); 
		DBCursor cursor = coll.find();
		
		try {
			   while(cursor.hasNext()) {
			       DBObject queryItem = cursor.next() ;
			       //queryItem.get("");
			       JSONObject postObj = new JSONObject(queryItem.toMap() );
			       postItems.add(postObj);
				   System.out.println(queryItem);
			   }
			} finally {
			   cursor.close();
			}
		postList.put("post",postItems);
		return postList ; 
	}

}