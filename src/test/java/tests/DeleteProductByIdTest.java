package tests;

import clients.UserAPI;
import com.github.javafaker.Faker;
import dto.CreatedProduct;
import dto.NewProduct;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import settings.Category;
import settings.DatabaseConnectionFactory;
import settings.StatusCode;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import static settings.StatusCode.BAD_REQUEST;

public class DeleteProductByIdTest {
    private final UserAPI userAPI = new UserAPI();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Faker faker = new Faker(Locale.ENGLISH);
    private Connection dbConnection;
    private CreatedProduct createdProduct;

    @BeforeEach
    void setUp() throws JsonProcessingException, SQLException {

        dbConnection = DatabaseConnectionFactory.getConnectionWithTransaction();
        NewProduct newProduct = NewProduct.builder()
                .name(faker.food().fruit())
                .article(UUID.randomUUID())
                .category(Category.VEGETABLES.getName())
                .dictionary("vegetable")
                .price(new BigDecimal(faker.number().randomDouble(2, 1, 1000) + ""))
                .qty(new BigDecimal(faker.number().randomDouble(2, 1, 50) + ""))
                .build();
        String requestBody = objectMapper.writeValueAsString(newProduct);
        Response response = userAPI.createProduct(requestBody);

        createdProduct = objectMapper.readValue(response.getBody().asString(), CreatedProduct.class);

    }

    @Test
    void testDeleteProductById() {
        Response response = userAPI.deleteProduct(createdProduct.getId());
        assertEquals(StatusCode.OK.getCode(), response.getStatusCode());

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
        assertEquals(StatusCode.BAD_REQUEST.getCode(), response.getStatusCode());
    }
}
