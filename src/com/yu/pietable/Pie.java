package com.yu.pietable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.yu.utils.AnimationListenerMy;
import com.yu.utils.CompareList;
import com.yu.utils.EntyAngle;
import com.yu.utils.EntyExpenses;
import com.yu.utils.FormatString;

public class Pie extends View {
	
	//��ͼ��ʾ����
	private Paint showPaint;
	
	//��������Դ
	private List<EntyExpenses> list;
	
	//ÿ������ĽǶȷ�Χ
	private List<EntyAngle> listAngle = new ArrayList<EntyAngle>();;
	
	private float centerRadius;
	
	private LineText lineText;
	
	private int height;
	private int width;
	private float mWidth;
	private float mHeight;
	
	public final int START_ANGLE = 0;
	public final int SWEEP_ANGLE = 1;
	
	private final int[] colors = new int[]{0xff87CEFF,0xffFFC1C1,0xffFFF68F,0xff76EEC6,0xffCAFF70}; 
	
	private List<Integer> colorsUse = new ArrayList<Integer>();
	
	private int colorCenterCircle = 0xe0ffffff;

	public List<Integer> getColorsUse() {
		return colorsUse;
	}

	public List<EntyExpenses> getList() {
		return list;
	}

	public void initi(List<EntyExpenses> list, LineText lineText){
		this.list = list;
		this.lineText = lineText;

		colorsUse.clear();
		if(list == null){
			list = new ArrayList<EntyExpenses>();
		}
		if(list.size()==0){
			list.add(new EntyExpenses("û������(��o��)Ŷ", 0));
			colorsUse.add(0xff000000);
		}
		else{
			CompareList compare = new CompareList();
			Collections.sort(list, compare);
			//��ȡ����
			int total = 0;
			for(EntyExpenses enty: list){
				total += enty.getExpensesNum();
			}
			float totalPer = 0;
			for(int i=0; i<list.size(); i++){
				exchange2Persent(list, i, total);
				if(i/colors.length > 0 && list.size() % colors.length != 1 ){
					colorsUse.add(colors[i%colors.length]);
				}
				else if(i/colors.length > 0 && list.size() % colors.length == 1){
					colorsUse.add(colors[i%colors.length+1]);
				}
				else{
					colorsUse.add(colors[i]);
				}
				if(i<list.size()-1)
					totalPer += list.get(i).getExpensesPersent();
			}
			list.get(list.size()-1).setExpensesPersent(Float.valueOf(FormatString.keep3AfterPoint(new BigDecimal(String.valueOf(1-totalPer)))));
		}
		
		this.invalidate();
	}
	
	public void fresh(List<EntyExpenses> list, List<Integer> colorsUse){
		this.list = list;
		this.colorsUse = colorsUse;
		this.invalidate();
	}
	
	public Pie(Context context,AttributeSet attrs) {
		super(context, attrs);
		//���û�������Ļ���ģ�ͣ����������������ܿ���Ӳ������
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		showPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		showPaint.setColor(0xff87CEFF);
		showPaint.setAntiAlias(true);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		
		if(list==null) return;
		
		showPaint.setStyle(Paint.Style.FILL);
		listAngle.clear();
		
		height = this.getHeight();
		width = this.getWidth();

		mWidth = width*0.45f;
		mHeight = height*0.45f;
		
		//�ƶ�����
        canvas.translate(width*0.5f, height*0.5f);
        
        centerRadius = getMin(mWidth, mHeight)*0.65f;
        
        float limit = centerRadius;
		RectF oval = new RectF(-limit, -limit,
				limit, limit);
		
			float startEvery = 0;
		for(int i=0; i<list.size(); i++){
			showPaint.setColor(colorsUse.get(i));
			
			float sweepAngle = list.get(i).getExpensesPersent()*360;

			if(i==0){
				startEvery = 270f - sweepAngle/2f;
			}
			listAngle.add(new EntyAngle(startEvery,sweepAngle));
			canvas.drawArc(oval, startEvery, sweepAngle , true, showPaint);
			startEvery = startEvery + sweepAngle;
		}
		
		//�������Բ��
		
		//�����ڲ�Բ
		if(!(list.get(0).getExpensesMainType()!=null && list.get(0).getExpensesMainType().equals("û������(��o��)Ŷ"))){
			showPaint.setColor(0x30000000);
			canvas.drawCircle(0, 0, centerRadius*0.5f, showPaint);
			showPaint.setColor(colorCenterCircle);
			canvas.drawCircle(0, 0, centerRadius*0.45f, showPaint);
		}
		
	}
	
	private List<EntyExpenses> exchange2Persent(List<EntyExpenses> list, int position,double total){
		double persent = list.get(position).getExpensesNum()/(double)total;
		list.get(position).setExpensesPersent(Float.valueOf(FormatString.keep3AfterPoint(new BigDecimal(persent))));
		return list;
	}

	public float getMin(float mWidth, float mHeight){
		float min;
		if(mWidth<=mHeight){
			min = mWidth;
		}
		else{
			min = mHeight;
		}
		return min;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		float YCenter = Pie.this.getHeight()*0.5f;
		float XCenter = Pie.this.getWidth()*0.5f;
		
		if( (x-XCenter)*(x-XCenter) + (y-YCenter)*(y-YCenter) >  centerRadius*0.5* centerRadius*0.5
			    && (x-XCenter)*(x-XCenter) + (y-YCenter)*(y-YCenter) < centerRadius*centerRadius
			    && event.getAction() == MotionEvent.ACTION_UP ) {
			//����ڲ�ɫԲ����
			//�������Բ��֮��ľ���
			double distance = Math.pow((x-XCenter)*(x-XCenter) + (y-YCenter)*(y-YCenter), 1/2.0);
			//��ȡ���ĽǶ�ֵ,��һ���޺͵ڶ�����������,3,4ͬ��
			double angle = (180*Math.asin((y-YCenter)/distance))/Math.PI;
			//��һ���޲���,���ֵڶ�����
			if(x-XCenter<0 && angle<90 && angle>0){
				angle = 180 - angle;
			}
			//���ֵ�������
			else if(x-XCenter<0 && angle>-90 && angle<0){
				angle = 180 - angle;
			}
			//���ֵ�������
			else if(x-XCenter>0 && angle>-90 && angle<0){
				angle = 360 + angle;
			}
			
			int flag = 0;
			//������ж�
			if(angle<listAngle.get(0).getStartAngle()) angle = angle + 360;
			//�ж�
			for(int i=0; i<listAngle.size(); i++){
				if(angle > listAngle.get(i).getStartAngle() && 
					angle< listAngle.get(i).getSweepAngle() + listAngle.get(i).getStartAngle()){
					flag = i;
				}
			}
			
			if(flag==0) return true;
			//��ת����
			Animation animation = (getRotateAnimation( 0, 270+360 - (listAngle.get(flag).getStartAngle() 
					+ listAngle.get(flag).getSweepAngle()/2f)));
			animation.setAnimationListener(new AnimationListenerMy(this, lineText, true, flag, colorsUse, list));
			this.startAnimation(animation);
		}
		//������м�����,����Ҫ��Ч�����Բ���
		else if((x-XCenter)*(x-XCenter) + (y-YCenter)*(y-YCenter) < centerRadius*0.5* centerRadius*0.5){
			
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				colorCenterCircle = 0x20000000;
				Pie.this.invalidate();
				//��ת����
				Animation animation = (getRotateAnimation( 0, 270+360 - (listAngle.get(listAngle.size()-1).getStartAngle() 
						+ listAngle.get(listAngle.size()-1).getSweepAngle()/2f)));
				animation.setAnimationListener(new AnimationListenerMy(this, lineText, true, listAngle.size()-1, colorsUse, list));
				this.startAnimation(animation);
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						colorCenterCircle = 0xe0ffffff;
						Pie.this.invalidate();    
					}
				}, 300);
			}
			
			if(event.getAction() == MotionEvent.ACTION_UP){
				colorCenterCircle = 0xe0ffffff;
				Pie.this.invalidate();                              
			}
			
		}
		
		return true;
	}
	
	public Animation getRotateAnimation(float startangle, float endAngle){
		RotateAnimation rotate = new RotateAnimation(startangle, endAngle, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(600);
		return rotate;
	}
	
}

