
/*
SERIES 3 - QUEST 5
Successfully submit a clawback operation
Goals & Details
Ask and ye shall receive. Today we conquer the asset clawback operation even as the steam wafts up from its freshly minted edifice.

While currently a testnet only feature, the asset clawback operation will be a regulated asset issuer's excalibur of conquest in this brave new world of old → new finance.

It's the Ctrl+Z for blockchain payments. The mechanic for undoing mistaken or fraudulent payments. Once an issuer and trustline have been created under the asset clawback umbrella the issuer has the power to "clawback" any amount of that asset back into the issuing account effectively burning it from existence.

Arguably a controversial feature it's an absolute requirement for issuing regulated assets on the blockchain and ultimately it's issuer specific so effectively opt-in and obvious from a consumers perspective.
*/
package s3q5;

import java.io.IOException;

//Server
import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.ChangeTrustAsset;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.ClawbackOperation;
import org.stellar.sdk.FeeBumpTransaction;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.SetOptionsOperation;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.Response;

public class main {
    private static String quest3PublicKey  = "GDOVRJGGQ3RH44S5NJPOF5RXCZNLUDSOAUHYJU7HYL3OHZYJBWWB6PRA";
    private static String quest3SecretKey  = "";
    private static String helper3PublicKey = "GCQSYHWY6FNGW7TWBQSMKEDBWDBY3JT5AFIXGI2BD2SM4CBXQWLQ2QQE";
    private static String helper3SecretKey = "";
    private static String destination      = new String("https://horizon-testnet.stellar.org");
    private static String startingBalance  = "1000";
    private static String limit            = new String("1000000");

    public static void main(String[] args) throws IOException {
        Server  server            = new Server("https://horizon-testnet.stellar.org");
        KeyPair questAccountKeys  = KeyPair.fromSecretSeed(quest3SecretKey);
        KeyPair helperAccountKeys = KeyPair.fromSecretSeed(helper3SecretKey);

        // Ask friendbot to fund our accounts
        // helperAccountKeys.friendBot();
        // questAccountKeys.friendBot();
        AccountResponse     questAccount  = server.accounts().account(questAccountKeys.getAccountId());
        AccountResponse     helperAccount = server.accounts().account(questAccountKeys.getAccountId());
        Transaction.Builder txBuilder     = new Transaction.Builder(questAccount,
                                                                    Network.TESTNET).setBaseFee(
                                                                        FeeBumpTransaction.MIN_BASE_FEE)
                                                                                    .setTimeout(180);

        // enable clawback for issuer account
        // 2: authorization revocable, 8: clawback enabled
        txBuilder.addOperation(new SetOptionsOperation.Builder().setSetFlags(10).build());    // setSetFlags(2 or 8)

        Asset            asset = Asset.createNonNativeAsset("MariusLenk", questAccountKeys.getAccountId());
        ChangeTrustAsset cTA   = ChangeTrustAsset.create(asset);

        // establish trustline from helper to issuer
        txBuilder.addOperation(new ChangeTrustOperation.Builder(cTA,
                                                                "1000000").setSourceAccount(
                                                                    helperAccountKeys.getAccountId())
                                                                          .build());

        // send asset to helper
        txBuilder.addOperation(new PaymentOperation.Builder(helperAccountKeys.getAccountId(), asset, "1000").build());

        Transaction createAssetTx = txBuilder.build();

        createAssetTx.sign(questAccountKeys);
        createAssetTx.sign(helperAccountKeys);
        System.out.println("Executing tx.. (sending asset)");

        try {
            Response response = server.submitTransaction(createAssetTx);

            System.out.println("Success!");
            System.out.println("txHash: ${response.hash}");
            System.out.println("result: ${response.resultXdr.get()}");
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e);
        }

        Transaction.Builder tx2Builder = new Transaction.Builder(questAccount,
                                                                 Network.TESTNET).setBaseFee(
                                                                     FeeBumpTransaction.MIN_BASE_FEE)
                                                                                 .setTimeout(180);

        // now claw back the 1000
        tx2Builder.addOperation(new ClawbackOperation.Builder(helperAccountKeys.getAccountId(), asset, "1000").build());

        Transaction clawbackTx = tx2Builder.build();

        clawbackTx.sign(questAccountKeys);
        System.out.println("Executing tx..");

        try {
            Response response = server.submitTransaction(clawbackTx);

            System.out.println("Success!");
            System.out.println("txHash: ${response.hash}");
            System.out.println("result: ${response.resultXdr.get()}");
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e);
        }
    }
}


