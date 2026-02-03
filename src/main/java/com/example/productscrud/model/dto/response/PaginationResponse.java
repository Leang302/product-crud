package com.example.productscrud.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationResponse<T> {
    private Integer page;
    private Integer size;
    private Integer totalItems;
    private Integer totalPages;
    private T items;
}
