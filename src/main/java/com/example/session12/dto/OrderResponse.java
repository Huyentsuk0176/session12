package com.example.session12.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {

    private Long id;
    private String userEmail;
    private LocalDateTime createdDate;
    private String status;
    private BigDecimal totalMoney;
    private List<OrderItemResponse> items;
}