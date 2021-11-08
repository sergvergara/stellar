/*
 * SERIES 2 - QUEST 7
Revoke account sponsorship for the account you're sponsoring
Goals & Details
Remember that account you sponsored in the last challenge? Well the winds of change are blowing and you no longer wish to sponsor their absolute minimum balance any longer.

In this challenge you need to revoke account sponsorship for the account you're currently sponsoring.
 */
package s2q7;

import java.io.*;

import java.net.*;

import java.util.*;

/*
SERIES 2 - QUEST 7
Revoke account sponsorship for the account you're sponsoring
Goals & Details
Remember that account you sponsored in the last challenge? Well the winds of change are blowing and you no longer wish to sponsor their absolute minimum balance any longer.

In this challenge you need to revoke account sponsorship for the account you're currently sponsoring.
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
    private static String sponsor1PublicKey = "GCC4YSX4KUEEURFB5N6T5RREVB2OQJC3UA7CA5J232ZWOA6W7DGCA4XE";
    private static String sponsor1SecretKey = "SDRLFMDOUXUWX42LU2PVOHEGIOOO2GCHQVO75LYLUSOTCYVZDEBX2KHV";
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
        AccountResponse     accountToSponsor     = server.accounts().account(accountToSponsorKeys.getAccountId());
        Transaction.Builder txBuilder            = new Transaction.Builder(questAccount,
                                                                           Network.TESTNET).setBaseFee(
                                                                               FeeBumpTransaction.MIN_BASE_FEE)
                                                                                           .setTimeout(180);
        Asset myAsset = Asset.createNonNativeAsset("MINE", questAccountKeys.getAccountId());

        // txBuilder.addOperation( new PaymentOperation.Builder(accountToSponsor.getAccountId(), myAsset, "10").build());

        txBuilder.addOperation(new RevokeAccountSponsorshipOperation.Builder(accountToSponsor.getAccountId()).build());

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


