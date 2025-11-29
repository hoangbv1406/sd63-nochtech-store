package com.project.shopapp.services.affiliate;

import com.project.shopapp.dtos.AffiliateLinkDTO;
import com.project.shopapp.models.AffiliateLink;

public interface AffiliateService {
    AffiliateLink createLink(AffiliateLinkDTO affiliateLinkDTO, Long userId) throws Exception;
}
