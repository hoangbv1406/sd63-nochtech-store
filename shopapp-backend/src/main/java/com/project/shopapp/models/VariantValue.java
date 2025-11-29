package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "variant_values")
@IdClass(VariantValueId.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariantValue {

    @Id
    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Id
    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "option_value_id", nullable = false)
    private OptionValue optionValue;

}
