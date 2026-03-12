package tests;

import clients.HibernateConfig;
import clients.UserAPI;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.datafaker.Faker;
import dto.CreatedProduct;
import dto.NewProduct;
import entity.ProductsEntity;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import repository.ProductRepository;
import settings.Category;
import settings.StatusCode;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {HibernateConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreateProductTest {

    @Autowired
    private ProductRepository productRepository;

    private final UserAPI userAPI = new UserAPI();
    JsonMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private final Faker faker = new Faker(Locale.ENGLISH);
    UUID article = UUID.randomUUID();
    private CreatedProduct createdProduct;

    @Test
    void testCreateNewFruits() throws com.fasterxml.jackson.core.JsonProcessingException {

        NewProduct newProduct = createValidProduct(Category.FRUITS.getName(), article);

        String requestBody = objectMapper.writeValueAsString(newProduct);
        Response response = userAPI.createProduct(requestBody);

        createdProduct = objectMapper.readValue(response.getBody().asString(), CreatedProduct.class);

        step("Проверяем поля в ответе", () -> {
            assertAll(() -> {
                assertEquals(newProduct.getName(), createdProduct.getName());
                assertEquals(newProduct.getArticle(), createdProduct.getArticle());
                assertEquals(newProduct.getCategory(), createdProduct.getCategory());
                assertEquals(newProduct.getPrice(), createdProduct.getPrice());
                assertEquals(newProduct.getQty(), createdProduct.getQty());
            });
        });

        step("Проверяем запись в БД", () -> {
            ProductsEntity savedProduct = productRepository.findById(createdProduct.getId())
                    .orElseThrow(() -> new AssertionError("Продукт не найден в БД"));

            assertAll("Проверка данных в БД",
                    () -> assertEquals(newProduct.getName(), savedProduct.getName()),
                    () -> assertEquals(newProduct.getArticle(), savedProduct.getArticle()),
                    () -> assertEquals(newProduct.getCategory(), savedProduct.getCategory()),
                    () -> assertEquals(newProduct.getPrice(), savedProduct.getPrice()),
                    () -> assertEquals(newProduct.getQty(), savedProduct.getQty()),
                    () -> assertEquals(createdProduct.getInsertedAt(), savedProduct.getInsertedAt())
            );

        });
    }

    @Test
    void testCreateNewVegetables() throws com.fasterxml.jackson.core.JsonProcessingException {

        NewProduct newProduct = createValidProduct(Category.VEGETABLES.getName(), article);

        String requestBody = objectMapper.writeValueAsString(newProduct);
        Response response = userAPI.createProduct(requestBody);

        createdProduct = objectMapper.readValue(response.getBody().asString(), CreatedProduct.class);

        step("Проверяем поля в ответе", () -> {
            assertAll(() -> {
                assertEquals(newProduct.getName(), createdProduct.getName());
                assertEquals(newProduct.getArticle(), createdProduct.getArticle());
                assertEquals(newProduct.getCategory(), createdProduct.getCategory());
                assertEquals(newProduct.getPrice(), createdProduct.getPrice());
                assertEquals(newProduct.getQty(), createdProduct.getQty());
            });
        });
    }

    @Test
    void testCreateProductWithDuplicateArticle() throws com.fasterxml.jackson.core.JsonProcessingException {
        UUID duplicateArticle = UUID.randomUUID();
        NewProduct newProduct = createValidProduct("FRUITS", duplicateArticle);
        String requestBody = objectMapper.writeValueAsString(newProduct);
        Response response = userAPI.createProduct(requestBody);
        createdProduct = objectMapper.readValue(response.getBody().asString(), CreatedProduct.class);

        NewProduct duplicateProduct = createValidProduct("FRUITS", duplicateArticle);
        String duplicateRequestBody = objectMapper.writeValueAsString(duplicateProduct);
        Response response2 = userAPI.createProduct(duplicateRequestBody);


        step("Проверяем статус код ответа", () -> {
            assertEquals(StatusCode.BAD_REQUEST.getCode(), response2.getStatusCode());
        });
    }

    private NewProduct createValidProduct(String category, UUID article) {
        return NewProduct.builder()
                .article(article)
                .name(faker.food().fruit())
                .category(category)
                .dictionary("test dict")
                .price(new BigDecimal(faker.commerce().price(1,1000).replace(",", ".")))
                .qty(new BigDecimal(faker.number().randomDouble(2, 1, 50) + ""))
                .build();
    }
}
