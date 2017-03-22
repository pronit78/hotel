package com.tops.hotelmanager.util;

public class AppDatabase {

	private int id;

	private String jdbcUrl;

	private String dbUser;

	private String dbMagicword;

	private int minCon;

	private int maxCon;

	private int maxIdleCon;

	private int appId;

	public void setId(int id) {
		this.id = id;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbMagicword() {
		return dbMagicword;
	}

	public void setDbMagicword(String dbMagicword) {
		this.dbMagicword = dbMagicword;
	}

	public int getMinCon() {
		return minCon;
	}

	public void setMinCon(int minCon) {
		this.minCon = minCon;
	}

	public int getMaxCon() {
		return maxCon;
	}

	public void setMaxCon(int maxCon) {
		this.maxCon = maxCon;
	}

	public int getMaxIdleCon() {
		return maxIdleCon;
	}

	public void setMaxIdleCon(int maxIdleCon) {
		this.maxIdleCon = maxIdleCon;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return getDbUser();
	}

	public String getDescription() {
		return getJdbcUrl();
	}
}
