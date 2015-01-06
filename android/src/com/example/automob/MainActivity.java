package com.example.automob;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

public class MainActivity extends Activity {

	private UsbManager manager; 
    private UsbSerialDriver driver;
    private static final String Tag = "Serial";
    private TextView v1;
    final static int loopCnt = 2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        v1= (TextView)findViewById(R.id.textView1);
    }
    
    
    public void checkConnectivity()
    {
	   try {
		   byte buffer[] = new byte[16];
		   buffer = "A".getBytes();
		   int numBytesSend = driver.write(buffer, 1);
     
		   v1.setText("Succeed!");   
		   
	   } 
	   catch (IOException e) {
    	 v1.setText("Error Occured!");
	   }
    }
    
    public void doAction(View view)
    {
    	if(connect()==-1)
    		return;
    	
    	switch(view.getId())
    	{
    	case R.id.courseA :
    		courseA();
    		break;
    	case  R.id.testVal :
    		testVal();
    		break;
    	case R.id.stop:
    		stop();
    		break;
    	case R.id.connectivity:
    		checkConnectivity();
    	case R.id.doSpeed:
    		doSpeed();
    	default:
    		break;
    	}
    	disconnect();
    }
    
    public void doSpeed()
    {
    	EditText et = (EditText) findViewById(R.id.speed);
    	CheckBox front, back, left, right, toque;
    	
    	front = (CheckBox)findViewById(R.id.front);
    	back = (CheckBox)findViewById(R.id.back);
    	left = (CheckBox)findViewById(R.id.left);
    	right = (CheckBox)findViewById(R.id.right);
    	toque = (CheckBox)findViewById(R.id.toque);
    	
    	
    	int speed;
    	
    	String value = et.getText().toString();
    	try{
    	speed = (Integer.parseInt(value))&0x7;
    	}
    	catch(NumberFormatException e)
    	{
    		v1.setText("Cannot recognize speed");
    		return;
    	}
    	
    	EditText tt = (EditText) findViewById(R.id.time);
    	String timeString = tt.getText().toString();
    	float time;
    	try{
    		time = (Float.parseFloat(timeString));
    	}
    	catch(NumberFormatException e)
    	{
    		v1.setText("Cannot recognize time");
    		return;
    	}
    	
    	int a;
    	
    	
    	a = (front.isChecked() ? 1 : 0);
    	a = a << 1;
    	a += (back.isChecked() ? 1 : 0);
    	a = a << 1;
    	a += (left.isChecked() ? 1 : 0);
    	a = a << 1;
    	a += (right.isChecked() ? 1 : 0);
    	a = a << 1;
    	a += (toque.isChecked() ? 1 : 0);
    	a = a << 3;
    	a += speed;
    	
    	byte b = (byte)(a & 0xff);
    	
    	Instruction inst = new Instruction(b);
    	execute(inst);
    	SystemClock.sleep((long)(time*1000));
        Instruction inst2 = new Instruction(0,0,0,0,0,0);
        execute(inst2);
		
    	v1.setText("Test End: "+b); 
    }
    
    public int connect()
    {
    	
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        driver = UsbSerialProber.acquire(manager);
        
        if(driver==null)
        {
        	v1.setText("Cannot find Device");
        	return -1;
        }
        
        try{
        	driver.open();
        	driver.setBaudRate(9600);
        }
     
        catch(IOException e)
        {
        	Log.d(Tag, "Setting Failed");
        	v1.setText("Serial Configuration Failed");
        }
        
        return 0;
    }
    
   @Override
   public void onDestroy()
   {
    	super.onDestroy();
   }
   
   public void disconnect()
   {
	   try{
   		driver.close();
   		}
   		catch(IOException e){}
   }
    
    private void courseA()
    {
    	int i;
    	
    	
    	// first
    	for(i=0; i<loopCnt; i++)
    	{
	        Instruction inst = new Instruction(1,0,0,1,1,7);
	        execute(inst);
	        SystemClock.sleep(2500);
	        
	        Instruction inst2 = new Instruction(1,0,1,0,1,7);
	        execute(inst2);
	        SystemClock.sleep(2500);
	        
    	}
    	Instruction inst3 = new Instruction(0,0,0,0,0,0);
        execute(inst3);
        SystemClock.sleep(2000);
        
        for(i=0; i<loopCnt; i++)
        {
	        //second
	        Instruction inst4 = new Instruction(0,1,1,0,1,7);
	        execute(inst4);
	        SystemClock.sleep(2500);
	        
	        Instruction inst5 = new Instruction(0,1,0,1,1,7);
	        execute(inst5);
	        SystemClock.sleep(2500);
	        
        }

        Instruction inst6 = new Instruction(0,0,0,0,0,0);
        execute(inst6);
        SystemClock.sleep(2000);
		
    	v1.setText("CoursA End: Loop "+loopCnt);
    }
    
    
    private void testVal()
    {
    	EditText et = (EditText) findViewById(R.id.value);
    	String value = et.getText().toString();
    	
    	int a;
    	
    	try{
    	a = Integer.parseInt(value, 2);}
    	catch(NumberFormatException e)
    	{
    		v1.setText("Cannot recognize value");
    		return;
    	}
    	
    	byte b = (byte)(a & 0xff);
    	
    	Instruction inst = new Instruction(b);
    	execute(inst);
		
    	v1.setText("Test End: "+b);
    }
    
    
    public void execute(Instruction inst)
    {
    	if(driver!=null)
    	{
    	
    	byte[] tmp = {inst.get_inst()};
        try{
        	int numBytesSend = driver.write(tmp, 1);
        }
        catch(IOException e)
        {
        	Log.d("Serial", "Write Fail");
        }
    	}
        
    }
    
    public void stop()
    {
    	Instruction inst = new Instruction();
    	execute(inst);
    }
}
