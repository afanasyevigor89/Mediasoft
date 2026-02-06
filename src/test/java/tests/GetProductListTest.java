package tests;

import clients.UserAPI;
import dot.ProductData;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class GetProductListTest {

    private final UserAPI userAPI = new UserAPI();

    @Test
    void testGetProducts() {
        step("Отправка GET-запроса", ()-> {
            Response response = userAPI.getProductList();

            List<ProductData> productData = response.jsonPath().getList(".", ProductData.class);
            productData.forEach(product -> {
                assertAll(() -> {
                    assertNotNull(product.getId(), "ID товара не может быть null, ID товара: " + product.getId());
                    assertNotNull(product.getArticle(), "Артикул товара не может быть null, ID товара: " + product.getId());
                    assertNotNull(product.getInsertedAt(), "Дата создания товара не может быть null, ID товара: " + product.getId());
                    assertTrue(product.getPrice() > 0, "Цена товара должна быть больше 0, ID товара: " + product.getId());
                    assertTrue(product.getQty() > 0, "Кол-во товара должно быть больше 0, ID товара: " + product.getId());});
            });
        });
    }
}
