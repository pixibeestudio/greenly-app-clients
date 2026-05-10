package com.pixibeestudio.greenly.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class tao QR code Bitmap local bang thu vien ZXing.
 *
 * Uu diem so voi goi qrserver.com:
 *  - Khong phu thuoc internet ngoai (chi can encode local)
 *  - Khong gioi han do dai data (qrserver gioi han URL ~8KB)
 *  - Tao tuc thi, khong can cho download
 *  - Hoat dong tot voi deeplink momo:// (data dai)
 */
public class QRCodeGenerator {

    private static final String TAG = "QRCodeGenerator";

    /**
     * Tao Bitmap QR code tu mot chuoi data.
     *
     * @param data    Noi dung de encode vao QR (vd: "momo://...", "https://...")
     * @param sizePx  Kich thuoc canh QR (pixel), nen >= 400 de scan ro
     * @return Bitmap QR vuong, hoac null neu encode loi (data null/empty)
     */
    public static Bitmap generate(String data, int sizePx) {
        if (data == null || data.isEmpty()) {
            Log.w(TAG, "generate: data rong, khong tao QR");
            return null;
        }

        try {
            // Cau hinh ZXing:
            //  - UTF-8 de ho tro ky tu Tieng Viet (du it dung trong deeplink MoMo)
            //  - Error Correction Level M (medium ~15%) - can bang giua chong loi va dung luong
            //  - Margin nho (1) de tan dung khong gian
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, sizePx, sizePx, hints);

            int width  = matrix.getWidth();
            int height = matrix.getHeight();

            // Toi uu: tao mang int 1 chieu de setPixels mot lan duy nhat (nhanh hon setPixel theo o)
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = matrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;

        } catch (WriterException e) {
            Log.e(TAG, "Loi encode QR: " + e.getMessage(), e);
            return null;
        } catch (IllegalArgumentException e) {
            // Truong hop data qua dai vuot capacity QR Code (~3KB)
            Log.e(TAG, "Data qua dai cho QR Code: " + e.getMessage(), e);
            return null;
        }
    }
}
