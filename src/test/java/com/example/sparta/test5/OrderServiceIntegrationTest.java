package com.example.sparta.test5;

import com.example.sparta.dto.OrderCreateRequest;
import com.example.sparta.dto.OrderLineRequest;
import com.example.sparta.entity.Order;
import com.example.sparta.entity.Product;
import com.example.sparta.repository.OrderLineRepository;
import com.example.sparta.repository.OrderRepository;
import com.example.sparta.repository.ProductRepository;
import com.example.sparta.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;

    @BeforeEach
    void setup() {
        productRepository.save(new Product(null, "티셔츠", "무지 티", 10000L, 100L, 0L));
        productRepository.save(new Product(null, "바지", "긴 바지", 15000L, 50L, 0L));
    }

    @Test
    void 주문_생성_시_Order_OrderLine_Product_모두_연동됨() {
        // given
        List<OrderLineRequest> orderLines = List.of(
            new OrderLineRequest(1L, 10L),
            new OrderLineRequest(2L, 5L)
        );
        OrderCreateRequest request = new OrderCreateRequest(150000L, orderLines);

        // when
        Order order = orderService.create(request);

        // then
        assertThat(order.getId()).isNotNull();
        assertThat(orderRepository.findAll()).hasSize(1);
        assertThat(orderLineRepository.findAll()).hasSize(2);

        Product product1 = productRepository.findById(1L).get();
        Product product2 = productRepository.findById(2L).get();

        assertThat(product1.getAmount()).isEqualTo(90L);
        assertThat(product1.getSaleCount()).isEqualTo(10L);
        assertThat(product2.getAmount()).isEqualTo(45L);
        assertThat(product2.getSaleCount()).isEqualTo(5L);
    }

    @Test
    void 존재하지_않는_상품은_주문_불가() {
        // given
        List<OrderLineRequest> orderLines = List.of(
                new OrderLineRequest(999L, 1L) // 없는 상품
        );
        OrderCreateRequest request = new OrderCreateRequest(1000L, orderLines);

        // when & then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("존재하지 않는 상품");
    }

}
