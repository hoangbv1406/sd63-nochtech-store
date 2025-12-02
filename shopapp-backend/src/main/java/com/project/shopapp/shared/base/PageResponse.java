package com.project.shopapp.shared.base;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<T> items;

    public static <T> PageResponse<T> of(Page<T> pageData) {
        return PageResponse.<T>builder()
                .page(pageData.getNumber() + 1)
                .size(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .hasNext(pageData.hasNext())
                .hasPrevious(pageData.hasPrevious())
                .items(pageData.getContent())
                .build();
    }
}
