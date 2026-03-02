package entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class ProductsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "article", nullable = false, unique = true)
    private UUID article;

    @Column(name = "dictionary")
    private String dictionary;

    @Column(name = "category")
    private String category;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "qty")
    private BigDecimal qty;

    @Column(name = "inserted_at", nullable = false)
    private LocalDateTime insertedAt;

    @Column(name = "last_qty_changed")
    private LocalDateTime lastQtyChanged;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

}
