package tests;

import clients.UserAPI;
import com.github.javafaker.Faker;
import dot.CreatedProduct;
import dot.NewProduct;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import settings.DatabaseConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class DeleteProductByIdTest {
    private final UserAPI userAPI = new UserAPI();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Faker faker = new Faker(Locale.ENGLISH);
    private Connection dbConnection;
    private CreatedProduct createdProduct;

    @BeforeEach
    void setUp() throws JsonProcessingException, SQLException {

        dbConnection = DatabaseConnectionFactory.getConnectionWithTransaction();
        NewProduct newProduct = new NewProduct.Builder()
                .name(faker.food().fruit())
                .article(UUID.randomUUID())
                .category("VEGETABLES")
                .dictionary("vegetable")
                .price(132.21)
                .qty(143.34)
                .build();
        String requestBody = objectMapper.writeValueAsString(newProduct);
        Response response = userAPI.createProduct(requestBody);

        createdProduct = objectMapper.readValue(response.getBody().asString(), CreatedProduct.class);

    }

    @Test
    void testDeleteProductById() {
        Response response = userAPI.deleteProduct(createdProduct.getId());
        assertEquals(200, response.getStatusCode());

        step("Проверяем, что запись удалена из БД", () -> {
            String sql = "SELECT * FROM product WHERE id = ?";
            try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
                pstmt.setObject(1, createdProduct.getId(), java.sql.Types.OTHER);

                try (ResultSet rs = pstmt.executeQuery()) {
                    assertFalse(rs.next(), "После удаления продукт не должен находиться в БД.");
                }
            }
        });
    }

    @Test
    void testDeleteNotExistProduct() {
        Response response = userAPI.deleteProduct(UUID.randomUUID());
        assertEquals(400, response.getStatusCode());
    }
}
