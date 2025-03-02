package com.test.gcp.config;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

public class ConfigLoader {
	private ConfigLoader() {
	}

	public static <T> T loadBasicYAMLConfig(String configPath, Class<T> ret) throws IOException {
		YAMLFactory yamlFactory = new YAMLFactory();
		File configFile = new File(configPath);

		System.out.println("Attempting to load YAML config: " + configFile.getCanonicalPath());

		try (YAMLParser yamlSource = yamlFactory.createParser(configFile)) {
			return (new YAMLMapper()).readValue(yamlSource, ret);
		}
	}
}
