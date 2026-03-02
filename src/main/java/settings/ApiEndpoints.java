package settings;

public enum ApiEndpoints {
    PRODUCTS("/products");
    //ORDER("/order");

    private final String path;
    ApiEndpoints(String path) {this.path = path;}
    public String getPath() { return path; }
}
