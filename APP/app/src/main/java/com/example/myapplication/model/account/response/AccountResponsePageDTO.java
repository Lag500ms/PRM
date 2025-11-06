package com.example.myapplication.model.account.response;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponsePageDTO {
    private List<AccountResponseDTO> content;
    private int totalPages;
    private long totalElements;
    private int number;
    private int size;

    // Manual getters for Lombok compatibility
    public List<AccountResponseDTO> getContent() {
        return content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }
}
