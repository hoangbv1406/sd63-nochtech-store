package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class CategoryDTO {

    @NotEmpty(message = "Category name cannot be empty")
    @JsonProperty("name")
    private String name;

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("slug")
    private String slug;

}
