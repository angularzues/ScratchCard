package com.example.gray_dog3.scratchcard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by gray_dog3 on 16/2/29.
 */
public class MyCard extends View {
    private Paint mPaint = new Paint();//画手的轨迹的画笔
    private Path mPath = new Path();//手指滑动的路径
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private Bitmap mBgBitmap;
    private int mEndX, mEndY;//滑动结束点的坐标

    public MyCard(Context context) {
        this(context, null);
    }

    public MyCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public MyCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //画涂层的画布,传一个Bitmap进去,所画的信息都存在Bitmap上.
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.parseColor("#c0c0c0"));
        setPaint();
    }

    /**
     * 设置画笔
     */
    public void setPaint() {
        //消除锯齿边,给画笔设置平滑的属性
        mPaint.setAntiAlias(true);
        //防抖动属性
        mPaint.setDither(true);
        //画线
        mPaint.setStyle(Paint.Style.STROKE);
        //画笔接洽点类型 如影响矩形但角的外轮廓
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        //画笔笔刷类型 如影响画笔但始末端
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(80);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //先把底层的画画到View的画布上
        canvas.drawBitmap(mBgBitmap, 0, 0, null);
        //把Path画到涂层的画布上保存在涂层的Bitmap
        drawPath();
        //注意这个cavas不要用错,不然会画到其他的画布上显式不出来.把混合有Path的涂层的Bitmap画到View的画布上
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }


    /**
     * 绘出手指移动路径
     */
    private void drawPath() {
        //给mPaint设置绘画时两个图形重叠的部分变透明的属性,也就是所有Path画的地方变透明.
        // 注意这个画笔只有在画Path和涂层混合到一起的时候用了.
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        //用设置好的画笔把Path画到涂层上,和涂层画在一起
        mCanvas.drawPath(mPath, mPaint);
    }


    /**
     * 手指滑动事件处理,把手指移动的轨迹保存在Path中.
     * 不停的移动,就不停的回调View的更新UI的方法:invalidate();
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mEndX = x;
                mEndY = y;
                mPath.moveTo(mEndX, mEndY);
                break;
            case MotionEvent.ACTION_MOVE:
                int dX = Math.abs(x - mEndX);
                int dY = Math.abs(y - mEndY);
                //太小的移动忽略不画
                if (dX > 3 || dY > 3) {
                    mPath.lineTo(x, y);
                }
                mEndX = x;
                mEndY = y;
                break;
        }
        //回调更新View
        invalidate();
        return true;
    }


}
