package com.project.shopapp.controllers;

import com.project.shopapp.components.SecurityUtils;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.order.OrderListResponse;
import com.project.shopapp.responses.order.OrderResponse;
import com.project.shopapp.services.orders.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final SecurityUtils securityUtils;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllOrder() {
        List<Order> order = orderService.getAllOrder();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Orders retrieved successfully.")
                .status(HttpStatus.OK)
                .data(order)
                .build()
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseObject> getOrder(@PathVariable("orderId") Long orderId) {
        Order existingOrder = orderService.getOrderById(orderId);
        OrderResponse orderResponse = OrderResponse.fromOrder(existingOrder);
        return ResponseEntity.ok(new ResponseObject(
                "Order retrieved successfully. orderId = " + orderId,
                HttpStatus.OK,
                orderResponse
        ));
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject> createOrder(
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result
    ) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(String.join(";", errorMessages))
                    .status(HttpStatus.BAD_REQUEST)
                    .build()
            );
        }
        User loginUser = securityUtils.getLoggedInUser();
        if (orderDTO.getUserId() == null) {
            orderDTO.setUserId(loginUser.getId());
        }
        Order orderResponse = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Order created successfully.")
                .data(orderResponse)
                .status(HttpStatus.OK)
                .build()
        );
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<ResponseObject> updateOrder(
            @Valid @PathVariable("orderId") Long orderId,
            @Valid @RequestBody OrderDTO orderDTO
    ) throws Exception {
        Order order = orderService.updateOrder(orderId, orderDTO);
        return ResponseEntity.ok(new ResponseObject(
                "Order updated successfully. orderId = " + orderId,
                HttpStatus.OK,
                order
        ));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ResponseObject> deleteOrder(@PathVariable("orderId") Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Order deleted successfully. orderId = " + orderId)
                .build()
        );
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ResponseObject> updateOrderStatus(
            @PathVariable("orderId") Long orderId,
            @RequestParam("status") String status
    ) throws Exception {
        Order updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Order status updated successfully.")
                .status(HttpStatus.OK)
                .data(OrderResponse.fromOrder(updatedOrder))
                .build()
        );
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<ResponseObject> cancelOrder(@Valid @PathVariable("orderId") Long orderId) throws Exception {
        Order order = orderService.getOrderById(orderId);
        User loginUser = securityUtils.getLoggedInUser();
        if (loginUser.getId() != order.getUser().getId()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message("You do not have permission to cancel this order")
                    .build()
            );
        }
        if (order.getStatus().equals(OrderStatus.DELIVERED) || order.getStatus().equals(OrderStatus.SHIPPED) || order.getStatus().equals(OrderStatus.PROCESSING)) {
            String message = "You cannot cancel an order with status: " + order.getStatus();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(message)
                    .build()
            );
        }
        OrderDTO orderDTO = OrderDTO.builder().userId(order.getUser().getId()).status(OrderStatus.CANCELLED).build();
        order = orderService.updateOrder(orderId, orderDTO);
        return ResponseEntity.ok(new ResponseObject(
                "Order cancelled successfully. orderId = " + orderId,
                HttpStatus.OK,
                order
        ));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseObject> getOrderByUser(@Valid @PathVariable("userId") Long userId) {
        User loginUser = securityUtils.getLoggedInUser();
        boolean isUserIdBlank = userId == null || userId <= 0;
        List<OrderResponse> orderResponses = orderService.findByUserId(isUserIdBlank ? loginUser.getId() : userId);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Orders for user retrieved successfully. userId = " + userId)
                .data(orderResponses)
                .status(HttpStatus.OK)
                .build()
        );
    }

    @GetMapping("/get-orders-by-keyword")
    public ResponseEntity<ResponseObject> getOrdersByKeyword(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());
        Page<OrderResponse> orderPage = orderService.getOrdersByKeyword(keyword, pageRequest).map(OrderResponse::fromOrder);
        OrderListResponse response = OrderListResponse.builder()
                .orders(orderPage.getContent())
                .totalPages(orderPage.getTotalPages())
                .currentPage(page)
                .build();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get orders successfully")
                .status(HttpStatus.OK)
                .data(response)
                .build()
        );
    }

}
