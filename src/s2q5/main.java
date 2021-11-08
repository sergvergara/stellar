/*
 * SERIES 2 - QUEST 5
Claim your claimable balance
Goals & Details
Remember that claimable balance you setup in the last challenge?

This challenge is to "simply" claim that balance and get your XLM back.
 */

package s2q5;

import java.io.*;

import java.net.*;

import java.util.*;

/*
 * SERIES 2 - QUEST 5
Claim your claimable balance
Goals & Details
Remember that claimable balance you setup in the last challenge?

This challenge is to "simply" claim that balance and get your XLM back.
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
        Server              server           = new Server("https://horizon-testnet.stellar.org");
        KeyPair             questAccountKeys = KeyPair.fromSecretSeed(quest1SecretKey);
        AccountResponse     questAccount     = server.accounts().account(questAccountKeys.getAccountId());
        Transaction.Builder txBuilder        = new Transaction.Builder(questAccount,
                                                                       Network.TESTNET).setBaseFee(
                                                                           FeeBumpTransaction.MIN_BASE_FEE)
                                                                                       .setTimeout(180);

        // we can get this from list of claimable balances by acc or in q4 result xdr
        String balanceId = "000000000c87aaef9188300de7a6fdad5630adadc56710d1267db53cfd14cd871ea1964c";

        txBuilder.addOperation(new ClaimClaimableBalanceOperation.Builder(balanceId).build());

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