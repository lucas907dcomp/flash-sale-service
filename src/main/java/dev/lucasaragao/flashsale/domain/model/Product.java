package dev.lucasaragao.flashsale.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Critical field for flash sales.
     * Represents the current available inventory.
     * Concurrency control will be managed via Redis Distributed Locks.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Optimistic locking version.
     * Prevents lost updates at the database level if concurrent transactions occur outside Redis.
     */
    @Version
    private Long version;
}