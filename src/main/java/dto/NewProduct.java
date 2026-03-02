package dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class NewProduct {
    private String name;
    private UUID article;
    private String category;
    private String dictionary;
    private BigDecimal price;
    private BigDecimal qty;
}
