package gr.gdschua.bloodapp.Utils;

import static android.graphics.Color.WHITE;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import gr.gdschua.bloodapp.R;

@SuppressWarnings("SuspiciousNameCombination")
public class QrEncoder {
    private final int CH_RED;

    public QrEncoder(Context context) {
            CH_RED = ContextCompat.getColor(context,R.color.changed_red);
    }


    public Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        int WIDTH = 600;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? CH_RED : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;

    }
}
