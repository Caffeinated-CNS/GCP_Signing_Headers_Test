package com.test.gcp.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor(staticName = "of")
public class DesktopAppConfig {
	// GCP project id to query against.
	@NonNull
	private String gcpProjectId;
	// GCP Service account with appropriate permissions.
	@NonNull
	private String saAccountName;
	private String accessTokenPath = null;


	@NonNull
	private String gcsBucket = null;

	@NonNull
	private String gcsObjectPath = null;

	private int timeoutInMins = 15;
	
	@NonNull
	private String signingServiceAccountID;

	private String appName = "com.test.gcp.GCS_Signed_URL_With_Addtional_Headers/0.1";
}
