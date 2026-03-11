package clients;

import settings.ApiEndpoints;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class APIClient {

    private static final Logger logger = LoggerFactory.getLogger(APIClient.class);
    private final String baseUrl;
    @Getter
    private final String usersPath;

    public APIClient() {
        Properties properties = loadProperties();
        this.baseUrl = properties.getProperty("baseUrl");
        this.usersPath = ApiEndpoints.PRODUCTS.getPath();

        logger.info("APIClient инициализирован:");
        logger.info("  baseUrl: {}", baseUrl);
    }

    private Properties loadProperties() {
        String environment = System.getProperty("env", "test");
        String configFileName = "application-" + environment + ".properties";

        logger.debug("Загрузка конфигурации для окружения: {}", environment);
        logger.debug("Имя файла конфигурации: {}", configFileName);

        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                String errorMessage = "Configuration file not found: " + configFileName;
                logger.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
            properties.load(input);
            logger.info("Конфигурация успешно загружена из файла: {}", configFileName);
        } catch (IOException e) {
            String errorMessage = "Unable to load configuration file: " + configFileName;
            logger.error(errorMessage, e);
            throw new IllegalStateException(errorMessage, e);
        }

        return properties;
    }

    public RequestSpecification getRequestSpec() {
        RequestSpecification spec = RestAssured.given()
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");

        return spec;
    }
}