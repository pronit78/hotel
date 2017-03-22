package com.tops.hotelmanager.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.tops.hotelmanager.exception.CustomException;

@Service
public class CommonUtil {
	private static Logger logger = Logger.getLogger(CommonUtil.class);

	private Properties properties = null;

	public CommonUtil() {
	}

	public CommonUtil(Properties properties) {
		this.properties = properties;
	}

	public static StringBuilder appendStringByComma(StringBuilder builder,
			String... strings) {
		for (String str : strings) {
			if (str != null && !str.trim().isEmpty()) {
				if (builder.length() == 0) {
					builder.append(str);
				} else {
					builder.append(",").append(str);
				}
			}
		}
		return builder;
	}

	public TreeMap<String, String> getPgResponse(HttpServletRequest request) {
		TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		Map<String, String[]> map = request.getParameterMap();
		for (String key : map.keySet()) {
			paramsMap.put(key, map.get(key)[0]);
		}
		return paramsMap;
	}

	public String handleCommonError(HttpServletRequest request, int status,
			String message) {
		request.setAttribute("errorMessage", message);
		return "common/servererror";
	}

	/**
	 * Method to ceil the digits upto 2 decimal points after truncating the
	 * digits after 5 decimal points
	 * 
	 * @param amount
	 *            double amount to be ceiled upto 2 digits
	 * @return resultant amount
	 */

	public int[] getIntArr(String data, String joinStr) {
		if (data != null) {
			String[] elems = data.split(joinStr);
			if (elems != null && elems.length > 0) {
				int[] values = new int[elems.length];
				for (int i = 0; i < elems.length; i++) {
					values[i] = Integer.parseInt(elems[i]);
				}
				return values;
			}
		}
		return null;
	}

	public void handleCommonException(Throwable throwable,
			CommonResponse commonResponse, Logger logger) {
		String message = null;
		if (throwable instanceof CustomException) {
			message = throwable.getMessage();
			logger.error(message);
		} else {
			message = "We have encountered an error, please try again later";
			logger.error(throwable);
		}
		commonResponse.setStatus(CommonResponse.STATUS_ERROR);
		commonResponse.setMessage(message);
	}

	public String encodeUtf8(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public boolean isProductionEnv() {
		return (System.getProperty(CommonConstant.ENVIRONMENT_MODE)
				.equals(CommonConstant.ENVIRONMENT_PROD));
	}

	public boolean isTestEnv() {
		return (System.getProperty(CommonConstant.ENVIRONMENT_MODE)
				.equals(CommonConstant.ENVIRONMENT_TEST));
	}

	public boolean isDevEnv() {
		return (System.getProperty(CommonConstant.ENVIRONMENT_MODE)
				.equals(CommonConstant.ENVIRONMENT_DEV));
	}

	public static DataSource getDataSource(AppDatabase appDatabase) {
		System.out.println("**********dataSource url: "
				+ appDatabase.getJdbcUrl());
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(CommonConstant.DB_MYSQL_DRIVER);
		dataSource.setUrl(appDatabase.getJdbcUrl());
		dataSource.setUsername(appDatabase.getDbUser());
		dataSource.setPassword(appDatabase.getDbMagicword());
		dataSource.setInitialSize(appDatabase.getMinCon());
		dataSource.setMaxIdle(appDatabase.getMaxCon());
		dataSource.setMaxWait(CommonConstant.DB_CONNECTION_TIMEOUT);
		dataSource.setMaxIdle(appDatabase.getMaxIdleCon());
		dataSource.setDefaultAutoCommit(true);
		dataSource.setPoolPreparedStatements(true);
		dataSource
				.setMaxOpenPreparedStatements(CommonConstant.DB_MAX_OPEN_PREPAREDSTATEMENT);
		dataSource.setTestOnBorrow(true);
		dataSource.setTestWhileIdle(true);
		dataSource.setValidationQuery("select 1");
		dataSource.setValidationQueryTimeout(60);
		return dataSource;
	}

	public static String getDbNameFromJdbcUrl(String url) {
		return url.substring(url.lastIndexOf("/") + 1);
	}

	public int getJsIndex() {
		return CommonConstant.JS_INDEX;
	}

	public String sha512(String input) throws NoSuchAlgorithmException {
		MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
		algorithm.reset();
		algorithm.update(input.getBytes());
		byte messageDigest[] = algorithm.digest();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < messageDigest.length; i++) {
			String hex = Integer.toHexString(0xFF & messageDigest[i]);
			if (hex.length() == 1) {
				sb.append("0");
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	public Connection getConnection() {
		try {
			Class.forName(this.properties.getProperty("DB_DRIVER"));
			Connection con = DriverManager.getConnection(
					this.properties.getProperty("DB_URL"),
					this.properties.getProperty("DB_USER"),
					this.properties.getProperty("DB_PASSWORD"));
			return con;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void closeConnection(Connection con) {
		try {
			con.close();
		} catch (SQLException e) {
		}
	}

	public String parserForSeoUrl(String content, Map<String, Object> map) {
		return parser(content, map, "", true, 0);
	}

	public String parser(String content, Map<String, Object> map, String prefix) {
		return parser(content, map, prefix, false, 0);
	}

	@SuppressWarnings("unchecked")
	public String parser(String content, Map<String, Object> map,
			String prefix, boolean seoUrl, int alertType) {
		if (content != null && map != null && !content.trim().isEmpty()) {
			for (String key : map.keySet()) {
				if (map.get(key) instanceof Map) {
					content = parser(content,
							(Map<String, Object>) map.get(key), prefix + key
									+ ".", seoUrl, alertType);
					continue;
				}
				String value = map.get(key) + "";
				if (map.get(key) == null) {
					value = "";
				}
				if (seoUrl) {
					value = replaceSpecialCharter(value);
				}
				content = content.replaceAll("\\{" + prefix + key + "\\}",
						value);
			}

		}
		return content;
	}

	public String replaceSpecialCharter(String Name) {
		return Name.replaceAll("\\/+", "").replaceAll("[^\\w]", " ")
				.replaceAll(" +", " ").trim().replaceAll("[^\\w]", "-")
				.toLowerCase();
	}

	public String getSessionId(HttpServletRequest request) {
		String sessionId = (String) request
				.getAttribute(CommonConstant.COOKIE_SESSION_ID);
		if (sessionId == null) {
			sessionId = request
					.getParameter(CommonConstant.PARAM_SJVTSESSIONID);
		}
		if (sessionId == null && request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(CommonConstant.COOKIE_SESSION_ID)
						&& cookie.getValue() != null) {
					sessionId = cookie.getValue();
					break;
				}
			}
		}
		return sessionId;
	}

	public void setCookie(String name, String data, int maxage,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			if (name != null && !name.isEmpty() && data != null) {
				Cookie cookie = new Cookie(name, data);
				cookie.setMaxAge((maxage > 0) ? maxage : Integer.MAX_VALUE);
				cookie.setPath("/");
				cookie.setHttpOnly(false);
				cookie.setSecure(false);
				if (!isDevEnv()) {
					String host = request.getServerName();
					int index = host.lastIndexOf(".");
					index = host.lastIndexOf(".", index - 1);
					if (index > 0) {
						host = host.substring(index);
					} else {
						host = "." + host;
					}
					cookie.setDomain(host);
				}
				response.addCookie(cookie);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public User getUser(HttpServletRequest request) {
	// return
	// (User)request.getSession(true).getAttribute(CommonConstant.SESSION_USER);
	// }

	public String getHostWithContext(HttpServletRequest request) {
		return getHostWithProtocol(request) + getAppContext(request);
	}

	public String getAppContext(HttpServletRequest request) {
		return request.getContextPath() + "/";
	}

	public String getHostWithProtocol(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		url = url.substring(0, url.indexOf("/", url.indexOf("//") + 2));
		return url;
	}

	public boolean isToday(Date date) {
		boolean isToday = false;
		try {
			Calendar todayCal = Calendar.getInstance();
			todayCal.setTime(new Date());
			int dateNum = todayCal.get(Calendar.DATE);
			int month = todayCal.get(Calendar.MONTH);
			int year = todayCal.get(Calendar.YEAR);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int dateNum1 = cal.get(Calendar.DATE);
			int month1 = cal.get(Calendar.MONTH);
			int year1 = cal.get(Calendar.YEAR);
			if (dateNum == dateNum1 && month == month1 && year == year1) {
				isToday = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return isToday;
	}

	public Date stringToDate(String date, String toPattern) {
		Date date1 = null;
		SimpleDateFormat sdf = new SimpleDateFormat(toPattern);
		try {
			date1 = sdf.parse(date);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return date1;
	}

	public String dateToString(Date date, String toPattern) {
		String dateStr = null;
		SimpleDateFormat sdf = new SimpleDateFormat(toPattern);
		try {
			dateStr = sdf.format(date);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dateStr;
	}

	public Date convertToPreMidNight(Date date) {
		Calendar calEnd = new GregorianCalendar();
		calEnd.setTime(date);
		calEnd.set(Calendar.HOUR_OF_DAY, 23);
		calEnd.set(Calendar.MINUTE, 59);
		calEnd.set(Calendar.SECOND, 59);
		calEnd.set(Calendar.MILLISECOND, 0);
		return calEnd.getTime();
	}

	public String validationCheck(BindingResult bindingResult) {
		String field = "The following field are required:";
		if (bindingResult.hasErrors()) {

			List<FieldError> errors = bindingResult.getFieldErrors();
			StringBuilder builder = new StringBuilder();
			for (FieldError fieldError : errors) {
				appendStringByComma(builder, fieldError.getField() + ":"
						+ fieldError.getDefaultMessage());

			}
			field = field + builder;

		}
		return field;
	}

}
