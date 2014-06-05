package uk.ac.aber.dcs.saw.mfc1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class Compass extends View {

	private float heading = 300.0f;
	private float width = 300.0f;
	private float height = 300.0f;
	private boolean upstairs = false;
	private boolean downstairs = false;
	
	
	public Compass(Context context) {
		super(context);
	}
	
	public Compass(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}
	
	 public Compass(Context context, AttributeSet attrs, int defStyle) {
		  super(context, attrs, defStyle);
	 }
	
	public void setHeading(float heading) {
		this.upstairs = false;
		this.downstairs = false;
		this.heading = -heading;
		invalidate();
	}
	
	public void setUpstairsDownstairs(boolean upstairs, boolean downstairs) {
		
		this.upstairs = upstairs;
		this.downstairs = downstairs;
		
	}
	
	
	protected void onDraw(Canvas canvas) {

		if (downstairs) {
			
			 Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.downstairs);
			 canvas.drawBitmap(bitmap, 0, 0, new Paint());
			 
			
		} else if (upstairs) {
			
			 Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.upstairs);
			 canvas.drawBitmap(bitmap, 0, 0, new Paint());
			
		} else {
		
		float radius = 0.0f;
		
			if (this.width  > this.height) radius  = this.height;
			if (this.height >= this.width) radius  = this.width;
			
			Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			circlePaint.setStyle(Paint.Style.STROKE);
			circlePaint.setStrokeWidth(3);
			circlePaint.setColor(Color.BLACK);
			
			Paint arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			arrowPaint.setStyle(Paint.Style.STROKE);
			arrowPaint.setStrokeWidth(5);
			arrowPaint.setColor(Color.RED);	
		
			canvas.drawCircle(this.width, this.height, (int)radius, circlePaint);
			
			canvas.drawLine(this.width, this.height, 
						(float)(this.width + radius*Math.sin(this.heading)),
						(float)(this.width + radius*Math.cos(this.heading)), 
																arrowPaint);
			
		}
	}

}