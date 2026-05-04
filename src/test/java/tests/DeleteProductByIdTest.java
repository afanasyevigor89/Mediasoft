package tests;

import clients.HibernateConfig;
import clients.UserAPI;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import config.KafkaProducerConfig;
import net.datafaker.Faker;
import dto.CreatedProduct;
import dto.NewProduct;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import service.ProductService;
import settings.Category;
import settings.StatusCode;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {HibernateConfig.class, KafkaProducerConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DeleteProductByIdTest {

    private final UserAPI userAPI = new UserAPI();
    JsonMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private final Faker faker = new Faker(Locale.ENGLISH);
    private CreatedProduct createdProduct;

    @Autowired
    private ProductService productService;

    @BeforeEach
    void setUp() throws  com.fasterxml.jackson.core.JsonProcessingException {

        NewProduct newProduct = NewProduct.builder()
                .name(faker.food().fruit())
                .article(UUID.randomUUID())
                .category(Category.VEGETABLES.getName())
                .dictionary("vegetable")
                .price(new BigDecimal(faker.commerce().price(1,1000).replace(",", ".")))
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
            boolean result = productService.isProductDeleted(createdProduct.getId());
            assertFalse(result, "Товар должен быть удален из БД");
            

        });
    }

    @Test
    void testDeleteNotExistProduct() {
        Response response = userAPI.deleteProduct(UUID.randomUUID());
        assertEquals(StatusCode.BAD_REQUEST.getCode(), response.getStatusCode());
    }
}
