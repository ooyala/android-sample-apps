package com.ooyala.sample;

public class Data {

	private final String embedCode;
	private final String pcode;
	private final String domain;

	private boolean autoPaused = true;
	private int playedHeadTime;

	public Data(String embedCode, String pcode, String domain) {
		this.embedCode = embedCode;
		this.pcode = pcode;
		this.domain = domain;
	}

	public Data(Data data) {
		this.embedCode = data.embedCode;
		this.pcode = data.pcode;
		this.domain = data.domain;
	}

	/**
	 * Get the embed code for this sample
	 *
	 * @return the embed code
	 */
	public String getEmbedCode() {
		return this.embedCode;
	}

	/**
	 * Get the pcode for this sample
	 *
	 * @return the pcode
	 */
	public String getPcode() {
		return pcode;
	}

	/**
	 * Get the domain for this sample
	 *
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	public int getPlayedHeadTime() {
		return playedHeadTime;
	}

	public void setPlayedHeadTime(int playedHeadTime) {
		this.playedHeadTime = playedHeadTime;
	}

	public void setAutoPaused(boolean autoPaused) {
		this.autoPaused = autoPaused;
	}

	public boolean isAutoPaused() {
		return autoPaused;
	}
}
