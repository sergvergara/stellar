package s3q6;

	
	import java.io.BufferedInputStream;
	import java.io.FileInputStream;
	import java.io.FileNotFoundException;
	import java.io.IOException;
	import java.io.InputStream;
	import java.net.URL;
	import org.apache.commons.codec.binary.Base64;

	public class ImageBase64 {

    String imageUrl="";	
    String imageDataString;
	public ImageBase64(String imagePath) {
		
			imageUrl = imagePath;
	        String destinationFile = "image_1.jpg";

	        try {           
	            // Reading a Image file from file system
	            URL url = new URL(imageUrl);
	            InputStream is = url.openStream();

	            BufferedInputStream imageInFile = new BufferedInputStream(url.openConnection().getInputStream());
	            byte imageData[] = new byte[2048];
	            imageInFile.read(imageData);

	            // Converting Image byte array into Base64 String
	            imageDataString = encodeImage(imageData);
	            System.out.println("imageDataString : " + imageDataString);

	            //System.out.println("Image Successfully Manipulated!");
	        } catch (FileNotFoundException e) {
	            System.out.println("Image not found" + e);
	        } catch (IOException ioe) {
	            System.out.println("Exception while reading the Image " + ioe);
	        }

	}
	public String getImageBase64String() {
		return imageDataString;
	}

	public static String encodeImage(byte[] imageByteArray) {
	    return Base64.encodeBase64URLSafeString(imageByteArray);
	}
	 

}
