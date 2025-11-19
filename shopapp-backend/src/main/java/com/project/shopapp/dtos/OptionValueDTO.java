package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class OptionValueDTO {

    @Min(value = 1, message = "Option ID must be > 0")
    @JsonProperty("option_id")
    private Long optionId;

    @NotBlank(message = "Value name is required")
    @JsonProperty("value")
    private String value;

}
