package Util;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.security.KeyPairGenerator;

public class ServerCripto {
    private String username;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private SecretKey aesKey;

    public void Authentication(){
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            KeyPair pair = generator.generateKeyPair();
            this.privateKey = pair.getPrivate();
            this.publicKey = pair.getPublic();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void SendPublicKey() throws IOException{
        byte[] messageCripto = this.publicKey.getEncoded();
        String messagetoClient = "CHAVE_PUBLICA " + Base64.getEncoder().encodeToString(messageCripto);
        //mandar mensagem pro cliente
    }

    public void DecryptSimetricKey(String simetricKey)  {
        try {
            byte[] authRequestBytes = Base64.getDecoder().decode(simetricKey);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] aesKeyBytes = cipher.doFinal(authRequestBytes);

            this.aesKey = new SecretKeySpec(aesKeyBytes, "AES");
            this.aesKey.getEncoded();

        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    public String DecryptMessage(String message) throws Exception {
        try {
            byte[] messageBytes = Base64.getDecoder().decode(message);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decryptedMessageBytes = cipher.doFinal(messageBytes);
            return new String(decryptedMessageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "ERRO: Falha ao desencripitar a mensagem";

        }
    }
}