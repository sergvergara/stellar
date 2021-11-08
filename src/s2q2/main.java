/*
 * SERIES 2 - QUEST 2
Construct and execute a multi-operational transaction
Goals & Details
Did you know each Stellar transaction can include as many as 100 unique operations? 😱 This is an incredible feature as each transaction is atomic meaning either the whole group of operations succeeds or fails together.

In this challenge your task is to create a multi-operational transaction which creates a custom asset trustline on your account and pays that asset to your account from the issuing account all in the same transaction.

Fun fact: This is actually what we do here at Stellar Quest when issuing prizes. The claim transaction is a nice little multi-operational transaction adding and issuing the badge to your account all in a single transaction.
 */

package s2q2;

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
    private static String quest1PublicKey  = "GCNR7XJLWM5ELQUZUK3P7ME3RPYAEIB342Q7VUK4UAT265NFOD7SK3UO";
    private static String quest1SecretKey  = "";
    private static String issuer1PublicKey = "GAILI4BQDGOCBVZCOWHUOFNZYQJ246JVBGESKSRRPNQBM4PG6A4FGRXC";
    private static String issuer1SecretKey = "SDXTA5HNHLOPPZCDOIZB5FKK2Y7CVKYZ7SQZQSBPCTMC4GR4QATDRBDY";
    private static String destination      = new String("https://horizon-testnet.stellar.org");
    private static String startingBalance  = "1000";
    private static String limit            = new String("10000");
    EdDSAPublicKey        pu;
    EdDSAPrivateKey       pr;

    public static void main(String[] args) throws IOException {
        Server              server            = new Server("https://horizon-testnet.stellar.org");
        KeyPair             questAccountKeys  = KeyPair.fromSecretSeed(quest1SecretKey);
        AccountResponse     questAccount      = server.accounts().account(questAccountKeys.getAccountId());
        KeyPair             issuerAccountKeys = KeyPair.fromSecretSeed(issuer1SecretKey);
        Transaction.Builder txBuilder         = new Transaction.Builder(questAccount,
                                                                        Network.TESTNET).setBaseFee(
                                                                            FeeBumpTransaction.MIN_BASE_FEE)
                                                                                        .setTimeout(180);
        Asset myAsset = Asset.createNonNativeAsset("MINE", issuerAccountKeys.getAccountId());

        txBuilder.addOperation(new ChangeTrustOperation.Builder(ChangeTrustAsset.create(myAsset), limit).build());
        txBuilder.addOperation(new PaymentOperation.Builder(questAccount.getAccountId(),
                                                            myAsset,
                                                            "1337.69").setSourceAccount(
                                                                issuerAccountKeys.getAccountId())
                                                                      .build());

        Transaction transaction = txBuilder.build();

        transaction.sign(questAccountKeys);
        transaction.sign(issuerAccountKeys);
        System.out.println("Executing tx..");

        try {
            Response response = server.submitTransaction(transaction);

            System.out.println("Success!");
            System.out.println("txHash: ${response.hash}");
            System.out.println("result: ${response.resultXdr.get()}");
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e.toString());
        }
    }
}