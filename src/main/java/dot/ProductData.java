package dot;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProductData {
    private String name;
    private String article;
    private String id;
    private String category;
    private double price;
    private double qty;
    private Date insertedAt;
    private Date last_qty_changed;
    private String currency;
}
