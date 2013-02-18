package datalogic;

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
	
	public static final int RESPONSE_OK = 0;
	public static final int RESPONSE_EMPTY_PARAMETER = 1;
	public static final int RESPONSE_INTERNAL_ERROR = 2;
	public static final int RESPONSE_UNKOWN_ERROR = 3;
	
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
			
			if (text.equals("<html><head></head><pre></pre></html>")) return 0;
			if (text.equals("<html><head></head><body>The Scheduling Service received empty parameter.</body></html>")) return 1;
			if (text.equals("Error 500 - Internal Server Error")) return 2;
			
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
	/**
	 * 	
	 * @param address
	 * Address of the server to connect.
	 * @param id
	 * Id of the scheduling to run.
	 * @return
	 * 0 = all ok
	 * 1 = received "The Scheduling Service received empty parameter."
	 * 2 = received "Error 500 - Internal Server Error"
	 * 3 = received something else or catched a ClientProtocolException or an IOException. Check output for more information.
	 */
	public int runId(String address, int id){
		String addressToCall = address + "/?runId=" + id;
		return httpGet(addressToCall);
	}
	
	/**
	 * 	
	 * @param address
	 * Address of the server to connect.
	 * @param id
	 * Id of the scheduling to edit.
	 * @return
	 * 0 = all ok
	 * 1 = received "The Scheduling Service received empty parameter."
	 * 2 = received "Error 500 - Internal Server Error"
	 * 3 = received something else or catched a ClientProtocolException or an IOException. Check output for more information.
	 */
	public int editId(String address, int id){
		String addressToCall = address + "/?editId=" + id;
		return httpGet(addressToCall);
	}
	
	/**
	 * 	
	 * @param address
	 * Address of the server to connect.
	 * @param id
	 * Id of the scheduling to add.
	 * @return
	 * 0 = all ok
	 * 1 = received "The Scheduling Service received empty parameter."
	 * 2 = received "Error 500 - Internal Server Error"
	 * 3 = received something else or catched a ClientProtocolException or an IOException. Check output for more information.
	 */
	public int addId(String address, int id){
		String addressToCall = address + "/?addId=" + id;
		return httpGet(addressToCall);
	}
	
	/**
	 * 	
	 * @param address
	 * Address of the server to connect.
	 * @return
	 * 0 = all ok
	 * 1 = received "The Scheduling Service received empty parameter."
	 * 2 = received "Error 500 - Internal Server Error"
	 * 3 = received something else or catched a ClientProtocolException or an IOException. Check output for more information.
	 */
	public int standby(String address){
		String addressToCall = address + "/?standby=0";
		return httpGet(addressToCall);
	}
	
	/**
	 * 	
	 * @param address
	 * Address of the server to connect.
	 * @return
	 * 0 = all ok
	 * 1 = received "The Scheduling Service received empty parameter."
	 * 2 = received "Error 500 - Internal Server Error"
	 * 3 = received something else or catched a ClientProtocolException or an IOException. Check output for more information.
	 */
	public int runall(String address){
		String addressToCall = address + "/?runall=0";
		return httpGet(addressToCall);
	}
}
