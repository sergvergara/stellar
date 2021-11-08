/*
 * SERIES 2 - QUEST 3
Create and submit a fee bump transaction
Goals & Details
Fee channels are a common best practice in Stellar development. Their goal is to delegate fee payments away from user accounts for an improved UX. Protocol 13 saw a huge improvement to this flow with the introduction of fee bump transactions.

In this challenge your task is to create and execute a fee bump transaction which consumes the sequence number from your account (seen below) but the transaction fee from some other account.

This is actually how Stellar Quest delivers your prizes to you. A multi-operational transaction wrapped in a fee bump transaction. You pay the sequence number but we pay the transaction fee. How nice!
 */

package s2q3;

import java.io.*;

import java.net.*;

import java.util.*;

/*
 * SERIES 2 - QUEST 3
Create and submit a fee bump transaction
Goals & Details
Fee channels are a common best practice in Stellar development. Their goal is to delegate fee payments away from user accounts for an improved UX. Protocol 13 saw a huge improvement to this flow with the introduction of fee bump transactions.

In this challenge your task is to create and execute a fee bump transaction which consumes the sequence number from your account (seen below) but the transaction fee from some other account.

This is actually how Stellar Quest delivers your prizes to you. A multi-operational transaction wrapped in a fee bump transaction. You pay the sequence number but we pay the transaction fee. How nice!
 */

//Server
import org.stellar.sdk.*;
import org.stellar.sdk.CreateAccountOperation.Builder;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.Response;

import shadow.net.i2p.crypto.eddsa.EdDSAPrivateKey;
import shadow.net.i2p.crypto.eddsa.EdDSAPublicKey;

public class main {
    private static String quest1PublicKey  = "GCNR7XJLWM5ELQUZUK3P7ME3RPYAEIB342Q7VUK4UAT265NFOD7SK3UO";
    private static String quest1SecretKey  = "";
    private static String helper1PublicKey = "GAILI4BQDGOCBVZCOWHUOFNZYQJ246JVBGESKSRRPNQBM4PG6A4FGRXC";
    private static String helper1SecretKey = "SDXTA5HNHLOPPZCDOIZB5FKK2Y7CVKYZ7SQZQSBPCTMC4GR4QATDRBDY";
    private static String destination      = new String("https://horizon-testnet.stellar.org");
    private static String startingBalance  = "1000";
    private static String limit            = new String("10000");
    EdDSAPublicKey        pu;
    EdDSAPrivateKey       pr;

    public static void main(String[] args) throws IOException {
        Server              server            = new Server("https://horizon-testnet.stellar.org");
        KeyPair             questAccountKeys  = KeyPair.fromSecretSeed(quest1SecretKey);
        AccountResponse     questAccount      = server.accounts().account(questAccountKeys.getAccountId());
        KeyPair             helperAccountKeys = KeyPair.fromSecretSeed(helper1SecretKey);
        AccountResponse     helperAccount     = server.accounts().account(helperAccountKeys.getAccountId());
        Transaction.Builder txBuilder         = new Transaction.Builder(questAccount,
                                                                        Network.TESTNET).setBaseFee(
                                                                            FeeBumpTransaction.MIN_BASE_FEE)
                                                                                        .setTimeout(180);

        // payback to friendbot as usual but from quest acc
        Asset myAsset = Asset.createNonNativeAsset("CULO", questAccountKeys.getAccountId());

        txBuilder.addOperation(new PaymentOperation.Builder(quest1PublicKey, myAsset, "10").build());

        Transaction innerTx = txBuilder.build();

        innerTx.sign(questAccountKeys);

        FeeBumpTransaction.Builder feeTxBuilder =
            new FeeBumpTransaction.Builder(innerTx).setBaseFee(FeeBumpTransaction.MIN_BASE_FEE)
                                                   .setFeeAccount(helperAccount.getAccountId());
        FeeBumpTransaction feeBumpTransaction = feeTxBuilder.build();

        feeBumpTransaction.sign(helperAccountKeys);
        System.out.println("Executing tx..");

        try {
            Response response = server.submitTransaction(feeBumpTransaction);

            System.out.println("Success!");
            System.out.println("txHash: ${response.hash}");
            System.out.println("result: ${response.resultXdr.get()}");
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e);
        }
    }
}