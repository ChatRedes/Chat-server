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
    public PrivateKey privateKey;
    public PublicKey publicKey;

    public ServerCripto (){
        System.out.println("Ta vindo aqui");
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
}