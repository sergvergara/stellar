/*
 SERIES 2 - QUEST 4
Create a claimable balance
Goals & Details
This one will be a bit tricky but I believe in you.

Today you've gotta sort out how to create a claimable balance that is only claimable by you and only claimable after the next challenge.

Additionally the claimable balance must be denoted in the native XLM asset for an exact amount of 100 XLM.
 */

package s2q4;

import java.io.*;

import java.net.*;

import java.util.*;

/*SERIES 2 - QUEST 4
Create a claimable balance
Goals & Details
This one will be a bit tricky but I believe in you.

Today you've gotta sort out how to create a claimable balance that is only claimable by you and only claimable after the next challenge.

Additionally the claimable balance must be denoted in the native XLM asset for an exact amount of 100 XLM.*/

//Server
import org.stellar.sdk.*;
import org.stellar.sdk.CreateAccountOperation.Builder;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.Response;
import org.stellar.sdk.Claimant;


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
        Server              server           = new Server("https://horizon-testnet.stellar.org");
        KeyPair             questAccountKeys = KeyPair.fromSecretSeed(quest1SecretKey);
        AccountResponse     questAccount     = server.accounts().account(questAccountKeys.getAccountId());
        Transaction.Builder txBuilder        = new Transaction.Builder(questAccount,
                                                                       Network.TESTNET).setBaseFee(
                                                                           FeeBumpTransaction.MIN_BASE_FEE)
                                                                                       .setTimeout(180);
        Predicate predicate = new Predicate.Not(new Predicate.RelBefore(30));
        Asset     myAsset   = Asset.createNonNativeAsset("CULO", questAccountKeys.getAccountId());

        txBuilder.addOperation(new CreateClaimableBalanceOperation.Builder("100", myAsset, List.of(new Claimant(
        		questAccount.getAccountId(), predicate))).build()
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