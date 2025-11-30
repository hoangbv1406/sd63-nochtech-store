package com.project.shopapp.controllers;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.enums.OrderStatus;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderShop;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.order.OrderListResponse;
import com.project.shopapp.responses.order.OrderResponse;
import com.project.shopapp.services.orders.OrderService;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> createOrder(
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result,
            @AuthenticationPrincipal User loginUser
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message(String.join(";", errorMessages))
                        .status(HttpStatus.BAD_REQUEST)
                        .build());
            }

            Order orderResponse = orderService.createOrder(orderDTO, loginUser.getId());

            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Tạo đơn hàng thành công")
                    .data(OrderResponse.fromOrder(orderResponse))
                    .status(HttpStatus.OK)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> getMyOrders(@AuthenticationPrincipal User loginUser) {
        List<OrderResponse> orderResponses = orderService.findByUserId(loginUser.getId());

        return ResponseEntity.ok(ResponseObject.builder()
                .message("Lấy danh sách đơn hàng của bạn thành công")
                .data(orderResponses)
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getOrder(@PathVariable("id") Long id) {
        Order existingOrder = orderService.getOrderById(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Lấy thông tin đơn hàng thành công")
                .data(OrderResponse.fromOrder(existingOrder))
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/shop/{shop_id}")
    @PreAuthorize("hasRole('ROLE_VENDOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getOrdersByShop(
            @PathVariable("shop_id") Long shopId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<OrderShop> orderShopPage = orderService.getOrdersByShopId(shopId, pageRequest);

        return ResponseEntity.ok(ResponseObject.builder()
                .message("Lấy danh sách đơn hàng cho Shop thành công")
                .data(orderShopPage.getContent())
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getOrdersByKeyword(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "", required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());
        Page<OrderResponse> orderPage = orderService.getOrdersByKeyword(keyword, status, pageRequest).map(OrderResponse::fromOrder);

        OrderListResponse response = OrderListResponse.builder()
                .orders(orderPage.getContent())
                .totalPages(orderPage.getTotalPages())
                .currentPage(page)
                .build();

        return ResponseEntity.ok(ResponseObject.builder()
                .message("Tìm kiếm đơn hàng thành công")
                .status(HttpStatus.OK)
                .data(response)
                .build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VENDOR')")
    public ResponseEntity<ResponseObject> updateOrderStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status
    ) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Cập nhật trạng thái đơn hàng thành công")
                    .status(HttpStatus.OK)
                    .data(OrderResponse.fromOrder(updatedOrder))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    @PutMapping("/cancel/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> cancelOrder(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal User loginUser
    ) {
        try {
            Order order = orderService.getOrderById(id);

            if (!order.getUser().getId().equals(loginUser.getId())) {
                throw new Exception("Bạn không có quyền hủy đơn hàng này!");
            }

            if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.PROCESSING) {
                throw new Exception("Không thể hủy đơn hàng đang trong trạng thái: " + order.getStatus());
            }

            Order cancelledOrder = orderService.updateOrderStatus(id, OrderStatus.CANCELLED.name());

            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Hủy đơn hàng thành công")
                    .status(HttpStatus.OK)
                    .data(OrderResponse.fromOrder(cancelledOrder))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteOrder(@PathVariable long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("xoá đơn hàng thành công")
                .status(HttpStatus.OK)
                .build());
    }
}
