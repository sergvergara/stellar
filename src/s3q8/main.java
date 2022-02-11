
/*
Now that we've got SEP-0010 under our belt let's make use of it to make an automated testnet SEP-0006 deposit to SDF's testanchor endpoint.

Between SEP-0006, SEP-0024 and SEP-0031 we have all we need to connect Stellar with all the real world's "anchored" assets. SEP-0006 is the API-only method for handling the flow so let's try that.

Your task today will be to use SEP-0006 to request a deposit of MULT issued by GDLD...FSMM from the testanchor.stellar.org endpoint.

This task will actually make use of SEP-0010, SEP-0006 and SEP-0012 so buckle up, read the docs, and enjoy the ride!

Finally I'm sure you're noticing by now we've begun to drift away from the Laboratory harbor into the more adventurous waters of the greater world wide web.

For Quests like this if you're uncomfortable whipping up some new code feel free to use curl or a tool like www.postman.com for constructing your non-Laboratory API calls.
*/

/*
 * SEP: 0006
 * Title: Deposit and Withdrawal API
 * 
 * SEP: 0010
 * Title: Stellar Web Authentication
 * 
 * SEP: 0012
 * Title: KYC API
 * 
 * SEP: 0024
 * Title: Hosted Deposit and Withdrawal
 * 
 * SEP: 0031
 * Title: Cross-Border Payments API
 */

package s3q8;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
//Server
import org.stellar.sdk.Asset;
import org.stellar.sdk.ChangeTrustAsset;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.FeeBumpTransaction;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Network;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

//Ajax
import shadow.okhttp3.MediaType;
import shadow.okhttp3.OkHttpClient;
import shadow.okhttp3.Request;
import shadow.okhttp3.RequestBody;
import shadow.okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;
import shadow.okhttp3.Request;

public class main {
    private static String quest3PublicKey  = "GBJ6BGUSCSSUKLS7LXZ7QPLY7AQUNLCYDLWZYICCRQ24YLQER5Y3MTVT";
    private static String quest3SecretKey  = "";
    private static JSONObject responseAjax;


    public static void main(String[] args) throws IOException, JSONException, InterruptedException {
      
    	
    	Server              server           = new Server("https://horizon-testnet.stellar.org");
    	// Fund and create the quest account.
   		String urlFriendBot = "https://friendbot.stellar.org/?addr=" + quest3PublicKey;
		OkHttpClient client = new OkHttpClient();
		Request requestCreateAccountFriendBot = new Request.Builder().url(urlFriendBot).build();
		Request requestSep0010;
		JSONObject responseBodyJSONSEP0010Request = null;
 		JSONObject myResponse1=null;
		Request requestGetSEP0010tokenByPost;
		String tokenReceived="";
		String id ="";
 		String idDepositRequest="";


		try (Response response1 = client.newCall(requestCreateAccountFriendBot).execute()) {
			String responseJson = response1.body().string();
			responseAjax = new JSONObject(responseJson);

			// Get and print the response from friendbot.
			if((responseAjax.getString("status")=="200 OK")	||(responseAjax.getString("detail").contains("createAccountAlreadyExist"))) 
			{
				if (responseAjax.getString("detail").contains("createAccountAlreadyExist")) 
				{
					System.out.println("Error funding account because it already exists");
				}else 
				{
					System.out.println("Successfully funded account");
				}
			}else
			{
					System.out.println("Error funding account.");
			}
		}
		
	   	// Get the keypair of the quest account from the secret key.
        KeyPair             questAccountKeys = KeyPair.fromSecretSeed(quest3SecretKey);
        AccountResponse     questAccount     = server.accounts().account(questAccountKeys.getAccountId());
    	    	   	
    	// Create the asset
        Asset            asset = Asset.createNonNativeAsset("MULT", "GDLD3SOLYJTBEAK5IU4LDS44UMBND262IXPJB3LDHXOZ3S2QQRD5FSMM");

        // Build a change trust operation.
        ChangeTrustAsset cTA   = ChangeTrustAsset.create(asset);
              	
    	// Construct the transaction.
        Transaction.Builder txBuilder = new Transaction.Builder(questAccount, Network.TESTNET)
				.setBaseFee(FeeBumpTransaction.MIN_BASE_FEE).setTimeout(180);
        txBuilder.addOperation(new ChangeTrustOperation.Builder(cTA,
                "1000000").setSourceAccount(
                		questAccountKeys.getAccountId())
                          .build());
        Transaction transaction = txBuilder.build();

        // Sign the transaction.
        transaction.sign(questAccountKeys);
   	
    	// Send the transaction to the network.
        try {
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            // Print the response.
            System.out.println("Success!");
            System.out.println("Is success? "+response.isSuccess());
            System.out.println("HASH "+response.getHash());

        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e);
        }
            
    	// Send a SEP-0010 request.
 		String urlSEP = "https://testanchor.stellar.org/auth?account=" + questAccountKeys.getAccountId();
 		client = new OkHttpClient();
 		requestSep0010  = new Request.Builder().url(urlSEP).build();
 		try (Response responseSEP0010Request = client.newCall(requestSep0010).execute()) {
 	    	// Parse the response.
 			responseBodyJSONSEP0010Request = new JSONObject(responseSEP0010Request.body().string());
  			//->transaction = AAAAAgAAAACpn2Fr7GAZ4XOcFvEz+xduBFDK1NDLQP875GtWWlJ0XQAAAMgAAAAAAAAAAAAAAAEAAAAAYgO7/QAAAABiA7+BAAAAAAAAAAIAAAABAAAAAFPgmpIUpUUuX13z+D14+CFGrFga7ZwgQow1zC4Ej3G2AAAACgAAABt0ZXN0YW5jaG9yLnN0ZWxsYXIub3JnIGF1dGgAAAAAAQAAAEBkbWtwaEhLNjlrdGQrTzE1Tm16U2Y5SmZCeHlZWkEvR1d0dnRWRVpoajNERi92NktxUjYvbTFrbHFtSjErRjU1AAAAAQAAAACpn2Fr7GAZ4XOcFvEz+xduBFDK1NDLQP875GtWWlJ0XQAAAAoAAAAPd2ViX2F1dGhfZG9tYWluAAAAAAEAAAAWdGVzdGFuY2hvci5zdGVsbGFyLm9yZwAAAAAAAAAAAAFaUnRdAAAAQDHqdblj+YfjA+UUIGGto6qf+M2VYLX+J6e9KwaGG+Z4mVEx8X5OUPBWQ2TbYme9Mk4a1yKhWWIrZI6b0gXqPAE=
 			//->transaction hash = 51cf8bbff35a8ad1324c56a604cd447c781824b28ee064ee740942e1ce83cec6
		} catch (IOException e) {
			e.printStackTrace();
		}
 		
		// Sign the transaction response.
		org.stellar.sdk.Transaction txResponse = (Transaction) org.stellar.sdk.Transaction.fromEnvelopeXdr(responseBodyJSONSEP0010Request.getString("transaction"), Network.TESTNET);
		txResponse.sign(questAccountKeys);
		String signedStr = txResponse.toEnvelopeXdrBase64();
 	    	
		// Post the signed transaction to get the SEP-0010 token.
		// signed Transaction to JSON MediaType
		JSONObject json = new JSONObject();
		json.put("transaction", signedStr);
		RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());
		requestGetSEP0010tokenByPost = new Request.Builder().url(urlSEP).post(body).build();
	
		// Token Request
		try (Response response2 = client.newCall(requestGetSEP0010tokenByPost).execute()) {
			if (response2.isSuccessful()) {
				System.out.println("all was ok: " + response2.toString());
		    	// Parse the response.
				JSONObject response2JSON = new JSONObject(response2.body().string());
				tokenReceived = response2JSON.getString("token");

			} else {
				System.out.println("Failed: " + response2.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Fill the KYC information.
    	// Send a PUT request to complete KYC.
		JSONObject jsonKYC = new JSONObject();
		jsonKYC.put("account", questAccountKeys.getAccountId().toString());
		jsonKYC.put("first_name","Stellar");
		jsonKYC.put("last_name","Quest");
		jsonKYC.put("email_address","quest@stellar.org");
		jsonKYC.put("bank_number","07312014");
		jsonKYC.put("bank_account_number","05282021");
		jsonKYC.put("type","bank_account");
		

		RequestBody requestBodyKYC	 = RequestBody.create(MediaType.parse("application/json"),jsonKYC.toString());
		Request requestPUTKYC = new Request.Builder().url("https://testanchor.stellar.org/kyc/customer").put(requestBodyKYC).
				addHeader("Authorization", "Bearer "+tokenReceived).addHeader("Content-Type", "application/json").build();
		//TODO RESPONSE
		// PUT Request
				try (Response responsePUT = client.newCall(requestPUTKYC).execute()) {
					if (responsePUT.isSuccessful()) {
				    	// Parse the response.
						JSONObject response2JSON = new JSONObject(responsePUT.body().string());
						id=response2JSON.getString("id");
						//JSON: {"id":"1290"}
					} else {
						System.out.println("Failed PUT REQUEST: " + responsePUT.toString());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
		
		
    	// Send the SEP-0006 request to deposit MULT to the quest account.
		Request requestSEP006 = new Request.Builder().url("https://testanchor.stellar.org/sep6/deposit?asset_code="+
				asset.toString().split(":")[0]+"&account="+questAccountKeys.getAccountId()+"&amount="+"100"+"&type="+"bank_account").addHeader("Authorization", "Bearer "+tokenReceived).build();
 		client = new OkHttpClient();
		try (Response response1 = client.newCall(requestSEP006).execute()) {
 	    	// Parse the response.
 			responseBodyJSONSEP0010Request = new JSONObject(response1.body().string());
 			// Parse the response.
 			idDepositRequest = responseBodyJSONSEP0010Request.getString("id");

		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	// Check the status of the request.
		Request requestCheckStatus = new Request.Builder().url("https://testanchor.stellar.org/sep6/transaction?id="+idDepositRequest).
				header("Authorization", "Bearer "+tokenReceived).build();
 		client = new OkHttpClient();
 		try (Response response3 = client.newCall(requestCheckStatus).execute())
 		{
 			// Parse the response.
			JSONObject response3JSON = new JSONObject(response3);
		}
 		catch(IOException e) {
 			e.printStackTrace();
 			System.out.println("Error");
 		}
 			
    	boolean reqStatusComplete=false;
    	
    	// Loop until the request is completed.
    	String requestStatus;
 		while (reqStatusComplete) {
 			try (Response response3 = client.newCall(requestCheckStatus).execute())
 	 		{
 	 			responseBodyJSONSEP0010Request = new JSONObject(response3.body().string());
 	 			requestStatus= responseBodyJSONSEP0010Request.getString("status");
 	 			if (requestStatus=="completed") {
 	 	 			reqStatusComplete=true;
 	 			}
 	 		}
 	 		catch(IOException e) {
 	 			reqStatusComplete=false;
 	 			e.printStackTrace();
 	 			System.out.println("Error");
 	 		}
 			TimeUnit.SECONDS.sleep(10*LocalDateTime.now().getSecond());
 		}
    }
}


