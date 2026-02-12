package dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class CreatedProduct {
    private String name;
    private UUID article;
    private UUID id;
    private String category;
    private BigDecimal price;
    private BigDecimal qty;

    private Date insertedAt;
    private Date last_qty_changed;

    private String currency;
}
