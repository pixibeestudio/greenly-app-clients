package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model parse response từ POST /api/momo/create-payment
 *
 * Cấu trúc:
 * {
 *   "success": true,
 *   "message": "...",
 *   "data": {
 *     "momo_order_id": "GREENLY_123_...",
 *     "amount": 50000,
 *     "payment_type": "app" | "qr",
 *     "pay_url": "...",
 *     "deeplink": "momo://...",
 *     "qr_code_url": "https://..."
 *   }
 * }
 */
public class MomoCreatePaymentResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("momo_order_id")
        private String momoOrderId;

        @SerializedName("amount")
        private long amount;

        @SerializedName("payment_type")
        private String paymentType; // 'app' hoặc 'qr'

        @SerializedName("pay_url")
        private String payUrl;

        @SerializedName("deeplink")
        private String deeplink;

        @SerializedName("qr_code_url")
        private String qrCodeUrl;

        public String getMomoOrderId() {
            return momoOrderId;
        }

        public long getAmount() {
            return amount;
        }

        public String getPaymentType() {
            return paymentType;
        }

        public String getPayUrl() {
            return payUrl;
        }

        public String getDeeplink() {
            return deeplink;
        }

        public String getQrCodeUrl() {
            return qrCodeUrl;
        }
    }
}
