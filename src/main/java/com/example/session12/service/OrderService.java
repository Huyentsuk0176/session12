package com.example.session12.service;

import com.example.session12.dto.OrderItemRequest;
import com.example.session12.dto.OrderItemResponse;
import com.example.session12.dto.OrderResponse;
import com.example.session12.entity.*;
import com.example.session12.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // =============================
    // 🧋 CREATE ORDER (CUSTOMER)
    // =============================
    public OrderResponse createOrder(Authentication auth, List<OrderItemRequest> items) {

        // 👤 lấy email từ JWT
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Order order = new Order();
        order.setUser(user);
        order.setCreatedDate(LocalDateTime.now());
        order.setStatus("NEW");

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest item : items) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product không tồn tại"));

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(product);
            oi.setQuantity(item.getQuantity());
            oi.setPriceBuy(product.getPrice());

            // 💰 tính tiền
            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            total = total.add(itemTotal);

            orderItems.add(oi);
        }

        order.setItems(orderItems);
        order.setTotalMoney(total);

        Order saved = orderRepository.save(order);

        return mapToResponse(saved);
    }

    // =============================
    // 👤 MY ORDERS (CUSTOMER)
    // =============================
    public List<OrderResponse> getMyOrders(Authentication auth) {

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =============================
    // 👀 ALL ORDERS (STAFF/ADMIN)
    // =============================
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =============================
    // 🔄 UPDATE STATUS (STAFF)
    // =============================
    public OrderResponse updateStatus(Long id, String status) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn"));

        order.setStatus(status);

        Order updated = orderRepository.save(order);

        return mapToResponse(updated);
    }

    // =============================
    // 🔁 MAPPING ENTITY → DTO
    // =============================
    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> items = order.getItems().stream()
                .map(i -> OrderItemResponse.builder()
                        .productId(i.getProduct().getId())
                        .productName(i.getProduct().getName())
                        .quantity(i.getQuantity())
                        .priceBuy(i.getPriceBuy())
                        .build()
                ).toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userEmail(order.getUser().getEmail())
                .createdDate(order.getCreatedDate())
                .status(order.getStatus())
                .totalMoney(order.getTotalMoney())
                .items(items)
                .build();
    }
}