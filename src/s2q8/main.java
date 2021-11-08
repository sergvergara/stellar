/*
 * SERIES 2 - QUEST 8
Create and host a stellar.toml file for your account
Goals & Details
Not all digital info should be stored on a blockchain. Some information needs to be mutable and derives no benefit from maintaining a blockchain paper trail. For these requirements we must look outside Stellar.

Blockchain database software like IPFS, Torrent or Filecoin can store stuff in a decentralized manner but are overkill when simply storing basic, mutable metadata for a Stellar account. For that we'll use SEP 1.

SEPs, or Stellar Ecosystem Proposals are ecosystem initiatives aimed at providing consensus around common Stellar use cases. For SEP 1 that's providing a common format for Stellar account metadata.

In today's challenge your task is to create, host and link to a stellar.toml file with an SQ02_EASTER_EGG field containing the text:

Log into series 2 of Stellar Quest then visit quest.stellar.org/series2. Finally drag and drop your Stellar Quest series 2 badge PNG images onto the screen. Enjoy!
Note you won't be able to solve today's challenge using only the laboratory. You'll need to host a toml file and for that you'll need a basic server. Personally I love RunKit and CodeSandbox but feel free to use whatever works. Good luck!
 */
package s2q8;

import java.io.*;

import java.net.*;

import java.util.*;

/*
SERIES 2 - QUEST 8
Create and host a stellar.toml file for your account
Goals & Details
Not all digital info should be stored on a blockchain. Some information needs to be mutable and derives no benefit from maintaining a blockchain paper trail. For these requirements we must look outside Stellar.

Blockchain database software like IPFS, Torrent or Filecoin can store stuff in a decentralized manner but are overkill when simply storing basic, mutable metadata for a Stellar account. For that we'll use SEP 1.

SEPs, or Stellar Ecosystem Proposals are ecosystem initiatives aimed at providing consensus around common Stellar use cases. For SEP 1 that's providing a common format for Stellar account metadata.

In today's challenge your task is to create, host and link to a stellar.toml file with an SQ02_EASTER_EGG field containing the text:

Log into series 2 of Stellar Quest then visit quest.stellar.org/series2. Finally drag and drop your Stellar Quest series 2 badge PNG images onto the screen. Enjoy!
Note you won't be able to solve today's challenge using only the laboratory. You'll need to host a toml file and for that you'll need a basic server. Personally I love RunKit and CodeSandbox but feel free to use whatever works. Good luck!

After drag and drop, this message appears:

Galactic Consensus – Chapter 8
The Sender pursed its mouth to taste the murky brew. After further deciphering the letter siPe k.Mcrl sent it, it deciphered that the two pebbles and strange leaf were actually meant for a delicacy native to siPe k.Mcrl’s world.

“Tasty and acerbic,” the Sender declared, smacking its lack of lips. Ladling some of the brew into a bowl for itself and pouring the rest into Sasara’s drinking basin, the Sender sat at the sprawl of crates that was the table. Unfolding the letter with great care, it reread Stroopy’s words with great deliberation.

To be honest, the Sender didn’t feel as omniscient as Stroopy possibly believed it to be. In fact, it was always delighted by the arrival of a new letter from a place it did not recognize. While certain habits between species remained ubiquitous, a being it had never interacted with before was sure to add something new to the Sender’s understanding of the universe.

That understanding itself hadn’t begun to manifest until the first letter arrived at the Sender’s doorstep. Then a package. Then a steady trickle of crates, parcels, plants, missives, fauna, treaties, sonnets, and more made their way to the little corner of the universe where the Sender lived.

Overwhelmed by the deluge of extradimensional material, the Sender had no choice but to be creative. As it learned more about the universe outside its own, the more the Sender used these boxes as building blocks, fashioning entire landmarks, terrain, and civilizations based on the accounts of who it was corresponding with at the time.

The only advantage that the Sender, it would later on write in reply to Stroopy, possessed over other lifeforms was the abundance of time and the freedom to spend it however it wanted. Couple that with the never ending stream of resources, and all that limited the Sender was its creativity and empathy.

It read somewhere that in other worlds, beings would pay tribute to their deities through offerings and sacrifice. This is how deities would sustain themselves and their powers, which they would then use, for better or for worse, on the beings who worshipped them. And the cycle continued.

The Sender was unsure if its existence was well-known to the universe at large. Certainly not enough to be worshipped. Right?

Perhaps the existence the Sender lived now would not be sustainable. Maybe, one day, the packages and mail would stop coming. The lifeforms it learned from would eventually die off as well. In that case, all the Sender could do was pray that someone was kind enough to send an unlimited subscription of food for Sasara.

In any case, it never wanted to take its correspondents for granted, which is why the Sender kept on writing.





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
        Server              server           = new Server("https://horizon-testnet.stellar.org");
        KeyPair             questAccountKeys = KeyPair.fromSecretSeed(quest1SecretKey);
        AccountResponse     questAccount     = server.accounts().account(questAccountKeys.getAccountId());
        Transaction.Builder txBuilder        = new Transaction.Builder(questAccount,
                                                                       Network.TESTNET).setBaseFee(
                                                                           FeeBumpTransaction.MIN_BASE_FEE)
                                                                                       .setTimeout(180);

        // firstly a stellar.toml has to be created and host accordingly to SEP1 with quest account linked
        // At sergv.pythonanywhere.com"
        // Static files:
        // URL                    //Directory
        // /.well-known/                  /home/sergv/.well-known/
        // then the home domain can be set
        txBuilder.addOperation(new SetOptionsOperation.Builder().setHomeDomain("sergv.pythonanywhere.com").build());

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


//~ Formatted by Jindent --- http://www.jindent.com
