package com.test.gcp;

import java.io.IOException;

import com.test.gcp.auth.RSAGCSSignedURL;
import com.test.gcp.config.ConfigLoader;
import com.test.gcp.config.DesktopAppConfig;

public class DesktopApp {
	private final static String DESKTOP_APP_CONFIG = "./configs/DesktopApp.yaml";

	public static void main(String[] args) throws IOException {
		DesktopAppConfig desktopAppConfig = ConfigLoader.loadBasicYAMLConfig(DESKTOP_APP_CONFIG,
				DesktopAppConfig.class);

		RSAGCSSignedURL.genSignedURL(desktopAppConfig);
		
	}

}
