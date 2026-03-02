package dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class UpdateProduct {
    private UUID id;
    private String name;
    private UUID article;
    private String category;
    private String dictionary;
    private LocalDateTime insertedAt;
    private LocalDateTime last_qty_changed;
    private BigDecimal price;
    private BigDecimal qty;
}
