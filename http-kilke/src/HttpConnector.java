import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;



public class HttpConnector {
	private HttpClient client;
	
	public HttpConnector(){
		this.client = new DefaultHttpClient();
	}
	
	private int httpGet(String address){
		try{
			HttpGet request = new HttpGet(address);
			HttpResponse response = this.client.execute(request);
			
			BufferedReader rd = new BufferedReader
					(new InputStreamReader(response.getEntity().getContent()));
			
			StringBuilder builder = new StringBuilder();
			String tmp = "";
			while ((tmp = rd.readLine()) != null) {
			    builder.append(tmp);
			}
			
			String text = builder.toString();
			
			if (text == "<html>\n<head></head>\n<pre></pre>\n</html>") return 0;
			if (text == "<html>\n<head></head>\n<body>The Scheduling Service received empty parameter.</body>\n</html>") return 1;
			if (text == "Error 500 - Internal Server Error") return 2;
			
			System.out.print(text);
			return 3;
		} 
		catch(ClientProtocolException e) {
		      e.printStackTrace();
		      return 3;
		}
		catch(IOException e) {
		      e.printStackTrace();
		      return 3;
		}    
		
	}
		
	public int runId(String address, int id){
		String addressToCall = address + "/?runId=" + id;
		return httpGet(addressToCall);
	}
	
	public int editId(String address, int id){
		String addressToCall = address + "/?editId=" + id;
		return httpGet(addressToCall);
	}
	
	public int addId(String address, int id){
		String addressToCall = address + "/?addId=" + id;
		return httpGet(addressToCall);
	}
	
	public int standby(String address){
		String addressToCall = address + "/?standby";
		return httpGet(addressToCall);
	}
	
	public int runall(String address){
		String addressToCall = address + "/?runall";
		return httpGet(addressToCall);
	}
}
