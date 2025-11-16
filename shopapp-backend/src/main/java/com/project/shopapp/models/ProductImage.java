package com.project.shopapp.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductImage {
    public static final int MAXIMUM_IMAGES_PER_PRODUCT = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    @Column(name = "image_url", length = 300)
    @JsonProperty("image_url")
    private String imageUrl;

}
