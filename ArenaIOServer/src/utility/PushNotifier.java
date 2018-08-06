package utility;

import java.sql.ResultSet;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class PushNotifier extends Thread {

	public static String[] chestNames = new String[] {
		"Common",
		"Heavy",
		"Royal",
		"Magic",
		"Super",
		// Tutorial
	};
	
	public static void main(String[] args) {
		DatabaseInterface.instanceCheck();
		while (true) {
			try {
				long time = System.currentTimeMillis();
				
				
				// Check for chests that have been unlocked but not yet seen
				
				ResultSet rs = DatabaseInterface.stmt(
						"SELECT Chests.ID, Accounts.PushDeviceCode, Chests.Type "
						+ "FROM Chests join Accounts on Accounts.ID = AccountID "
						+ "WHERE Notified=0 and UnlockEndTime < ? and Accounts.PushDeviceCode IS NOT NULL AND Accounts.Online = 0", false, time);
				
				boolean any = false;
				while (rs.next()) {
					any = true; // Has issue with online being 1 and no other devices to update
					String PushDeviceCode = rs.getString("PushDeviceCode");
					//System.out.println("Pushing to " + PushDeviceCode);
					byte type = rs.getByte("Type");
					String notificationTitle = chestNames[type] + " chest unlocked!";
					String notification = "Log in to unlock your chest.";
					
					String strJsonBody = "{"
		                      +   "\"app_id\": \"c86bf3e0-16af-4c9d-9c72-e5af7055c78c\","
		                      +   "\"include_player_ids\": [\""+PushDeviceCode+"\"],"
		                      +   "\"data\": {\""+notificationTitle+"\": \""+notification+"\"},"
		                      +   "\"contents\": {\"en\": \""+notificationTitle+"\"}"
		                      + "}";
					
					HttpResponse<JsonNode> resp = Unirest.post("https://onesignal.com/api/v1/notifications")
					.header("Content-Type", "application/json; charset=UTF-8")
					.header("authorization", "Basic ZTAwNDMxYTQtM2IwYi00MDFlLWExYmUtOGZjMGZmNjcwNzY2")
					.body(strJsonBody).asJson();
					
					//System.out.println(resp);
					//System.out.println(resp.getBody());
					
					
				}
				
				if (any) {
					DatabaseInterface.stmt(
							"Update Chests join Accounts on Accounts.ID = Chests.AccountID "
							+ "Set notified = 1 "
							+ "WHERE Notified=0 and UnlockEndTime < ? and Accounts.PushDeviceCode IS NOT NULL", true, time);
				}
				// Every 10 seconds, check the server for things that should be push notified
				Thread.sleep(10000);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
