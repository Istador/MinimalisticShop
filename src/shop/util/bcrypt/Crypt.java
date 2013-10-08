package shop.util.bcrypt;


import java.security.MessageDigest;

/**
 * 
 * Quellen:
 * http://blog.fethilale.com/sha512-hashing-on-java/
 *
 * @author Robin Christopher Ladiges
 */
public class Crypt {
	
	
	/**
	 * SHA512 einer Bytefolge
	 * @param in zu hashender Wert
	 * @return der gehashte Wert
	 * @author Robin Christopher Ladiges
	 */
	public static byte[] sha512(byte[] in){
		byte[] result = null;
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(in);
			result = md.digest();
		}
		catch(Exception e){System.out.println(e.getMessage());}
		return result;
	}
	
	
	/**
	 * SHA512 einer Bytefolge eines Strings
	 * @param in zu hashende Zeichenkette
	 * @return der gehashte Wert
	 * @author Robin Christopher Ladiges
	 */
	public static byte[] sha512(String in){
		return sha512(in.getBytes());
	}
	
	
	/**
	 * Wandelt eine Bytefolge in die dazugeh�rige Hrxadezimalschreibweise als String um
	 * @param in Bytefolge
	 * @return Hexadezimale Repr�sentation der Eingabe 
	 * @author Robin Christopher Ladiges
	 */
	public static String byteToHexString(byte[] in){
		String out = "";
		
		for(int i = 0; i < in.length; i++){
			String tmp = Integer.toHexString(new Byte(in[i]));
			
			while(tmp.length() < 2){
				tmp = "0"+tmp;
			}
			
			tmp = tmp.substring(tmp.length()-2);
			out += tmp;
		}
		
		return out;
	}

	/**
	 * Gibt von einem Eingabestring den SHA512 Hash in Hexadezimaler Schreibweise als String aus
	 * @param in Eingabestring
	 * @return Hexadezimale Repr�sentation des SHA512 Eingabestring 
	 * @author Robin Christopher Ladiges
	 */	
	public static String sha512hex(String in){
		return byteToHexString(sha512(in));
	}
	
	
	
	/**
	 * Passwort mittels bcrypt in 2^14 Runden hashen.
	 * Salt von bcrypt ist die Ausgabe des vom usernamen geseedeten MersenneTwister PRNG
	 * @param username Benutzername
	 * @param pw Passwort
	 * @return Hexadezimale Repr�sentation des SHA512 gehashten mit bcrypt gehashten Passwortes
	 * @author Robin Christopher Ladiges
	 */	
	public static String bcryptHex(String pw, String username){
		return byteToHexString(bcrypt(pw, username));
	}
	
	/**
	 * Passwort mittels bcrypt in 2^10 Runden hashen.
	 * Salt von bcrypt ist die Ausgabe des vom usernamen geseedeten MersenneTwister PRNG
	 * @param username Benutzername
	 * @param pw Passwort
	 * @return SHA512
	 * @author Robin Christopher Ladiges
	 */	
	public static byte[] bcrypt(String pw, String username){
		//Usernamehash
		String sha = Crypt.sha512hex(username);
		//System.out.println("un: "+username);
		//System.out.println("sha: "+sha);
		
		//Generiere PRNG Seed von Usernamen
		int seed = 0;
		for(int i=0; i<16; i++){
			//XOR
			seed ^= ((int)Long.parseLong(sha.substring(i*8, i*8+8),16));
		}
		//System.out.println("seed: "+seed);
		
		//Seede den MersenneTwister PRNG (basierend auf der PHP Variante mt_rand)
		MersenneTwister.mt_srand(seed);
				
		//Erzeuge mit den PRNG den salt f�r die Hash-Funktion
		String salt = "$2a$10$"; //2^10 Runden
		for(int i=0; i<=21; i++){
			long rnd = MersenneTwister.mt_rand(0, 9+2*26);
			salt += (char)(rnd<10 ? 0x30+rnd : (rnd<36 ? 0x61+rnd-10 : 0x41+rnd-10-26 ) );
			}
		//System.out.println("salt: "+salt);
		
		//Hashe das Klartexpasswort mit bcrypt
		String salthash = BCrypt.hashpw(pw, salt);
		//System.out.println("salthash: "+salthash);
		
		//entferne den salt aus dem Hash
		String hash = salthash.substring(salt.length()-1);
		//System.out.println("hash: "+hash);
		
		//Hashe den Hash erneut mit SHA512 um das API-Format nicht zu ver�ndern
		return Crypt.sha512(hash);
	}
	

}