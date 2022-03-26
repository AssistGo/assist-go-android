package com.example.assistgoandroid;

import static android.graphics.ImageFormat.YUV_420_888;
import static android.graphics.ImageFormat.YUV_422_888;
import static android.graphics.ImageFormat.YUV_444_888;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import java.nio.ByteBuffer;

/**
 * Le Jie Bennett
 * https://learntodroid.com/how-to-create-a-qr-code-scanner-app-in-android/
 */
public class QRCodeImageAnalyzer implements ImageAnalysis.Analyzer{
    private QRCodeFoundListener listener;
    public QRCodeImageAnalyzer(QRCodeFoundListener listener){
        Log.d("created", "QRCodeImageAnalyzer: createdImageAnalyzer");
        this.listener=listener;
    }


    @Override
    public void analyze(@NonNull ImageProxy image) {
        Log.d("here", "analyze: " + image);
        Log.d("analyzing", "analyze: method started");
        if (image.getFormat() == YUV_420_888 || image.getFormat() == YUV_422_888 || image.getFormat() == YUV_444_888) {
            ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
            byte[] imageData = new byte[byteBuffer.capacity()];
            byteBuffer.get(imageData);

            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                    imageData,
                    image.getWidth(), image.getHeight(),
                    0, 0,
                    image.getWidth(), image.getHeight(),
                    false
            );

            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                Result result = new QRCodeMultiReader().decode(binaryBitmap);
                listener.onQRCodeFound(result.getText());
            } catch (ChecksumException | NotFoundException | com.google.zxing.FormatException e) {
                listener.qrCodeNotFound();
            }
        }

        image.close();
    }
}
