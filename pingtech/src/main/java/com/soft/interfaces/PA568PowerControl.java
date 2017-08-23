package com.soft.interfaces;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

public class PA568PowerControl {
	
	  public static final byte[] DEVICE_TYPE;
	  public static final byte[] ENERGY_TYPE;
	  public static final byte[] POWER_OFF;
	  public static final byte[] POWER_ON;
	  public static final byte[] POWER_TYPE;
	  public static final byte[] SWITCH_POWER_OFF;
	  public static final byte[] SWITCH_POWER_ON;
	  
	  static
	  {
		    byte[] arrayOfByte1 = new byte[1];
		    arrayOfByte1[0] = 50;
		    POWER_TYPE = arrayOfByte1;
		    byte[] arrayOfByte2 = new byte[1];
		    arrayOfByte2[0] = 50;
		    DEVICE_TYPE = arrayOfByte2;
		    byte[] arrayOfByte3 = new byte[1];
		    arrayOfByte3[0] = 49;
		    ENERGY_TYPE = arrayOfByte3;
		    byte[] arrayOfByte4 = new byte[1];
		    arrayOfByte4[0] = 49;
		    POWER_ON = arrayOfByte4;
		    byte[] arrayOfByte5 = new byte[1];
		    arrayOfByte5[0] = 48;
		    POWER_OFF = arrayOfByte5;
		    byte[] arrayOfByte6 = new byte[5];
		    arrayOfByte6[0] = 49;
		    arrayOfByte6[1] = 45;
		    arrayOfByte6[2] = 49;
		    arrayOfByte6[3] = 45;
		    arrayOfByte6[4] = 51;
		    SWITCH_POWER_ON = arrayOfByte6;
		    byte[] arrayOfByte7 = new byte[5];
		    arrayOfByte7[0] = 49;
		    arrayOfByte7[1] = 45;
		    arrayOfByte7[2] = 48;
		    arrayOfByte7[3] = 45;
		    arrayOfByte7[4] = 51;
		    SWITCH_POWER_OFF = arrayOfByte7;
	  }
	  
	  public  static void powerOn(){
		  
		  FileOutputStream wrt=null;
		try {
			 wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_power");
		     Log.d("IDRead", "write switch power on");
		      wrt.write(SWITCH_POWER_ON);
		      wrt.close();
		      wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_state");
		      Log.d("IDRead", "write device type---UART");
		      wrt.write(DEVICE_TYPE);
		      wrt.flush();
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(wrt!=null){
				  try {
					wrt.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

	  }
	  
	  /**
	   * 
	   */
	  public static void powerOff(){
		  FileOutputStream wrt=null;
			try {
				 wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_power");
			     Log.d("IDRead", "write switch power off");
			      wrt.write(SWITCH_POWER_OFF);
			      wrt.flush();
			      wrt.close();
			      wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_state");
			      Log.d("IDRead", "write device type---UART");
			      wrt.write(DEVICE_TYPE);
			      wrt.flush();
			    
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(wrt!=null){
					  try {
						wrt.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
	  }

}
