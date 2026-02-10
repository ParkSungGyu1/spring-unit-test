package com.example.sparta.controller;

import com.example.sparta.dto.OrderCreateRequest;
import com.example.sparta.entity.Order;
import com.example.sparta.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/orders")
    public ResponseEntity<Long> createOrder(@RequestBody OrderCreateRequest request) {
        // 1. 간단한 유효성 검사 (테스트 시나리오용)
        if (request.getTotalPrice() <= 0) {
            return ResponseEntity.badRequest().build(); // 400 Error
        }

        // 2. 서비스 호출
        Order createdOrder = orderService.create(request);

        // 3. 결과 반환 (주문 ID)
        return ResponseEntity.ok(createdOrder.getId());
    }
}