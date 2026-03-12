package tests;

import clients.HibernateConfig;
import clients.UserAPI;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entity.ProductsEntity;
import dto.ProductData;
import dto.UpdateProduct;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import repository.ProductRepository;
import settings.StatusCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {HibernateConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UpdateProductTest {

    @Autowired
    private ProductRepository productRepository;

    private final UserAPI userAPI = new UserAPI();
    JsonMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private final Faker faker = new Faker(Locale.ENGLISH);

    @Test
    void testUpdateProduct() throws com.fasterxml.jackson.core.JsonProcessingException {

        ProductsEntity createdProductEntity = ProductsEntity.builder()
                .name(faker.food().vegetable())
                .article(UUID.fromString("1e107b16-35dd-48d0-9b03-33f4dc1b8f9e"))
                .category("VEGETABLES")
                .dictionary("vegetable")
                .price(new BigDecimal(faker.commerce().price(1,1000).replace(",", ".")))
                .qty(new BigDecimal(faker.number().randomDouble(2, 1, 50) + ""))
                .isAvailable(true)
                .insertedAt(LocalDateTime.now())
                .build();
        productRepository.save(createdProductEntity);

        UUID articleUuid = UUID.fromString("1e107b16-35dd-48d0-9b03-33f4dc1b8f9e");
        ProductsEntity savedProductEntity =productRepository.findAllByArticle(articleUuid);

        UpdateProduct updateProduct = UpdateProduct.builder()
                .id(savedProductEntity.getId())
                .name("Carrot")
                .price(new BigDecimal("121.11"))
                .qty(new BigDecimal("12.12"))
                .build();
        String requestBody = objectMapper.writeValueAsString(updateProduct);
        Response response = userAPI.updateProduct(requestBody);
        ProductData productData = objectMapper.readValue(response.getBody().asString(), ProductData.class);
        assertNotNull(productData.getLast_qty_changed(), "Дата изменения кол-ва должна быть заполнена");

        step("Проверяем запись в БД", () -> {
            ProductsEntity savedProduct = productRepository.findById(savedProductEntity.getId())
                    .orElseThrow(() -> new AssertionError("Продукт не найден в БД"));

            assertAll("Проверка данных в БД",
                    () -> assertEquals(updateProduct.getName(), savedProduct.getName()),
                    () -> assertEquals(UUID.fromString("1e107b16-35dd-48d0-9b03-33f4dc1b8f9e"), savedProduct.getArticle()),
                    () -> assertEquals("VEGETABLES", savedProduct.getCategory()),
                    () -> assertEquals(updateProduct.getPrice(), savedProduct.getPrice()),
                    () -> assertEquals(updateProduct.getQty(), savedProduct.getQty())
            );

        });
        step("Удаляем созданный продукт", () -> {
            userAPI.deleteProduct(savedProductEntity.getId());
        });
    }

    @Test
    void testUpdateProductWithoutID() throws com.fasterxml.jackson.core.JsonProcessingException {
        UpdateProduct updateProduct = UpdateProduct.builder()
                .name("Carrot")
                .price(new BigDecimal("121.11"))
                .qty(new BigDecimal("12.12"))
                .build();
        String requestBody = objectMapper.writeValueAsString(updateProduct);
        Response response = userAPI.updateProduct(requestBody);

        assertEquals(StatusCode.BAD_REQUEST.getCode(), response.getStatusCode());
    }
}
