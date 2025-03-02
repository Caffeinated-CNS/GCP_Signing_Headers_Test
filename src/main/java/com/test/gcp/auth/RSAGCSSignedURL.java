package com.test.gcp.auth;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.test.gcp.config.DesktopAppConfig;

public class RSAGCSSignedURL {
	
	private RSAGCSSignedURL() {}
	
	public static void genSignedURL(DesktopAppConfig desktopAppConfig) {

		ServiceAccountCredentials serviceAccountCredentials;
		try {
			serviceAccountCredentials = ServiceAccountCredentials
					.fromStream(new FileInputStream(desktopAppConfig.getAccessTokenPath()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load service account key file.", e);
		}
		
		String saInKeyFile = serviceAccountCredentials.getAccount();
		if(!saInKeyFile.equalsIgnoreCase(desktopAppConfig.getSigningServiceAccountID())) {
			throw new RuntimeException("Configured signingServiceAccountID does not match provided keyfile, configured: " + desktopAppConfig.getSigningServiceAccountID() + " recieved: " + saInKeyFile);
		}

		StorageOptions storageOptions = StorageOptions.newBuilder().setProjectId(desktopAppConfig.getGcpProjectId())
				.build();
//				.setCredentials(serviceAccountCredentials).build();

		// Define resource
		BlobInfo blobInfo = BlobInfo
				.newBuilder(BlobId.of(desktopAppConfig.getGcsBucket(), desktopAppConfig.getGcsObjectPath())).build();

		URL url = storageOptions.getService().signUrl(blobInfo, desktopAppConfig.getTimeoutInMins(), TimeUnit.MINUTES,
				Storage.SignUrlOption.withV4Signature(), Storage.SignUrlOption.signWith(serviceAccountCredentials),
				Storage.SignUrlOption.withQueryParams(getExtendedQueryParams(desktopAppConfig)),
				Storage.SignUrlOption.withExtHeaders(getExtendedHeaders(desktopAppConfig)));

		System.out.println("Generated GET signed URL:");
		System.out.println(url);
		System.out.println("You can use this URL with any user agent, for example:");
		System.out.println("curl '" + url + "'");
	}

	private static HashMap<String, String> getExtendedQueryParams(DesktopAppConfig desktopAppConfig) {
		HashMap<String, String> extendedQueryParams = new HashMap<>();

		// NOTE: These will not persist into GCS Audit Logs
		extendedQueryParams.put("Consumer-Name", "client_asdasdasd_1234");
		extendedQueryParams.put("Extra-Log-Info", "some thing for query log discovery");
		extendedQueryParams.put("Generator-Service", desktopAppConfig.getAppName());

		return extendedQueryParams;
	}

	/*
	 * These are not persisted into the Cloud Storage event queue, i.e., printing
	 * the CloudEvent received at Cloud Run Functions does not include these
	 * headers.
	 */
	private static HashMap<String, String> getExtendedHeaders(DesktopAppConfig desktopAppConfig) {
		HashMap<String, String> extendedHeaders = new HashMap<>();

		// NOTE: This will not persist into GCS Audit Logs
		extendedHeaders.put("X-Goog-Log-Me", "header not included in logging");
		
		//NOTE: This _will_ persist into the audit logs
		//NOTE: Headers with x-goog-custom-audit-<key name> will make it to the GCS audit logs.
		//REFERENCE: https://cloud.google.com/storage/docs/audit-logging#add-custom-metadata
		extendedHeaders.put("x-goog-custom-audit-correlation-id", "c3ce12fc-0ce5-4ff4-95c6-fee9b466a71b");
		extendedHeaders.put("x-goog-custom-audit-source", desktopAppConfig.getAppName());		
		extendedHeaders.put("x-goog-custom-audit-signed-by", desktopAppConfig.getSigningServiceAccountID());

		return extendedHeaders;
	}
}