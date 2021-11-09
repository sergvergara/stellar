/*
SERIES 3 - QUEST 3
Submit a hash signed transaction
Goals & Details
So you've heard about Stellar's multisig, but did you know that you can sign with far more than
 just a simple Ed25519 secret key? That's right! There's also sha256 hashes and pre-authorized transaction hashes.

In today's challenge your task is to add a very specific and special sha256 hash signer to your
 account and then to submit a second transaction using that signer to remove itself as a signer
  from the account. A sort of one time use key if you will.

Your clue for what your hash signer should be is included in the Resources links below.
*/

package s3q3;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
//Server
import org.stellar.sdk.*;
import org.stellar.sdk.CreateAccountOperation.Builder;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.Response;
import org.stellar.sdk.xdr.SignerKey;
import shadow.net.i2p.crypto.eddsa.EdDSAPrivateKey;
import shadow.net.i2p.crypto.eddsa.EdDSAPublicKey;

public class main {
    private static String signer3PublicKey  = "GDEROREW2TTOANZFEXFRJBINMXZU2BF2DPMDLL6CCW4LN4WZIDZ2BMGV";
    private static String signer3SecretKey  = "SA4LNA2WDAFKDRO5V54DT36QLUVAR6P5CGMQM2PEL4JXK4WFT7TRWSBO";
    private static String destination      = new String("https://horizon-testnet.stellar.org");
    private static String startingBalance  = "1000";
    private static String limit            = new String("10000");
    EdDSAPublicKey        pu;
    EdDSAPrivateKey       pr;

    public static void main(String[] args) throws IOException {

    	 Server server = new Server("https://horizon-testnet.stellar.org");
    	 KeyPair questAccountKeys = KeyPair.fromSecretSeed(signer3SecretKey);

		    // Ask friendbot to fund our account
		    //questAccountKeys.friendBot()

    	 AccountResponse questAccount = server.accounts().account(questAccountKeys.getAccountId());

    	 Transaction.Builder txBuilder = new Transaction.Builder(questAccount, Network.TESTNET)
		        .setBaseFee(FeeBumpTransaction.MIN_BASE_FEE)
		        .setTimeout(180);

    	    byte[] secret = Base64.getDecoder().decode("S2FuYXllTmV0");
    	    byte[] hashX = sha256_1(new String(secret));
		    SignerKey hashXSignerKey = Signer.sha256Hash(hashX);

		    txBuilder.addOperation(new 
		        SetOptionsOperation.Builder()
		            .setSigner(hashXSignerKey,1)
		            .build()
		    );

		    Transaction transaction = txBuilder.build();
		    transaction.sign(questAccountKeys);
		    System.out.println("Executing tx.. (adding hash signer)");
		    try {
		    	Response response = server.submitTransaction(transaction);
	            System.out.println("Success!");
	            System.out.println("txHash: ${response.hash}");
	            System.out.println("result: ${response.resultXdr.get()}");
	        } catch (Exception e) {
	            System.out.println("Something went wrong!");
	            System.out.println(e);
	        }
		    Transaction.Builder remSignBuilder = new Transaction.Builder(questAccount, Network.TESTNET)
		        .setBaseFee(FeeBumpTransaction.MIN_BASE_FEE)
		        .setTimeout(180);

		    remSignBuilder.addOperation(new 
		        SetOptionsOperation.Builder()
		            .setSigner(hashXSignerKey,0)
		            .build()
		    );

		    Transaction txRemoveSigner = remSignBuilder.build();
		    txRemoveSigner.sign(secret);
		    System.out.println("Executing tx.. (removing hash signer)");
		    try {
		    	Response response = server.submitTransaction(txRemoveSigner);
	            System.out.println("Success!");
	            System.out.println("txHash: ${response.hash}");
	            System.out.println("result: ${response.resultXdr.get()}");
	        } catch (Exception e) {
	            System.out.println("Something went wrong!");
	            System.out.println(e);
	        }
    }
    public static byte[] sha256_1(final String data) {
        try {
            final byte[] hash = MessageDigest.getInstance("SHA-256").digest(data.getBytes());
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}

