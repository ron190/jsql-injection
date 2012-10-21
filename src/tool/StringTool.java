package tool;

import java.math.BigInteger;

public class StringTool {
	public static String join(String[] strings, String separator) {
	    StringBuffer sb = new StringBuffer();
	    for (int i=0; i < strings.length; i++) {
	        if (i != 0) sb.append(separator);
	  	    sb.append(strings[i]);
	  	}
	  	return sb.toString();
	}
	
	public static String hexstr(String hex){
		  byte[] bytes = new byte[hex.length() / 2];
		  for (int i = 0; i < bytes.length; i++) {
		     bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		  }
		  String multi = new String(bytes);
		  return multi;
	}
	
	public static String strhex(String arg) {
	    return String.format("%x", new BigInteger(arg.getBytes()));
	}
}
