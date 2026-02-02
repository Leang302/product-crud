package com.example.productscrud.model.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagedResponse<T> {
    private List<T> items;
    private PaginationInfo pagination;
}
