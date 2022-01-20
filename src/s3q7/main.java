
/*
SERIES 3 - QUEST 7
Acquire and make use of a SEP-0010 JWT
Goals & Details
Outside of the relatively simple and controlled world of Stellar operations there's a whole universe of use cases and implementations. Even here though there is need for order and interoperability.

These needs are met by Stellar Ecosystem Proposals or SEPs. SEPs are the Stellar wilderness guidebooks ensuring everyone is following the same path and rules and is thus able to interoperate with each other.

SEP-0010 is an authentication SEP outlining how to prove ownership of a Stellar account to a service. It is used in many other SEPs so it's an important foundational SEP to understand.

Today's task will be to acquire a SEP-0010 JWT and then embed that JWT back into your account's () manageData fields in identical fashion to how we embedded the NFT data in the previous quest.

Please note that this embed step is not part of SEP-0010 and is definitely not something you'd do in practice. I'm just including it here as a method for reading back and making use of the generated SEP-0010 JWT as part of the verification step. It's also a good refresher for Quest 6.
*/
package s3q7;

import org.stellar.sdk.AccountRequiresMemoException;

//Server
import org.stellar.sdk.FeeBumpTransaction;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.ManageDataOperation;
import org.stellar.sdk.Network;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;

//Ajax
import shadow.okhttp3.MediaType;
import shadow.okhttp3.OkHttpClient;
import shadow.okhttp3.Request;
import shadow.okhttp3.RequestBody;
import shadow.okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class main {

	private static String quest3PublicKey = "GAJ2VL2KVWYKTWD2YJZTFGXOWUCI6XSXH6N34AEHHBWNBNQFHQKUCOI6";
	private static String quest3SecretKey = "";
	private static String url = new String("https://testanchor.stellar.org/auth?account=");

	public static void main(String[] args) throws IOException, InterruptedException, JSONException {
		Server server = new Server("https://horizon-testnet.stellar.org");
		KeyPair questAccountKeys = KeyPair.fromSecretSeed(quest3SecretKey);
		AccountResponse questAccount = server.accounts().account(questAccountKeys.getAccountId());
		Transaction.Builder txBuilder = new Transaction.Builder(questAccount, Network.TESTNET)
				.setBaseFee(FeeBumpTransaction.MIN_BASE_FEE).setTimeout(180);

		JSONObject myResponse1;
		Request requestSep0010token, requestGetSEP0010tokenByPost;
		String signedStr, tokenReceived = "";
		String token;
		JSONObject response2JSON;

		// Send a SEP-0010 request.
		String urlSEP = "https://testanchor.stellar.org/auth?account=" + questAccountKeys.getAccountId();
		OkHttpClient client = new OkHttpClient();
		requestSep0010token = new Request.Builder().url(urlSEP).build();
		try (Response response1 = client.newCall(requestSep0010token).execute()) {
			String responseJson = response1.body().string();
			myResponse1 = new JSONObject(responseJson);
			/*
			 * Example: "transaction":
			 * "AAAAAgAAAACpn2Fr7GAZ4XOcFvEz+xduBFDK1NDLQP875GtWWlJ0XQAAAMgAAAAAAAAAAAAAAAEAAAAAYbsw9gAAAABhuzR6AAAAAAAAAAIAAAABAAAAABOqr0qtsKnYesJzMprutQSPXlc/m74Ahzhs0LYFPBVBAAAACgAAABt0ZXN0YW5jaG9yLnN0ZWxsYXIub3JnIGF1dGgAAAAAAQAAAEBrRU0vSFhNVGsvejlwMlpIUGtmVG55WGxZaUVqVTRjWDVnVjNDeTdlZHFYNVhQSWc4V0ZOV1IyNHEyOVdBbWdSAAAAAQAAAACpn2Fr7GAZ4XOcFvEz+xduBFDK1NDLQP875GtWWlJ0XQAAAAoAAAAPd2ViX2F1dGhfZG9tYWluAAAAAAEAAAAWdGVzdGFuY2hvci5zdGVsbGFyLm9yZwAAAAAAAAAAAAFaUnRdAAAAQBCnEtVLsEyPZN7Pg5LCp0jIaoiv00Fu4NuFaYZuvyte4NhbNws1tG9Pu7MnlufrfnBnhiDKOaNQXxB/9zuJXA8=",
			 * 
			 * "network_passphrase": "Test SDF Network ; September 2015"
			 */
		}

		// Sign the transaction response.
		org.stellar.sdk.Transaction txResponse = (Transaction) org.stellar.sdk.Transaction
				.fromEnvelopeXdr(myResponse1.getString("transaction"), Network.TESTNET);
		txResponse.sign(questAccountKeys);
		signedStr = txResponse.toEnvelopeXdrBase64();

		// EXAMPLE SIGNED TRANSACTION
		/*
		 * Transaction signed (laboratory webpage output) AAAAAgAAAACpn2Fr7GAZ4XOcFvEz+
		 * xduBFDK1NDLQP875GtWWlJ0XQAAAMgAAAAAAAAAAAAAAAEAAAAAYbsw9gAAAABhuzR6AAAAAAAAAAIAAAABAAAAABOqr0qtsKnYesJzMprutQSPXlc/
		 * m74Ahzhs0LYFPBVBAAAACgAAABt0ZXN0YW5jaG9yLnN0ZWxsYXIub3JnIGF1dGgAAAAAAQAAAEBrRU0vSFhNVGsvejlwMlpIUGtmVG55WGxZaUVqVTRjWDVnVjNDeTdlZHFYNVhQSWc4V0ZO
		 * V1IyNHEyOVdBbWdSAAAAAQAAAACpn2Fr7GAZ4XOcFvEz+
		 * xduBFDK1NDLQP875GtWWlJ0XQAAAAoAAAAPd2ViX2F1dGhfZG9tYWluAAAAAAEAAAAWdGVzdGFuY2hvci5zdGVsbGFyLm9yZwAA
		 * AAAAAAAAAAJaUnRdAAAAQBCnEtVLsEyPZN7Pg5LCp0jIaoiv00Fu4NuFaYZuvyte4NhbNws1tG9Pu7MnlufrfnBnhiDKOaNQXxB
		 * /9zuJXA8FPBVBAAAAQLtag6hNvB5zdfTrXjFxwz35/N5d
		 * XK24EjkhcyG4nQiZgHzzJR08BbHh72rolF9j9K37HfIx1wUbiBAXlb13TQM=
		 */

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
				response2JSON = new JSONObject(response2.body().string());
				tokenReceived = response2JSON.getString("token");
			} else {
				System.out.println("Failed: " + response2.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// From here to the end: Split the token and add it to data field in a
		// transaction
		ManageDataOperation operation;
		int counter = 0;
		int limit = 399;
		String buff1, name, value = "";
		// Token Example
		// token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3Rlc3RhbmNob3Iuc3RlbGxhci5vcmcvYXV0aCIsInN1YiI6IkdBSjJWTDJLVldZS1RXRDJZSlpURkdYT1dVQ0k2WFNYSDZOMzRBRUhIQldOQk5RRkhRS1VDT0k2IiwiaWF0IjoxNjQyMDkyNDM2LCJleHAiOjE2NDIxNzg4MzYsImp0aSI6IjAwYTk5MzhiYWVlYmFlZjYzYjU3MTBkMTA4NDNkZGRmN2ZiZDI2OGE2ODE3ZGMxZGViNjMxOWE2NmVmZjhkNDYiLCJjbGllbnRfZG9tYWluIjpudWxsfQ.zMfgJOo1gUP2udg7JCqV1ZK4DaMtoJzgoLkiAkl4Qe0";

		token = tokenReceived;
		buff1 = token;

		for (int i = 0; i < buff1.length(); i = i + 126) {
			if (limit - i < 126) {
				if (limit - i < 62) {
					// name = buff1.substring(i, limit - i);
					name = buff1.substring(i, limit);
				} else {
					name = buff1.substring(i, i + 62);
				}
			} else {
				name = buff1.substring(i, i + 62);
			}
			if (limit - i < 126) {
				if (limit - i > 62) {
					value = buff1.substring(i + 62, limit);
				} else {
					value = "";
				}
			} else {
				value = buff1.substring(i + 62, i + 126);
			}
			if (counter < 10) {
				name = "0" + counter + name;
			} else {
				name = counter + name;
			}

			operation = new ManageDataOperation.Builder(name, value.getBytes(StandardCharsets.UTF_8))
					.setSourceAccount(questAccountKeys.getAccountId()).build();

			txBuilder.addOperation(operation);
			counter++;
		}

		// value as 'null' to remove data entry
		/*
		 * txBuilder=new Transaction.Builder(questAccount,Network.TESTNET).setBaseFee(
		 * FeeBumpTransaction.MIN_BASE_FEE).setTimeout(180); operation = new
		 * ManageDataOperation.Builder(
		 * "00eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3Rlc",null).
		 * setSourceAccount(questAccountKeys.getAccountId()).build();
		 * txBuilder.addOperation(operation);
		 */

		Transaction transaction = txBuilder.build();
		transaction.sign(questAccountKeys);
		try {
			org.stellar.sdk.responses.Response response3 = server.submitTransaction(transaction);
		} catch (IOException | AccountRequiresMemoException e) {

			System.out.println(e.toString());
		}

	}

}