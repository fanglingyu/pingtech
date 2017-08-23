package com.pingtech.hgqw.zxing;

import java.util.Collection;
import java.util.HashSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.pingtech.R;
import com.pingtech.hgqw.zxing.client.android.camera.CameraManager;


/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 */
@SuppressLint("ResourceAsColor")
public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {
            0, 64, 128, 192, 255, 192, 128, 64
    };
    private static final long ANIMATION_DELAY = 100L;
    private static final int OPAQUE = 0xFF;

    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int frameColor;
    private final int resultPointColor;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    private CameraManager cameraManager;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every
        // time in onDraw().
        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        frameColor = resources.getColor(R.color.viewfinder_frame);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        possibleResultPoints = new HashSet<ResultPoint>(5);
    }
    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
      }
    @SuppressLint("ResourceAsColor")
	@Override
    public void onDraw(Canvas canvas) {
        Rect frame = cameraManager.getFramingRect();
        if (frame == null) {
            return;
        }
        
       
        
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        
        frame = new Rect(frame.left, (height - (width-2*frame.left))/2, frame.right, (width-2*frame.left)+(height - (width-2*frame.left))/2);
        

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        paint.setColor(android.R.color.darker_gray);
        paint.setAlpha(150);
        //top
        canvas.drawRect(0, 0, width, frame.top, paint);
        //top_left
        canvas.drawRect(0, frame.top, frame.left, frame.top, paint);
        //top_right
        canvas.drawRect(frame.right, frame.top, width, frame.top, paint);
        //Left
        canvas.drawRect(0, frame.top, frame.left, frame.bottom, paint);
        //Right
        canvas.drawRect(frame.right , frame.top, width, frame.bottom, paint);
        //Bottom
        canvas.drawRect(0, frame.bottom , width, height, paint);
        //Bottom_left
        canvas.drawRect(0, frame.bottom, frame.left, frame.bottom, paint);
        //Bottom_right
        canvas.drawRect(frame.right, frame.bottom, width, frame.bottom, paint);

        int frame_with = 10;
		
        Rect rect = new Rect(frame.left, frame.top, frame.right, frame.bottom);
        
        //RectF rectF = new RectF(frame.left-10, frame.top-10, frame.right+10, frame.bottom+10);
		Paint paint2 = new Paint();
		paint2.setColor(android.R.color.black);
		paint2.setStyle(Style.STROKE);
		paint2.setStrokeWidth(2);
		paint2.setAlpha(120);
		canvas.drawRect(rect, paint2);
		//canvas.drawRoundRect(rect, 20, 20, paint2);
		
		
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        
        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {
            int linewidth = 10;
            paint.setColor(frameColor);

            // draw rect
            canvas.drawRect(15 + frame.left, 15 + frame.top,
                    15 + (linewidth + frame.left), 15 + (50 + frame.top), paint);
            canvas.drawRect(15 + frame.left, 15 + frame.top,
                    15 + (50 + frame.left), 15 + (linewidth + frame.top), paint);
            canvas.drawRect(-15 + ((0 - linewidth) + frame.right),
                    15 + frame.top, -15 + (1 + frame.right),
                    15 + (50 + frame.top), paint);
            canvas.drawRect(-15 + (-50 + frame.right), 15 + frame.top, -15
                    + frame.right, 15 + (linewidth + frame.top), paint);
            canvas.drawRect(15 + frame.left, -15 + (-49 + frame.bottom),
                    15 + (linewidth + frame.left), -15 + (1 + frame.bottom),
                    paint);
            canvas.drawRect(15 + frame.left, -15
                    + ((0 - linewidth) + frame.bottom), 15 + (50 + frame.left),
                    -15 + (1 + frame.bottom), paint);
            canvas.drawRect(-15 + ((0 - linewidth) + frame.right), -15
                    + (-49 + frame.bottom), -15 + (1 + frame.right), -15
                    + (1 + frame.bottom), paint);
            canvas.drawRect(-15 + (-50 + frame.right), -15
                    + ((0 - linewidth) + frame.bottom), -15 + frame.right, -15
                    + (linewidth - (linewidth - 1) + frame.bottom), paint);

            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<ResultPoint>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 6.0f, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 3.0f, paint);
                }
            }

            // Request another update at the animation interval, but only
            // repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
                    frame.right, frame.bottom);
        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live
     * scanning display.
     * 
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }
    /**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
