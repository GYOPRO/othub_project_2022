package chatbot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.project.othub.ChatbotProc;
import com.project.othub.NaverInform;
import com.project.othub.NaverService;

@Service("chatbotservice")
public class ChatbotService implements NaverService {

	@Override
	public String test(String request ) {
		return test(request, "open");
	}
	
	public String test(String request, String event) {
			

	        String chatbotMessage = "";

	        try {
	            String apiURL = NaverInform.chatbot_url;
	            String secretKey = NaverInform.chatbot_key;

	            URL url = new URL(apiURL);

	            String message = getReqMessage(request, event); //open / send
	            System.out.println("##" + message);

	            String encodeBase64String = makeSignature(message, secretKey);

	            HttpURLConnection con = (HttpURLConnection)url.openConnection();
	            con.setRequestMethod("POST");
	            con.setRequestProperty("Content-Type", "application/json;UTF-8");
	            con.setRequestProperty("X-NCP-CHATBOT_SIGNATURE", encodeBase64String);

	            // post request
	            con.setDoOutput(true);
	            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	            wr.write(message.getBytes("UTF-8"));
	            wr.flush();
	            wr.close();
	            int responseCode = con.getResponseCode();

	            BufferedReader br;

	            if(responseCode==200) { // Normal call
	                System.out.println(con.getResponseMessage());

	                BufferedReader in = new BufferedReader(
	                        new InputStreamReader(
	                                con.getInputStream()));
	                String decodedString;
	                while ((decodedString = in.readLine()) != null) {
	                    chatbotMessage = decodedString;
	                }
	                //chatbotMessage = decodedString;
	                in.close();

	            } else {  // Error occurred
	                chatbotMessage = con.getResponseMessage();
	            }
	        } catch (Exception e) {
	            System.out.println(e);
	        }

	        return chatbotMessage;
	    }//main end

	    public static String makeSignature(String message, String secretKey) {

	        String encodeBase64String = "";

	        try {
	            byte[] secrete_key_bytes = secretKey.getBytes("UTF-8");

	            SecretKeySpec signingKey = new SecretKeySpec(secrete_key_bytes, "HmacSHA256");
	            Mac mac = Mac.getInstance("HmacSHA256");
	            mac.init(signingKey);

	            byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
	            encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);//암호

	            return encodeBase64String;

	        } catch (Exception e){
	            System.out.println(e);
	        }

	        return encodeBase64String;

	    }//make... end

	    public static String getReqMessage(String voiceMessage, String event) {

	        String requestBody = "";

	        try {

	            JSONObject obj = new JSONObject();

	            long timestamp = new Date().getTime();

	            System.out.println("##"+timestamp);

	            obj.put("version", "v2");
	            obj.put("userId", "U47b00b58c90f8e47428af8b7bddc1231heo2");
	//=> userId is a unique code for each chat user, not a fixed value, recommend use UUID. use different id for each user could help you to split chat history for users.

	            obj.put("timestamp", timestamp);

	            JSONObject bubbles_obj = new JSONObject();

	            bubbles_obj.put("type", "text");

	            JSONObject data_obj = new JSONObject();
	            data_obj.put("description", voiceMessage);

	            bubbles_obj.put("type", "text");
	            bubbles_obj.put("data", data_obj);

	            JSONArray bubbles_array = new JSONArray();
	            bubbles_array.put(bubbles_obj);

	            obj.put("bubbles", bubbles_array);
	            obj.put("event", event);//send or open

	            requestBody = obj.toString();

	        } catch (Exception e){
	            System.out.println("## Exception : " + e);
	        }

	        return requestBody;

	    }//getReq... end

}






