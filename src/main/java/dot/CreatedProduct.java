package dot;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class CreatedProduct {
    private String name;
    private UUID article;
    private UUID id;
    private String category;
    private double price;
    private double qty;
    private Date insertedAt;
    private Date last_qty_changed;
    private String currency;
}
