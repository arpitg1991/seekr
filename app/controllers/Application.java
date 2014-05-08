package controllers;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import models.mongoDBHandler;

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
  	Map<String, String> post = new HashMap<String, String>();
  	post.put("userId","123");
  	post.put("catId","E");
  	post.put("text",comment);
  	post.put("lat", "40.808142");
  	post.put("lon","-73.960543");
  	post.put("exp","900");
  	mongoDBHandler mdbh = new mongoDBHandler();
    String receivedJSON = request.params.get("body") ;
  	JSONObject postItem = new JSONObject(receivedJSON);
  	//postItem.put("post", post);
      mdbh.addPost(postItem);
     return postItem.toString();
     // index();
  }
  public static void createComment(String comment ) throws Exception
  {
  	System.out.println("in Create") ; 
  	Map<String, String> post = new HashMap<String, String>();
  	post.put("userId","123");
  	post.put("postId","1234");
  	post.put("text",comment);
  	
  	
  	mongoDBHandler mdbh = new mongoDBHandler();
  	String receivedJSON = request.params.get("body") ;
  	JSONObject postItem = new JSONObject(receivedJSON);
  	
  	
  	//postItem.put("post", post);
      mdbh.addPost(postItem);
      index();
  }
  public static void getPost (String lat, String lon) throws Exception{
  	System.out.println("in getpost") ;
  	System.out.println(lat + " "  + lon) ; 
  	Map<String, String> query = new HashMap<String, String>();
  	query.put("lat", "40.808160");
  	query.put("lon","-73.960560");
  	query.put("dist","500");
  	mongoDBHandler mdbh = new mongoDBHandler();
  	//String receivedJSON = request.params.get("body") ;
  	//JSONObject jQuery = new JSONObject(receivedJSON);
  	
  	JSONObject jQuery = new JSONObject();
  	jQuery.put("query", query);
    
  	JSONObject post = mdbh.getPost(jQuery);
    System.out.println(post.toString());
    renderJSON(post);
    //index() ;
  	//return ok(index.render("Your new application is ready."));
  }

  
}
