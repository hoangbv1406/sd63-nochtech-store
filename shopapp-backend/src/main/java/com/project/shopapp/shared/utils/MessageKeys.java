package com.project.shopapp.shared.utils;

public final class MessageKeys {

    private MessageKeys() {
    } // prevent instantiation

    public static final class User {
        public static final String LOGIN_SUCCESS = "user.login.success";
        public static final String LOGIN_FAILED = "user.login.failed";
        public static final String REGISTER_SUCCESS = "user.register.success";
        public static final String PASSWORD_NOT_MATCH = "user.register.password_not_match";
        public static final String USER_IS_LOCKED = "user.login.user_is_locked";
        public static final String WRONG_PHONE_PASSWORD = "user.login.wrong_phone_password";
        public static final String ROLE_DOES_NOT_EXIST = "user.login.role_not_exist";
    }

    public static final class Category {
        public static final String CREATE_SUCCESS = "category.create.success";
        public static final String CREATE_FAILED = "category.create.failed";
        public static final String UPDATE_SUCCESS = "category.update.success";
        public static final String DELETE_SUCCESS = "category.delete.success";
    }

    public static final class Product {
        public static final String UPLOAD_MAX_5 = "product.upload_images.error_max_5_images";
        public static final String UPLOAD_FILE_LARGE = "product.upload_images.file_large";
        public static final String UPLOAD_MUST_BE_IMAGE = "product.upload_images.file_must_be_image";
    }

    public static final class Order {
        public static final String DELETE_SUCCESS = "order.delete.success";
        public static final String DELETE_DETAIL_SUCCESS = "order.delete_detail.success";
    }

}
