package stellar_1_1;

import java.io.*;

import java.net.*;

import java.util.*;

import org.stellar.sdk.*;
import org.stellar.sdk.CreateAccountOperation.Builder;
import org.stellar.sdk.KeyPair;

//Server
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;

import shadow.net.i2p.crypto.eddsa.EdDSAPrivateKey;
import shadow.net.i2p.crypto.eddsa.EdDSAPublicKey;

public class main {
    private static String quest1PublicKey = "GDY3ETTPLFUGRSKO4VT3DPHOFIWXGQYSYLE4EWPIN4YOF3B45YY2QPHT";
    private static String quest1SecretKey = "";
    private static String destination     = new String("https://horizon-testnet.stellar.org");
    private static String startingBalance = "1000";
    EdDSAPublicKey        pu;
    EdDSAPrivateKey       pr;

    public static void main(String[] args) {
        InputStream response = null;

        // KeyPair pair = KeyPair.fromPublicKey(quest1PublicKey.getBytes());
        KeyPair pair = org.stellar.sdk.KeyPair.fromAccountId(quest1PublicKey);

        try {
            Builder builder = new Builder(destination, startingBalance);

            builder.setSourceAccount(quest1PublicKey);

            String friendlybotUrl = String.format("https://friendbot.stellar.org/?addr=%s", pair.getAccountId());

            response = new URL(friendlybotUrl).openStream();
            System.out.println("Balances for the account " + pair.getAccountId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();

        System.out.println("SUCCESS! You have a new account");

        Server          server = new Server("https://horizon-testnet.stellar.org");
        AccountResponse account;

        try {
            account = server.accounts().account(pair.getAccountId());

            for (AccountResponse.Balance balance : account.getBalances()) {
                System.out.println(String.format("Type %s, Code %s, Balance:%s",
                                                 balance.getAssetType(),
                                                 balance.getAssetCode(),
                                                 balance.getBalance()));
            }
        } catch (IOException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
