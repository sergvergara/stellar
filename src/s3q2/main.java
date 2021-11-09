/*
SERIES 3 - QUEST 2
Submit a transaction containing 100 operations
Goals & Details
Stellar, like all good software, has lots of different caps, limits, and ceilings. For instance the cap on the number of operations per transaction is 100 atm.

Your challenge today is to successfully orchestrate and submit a transaction stuffed full of 100 operations.

For the developers out there still clinging to the Laboratory, now might be a good time to flex your coding skills and utilize one of our SDKs inside a loop.
*/

package s3q2;

import java.io.*;
import java.net.*;
import java.util.*;

//Server
import org.stellar.sdk.*;
import org.stellar.sdk.CreateAccountOperation.Builder;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.Response;

import shadow.net.i2p.crypto.eddsa.EdDSAPrivateKey;
import shadow.net.i2p.crypto.eddsa.EdDSAPublicKey;


public class main {
    private static String quest3PublicKey  = "GAVEJ4C5IFWUD6JHYKS7QG64KMD64BMZC66T7IW6HFM6B3QNSWNC7V2K";
    private static String quest3SecretKey  = "SBDXHT34GLKTXL3F76UBJAVRVHSHLT7DIAYWTTBH7PQGRAZJQLLIUZ4O";
    private static String destination      = new String("https://horizon-testnet.stellar.org");
    private static String startingBalance  = "1000";
    private static String limit            = new String("10000");
    EdDSAPublicKey        pu;
    EdDSAPrivateKey       pr;

    public static void main(String[] args) throws IOException {

    	Server server = new Server("https://horizon-testnet.stellar.org");
        KeyPair questAccountKeys = KeyPair.fromSecretSeed(quest3SecretKey);
        
        AccountResponse     questAccount     = server.accounts().account(questAccountKeys.getAccountId());

        Transaction.Builder txBuilder = new Transaction.Builder(questAccount, Network.TESTNET)
	        .setBaseFee(FeeBumpTransaction.MIN_BASE_FEE)
	        .setTimeout(180);
        
        Asset myAsset = Asset.createNonNativeAsset("MINE", questAccountKeys.getAccountId());

        String amount;
        txBuilder.addOperation( new 
	            PaymentOperation.Builder("GAIH3ULLFQ4DGSECF2AR555KZ4KNDGEKN4AFI4SU2M7B43MGK3QJZNSR", myAsset, "0.03").build()
	        );
	    // 100 Payments to our friendbot
	    for (int i=1;i<=100; i++) {
	    	amount= String.valueOf(i);
	        txBuilder.addOperation( new 
	            PaymentOperation.Builder("GAIH3ULLFQ4DGSECF2AR555KZ4KNDGEKN4AFI4SU2M7B43MGK3QJZNSR", myAsset, amount).build()
	        );
		    System.out.println(amount);

	    }
	    Transaction transaction = txBuilder.build();
        transaction.sign(questAccountKeys);
        System.out.println("Executing tx..");
        try {
        
            Response response = server.submitTransaction(transaction);
            System.out.println("Success!");
            System.out.println("txHash: ${response.hash}");
            System.out.println("result: ${response.resultXdr.get()}");
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e);
        }
    }
}