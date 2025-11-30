package com.project.shopapp.controllers;

import com.project.shopapp.dtos.ShopDTO;
import com.project.shopapp.models.Shop;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.shop.ShopResponse;
import com.project.shopapp.services.shop.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> registerShop(
            @Valid @RequestBody ShopDTO shopDTO,
            @AuthenticationPrincipal User loginUser
    ) {
        try {
            Shop shop = shopService.createShop(shopDTO, loginUser.getId());

            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .message("Đăng ký mở gian hàng thành công! Vui lòng chờ Admin duyệt.")
                    .data(ShopResponse.fromShop(shop))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getShopDetail(@PathVariable("id") Long id) {
        try {
            Shop shop = shopService.getShopById(id);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Lấy thông tin cửa hàng thành công")
                    .data(ShopResponse.fromShop(shop))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> getActiveShops(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());

        Page<Shop> shopPage = shopService.getActiveShops(pageRequest); // Gọi hàm đã sửa

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Lấy danh sách cửa hàng thành công")
                .data(shopPage.map(ShopResponse::fromShop))
                .build());
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getAllShopsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());

        Page<Shop> shopPage = shopService.getAllShopsForAdmin(pageRequest);

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Lấy toàn bộ danh sách cửa hàng cho Admin thành công")
                .data(shopPage.map(ShopResponse::fromShop))
                .build());
    }

}
