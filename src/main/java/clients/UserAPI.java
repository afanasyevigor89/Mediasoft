package clients;

import settings.StatusCode;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.UUID;

public class UserAPI {

    private final APIClient apiClient;
    private final String usersPath;

    public UserAPI() {
        this.apiClient = new APIClient();
        this.usersPath = apiClient.getUsersPath();
    }

    private RequestSpecification getRequestSpec() {
        return apiClient.getRequestSpec();
    }

    public Response createProduct(String newProduct) {
        return getRequestSpec()
                .body(newProduct)
                .when()
                .post(usersPath + "/products")
                .then()
                .log().all()
                .extract().response();
    }

    public Response getProductList() {
        return getRequestSpec()
                .get(usersPath)
                .then()
                .statusCode(StatusCode.OK.getCode())
                .log().all()
                .extract().response();
    }

    public Response updateProduct(String updateProduct) {
        return getRequestSpec()
                .body(updateProduct)
                .patch(usersPath)
                .then()
                .log().all()
                .extract().response();
    }

    public Response getProductById(UUID id) {
        return getRequestSpec()
                .pathParam("id", id)
                .get(usersPath + "/{id}")
                .then()
                .log().all()
                .extract().response();
    }

    public Response deleteProduct(UUID id) {
        return getRequestSpec()
                .pathParam("id", id)
                .delete(usersPath + "/{id}")
                .then()
                .extract().response();
    }
}