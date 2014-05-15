package controllers;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import models.mongoDBHandler;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.google.gson.Gson;

import play.*;
import play.mvc.*;
//import play.mvc.results.Result;

public class Application extends Controller {
  
  public static void  index() {
  	System.out.println("hello");
  	
    render() ; 
  }
  public static String createPost(String comment ) throws Exception
  {
  	System.out.println("in Create") ; 
  	
//	  	Map<String, String> post = new HashMap<String, String>();
//	  	post.put("userId","123");
//	  	post.put("catId","E");
//	  	post.put("text",comment);
//	  	post.put("lat", "40.808142");
//	  	post.put("lon","-73.960543");
//	  	post.put("exp","900");
  	mongoDBHandler mdbh = new mongoDBHandler();
    
  	String receivedJSON = request.params.get("body") ;
  	JSONObject postItem = new JSONObject(receivedJSON);
  	//postItem.put("post", post);
      mdbh.addPost(postItem);
     return postItem.toString();
     // index();
  }
  public static String createComment(String comment ) throws Exception
  {
  	System.out.println("in Create comments") ; 
  	Map<String, String> post = new HashMap<String, String>();
  	post.put("userId","123");
  	post.put("postId","1234");
  	post.put("text",comment);
  	mongoDBHandler mdbh = new mongoDBHandler();
  	//String receivedJSON = request.params.get("body") ;
  	JSONObject postItem = new JSONObject();
  	postItem.put("comment", post);
    mdbh.addComment(postItem);
    System.out.println(postItem) ;
    return  "ok" ;
    		//index();
  }
  public static JSONObject getComments ( String postId) throws JSONException, UnknownHostException{
	  System.out.println("in getComments") ; 
	  mongoDBHandler mdbh = new mongoDBHandler();
	  JSONObject comments = mdbh.getCommentForPost(postId);
	  
	  return comments ;
	  
  }
  public static JSONObject getPost (String lat, String lon, String dist) throws Exception{
  	System.out.println("in getpost") ;
  	System.out.println(lat + " "  + lon) ; 
  	Map<String, String> query = new HashMap<String, String>();
  	query.put("lat", lat);//"40.808160"
  	query.put("lon",lon);//"-73.960560"
  	query.put("dist",dist);//500
  	mongoDBHandler mdbh = new mongoDBHandler();
  	//String receivedJSON = request.params.get("body") ;
  	//JSONObject jQuery = new JSONObject(receivedJSON);
  	
  	JSONObject jQuery = new JSONObject();
  	jQuery.put("query", query);
    
  	JSONObject post = mdbh.getPost(jQuery);
    System.out.println(post.toString());
   // System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n") ; 
   // System.out.println(jQuery.toString() );
    //renderJSON(post);
    return post ;
    
    //index() ;
  	//return ok(index.render("Your new application is ready."));
  }
  public static JSONObject searchPosts(String searchText) throws UnknownHostException{
	  mongoDBHandler mdbh = new mongoDBHandler() ; 
	  JSONObject posts = mdbh.searchPosts(searchText) ;
	return posts; 
	  
  }
  
}
