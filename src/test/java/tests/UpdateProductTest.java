package tests;

import clients.HibernateConfig;
import clients.UserAPI;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import config.KafkaProducerConfig;
import entity.ProductEntity;
import dto.ProductData;
import dto.UpdateProduct;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import repository.ProductRepository;
import service.ProductService;
import settings.StatusCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {HibernateConfig.class, KafkaProducerConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UpdateProductTest {

    @Autowired
    private ProductService productService;


    private final UserAPI userAPI = new UserAPI();
    JsonMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private final Faker faker = new Faker(Locale.ENGLISH);

    @Test
    void testUpdateProduct() throws com.fasterxml.jackson.core.JsonProcessingException {

        ProductEntity createdProductEntity = ProductEntity.builder()
                .name(faker.food().vegetable())
                .article(UUID.fromString("1e107b16-35dd-48d0-9b03-33f4dc1b8f9e"))
                .category("VEGETABLES")
                .dictionary("vegetable")
                .price(new BigDecimal(faker.commerce().price(1,1000).replace(",", ".")))
                .qty(new BigDecimal(faker.number().randomDouble(2, 1, 50) + ""))
                .isAvailable(true)
                .insertedAt(OffsetDateTime.now())
                .build();
        ProductEntity savedProductEntity = productService.saveProduct(createdProductEntity);

        UUID articleUuid = UUID.fromString("1e107b16-35dd-48d0-9b03-33f4dc1b8f9e");
        productService.findProductByArticle(articleUuid);

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
            var result = productService.findProductById(createdProductEntity.getId());

            assertAll("Проверка данных в БД",
                    () -> assertThat(result.getName(), equalTo(updateProduct.getName())),
                    () -> assertThat(result.getArticle(), equalTo(UUID.fromString("1e107b16-35dd-48d0-9b03-33f4dc1b8f9e"))),
                    () -> assertThat(result.getCategory(), equalTo("VEGETABLES")),
                    () -> assertThat(result.getPrice(), equalTo(updateProduct.getPrice())),
                    () -> assertThat(result.getQty(), equalTo(updateProduct.getQty()))
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
