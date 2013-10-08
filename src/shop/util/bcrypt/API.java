package shop.util.bcrypt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class API {

	

	
	/**
	 * ./login/			https://api.mcheck.blackpinguin.de/login/
	 * 
	 * Helferfunktion um das Passwort ins richtige Format zu bringen
	 *   
	 * @param klartextpw Das Passwort im Klartext
	 * @return Das Passwort in einem verschl�sseltem Format, damit die API es akzeptiert
	 * @author Robin Christopher Ladiges
	 */	
	public static String loginPassword(String klartextpw, String username){
		
		//Passworthash
		String pw = Crypt.bcryptHex(klartextpw, username);
		
		//Zeitformat
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyyHH");
		
		//Zeitzone
		TimeZone timezone = TimeZone.getTimeZone("Europe/Berlin");
		format.setTimeZone(timezone);
		
		//Zeit ermitteln
		String date = format.format(new Date());
		
		//Concat und neuer Hash
		return Crypt.sha512hex(pw+date);
	}
	
	/**
	 * ./p/pwc			https://api.mcheck.blackpinguin.de/p/pwc/
	 *  
	 * Helferfunktion um das alte Passwort ins richtige Format zu bringen
	 *   
	 * @param pw das Passwort im Klartext
	 * @return Das Passwort in einem verschl�sseltem Format, damit die API es akzeptiert
	 * @author Robin Christopher Ladiges
	 */	
	public static String pwcPassword(String pw, String username){
		return Crypt.sha512hex(Crypt.bcryptHex(pw, username));
	}
	
	/**
	 * ./p/pwc			https://api.mcheck.blackpinguin.de/p/pwc/
	 *  
	 * Helferfunktion um das neue Passwort ins richtige Format zu bringen
	 *   
	 * @param oldpw das alte Passwort im Klartext
	 * @param newpw das neue Passwort im Klartext
	 * @return Das neue Passwort in einem verschl�sseltem Format, damit die API es akzeptiert
	 * @author Robin Christopher Ladiges
	 */	
	public static String pwcXorPassword(String oldpw, String newpw, String username){
		byte[] oldb = Crypt.bcrypt(oldpw, username);
		byte[] newb = Crypt.bcrypt(newpw, username);
		
		byte[] xorb = new byte[64];
		
		//xor
		for(int i=0; i<64; i++){
			xorb[i] = (byte)(oldb[i] ^ newb[i]);
		}
		
		//toHex und return
		return Crypt.byteToHexString(xorb);
	}
	
	
	
}
