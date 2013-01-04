
public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HttpConnector http = new HttpConnector();
		String address = "http://otakala.ayy.fi/sdp12/test.php";
		System.out.println("starting the http test.");
		
		System.out.println("testing the runId");
		System.out.println(http.runId(address, 47));
		System.out.println("testing the editId");
		System.out.println(http.editId(address, 47));
		System.out.println("testing the addId");
		System.out.println(http.addId(address, 47));
		System.out.println("testing the standby");
		System.out.println(http.standby(address));
		System.out.println("testing the runall");
		System.out.println(http.runall(address));
		System.out.println("quiting.");
		
	}

}
