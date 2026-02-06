package dot;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class UpdateProduct {
    private UUID id;
    private String name;
    private UUID article;
    private String category;
    private String dictionary;
    private Date insertedAt;
    private Date last_qty_changed;
    private double price;
    private double qty;

    private UpdateProduct(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.article = builder.article;
        this.category = builder.category;
        this.dictionary = builder.dictionary;
        this.insertedAt = builder.insertedAt;
        this.last_qty_changed = builder.last_qty_changed;
        this.price = builder.price;
        this.qty = builder.qty;
    }

    public static class Builder {
        private UUID id;
        private String name;
        private UUID article;
        private String category;
        private String dictionary;
        private Date insertedAt;
        private Date last_qty_changed;
        private double price;
        private double qty;

        public Builder() {}

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }
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

        public Builder insertedAt(Date insertedAt) {
            this.insertedAt = insertedAt;
            return this;
        }

        public Builder last_qty_changed(Date last_qty_changed) {
            this.last_qty_changed = last_qty_changed;
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

        public UpdateProduct build() { return new UpdateProduct(this); }
    }
}
