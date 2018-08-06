package utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;

public class GoogleAPI {
	private static String PACKAGE_NAME = "com.Odessa.WarOfWizards";

	private static String GOOGLE_CLIENT_ID = "479096147338-8clg8lonlc3unuiu8nm744i13277lign.apps.googleusercontent.com";
	private static String GOOGLE_CLIENT_SECRET = "su0YQ7Z0EaCsxl8zZLjCOssc";
	private static String GOOGLE_REDIRECT_URI = "http://odessasoftware.com";
	private static String code = "4/6JIQS4ANItClTshm8sfUv3uWpKSDjJhT9WFjeKCptk0";
	private static String refreshToken = "1/FfQfLaB4rHhIcu4rPPKPO6hX7TffUl2KpN_tVPNYjao";
	private static String accessToken;
	
	public static void getRefreshToken() {
	    HttpClient client = new DefaultHttpClient();
	    HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
	    try 
	    {
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
	        nameValuePairs.add(new BasicNameValuePair("grant_type",    "authorization_code"));
	        nameValuePairs.add(new BasicNameValuePair("client_id",     GOOGLE_CLIENT_ID));
	        nameValuePairs.add(new BasicNameValuePair("client_secret", GOOGLE_CLIENT_SECRET));
	        nameValuePairs.add(new BasicNameValuePair("code", code));
	        nameValuePairs.add(new BasicNameValuePair("redirect_uri", GOOGLE_REDIRECT_URI));
	        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        org.apache.http.HttpResponse response = client.execute(post);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	        StringBuffer buffer = new StringBuffer();
	        for (String line = reader.readLine(); line != null; line = reader.readLine())
	        {
	            buffer.append(line);
	        }

	        System.out.println(buffer.toString());
	        JSONObject json = new JSONObject(buffer.toString());
	        String refreshToken = json.getString("refresh_token");                      
	        GoogleAPI.refreshToken = refreshToken;
	    }
	    catch (Exception e) { e.printStackTrace(); }
	}
	
	
	public static void getAccessToken() {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
		try 
		{
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		    nameValuePairs.add(new BasicNameValuePair("grant_type",    "refresh_token"));
		    nameValuePairs.add(new BasicNameValuePair("client_id",     GOOGLE_CLIENT_ID));
		    nameValuePairs.add(new BasicNameValuePair("client_secret", GOOGLE_CLIENT_SECRET));
		    nameValuePairs.add(new BasicNameValuePair("refresh_token", refreshToken));
		    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		    org.apache.http.HttpResponse response = client.execute(post);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    StringBuffer buffer = new StringBuffer();
		    for (String line = reader.readLine(); line != null; line = reader.readLine())
		    {
		        buffer.append(line);
		    }

		    JSONObject json = new JSONObject(buffer.toString());
		    String accessToken = json.getString("access_token");
		    
		    GoogleAPI.accessToken = accessToken;

		}
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public static String getProductOrderId(String productID, String purchaseToken){
		System.out.println("Getting");
		System.out.println("https://www.googleapis.com/androidpublisher/v2/applications/"+PACKAGE_NAME+"/purchases/products/"+productID+"/tokens/"+purchaseToken+"?access_token="+accessToken);
		GetRequest request = Unirest.get("https://www.googleapis.com/androidpublisher/v2/applications/"+PACKAGE_NAME+"/purchases/products/"+productID+"/tokens/"+purchaseToken+"?access_token="+accessToken);
		try {
			HttpResponse<JsonNode> json = request.asJson();
			String jsonStr = json.getBody().toString();
			
			if (jsonStr.contains("error\":")){
				getAccessToken();
				return getProductOrderId(productID, purchaseToken);
			}
			
			if (!jsonStr.contains("orderId")) return null;
			jsonStr = jsonStr.substring(jsonStr.indexOf("orderId\":\"") + 10);
			jsonStr = jsonStr.substring(0, jsonStr.indexOf("\""));
			return jsonStr;
		} catch (UnirestException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
