package com.pixibeestudio.greenly.data.model;

/**
 * Model chứa kết quả trả về sau khi đặt hàng thành công từ API /checkout
 * Bao gồm orderId và grandTotal (đã có phí vận chuyển) từ backend
 */
public class CheckoutResult {
    private int orderId;
    private int grandTotal;
    private String orderCode;

    public CheckoutResult(int orderId, int grandTotal, String orderCode) {
        this.orderId = orderId;
        this.grandTotal = grandTotal;
        this.orderCode = orderCode;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getGrandTotal() {
        return grandTotal;
    }

    public String getOrderCode() {
        return orderCode;
    }
}
