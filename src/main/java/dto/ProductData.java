package dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductData {
    private String name;
    private String article;
    private String id;
    private String category;
    private BigDecimal price;
    private BigDecimal qty;
    private LocalDateTime insertedAt;
    private LocalDateTime last_qty_changed;
    private String currency;
}
