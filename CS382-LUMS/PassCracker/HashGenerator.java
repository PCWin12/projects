import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HashGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		md.update("aportf".getBytes());

		
	
	byte bytes[] = md.digest();
	    String s = byteArrayToHexString(bytes);
	    byte b[] = hexStringToByteArray(s);
	    System.out.println(s);
	    
	}

	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	 public static String byteArrayToHexString(byte[] b) {
		   int len = b.length;
		   String data = new String();

		   for (int i = 0; i < len; i++){
		   data += Integer.toHexString((b[i] >> 4) & 0xf);
		   data += Integer.toHexString(b[i] & 0xf);
		   }
		   return data;
		  }
	
	
	
}
