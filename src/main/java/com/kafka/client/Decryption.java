package com.kafka.client;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Decryption 
{
	
private static final  String AES ="AES"; // Default uses ECB PKCS5Padding

	
	public static String decrypt(String strToDecrypt, String secret)
	{
		try 
		{
			Key key = generateKey(secret);
			Cipher cipher = Cipher.getInstance(AES);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} 
		catch (Exception e) 
		{
		System.out.println("Error while decrypting: " + e.toString());
		}
	return null;
	}
	
	private static Key generateKey(String secret) throws Exception 
	{
		byte[] decoded = Base64.getDecoder().decode(secret.getBytes());
		Key key = new SecretKeySpec(decoded, AES);
		return key;
	}
	
	
	
	public static String encodeKey(String str)
	{
		byte[] encoded = Base64.getEncoder().encode(str.getBytes());
		return new String(encoded);
	}
	
	public static String getDecryptedData(String inputvalue)
	{
		String encodedBase64Key = encodeKey( Config.ENCRYPTIONKEY);
		System.out.println("EncodedBase64Key = " + encodedBase64Key); // This need to be share between client and server
		
		String decrStr = Decryption.decrypt(inputvalue, encodedBase64Key);
		System.out.println("Decryption of str = " + decrStr);
		return decrStr;
	}
	
}