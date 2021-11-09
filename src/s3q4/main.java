
/*
SERIES 3 - QUEST 4
Submit a pre-authorized transaction
Goals & Details
So you probably saw this one coming considering the previous quest, but today's challenge is to add as a signer and then submit to the network, a pre-authorized transaction. The last of the signer types accepted for submitting transactions to the network.

Pre-authorized transactions are a fantastic way to get around the somewhat cumbersome issue of multisig coordination. Just add a transaction hash as a signer to your account and then pass along that XDR to any other signers for additional signing or final submission.

Simple, yet smart! A Simart Contract™! And just another great tool in your ever expanding tool belt of experience as a Stellar developer.

*/
package s3q4;

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
    private static String signer3PublicKey = "GBGCE3IY77SM7QNP5HWBXLXDOM323EQ52NXWSEJCUZNEGO5HAIR75DDC";
    private static String signer3SecretKey = "SAEU5GQAMDQ5KHEJOZWY2DTTPWGK6VPYL6FHXWLTQESUX2DZJJXYP2E3";
    private static String destination      = new String("https://horizon-testnet.stellar.org");
    private static String startingBalance  = "1000";
    private static String limit            = new String("10000");
    EdDSAPublicKey        pu;
    EdDSAPrivateKey       pr;

    public static void main(String[] args) throws IOException {
        Server  server           = new Server("https://horizon-testnet.stellar.org");
        KeyPair questAccountKeys = KeyPair.fromSecretSeed(signer3SecretKey);

        // Ask friendbot to fund our account
        AccountResponse questAccount       = server.accounts().account(questAccountKeys.getAccountId());
        AccountResponse bumpedQuestAccount = server.accounts().account(questAccountKeys.getAccountId());

        bumpedQuestAccount.incrementSequenceNumber();

        Asset       myAsset   = Asset.createNonNativeAsset("MINE", questAccountKeys.getAccountId());
        Transaction preAuthTx = new Transaction.Builder(bumpedQuestAccount,
                                                        Network.TESTNET).setBaseFee(FeeBumpTransaction.MIN_BASE_FEE)
                                                                        .setTimeout(180)
                                                                        .addOperation(
                                                                            new PaymentOperation.Builder(
                                                                                signer3PublicKey,
                                                                                myAsset,
                                                                                "1").build())
                                                                        .build();
        Transaction.Builder txBuilder = new Transaction.Builder(questAccount,
                                                                Network.TESTNET).setBaseFee(
                                                                    FeeBumpTransaction.MIN_BASE_FEE)
                                                                                .setTimeout(180);
        SignerKey preAuthTxKey = Signer.preAuthTx(preAuthTx);

        txBuilder.addOperation(new SetOptionsOperation.Builder().setSigner(preAuthTxKey, 1).build());

        Transaction transaction = txBuilder.build();

        transaction.sign(questAccountKeys);
        System.out.println("Executing tx.. (adding hash tx signer)");

        try {
            Response response = server.submitTransaction(transaction);

            System.out.println("Success!");
            System.out.println("txHash: ${response.hash}");
            System.out.println("result: ${response.resultXdr.get()}");
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e);
        }

        System.out.println("Executing tx.. (pre signed tx)");

        try {
            Response response = server.submitTransaction(preAuthTx);

            System.out.println("Success!");
            System.out.println("txHash: ${response.hash}");
            System.out.println("result: ${response.resultXdr.get()}");
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e);
        }
    }
}
