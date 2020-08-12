package com.czc.wavedemoone;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class WaveView extends View {
    private Paint wPaintWave;//波浪画笔
    private Paint wPaintProgress;//进度画笔
    private float wItemWaveLength;//波浪长度
    private float wRadius;//圆的半径
    private float wWaveHeight = 20;//波浪高度
    private float wProgressTextSize = 130;//进度文字大小
    private Path wPathWave;//波浪路径(不透明)
    private Path wPathWaveAlpha;//波浪路径(透明)
    private Path wWaveCircle;//圆形球的路径
    private float wWave = 0;//波浪的偏移(实现波浪效果的关键)
    private float wProgress = 0;//进度
    private Paint.FontMetricsInt wFontMetricsInt;//字体度量整型
    private ObjectAnimator wWaveobjectAnimator;//属性动画

    public WaveView(Context context) {
        this(context,null);
    }

    public WaveView(Context context, @Nullable AttributeSet attr){
        this(context,attr,0);
    }

    public WaveView(Context context,@Nullable AttributeSet attr,int defStyleAttr){
        super(context,attr,defStyleAttr);
        init();
    }
    //初始化
    private void  init(){
        setLayerType(LAYER_TYPE_SOFTWARE,null);
        //波浪画布
        wPaintWave = new Paint(Paint.ANTI_ALIAS_FLAG);
        wPaintWave.setColor(Color.parseColor("#FFFFFFFF"));//设置画布颜色
        wPaintWave.setStyle(Paint.Style.FILL);//设置样式
        //进度画布
        wPaintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        wPaintProgress.setStrokeWidth(10);//设置高
        wPaintProgress.setStrokeCap(Paint.Cap.ROUND);//设置笔帽为圆形
        wPaintProgress.setStrokeJoin(Paint.Join.ROUND);//设置拐角为圆形
        wPaintProgress.setColor(Color.WHITE);//进度颜色设置为白色
        wPaintProgress.setTextAlign(Paint.Align.CENTER);//文字设置为居中
        //初始化
        wPathWave = new Path();
        wPathWaveAlpha = new Path();
        wWaveCircle = new Path();
    }
    /**
     * 修改高度
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWith(widthMeasureSpec),measureHeight(heightMeasureSpec));
    }

    /**
     * 测量宽度
     * @param widthMeasureSpec
     * @return
     */
    private int measureWith(int widthMeasureSpec){
        int result ;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else {
            result = 200;
            if (specMode == MeasureSpec.AT_MOST){
                result = Math.min(result,specSize);
            }
        }
        return result;
    }

    /**
     * 测量高度
     * @param heightMeasureSpec
     * @return
     */
    private int measureHeight(int heightMeasureSpec){
        int result ;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else{
            result = 200;
            if(specMode == MeasureSpec.AT_MOST){
                result = Math.min(result,specSize);
            }
        }
        return  result;
    }

    /**
     *  当控件大小发生改变时，调整一些数值以适应控件大小
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        wRadius = (float) (Math.min(w,h)*0.9/2);
        wItemWaveLength = wRadius*2;//一段波浪长度
        wWaveCircle.addCircle(w/2,h/2,wRadius,Path.Direction.CW);
        wProgressTextSize = wRadius*0.7f;//当控件大小发生改变时，动态修改文字大小
        wPaintProgress.setTextSize(wProgressTextSize);
        wFontMetricsInt = wPaintProgress.getFontMetricsInt();
        wWaveHeight = wRadius/8;//当控件大小发生改变时，动态修改画布高度
        invalidate();
        StartWaveAnim();
    }
    @SuppressLint("ObjectAnimatorBinding")
    public void StartWaveAnim(){
        wWaveobjectAnimator = ObjectAnimator.ofFloat(this, "wave", 0, wItemWaveLength).setDuration(4000);
        wWaveobjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        wWaveobjectAnimator.setInterpolator(new LinearInterpolator());
        wWaveobjectAnimator.start();
    }

    /**
     * 设置波浪偏移量（波浪的动画效果核心是靠这个实现的）
     * @param wave
     */
    public void setWave(float wave){
        wWave = wave;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.clipPath(wWaveCircle);//将画布裁剪为圆形
        canvas.drawColor(Color.parseColor("#3233F92B"));
        wPathWave.reset();
        wPathWaveAlpha.reset();
        //将波浪路径起始点移至圆形球的左侧一段波浪长度（即上下起伏为一段）的位置处
        wPathWave.moveTo(getWidth()/2-wRadius-wItemWaveLength+wWave,getHeight()/2+wRadius+wWaveHeight-wProgress*(wRadius*2+wWaveHeight*2));
        wPathWaveAlpha.moveTo(getWidth()/2-wRadius-wItemWaveLength+wWave+wItemWaveLength/8,getHeight()/2+wRadius+wWaveHeight-wProgress*(wRadius*2+wWaveHeight*2));

        float half = wItemWaveLength / 4;
        for(float x = -wItemWaveLength;x<getWidth()+wItemWaveLength;x+=wItemWaveLength){
            wPathWave.rQuadTo(half/2,-wWaveHeight,half,0);//贝赛尔曲线实现波浪
            wPathWave.rQuadTo(half/2,wWaveHeight,half,0);
            wPathWaveAlpha.rQuadTo(half/2,-wWaveHeight,half,0);//贝赛尔曲线实现波浪
            wPathWaveAlpha.rQuadTo(half/2,wWaveHeight,half,0);
        }
        wPathWave.lineTo(getWidth(),getHeight());
        wPathWave.lineTo(0,getHeight());
        wPathWave.close();//制造闭合路径
        wPathWaveAlpha.lineTo(getWidth(),getHeight());
        wPathWaveAlpha.lineTo(0,getHeight());
        wPathWaveAlpha.close();//制造闭合路径
        wPaintWave.setColor(Color.parseColor("#7AF92B84"));//设置后面的波浪为半透明
        canvas.drawPath(wPathWaveAlpha, wPaintWave);
        wPaintWave.setColor(Color.parseColor("#F92B2B"));//设置前面的波浪为不透明
        canvas.drawPath(wPathWave, wPaintWave);
        canvas.drawText((int)(wProgress*100)+"%",getWidth()/2,getHeight()/2+((wFontMetricsInt.bottom-wFontMetricsInt.top)/2 -wFontMetricsInt.bottom),wPaintProgress);
        canvas.restore();
    }

    /**
     * 设置进度(不带动画)
     * @param progress
     */
    public void setProgress(float progress){
        wProgress = progress;
        invalidate();
    }

    /**
     * 设置进度(带动画)
     * @param progress
     */
    @SuppressLint("ObjectAnimatorBinding")
    public void setProgressWithAnim(float progress){
        ObjectAnimator.ofFloat(this,"progress",0,progress).setDuration(5000).start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (wWaveobjectAnimator !=null){
            //当控件移除时取消动画
            wWaveobjectAnimator.cancel();
        }
    }
}
