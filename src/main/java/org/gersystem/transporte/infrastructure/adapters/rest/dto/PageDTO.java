package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PageDTO<T> {

    private List<T> content;
    @JsonProperty("page")
    private int pageNumber;
    @JsonProperty("size")
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PageDTO(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }
} 