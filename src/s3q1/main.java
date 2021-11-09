
/*
SERIES 3 - QUEST 1
Make use of a sequence number bump operation in a transaction
Goals & Details
Welcome to Stellar Quest series 3! In today's inaugural challenge you must submit a transaction from your account,
making use of the sequence number bump operation.

What good is the sequence number bump operation you ask? While it may not be a heavily utilized operation within
an account's lifecycle it's an incredibly useful op when dealing with smart contracts, particularly around pre-signed transactions.

Imagine a scenario where you have two potential outcomes but only one of them should actually execute. Rather than having both transactions
compete for the same sequence number you can control the outcome by bumping the sequence number to support whichever of the two scenarios you wish.

With functionality like this you can now block transaction submission both by time and by sequence. Control all the things!

⚠️ This quest isn't as simple at seems. We're just as much about coding as we are about gaming and riddles here at the SQ HQ. Be sure and click all the provided links and peruse for clues on how to conquer this quest.
 */
package s3q1;

import java.io.*;

import java.net.*;

import java.util.*;

import org.stellar.sdk.*;
import org.stellar.sdk.CreateAccountOperation.Builder;
import org.stellar.sdk.KeyPair;

//Server
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.Response;

import shadow.net.i2p.crypto.eddsa.EdDSAPrivateKey;
import shadow.net.i2p.crypto.eddsa.EdDSAPublicKey;

public class main {
    private static String quest3PublicKey = "GA3L3VLTJ7DESRUXVNYLHXM6I3ISTF6QKXMMAJ3SXOVFCY5F6DCJANOD";
    private static String quest3SecretKey = "SBHUEXYSND7Z2UMCRDEZZ3NBCD3ZPFJN52EOYHU56GCNI4S6JTYUKPXO";
    private static String destination     = new String("https://horizon-testnet.stellar.org");
    private static String startingBalance = "1000";
    private static String limit           = new String("10000");
    EdDSAPublicKey        pu;
    EdDSAPrivateKey       pr;

    public static void main(String[] args) throws IOException {
        Server  server           = new Server("https://horizon-testnet.stellar.org");
        KeyPair questAccountKeys = KeyPair.fromSecretSeed(quest3SecretKey);

        // Ask friendbot to fund our account
        // questAccountKeys.friendBot();
        AccountResponse     questAccount = server.accounts().account(questAccountKeys.getAccountId());
        Transaction.Builder txBuilder    = new Transaction.Builder(questAccount,
                                                                   Network.TESTNET).setBaseFee(
                                                                       FeeBumpTransaction.MIN_BASE_FEE)
                                                                                   .setTimeout(180);
        long n;

        n = 110101115104111L;
        txBuilder.addOperation(new BumpSequenceOperation.Builder(n).build()    // "number" in int representation
                );

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


