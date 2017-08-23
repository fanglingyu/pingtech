package com.pingtech.hgqw.base.entity;

public class SystemSettingInfo {
	private String serverKadm = null;
	private String serverHost = null;
	private String serverPort = null;
	private boolean webServiceConnect;
	private String webServiceNamespace = null;
	private String webServiceUserName = null;
	private String webServicePassword = null;
	private String webServiceCode = null;
	private String webServiceWSDLUrl = null;
	private String webServiceArg1 = null;
	private String webServiceArg2 = null;
	private String webServiceArg3 = null;
	private String webServiceArg4 = null;
	private String webServiceArg5 = null;
	private String gPSTimer = null;
	private String pdaCode = null;

	public boolean isWebServiceConnect() {
		return webServiceConnect;
	}

	public void setWebServiceConnect(boolean webServiceConnect) {
		this.webServiceConnect = webServiceConnect;
	}

	public String getServerKadm() {
		return serverKadm;
	}

	public void setServerKadm(String serverKadm) {
		this.serverKadm = serverKadm;
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getWebServiceNamespace() {
		return webServiceNamespace;
	}

	public void setWebServiceNamespace(String webServiceNamespace) {
		this.webServiceNamespace = webServiceNamespace;
	}

	public String getWebServiceUserName() {
		return webServiceUserName;
	}

	public void setWebServiceUserName(String webServiceUserName) {
		this.webServiceUserName = webServiceUserName;
	}

	public String getWebServicePassword() {
		return webServicePassword;
	}

	public void setWebServicePassword(String webServicePassword) {
		this.webServicePassword = webServicePassword;
	}

	public String getWebServiceCode() {
		return webServiceCode;
	}

	public void setWebServiceCode(String webServiceCode) {
		this.webServiceCode = webServiceCode;
	}

	public String getWebServiceWSDLUrl() {
		return webServiceWSDLUrl;
	}

	public void setWebServiceWSDLUrl(String webServiceWSDLUrl) {
		this.webServiceWSDLUrl = webServiceWSDLUrl;
	}

	public String getWebServiceArg1() {
		return webServiceArg1;
	}

	public void setWebServiceArg1(String webServiceArg1) {
		this.webServiceArg1 = webServiceArg1;
	}

	public String getWebServiceArg2() {
		return webServiceArg2;
	}

	public void setWebServiceArg2(String webServiceArg2) {
		this.webServiceArg2 = webServiceArg2;
	}

	public String getWebServiceArg3() {
		return webServiceArg3;
	}

	public void setWebServiceArg3(String webServiceArg3) {
		this.webServiceArg3 = webServiceArg3;
	}

	public String getWebServiceArg4() {
		return webServiceArg4;
	}

	public void setWebServiceArg4(String webServiceArg4) {
		this.webServiceArg4 = webServiceArg4;
	}

	public String getWebServiceArg5() {
		return webServiceArg5;
	}

	public void setWebServiceArg5(String webServiceArg5) {
		this.webServiceArg5 = webServiceArg5;
	}

	public String getgPSTimer() {
		return gPSTimer;
	}

	public void setgPSTimer(String gPSTimer) {
		this.gPSTimer = gPSTimer;
	}

	public String getPdaCode() {
		return pdaCode;
	}

	public void setPdaCode(String pdaCode) {
		this.pdaCode = pdaCode;
	}
}
