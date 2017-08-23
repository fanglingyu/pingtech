package com.soft.interfaces;

public class BarcodeEngine {
	
	public native int OpenPort(String device, int rx, String port);
	public native int ClosePort();
	public native int OpenPort2(String device, int rx, String port);
	public native int ClosePort2();
	public native int OpenPort4(String port);//x710机型打开串口
	public native int ClosePort4();//x710机型关闭串口
	public native int ScanBegin(int mode);//mode:0为一次性扫描，1为自动扫描，2为连续扫描
	public native int ScanEnd();
	public native int ScanBegin2(byte[] b);
	public native int ScanEnd2();
	public native void ReceiveData(byte[] b);
	public native int SetScanMode();
	public native int SetScanMode2();
	public native int SetScanMode3();

	static {
		System.loadLibrary("barcode");
	}
}