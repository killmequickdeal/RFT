import java.io.PrintWriter;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Utility
{
    private static SecretKeySpec secretKey = new SecretKeySpec("5u8x/A?D(G+KbPeS".getBytes(), "AES");
    private Cipher cipher = null;
	private String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ";
	private Random randgen = new Random();

	public Utility () {

    }

    public String GetResponse(Boolean encrypted, DataInputStream receive) throws IOException {
		// generic response function which can get both encrypted and unencrypted responses
        String response;
		if (encrypted) {
			response = decrypt(receive.readUTF());
		} else {
			response = receive.readUTF();
		}

        System.out.println(response);
        return response;

    }

    public String decrypt(String s) {
		// decrypt a string using the secret key
        String plaintextstring = null;

        try
        {
        	// build a decrypt mode cipher
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            // decode from base64 and then decrypt
            plaintextstring = new String(cipher.doFinal(Base64.getDecoder().decode(s)));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException
                | BadPaddingException | InvalidKeyException ex) {
            ex.printStackTrace();
        }

        return plaintextstring;
    }

    public String EncryptAndEncodeToBase64String(String s) {
		// encrypt a string using the secret key

		String secretstring = null;
        try
        {
        	// build an encrypt cipher
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // encrypt and then base64 encode into a string
            secretstring = Base64.getEncoder().encodeToString(cipher.doFinal(s.getBytes()));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException
                | BadPaddingException | InvalidKeyException ex) {
            ex.printStackTrace();
        }
        return secretstring;
    }

    public void Send(Boolean encrypted, String msg, DataOutputStream send) {
		// generic send function which can send encrypted and unencrypted
        try
        {
        	if(encrypted) {
				send.writeUTF(EncryptAndEncodeToBase64String(msg));
			} else {
				send.writeUTF(msg);
			}
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public String ReadFile(String filename) throws IOException
    {
    	// read a file as bytes and convert to string on the way out
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    public void WriteFile(String filename, String contents) {
		// write a file
		try (PrintWriter out = new PrintWriter(filename)) {
			out.print(contents);
		} catch	(IOException ex) {
			ex.printStackTrace();
		}
	}

	public String CreateRandomContents() {
		// create a random string from the alphabet and space
		String contents = "";
		for (int i = 0; i < 100; i++)
		{
			contents += alphabet.charAt(randgen.nextInt(53));
		}

    	return contents;
	}

	public String GetSubstring(String s) {
		// get a random substring of the string from 0 to randInt
		int rand = GetRandom(s.length());
		return s.substring(0, rand);
	}

	public int GetRandom(int bound) {
		// get a random number from 0 to bound, non inclusive
		return randgen.nextInt(bound);
	}
}
