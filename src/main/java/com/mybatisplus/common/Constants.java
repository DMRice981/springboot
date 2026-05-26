package com.mybatisplus.common;

/**
 * 统一常量类
 */
public class Constants {

    private Constants() {
    }

    /**
     * 状态码
     */
    public static class Status {
        public static final Integer DISABLED = 0;
        public static final Integer ENABLED = 1;
        public static final Integer DELETED = 1;
        public static final Integer NOT_DELETED = 0;
    }

    /**
     * 订单状态
     */
    public static class OrderStatus {
        public static final Integer PENDING = 0;
        public static final Integer SHIPPED = 1;
        public static final Integer DELIVERING = 2;
        public static final Integer COMPLETED = 3;
        public static final Integer CANCELLED = 4;
    }

    /**
     * 支付状态
     */
    public static class PayStatus {
        public static final Integer UNPAID = 0;
        public static final Integer PAID = 1;
    }

    /**
     * 售后状态
     */
    public static class AfterSaleStatus {
        public static final Integer PENDING = 0;
        public static final Integer PROCESSED = 1;
    }

    /**
     * 商品状态
     */
    public static class GoodsStatus {
        public static final Integer OFF_SHELF = 0;
        public static final Integer ON_SHELF = 1;
    }

    /**
     * 错误码
     */
    public static class ErrorCode {
        public static final Integer SUCCESS = 200;
        public static final Integer ERROR = 500;
        public static final Integer PARAM_ERROR = 400;
        public static final Integer UNAUTHORIZED = 401;
        public static final Integer FORBIDDEN = 403;
        public static final Integer NOT_FOUND = 404;
    }

    /**
     * 订单前缀
     */
    public static final String ORDER_PREFIX = "ORD";
}
