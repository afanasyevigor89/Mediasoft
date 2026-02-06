package dot;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
public class NewProduct {
    private String name;
    private UUID article;
    private String category;
    private String dictionary;
    private double price;
    private double qty;

    private NewProduct(Builder builder) {
        this.name = builder.name;
        this.article = builder.article;
        this.category = builder.category;
        this.dictionary = builder.dictionary;
        this.price = builder.price;
        this.qty = builder.qty;
    }

    public static class Builder {
        private String name;
        private UUID article;
        private String category;
        private String dictionary;
        private double price;
        private double qty;

        public Builder() {}

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder article(UUID article) {
            this.article = article;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder dictionary(String dictionary) {
            this.dictionary = dictionary;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public Builder qty(double qty) {
            this.qty = qty;
            return this;
        }
        public NewProduct build() { return new NewProduct(this); }
    }
}
