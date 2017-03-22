package com.tops.hotelmanager.util;

public class CommonConstant {
	public static final String SPRING_PACKAGE_SCAN = "com.tops";

	public static final int SESSION_EXPIRATION_SECONDS = 3600;
	public static final String SESSION_USER = "_user";
	public static final String CACHE_PRIFIX = "TOPS";

	public static final String ENVIRONMENT_DEV = "DEV";
	public static final String ENVIRONMENT_TEST = "TEST";
	public static final String ENVIRONMENT_PROD = "PROD";
	public static final String ENVIRONMENT_MODE = "EnvironmentMode";

	public static final String SYSTEM_DOMAIN = "hotelmanager.com";

	public static final String NODENAME = "NodeName";
	public static final int DB_MAX_OPEN_PREPAREDSTATEMENT = 200;
	public static final int DB_CONNECTION_TIMEOUT = 60000;
	public static final String DB_MYSQL_DRIVER = "com.mysql.jdbc.Driver";

	public static final String APPLICATION_KEY_COMMONUTIL = "commonUtil";

	public static final String APPLICATION_KEY_JSPUTIL = "jspUtil";

	public static int JS_INDEX = 30;

	public static final String COOKIE_SESSION_ID = "TopsSessionId";
	public static final String PARAM_PREFIXSJVT = "_TOPS";
	public static final String PARAM_SJVTSESSIONID = PARAM_PREFIXSJVT
			+ "SessionId";

	public static final String COOKIE_CITY = "TopsCity";
	public static final String COOKIE_DEVICE_ID = "CookieDeviceId";
	public static final String COOKIE_FLUSH_USER_DATA = "FlushUserData";
	public static final String COOKIE_DATA = "TopsData";

	public static final String HEADER_X_PROXY_SCHEME = "x_proxy_scheme";

	public static final String DASHBOARD = "dashboard";
	public static final String LOGIN_PAGE = "login";
	public static final String TOKEN = "_token";

}
