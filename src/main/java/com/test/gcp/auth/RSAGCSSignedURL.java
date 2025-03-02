package com.test.gcp.auth;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.test.gcp.config.DesktopAppConfig;

public class RSAGCSSignedURL {

	private RSAGCSSignedURL() {
	}

	/**
	 * Function to generate a signed URL, based on {@link DesktopAppConfig} instance
	 * parameters.
	 * 
	 * @param desktopAppConfig input parameters to generate the URL with.
	 */
	public static void genSignedURL(DesktopAppConfig desktopAppConfig) {

		// Load exported service account RSA key.
		ServiceAccountCredentials serviceAccountCredentials;
		try {
			serviceAccountCredentials = ServiceAccountCredentials
					.fromStream(new FileInputStream(desktopAppConfig.getAccessTokenPath()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load service account key file.", e);
		}

		// Check that the configuration value matches the service account key file's SA
		// name; this is meant to ensure that you don't generate a URL for an unexpected
		// SA or keyfile. This check can be removed.
		String saInKeyFile = serviceAccountCredentials.getAccount();
		if (!saInKeyFile.equalsIgnoreCase(desktopAppConfig.getSigningServiceAccountID())) {
			throw new RuntimeException(
					"Configured signingServiceAccountID does not match provided keyfile, configured: "
							+ desktopAppConfig.getSigningServiceAccountID() + " recieved: " + saInKeyFile);
		}

		// Set context for where to attempt to sign the GCS URL
		StorageOptions storageOptions = StorageOptions.newBuilder().setProjectId(desktopAppConfig.getGcpProjectId())
				.build();
//				.setCredentials(serviceAccountCredentials).build();

		// Define resource to construct a signed URL for.
		BlobInfo blobInfo = BlobInfo
				.newBuilder(BlobId.of(desktopAppConfig.getGcsBucket(), desktopAppConfig.getGcsObjectPath())).build();

		// V4 signed URL based on configuration object's values.
		URL url = storageOptions.getService().signUrl(blobInfo, desktopAppConfig.getTimeoutInMins(), TimeUnit.MINUTES,
				Storage.SignUrlOption.withV4Signature(), Storage.SignUrlOption.signWith(serviceAccountCredentials),
				Storage.SignUrlOption.withQueryParams(getExtendedQueryParams(desktopAppConfig)),
				Storage.SignUrlOption.withExtHeaders(getExtendedHeaders(desktopAppConfig)));

		System.out.println("Generated GET signed URL:");
		System.out.println(url);
		System.out.println("You can use this URL with any user agent, for example:");
		System.out.printf("\ncurl %s \"%s\"\n\n", genCURLHeadersStr(getExtendedHeaders(desktopAppConfig)).substring(1), url);
	}

	/**
	 * Formatting function to provide CURL header parameters.
	 * 
	 * @param extHeaders header map
	 * @return string
	 */
	private static String genCURLHeadersStr(Map <String, String> extHeaders) {
		StringBuilder sb = new StringBuilder();
		
		for(Entry<String, String> curHeader : extHeaders.entrySet()) {
			sb.append(" -H \"");
			sb.append(curHeader.getKey());
			sb.append(": ");
			sb.append(curHeader.getValue());
			sb.append("\"");
		}
		
		return sb.toString();
	}

	/**
	 * Add extra query parameters, will make finding the requested URL easier if
	 * going through a gateway.
	 * 
	 * @param desktopAppConfig app config reference to pull parameters from
	 * @return a hashmap of query-param-name, query-param-value pairs
	 */
	private static HashMap<String, String> getExtendedQueryParams(DesktopAppConfig desktopAppConfig) {
		HashMap<String, String> extendedQueryParams = new HashMap<>();

		// NOTE: These will not persist into GCS Audit Logs
		extendedQueryParams.put("Consumer-Name", "client_asdasdasd_1234");
		extendedQueryParams.put("Extra-Log-Info", "some thing for query log discovery");
		extendedQueryParams.put("Generator-Service", desktopAppConfig.getAppName());

		return extendedQueryParams;
	}

	/**
	 * Add additional required headers to call URL.
	 * 
	 * If named correctly, these headers will be persisted into the GCS Audit logs &
	 * thus could allow for correlating specific GCS call and the generating app
	 * that created the URL.
	 * 
	 * @param desktopAppConfig app config reference to pull parameters from
	 * @return a hashmap of header-name, header-value pairs
	 */
	private static HashMap<String, String> getExtendedHeaders(DesktopAppConfig desktopAppConfig) {
		HashMap<String, String> extendedHeaders = new HashMap<>();

		// NOTE: This will not persist into GCS Audit Logs
		extendedHeaders.put("X-Goog-Log-Me", "header not included in logging");

		// NOTE: This _will_ persist into the audit logs
		// NOTE: Headers with x-goog-custom-audit-<key name> will make it to the GCS
		// audit logs.
		// REFERENCE:
		// https://cloud.google.com/storage/docs/audit-logging#add-custom-metadata
		extendedHeaders.put("x-goog-custom-audit-correlation-id", "c3ce12fc-0ce5-4ff4-95c6-fee9b466a71b");
		extendedHeaders.put("x-goog-custom-audit-source", desktopAppConfig.getAppName());
		extendedHeaders.put("x-goog-custom-audit-signed-by", desktopAppConfig.getSigningServiceAccountID());

		return extendedHeaders;
	}
}