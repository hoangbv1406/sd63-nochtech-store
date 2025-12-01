package com.project.shopapp.controllers;

import com.project.shopapp.dtos.AffiliateLinkDTO;
import com.project.shopapp.models.AffiliateLink;
import com.project.shopapp.models.User;
import com.project.shopapp.shared.base.ResponseObject;
import com.project.shopapp.services.affiliate.AffiliateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/affiliate")
@RequiredArgsConstructor
public class AffiliateController {
    private final AffiliateService affiliateService;

    @PostMapping("/generate-link")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_VENDOR')")
    public ResponseEntity<ResponseObject> generateLink(
            @Valid @RequestBody AffiliateLinkDTO dto,
            @AuthenticationPrincipal User loginUser
    ) {
        try {
            AffiliateLink link = affiliateService.createLink(dto, loginUser.getId());

            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Tạo link tiếp thị liên kết thành công")
                    .status(HttpStatus.CREATED)
                    .data(link)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }
}
