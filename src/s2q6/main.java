/*
 * SERIES 2 - QUEST 6
Sponsor the absolute minimum balance for a new account
Goals & Details
Storing stateful data on the Stellar blockchain isn't free. Everything from data attributes, trustlines and offers all the way down to just creating an account all increase the minimum balance of the account in question.

Up until protocol 15 this minimum balance had to be paid for by the account itself which often makes sense. However there are instances where it would be more convenient or even essential for these fees to be staked by some other account, a "sponsor" account.

In this challenge your task is to create a brand new 0 XLM balance account with the absolute minimum balance sponsored by your account (seen below).
 */

package s2q6;

import java.io.*;

import java.net.*;

import java.util.*;

/*
 * SERIES 2 - QUEST 6
Sponsor the absolute minimum balance for a new account
Goals & Details
Storing stateful data on the Stellar blockchain isn't free. Everything from data attributes, trustlines and offers all the way down to just creating an account all increase the minimum balance of the account in question.

Up until protocol 15 this minimum balance had to be paid for by the account itself which often makes sense. However there are instances where it would be more convenient or even essential for these fees to be staked by some other account, a "sponsor" account.

In this challenge your task is to create a brand new 0 XLM balance account with the absolute minimum balance sponsored by your account (seen below).

 */

//Server
import org.stellar.sdk.*;
import org.stellar.sdk.CreateAccountOperation.Builder;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.Response;

import shadow.net.i2p.crypto.eddsa.EdDSAPrivateKey;
import shadow.net.i2p.crypto.eddsa.EdDSAPublicKey;

public class main {
    private static String quest1PublicKey   = "GCNR7XJLWM5ELQUZUK3P7ME3RPYAEIB342Q7VUK4UAT265NFOD7SK3UO";
    private static String quest1SecretKey   = "";
    private static String sponsor1PublicKey = "GAILI4BQDGOCBVZCOWHUOFNZYQJ246JVBGESKSRRPNQBM4PG6A4FGRXC";
    private static String sponsor1SecretKey = "SDXTA5HNHLOPPZCDOIZB5FKK2Y7CVKYZ7SQZQSBPCTMC4GR4QATDRBDY";
    private static String destination       = new String("https://horizon-testnet.stellar.org");
    private static String startingBalance   = "1000";
    private static String limit             = new String("10000");
    EdDSAPublicKey        pu;
    EdDSAPrivateKey       pr;

    public static void main(String[] args) throws IOException {
        Server              server               = new Server("https://horizon-testnet.stellar.org");
        KeyPair             questAccountKeys     = KeyPair.fromSecretSeed(quest1SecretKey);
        AccountResponse     questAccount         = server.accounts().account(questAccountKeys.getAccountId());
        KeyPair             accountToSponsorKeys = KeyPair.fromSecretSeed(sponsor1SecretKey);
        Transaction.Builder txBuilder            = new Transaction.Builder(questAccount,
                                                                           Network.TESTNET).setBaseFee(
                                                                               FeeBumpTransaction.MIN_BASE_FEE)
                                                                                           .setTimeout(180);

        txBuilder.addOperation(
            new BeginSponsoringFutureReservesOperation.Builder(accountToSponsorKeys.getAccountId()).build());
        txBuilder.addOperation(new CreateAccountOperation.Builder(accountToSponsorKeys.getAccountId(), "0").build());
        txBuilder.addOperation(new EndSponsoringFutureReservesOperation(accountToSponsorKeys.getAccountId()));

        Transaction transaction = txBuilder.build();

        transaction.sign(questAccountKeys);
        transaction.sign(accountToSponsorKeys);
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