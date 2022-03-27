package com.example.assistgoandroid;

/**
 * Le Jie Bennett
 * https://learntodroid.com/how-to-create-a-qr-code-scanner-app-in-android/
 */
public interface QRCodeFoundListener {
    void onQRCodeFound(String qrCode);
    void qrCodeNotFound();
}
