package com.monkcommerce.monkcommerceapi;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.monkcommerce.monkcommerceapi.constants.FirebaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

@SpringBootApplication
public class MonkCommerceApiApplication {
	private static final Logger logger = LoggerFactory.getLogger(MonkCommerceApiApplication.class);
	public static void main(String[] args) throws IOException {

		SpringApplication.run(MonkCommerceApiApplication.class, args);

		logger.info("Firebase Configuration Started");
		ClassLoader classLoader = MonkCommerceApiApplication.class.getClassLoader();

		File file = new File(Objects.requireNonNull(classLoader.getResource(FirebaseConstants.FIREBASE_SERVICE_ACCOUNT_KEY)).getFile());
		FileInputStream serviceAccount = new FileInputStream(file.getAbsoluteFile());

		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setDatabaseUrl(FirebaseConstants.FIREBASE_URL)
				.build();

		FirebaseApp.initializeApp(options);
		logger.info("Firebase Configured");
	}

}
