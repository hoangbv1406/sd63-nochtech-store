DROP DATABASE IF EXISTS ShopApp;
CREATE DATABASE IF NOT EXISTS ShopApp;

USE ShopApp;
SET time_zone = "+00:00";
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_SAFE_UPDATES = 0;
START TRANSACTION;


CREATE TABLE `affiliate_links` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `product_id` int NOT NULL,
  `code` varchar(50) NOT NULL,
  `clicks` int DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `fk_aff_user` (`user_id`),
  KEY `fk_aff_pro` (`product_id`),
  CONSTRAINT `fk_aff_pro` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `fk_aff_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `affiliate_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `affiliate_link_id` int NOT NULL,
  `order_shop_id` int NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `status` enum('PENDING','APPROVED','CANCELLED') DEFAULT 'PENDING',
  `payout_date` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_aff_trans_link` (`affiliate_link_id`),
  KEY `fk_aff_trans_order` (`order_shop_id`),
  CONSTRAINT `fk_aff_trans_link` FOREIGN KEY (`affiliate_link_id`) REFERENCES `affiliate_links` (`id`),
  CONSTRAINT `fk_aff_trans_order` FOREIGN KEY (`order_shop_id`) REFERENCES `orders_shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `brands` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `icon_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `cart_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cart_id` int NOT NULL,
  `product_id` int NOT NULL,
  `variant_id` int DEFAULT NULL COMMENT '[NEW] Chọn biến thể trong giỏ',
  `quantity` int DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_cart_product_variant` (`cart_id`,`product_id`,`variant_id`),
  KEY `fk_cart_items_cart` (`cart_id`),
  KEY `fk_cart_items_product` (`product_id`),
  KEY `fk_cart_items_variant` (`variant_id`),
  CONSTRAINT `fk_cart_items_cart` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cart_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cart_items_variant` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `carts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `session_id` varchar(100) DEFAULT NULL COMMENT 'Lưu Session ID cho khách chưa login',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_carts_user_id` (`user_id`),
  KEY `idx_session_id` (`session_id`),
  CONSTRAINT `fk_carts_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  `slug` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `fk_categories_parent` (`parent_id`),
  CONSTRAINT `fk_categories_parent` FOREIGN KEY (`parent_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `chat_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` int NOT NULL,
  `sender_id` int NOT NULL,
  `content` text,
  `type` enum('TEXT','IMAGE','VIDEO','PRODUCT','ORDER') DEFAULT 'TEXT',
  `attachment_url` json DEFAULT NULL,
  `is_read` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_msg_room` (`room_id`,`created_at`),
  CONSTRAINT `fk_msg_room` FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `chat_participants` (
  `room_id` int NOT NULL,
  `user_id` int NOT NULL,
  `role` enum('MEMBER','ADMIN') DEFAULT 'MEMBER',
  `joined_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`room_id`,`user_id`),
  KEY `fk_chat_user` (`user_id`),
  CONSTRAINT `fk_chat_room` FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`id`),
  CONSTRAINT `fk_chat_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `chat_rooms` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `type` enum('PRIVATE','GROUP','SUPPORT') DEFAULT 'PRIVATE',
  `last_message` text,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `coupon_applicables` (
  `id` int NOT NULL AUTO_INCREMENT,
  `coupon_id` int NOT NULL,
  `object_type` enum('CATEGORY','PRODUCT','BRAND') NOT NULL,
  `object_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `coupon_id` (`coupon_id`),
  CONSTRAINT `coupon_applicables_ibfk_1` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `coupons` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text,
  `discount_type` enum('PERCENTAGE','FIXED_AMOUNT') NOT NULL DEFAULT 'FIXED_AMOUNT',
  `discount_value` decimal(15,2) NOT NULL,
  `max_discount_amount` decimal(15,2) DEFAULT NULL,
  `min_order_amount` decimal(15,2) DEFAULT '0.00',
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `usage_limit` int DEFAULT NULL,
  `usage_per_user` int DEFAULT '1',
  `is_active` tinyint(1) DEFAULT '1',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_coupon_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `districts` (
  `code` varchar(20) NOT NULL,
  `province_code` varchar(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `name_en` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `full_name_en` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`code`),
  KEY `province_code` (`province_code`),
  CONSTRAINT `districts_ibfk_1` FOREIGN KEY (`province_code`) REFERENCES `provinces` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `favorites` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fav_user_fk` (`user_id`),
  KEY `fav_product_fk` (`product_id`),
  CONSTRAINT `fav_product_fk` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `fav_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `body` text,
  `type` enum('ORDER','SYSTEM','PROMOTION','CHAT') NOT NULL,
  `reference_id` varchar(100) DEFAULT NULL,
  `is_read` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_noti_user` (`user_id`),
  CONSTRAINT `fk_noti_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `option_values` (
  `id` int NOT NULL AUTO_INCREMENT,
  `option_id` int NOT NULL,
  `value` varchar(50) NOT NULL COMMENT 'Red, Blue, 64GB',
  PRIMARY KEY (`id`),
  KEY `option_id` (`option_id`),
  CONSTRAINT `option_values_ibfk_1` FOREIGN KEY (`option_id`) REFERENCES `options` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `options` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT 'Color, Ram, Size',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `order_details` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `order_shop_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `product_item_id` int DEFAULT NULL COMMENT 'Nếu bán theo IMEI thì fill vào đây',
  `supplier_id` int DEFAULT NULL,
  `price` decimal(15,2) DEFAULT NULL,
  `number_of_products` int DEFAULT '1',
  `total_money` decimal(15,2) DEFAULT '0.00',
  `configuration` json DEFAULT NULL,
  `coupon_id` int DEFAULT NULL,
  `cost_price` decimal(15,2) DEFAULT NULL COMMENT 'Giá vốn tại thời điểm bán',
  `is_settled` tinyint(1) DEFAULT '0' COMMENT 'Đã thanh toán tiền gốc cho Supplier chưa',
  `settlement_date` datetime DEFAULT NULL,
  `warranty_expire_date` date DEFAULT NULL COMMENT 'Ngày hết hạn bảo hành cho item này',
  `settlement_ref` varchar(100) DEFAULT NULL COMMENT 'Mã giao dịch/UNC khi chuyển khoản trả tiền cho Supplier',
  `settlement_note` varchar(255) DEFAULT NULL COMMENT 'Ghi chú đối soát',
  `product_name` varchar(350) DEFAULT NULL COMMENT 'Lưu cứng tên SP tại thời điểm bán',
  `variant_name` varchar(255) DEFAULT NULL COMMENT 'Lưu cứng tên biến thể (Ví dụ: Đen, 64GB)',
  PRIMARY KEY (`id`),
  KEY `order_details_order_fk` (`order_id`),
  KEY `order_details_product_fk` (`product_id`),
  KEY `order_details_item_fk` (`product_item_id`),
  KEY `fk_order_details_supplier` (`supplier_id`),
  KEY `idx_supplier_settlement` (`supplier_id`,`is_settled`),
  KEY `fk_details_order_shop` (`order_shop_id`),
  CONSTRAINT `fk_details_order_shop` FOREIGN KEY (`order_shop_id`) REFERENCES `orders_shop` (`id`),
  CONSTRAINT `fk_order_details_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`),
  CONSTRAINT `order_details_item_fk` FOREIGN KEY (`product_item_id`) REFERENCES `product_items` (`id`),
  CONSTRAINT `order_details_order_fk` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `order_details_product_fk` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `order_histories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `status` varchar(50) NOT NULL,
  `note` text,
  `updated_by` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_histories_order` (`order_id`),
  CONSTRAINT `fk_histories_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `pos_session_id` int DEFAULT NULL,
  `fullname` varchar(100) DEFAULT '',
  `email` varchar(100) DEFAULT '',
  `phone_number` varchar(20) NOT NULL,
  `address` varchar(200) NOT NULL,
  `province_code` varchar(20) DEFAULT NULL,
  `district_code` varchar(20) DEFAULT NULL,
  `ward_code` varchar(20) DEFAULT NULL,
  `note` varchar(100) DEFAULT '',
  `order_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` enum('pending','processing','shipped','delivered','cancelled') DEFAULT 'pending',
  `sub_total` decimal(15,2) DEFAULT '0.00',
  `shipping_fee` decimal(15,2) DEFAULT '0.00',
  `discount_amount` decimal(15,2) DEFAULT '0.00',
  `total_money` decimal(15,2) DEFAULT NULL,
  `shipping_method` varchar(100) DEFAULT NULL,
  `shipping_address` varchar(200) DEFAULT NULL,
  `shipping_date` date DEFAULT NULL,
  `tracking_number` varchar(100) DEFAULT NULL,
  `payment_method` varchar(100) DEFAULT NULL,
  `payment_status` enum('unpaid','paid') DEFAULT 'unpaid',
  `order_channel` enum('ONLINE','POS','APP') DEFAULT 'ONLINE',
  `active` tinyint(1) DEFAULT '1',
  `coupon_id` int DEFAULT NULL,
  `vnp_txn_ref` varchar(255) DEFAULT NULL,
  `total_cost_price` decimal(15,2) DEFAULT '0.00' COMMENT 'Tổng giá vốn của đơn hàng',
  PRIMARY KEY (`id`),
  KEY `idx_order_date` (`order_date`),
  KEY `fk_orders_user` (`user_id`),
  KEY `fk_orders_coupon` (`coupon_id`),
  KEY `idx_order_status` (`status`),
  KEY `fk_order_pos` (`pos_session_id`),
  CONSTRAINT `fk_order_pos` FOREIGN KEY (`pos_session_id`) REFERENCES `pos_sessions` (`id`),
  CONSTRAINT `fk_orders_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`),
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `orders_shop` (
  `id` int NOT NULL AUTO_INCREMENT,
  `parent_order_id` int NOT NULL COMMENT 'Link tới bảng orders gốc',
  `shop_id` int NOT NULL,
  `shipping_method` varchar(100) DEFAULT NULL,
  `shipping_fee` decimal(15,2) DEFAULT '0.00',
  `sub_total` decimal(15,2) DEFAULT '0.00' COMMENT 'Tổng tiền hàng',
  `admin_commission` decimal(15,2) DEFAULT '0.00' COMMENT 'Phí sàn thu',
  `shop_income` decimal(15,2) DEFAULT '0.00' COMMENT 'Thực nhận = Sub - Comm',
  `status` enum('pending','processing','shipped','delivered','cancelled','returned') DEFAULT 'pending',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_sub_order_parent` (`parent_order_id`),
  KEY `fk_sub_order_shop` (`shop_id`),
  CONSTRAINT `fk_sub_order_parent` FOREIGN KEY (`parent_order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_sub_order_shop` FOREIGN KEY (`shop_id`) REFERENCES `shops` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `pos_sessions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `shop_id` int NOT NULL,
  `user_id` int NOT NULL,
  `start_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `end_at` datetime DEFAULT NULL,
  `opening_cash` decimal(15,2) DEFAULT '0.00',
  `closing_cash` decimal(15,2) DEFAULT '0.00',
  `status` enum('OPEN','CLOSED') DEFAULT 'OPEN',
  `note` text,
  PRIMARY KEY (`id`),
  KEY `fk_pos_shop` (`shop_id`),
  KEY `fk_pos_user` (`user_id`),
  CONSTRAINT `fk_pos_shop` FOREIGN KEY (`shop_id`) REFERENCES `shops` (`id`),
  CONSTRAINT `fk_pos_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `price_histories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `variant_id` int DEFAULT NULL,
  `old_price` decimal(15,2) DEFAULT NULL,
  `new_price` decimal(15,2) DEFAULT NULL,
  `updated_by` int DEFAULT NULL COMMENT 'Admin nào sửa',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product_images` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int DEFAULT NULL,
  `image_url` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_product_images_product_id` (`product_id`),
  CONSTRAINT `fk_product_images_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `variant_id` int DEFAULT NULL COMMENT '[LINK] Liên kết với biến thể',
  `supplier_id` int DEFAULT NULL,
  `order_id` int DEFAULT NULL,
  `imei_code` varchar(50) NOT NULL,
  `inbound_price` decimal(15,2) DEFAULT NULL,
  `status` enum('AVAILABLE','PENDING','SOLD','DEFECTIVE','WARRANTY','HOLD') DEFAULT 'AVAILABLE',
  `attributes` json DEFAULT NULL COMMENT 'Optional: Thông số phụ',
  `import_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `sold_date` datetime DEFAULT NULL,
  `locked_until` datetime DEFAULT NULL COMMENT 'Thời gian hết hạn giữ hàng (Reservation)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_imei` (`imei_code`),
  KEY `idx_item_status` (`status`),
  KEY `items_products_fk` (`product_id`),
  KEY `items_variants_fk` (`variant_id`),
  KEY `items_suppliers_fk` (`supplier_id`),
  KEY `items_orders_fk` (`order_id`),
  KEY `idx_imei_search` (`imei_code`),
  KEY `idx_items_locked_until` (`locked_until`),
  KEY `idx_pid_status` (`product_id`,`status`),
  CONSTRAINT `items_orders_fk` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `items_products_fk` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `items_suppliers_fk` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`),
  CONSTRAINT `items_variants_fk` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product_reviews` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `rating` tinyint DEFAULT '5',
  `images` json DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `reviews_product_fk` (`product_id`),
  KEY `reviews_user_fk` (`user_id`),
  CONSTRAINT `reviews_product_fk` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `reviews_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product_variants` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `sku` varchar(100) DEFAULT NULL,
  `price` decimal(15,2) DEFAULT NULL,
  `original_price` decimal(15,2) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `quantity` int DEFAULT '0' COMMENT '[FIX] Tồn kho riêng cho từng biến thể',
  `weight` decimal(10,2) DEFAULT '0.00' COMMENT 'Gram',
  `dimensions` varchar(50) DEFAULT NULL COMMENT 'L x W x H',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `product_variants_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `products` (
  `id` int NOT NULL AUTO_INCREMENT,
  `shop_id` int DEFAULT '1',
  `name` varchar(350) DEFAULT NULL,
  `slug` varchar(350) DEFAULT NULL,
  `price` decimal(15,2) DEFAULT NULL,
  `thumbnail` varchar(255) DEFAULT NULL,
  `description` longtext,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `category_id` int DEFAULT NULL,
  `brand_id` int DEFAULT NULL,
  `product_type` enum('OWN','CONSIGNED') DEFAULT 'OWN' COMMENT 'Nguồn gốc sản phẩm',
  `warranty_period` int DEFAULT '12',
  `quantity` int DEFAULT '0' COMMENT 'Tổng tồn kho tất cả biến thể',
  `deleted_at` datetime DEFAULT NULL,
  `specs` json DEFAULT NULL COMMENT 'Thông số kỹ thuật chung: {"screen": "6.1 inch", "chip": "A17 Pro"}',
  `is_imei_tracked` tinyint(1) DEFAULT '1' COMMENT '1: Quản lý IMEI, 0: Số lượng thường',
  `meta_title` varchar(255) DEFAULT NULL,
  `meta_description` text,
  `is_featured` tinyint(1) DEFAULT '0',
  `min_price` decimal(15,2) DEFAULT NULL,
  `max_price` decimal(15,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_products_slug` (`slug`),
  KEY `products_categories_fk` (`category_id`),
  KEY `products_brands_fk` (`brand_id`),
  KEY `idx_category_price` (`category_id`,`price`),
  KEY `fk_products_shop` (`shop_id`),
  FULLTEXT KEY `ft_product_name` (`name`),
  CONSTRAINT `fk_products_shop` FOREIGN KEY (`shop_id`) REFERENCES `shops` (`id`),
  CONSTRAINT `products_brands_fk` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`id`),
  CONSTRAINT `products_categories_fk` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `provinces` (
  `code` varchar(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `name_en` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `full_name_en` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `shop_employees` (
  `id` int NOT NULL AUTO_INCREMENT,
  `shop_id` int NOT NULL,
  `user_id` int NOT NULL,
  `role` enum('MANAGER','SALES','WAREHOUSE') DEFAULT 'SALES',
  `status` enum('ACTIVE','RESIGNED') DEFAULT 'ACTIVE',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_shop_emp` (`shop_id`,`user_id`),
  KEY `fk_shop_emp_user` (`user_id`),
  CONSTRAINT `fk_shop_emp_shop` FOREIGN KEY (`shop_id`) REFERENCES `shops` (`id`),
  CONSTRAINT `fk_shop_emp_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `shops` (
  `id` int NOT NULL AUTO_INCREMENT,
  `owner_id` int NOT NULL COMMENT 'Chủ shop (User ID)',
  `name` varchar(255) NOT NULL,
  `slug` varchar(255) DEFAULT NULL,
  `logo_url` varchar(255) DEFAULT NULL,
  `banner_url` varchar(255) DEFAULT NULL,
  `description` text,
  `commission_rate` decimal(5,2) DEFAULT '5.00' COMMENT 'Phí sàn thu %',
  `status` enum('ACTIVE','BANNED','PENDING') DEFAULT 'ACTIVE',
  `rating_avg` float DEFAULT '5',
  `total_orders` int DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`),
  KEY `fk_shops_users` (`owner_id`),
  CONSTRAINT `fk_shops_users` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `social_accounts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `provider` varchar(20) NOT NULL,
  `provider_id` varchar(50) NOT NULL,
  `email` varchar(150) NOT NULL,
  `name` varchar(100) NOT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `social_accounts_fk` (`user_id`),
  CONSTRAINT `social_accounts_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `social_posts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `content` text,
  `media_type` enum('IMAGE','VIDEO') DEFAULT 'IMAGE',
  `media_urls` json DEFAULT NULL,
  `linked_product_id` int DEFAULT NULL,
  `total_likes` int DEFAULT '0',
  `total_comments` int DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_posts_user` (`user_id`),
  KEY `fk_posts_pro` (`linked_product_id`),
  CONSTRAINT `fk_posts_pro` FOREIGN KEY (`linked_product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `fk_posts_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `suppliers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `shop_id` int NOT NULL DEFAULT '1',
  `name` varchar(100) NOT NULL,
  `contact_email` varchar(100) DEFAULT NULL,
  `contact_phone` varchar(20) DEFAULT NULL,
  `status` enum('active','inactive') DEFAULT 'active',
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sup_shop` (`shop_id`),
  CONSTRAINT `fk_supplier_shop` FOREIGN KEY (`shop_id`) REFERENCES `shops` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tokens` (
  `id` int NOT NULL AUTO_INCREMENT,
  `token` varchar(255) NOT NULL,
  `token_type` varchar(50) NOT NULL,
  `expiration_date` datetime DEFAULT NULL,
  `revoked` tinyint(1) NOT NULL,
  `expired` tinyint(1) NOT NULL,
  `user_id` int DEFAULT NULL,
  `is_mobile` tinyint(1) DEFAULT '0',
  `refresh_token` varchar(255) DEFAULT '',
  `refresh_expiration_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `tokens_user_fk` (`user_id`),
  CONSTRAINT `tokens_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `transactions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `payment_method` varchar(50) NOT NULL,
  `transaction_code` varchar(100) DEFAULT NULL,
  `amount` decimal(15,2) NOT NULL,
  `status` enum('PENDING','SUCCESS','FAILED','REFUNDED') DEFAULT 'PENDING',
  `response_json` json DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_transactions_order` (`order_id`),
  CONSTRAINT `fk_transactions_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_addresses` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `recipient_name` varchar(100) DEFAULT NULL,
  `phone_number` varchar(15) DEFAULT NULL,
  `address_detail` varchar(200) NOT NULL,
  `province_code` varchar(20) DEFAULT NULL,
  `district_code` varchar(20) DEFAULT NULL,
  `ward_code` varchar(20) DEFAULT NULL,
  `is_default` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_addr_user` (`user_id`),
  KEY `fk_addr_province` (`province_code`),
  KEY `fk_addr_district` (`district_code`),
  KEY `fk_addr_ward` (`ward_code`),
  CONSTRAINT `fk_addr_district` FOREIGN KEY (`district_code`) REFERENCES `districts` (`code`),
  CONSTRAINT `fk_addr_province` FOREIGN KEY (`province_code`) REFERENCES `provinces` (`code`),
  CONSTRAINT `fk_addr_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_addr_ward` FOREIGN KEY (`ward_code`) REFERENCES `wards` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_credentials` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `credential_id` text NOT NULL,
  `public_key` text NOT NULL,
  `sign_count` int DEFAULT '0',
  `device_label` varchar(255) DEFAULT NULL COMMENT 'VD: Macbook Pro của Tùng',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_cred_user` (`user_id`),
  CONSTRAINT `fk_cred_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_devices` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `fcm_token` varchar(500) NOT NULL COMMENT 'Firebase Token',
  `device_type` enum('ANDROID','IOS','WEB') NOT NULL,
  `device_name` varchar(255) DEFAULT NULL,
  `last_active_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_device_user` (`user_id`),
  CONSTRAINT `fk_device_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_follows` (
  `id` int NOT NULL AUTO_INCREMENT,
  `follower_id` int NOT NULL,
  `following_user_id` int DEFAULT NULL,
  `following_shop_id` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_follow_user` (`follower_id`,`following_user_id`),
  UNIQUE KEY `ux_follow_shop` (`follower_id`,`following_shop_id`),
  KEY `fk_follow_dest_user` (`following_user_id`),
  KEY `fk_follow_dest_shop` (`following_shop_id`),
  CONSTRAINT `fk_follow_dest_shop` FOREIGN KEY (`following_shop_id`) REFERENCES `shops` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_follow_dest_user` FOREIGN KEY (`following_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_follow_src` FOREIGN KEY (`follower_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_interactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `product_id` int DEFAULT NULL,
  `post_id` bigint DEFAULT NULL,
  `action_type` enum('VIEW','LIKE','SHARE','CLICK','ADD_CART') NOT NULL,
  `duration_ms` int DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_algo_score` (`user_id`,`product_id`,`action_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_showcase_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `product_id` int NOT NULL,
  `is_hidden` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_showcase` (`user_id`,`product_id`),
  KEY `fk_showcase_pro` (`product_id`),
  CONSTRAINT `fk_showcase_pro` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `fk_showcase_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fullname` varchar(100) DEFAULT '',
  `phone_number` varchar(15) DEFAULT NULL,
  `address` varchar(200) DEFAULT '',
  `password` char(60) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `date_of_birth` date DEFAULT NULL,
  `role_id` int DEFAULT '1',
  `email` varchar(255) DEFAULT '',
  `profile_image` varchar(255) DEFAULT '',
  `email_verified_at` datetime DEFAULT NULL,
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_users_email` (`email`),
  UNIQUE KEY `ux_users_phone` (`phone_number`),
  KEY `users_role_fk` (`role_id`),
  CONSTRAINT `users_role_fk` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `variant_values` (
  `variant_id` int NOT NULL,
  `product_id` int NOT NULL,
  `option_id` int NOT NULL,
  `option_value_id` int NOT NULL,
  PRIMARY KEY (`variant_id`,`option_id`),
  KEY `option_value_id` (`option_value_id`),
  KEY `option_id` (`option_id`),
  CONSTRAINT `variant_values_ibfk_1` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`id`) ON DELETE CASCADE,
  CONSTRAINT `variant_values_ibfk_2` FOREIGN KEY (`option_value_id`) REFERENCES `option_values` (`id`),
  CONSTRAINT `variant_values_ibfk_3` FOREIGN KEY (`option_id`) REFERENCES `options` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `wallet_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `wallet_id` int NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `type` enum('DEPOSIT','WITHDRAWAL','SALE_REVENUE','AFFILIATE_COMMISSION','REFUND') NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `ref_order_id` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_trans_wallet` (`wallet_id`),
  CONSTRAINT `fk_trans_wallet` FOREIGN KEY (`wallet_id`) REFERENCES `wallets` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `wallets` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `balance` decimal(15,2) DEFAULT '0.00',
  `frozen_balance` decimal(15,2) DEFAULT '0.00' COMMENT 'Tiền chờ đối soát',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_wallets_user` (`user_id`),
  CONSTRAINT `fk_wallets_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `wards` (
  `code` varchar(20) NOT NULL,
  `district_code` varchar(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `name_en` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `full_name_en` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`code`),
  KEY `district_code` (`district_code`),
  CONSTRAINT `wards_ibfk_1` FOREIGN KEY (`district_code`) REFERENCES `districts` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `warranty_requests` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `order_detail_id` int NOT NULL COMMENT 'Link chính xác tới cái máy đã mua',
  `product_item_id` int DEFAULT NULL COMMENT 'IMEI của máy lỗi',
  `request_type` enum('WARRANTY','RETURN','EXCHANGE') NOT NULL COMMENT 'Bảo hành, Trả hàng hay Đổi mới',
  `status` enum('PENDING','RECEIVED','PROCESSING','COMPLETED','REJECTED') DEFAULT 'PENDING',
  `reason` text COMMENT 'Lý do lỗi (Màn hình xanh, sạc không vào...)',
  `images` json DEFAULT NULL COMMENT 'Ảnh chụp tình trạng máy',
  `admin_note` text COMMENT 'Ghi chú của kỹ thuật viên',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `quantity` int DEFAULT '1' COMMENT 'Số lượng sản phẩm cần bảo hành/trả',
  PRIMARY KEY (`id`),
  KEY `fk_warranty_user` (`user_id`),
  KEY `fk_warranty_detail` (`order_detail_id`),
  KEY `fk_warranty_item` (`product_item_id`),
  CONSTRAINT `fk_warranty_detail` FOREIGN KEY (`order_detail_id`) REFERENCES `order_details` (`id`),
  CONSTRAINT `fk_warranty_item` FOREIGN KEY (`product_item_id`) REFERENCES `product_items` (`id`),
  CONSTRAINT `fk_warranty_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `withdrawal_requests` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `bank_name` varchar(100) DEFAULT NULL,
  `bank_account` varchar(50) DEFAULT NULL,
  `account_holder` varchar(100) DEFAULT NULL,
  `status` enum('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_withdraw_user` (`user_id`),
  CONSTRAINT `fk_withdraw_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;
COMMIT;

INSERT INTO `roles` (`id`, `name`) VALUES
(1, 'user'),
(2, 'admin'),
(3, 'vendor'),
(4, 'staff');
