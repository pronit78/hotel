/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tops.hotelmanager.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.tops.hotelmanager.exception.CustomException;

/**
 *
 * @author Charlie
 */
public class CommonHttpClient {

	private static org.apache.log4j.Logger logger = Logger
			.getLogger(CommonHttpClient.class);

	public static final int TIME_OUT = 180000;
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String IS_REQUEST_FAILED = "Request Failed";

	public static String executeRequest(String url, Map<String, String> map,
			Map<String, String> header, int timeOut, boolean isGet) {
		if (isGet) {
			return executeGetRequest(url, map, header, timeOut);
		} else {
			return executePostRequest(url, map, header, timeOut);
		}
	}

	public static String executeRequest(String url, Map<String, String> map,
			int timeOut, boolean isGet) {
		return executeRequest(url, map, null, timeOut, isGet);
	}

	public static String executePostRequest(String url,
			Map<String, String> map, Map<String, String> header, int timeOut) {
		HttpClient client = new HttpClient();

		// HostConfiguration configuration = new HostConfiguration();
		// configuration.setProxy("localhost", 8888);
		// client.setHostConfiguration(configuration);

		client.getHttpConnectionManager().getParams().setSoTimeout(timeOut);
		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(timeOut);
		PostMethod post = new PostMethod(url);
		try {
			if (map != null && map.size() > 0) {
				for (Map.Entry<String, String> entry : map.entrySet()) {
					post.addParameter(entry.getKey(), entry.getValue());
				}
			}
			if (header != null && header.size() > 0) {
				for (Map.Entry<String, String> entry : header.entrySet()) {
					post.addRequestHeader(entry.getKey(), entry.getValue());
				}
			}
			int i = client.executeMethod(post);
			if (i != -1) {
				return post.getResponseBodyAsString();
			}
		} catch (Exception e) {
			logger.error("HttpClient post method error url: " + url
					+ ", Parameters: " + map, e);
		} finally {
			if (post != null) {
				post.releaseConnection();
			}
		}

		return "error~Request Failed";

	}

	public static String executeGetRequest(String url, Map<String, String> map,
			Map<String, String> header, int timeOut) {
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setSoTimeout(timeOut);
		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(timeOut);
		StringBuilder sb = new StringBuilder(url);
		GetMethod get = null;
		try {
			if (map != null && map.size() > 0) {
				sb.append("?");
				int i = 0;
				for (Map.Entry<String, String> entry : map.entrySet()) {
					if (i == 0) {
						sb.append(entry.getKey())
								.append("=")
								.append(URLEncoder.encode(entry.getValue(),
										"UTF-8"));
						i = 1;
					} else {
						sb.append("&")
								.append(entry.getKey())
								.append("=")
								.append(URLEncoder.encode(entry.getValue(),
										"UTF-8"));
					}
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Requested URL : "
						+ URLDecoder.decode(sb.toString(), "UTF-8"));
			}
			get = new GetMethod(sb.toString());
			if (header != null && header.size() > 0) {
				for (Map.Entry<String, String> entry : header.entrySet()) {
					get.addRequestHeader(entry.getKey(), entry.getValue());
				}
			}
			int i = client.executeMethod(get);
			if (i != -1) {
				String response = get.getResponseBodyAsString();
				/*
				 * if (logger.isDebugEnabled()) { logger.debug("Response : " +
				 * response); }
				 */
				return response;
			}
		} catch (Exception e) {
			logger.error("HttpClient get method error url: " + url
					+ ", Parameters: " + map, e);
		} finally {
			if (get != null) {
				get.releaseConnection();
			}
		}

		return "error~Request Failed";
	}

	public static String executeJSONPOSTRequestV1(String url,
			Map<String, Object> requestData, int timeOut) {
		return executePOSTRequestV1(url, requestData, null, CONTENT_TYPE_JSON,
				timeOut);
	}

	public static String executeJSONPOSTRequestV1(String url,
			Map<String, Object> requestData, Map<String, String> headerMap,
			int timeOut) {
		return executePOSTRequestV1(url, requestData, headerMap,
				CONTENT_TYPE_JSON, timeOut);
	}

	public static String executePOSTRequestV1(String url,
			Map<String, Object> requestData, Map<String, String> headerMap,
			String contentType, int timeOut) {
		return executePOSTRequestV1(url, requestData, headerMap, contentType,
				timeOut, 0);
	}

	private static String executePOSTRequestV1(String url,
			Map<String, Object> requestData, Map<String, String> headerMap,
			String contentType, int timeOut, int retry) {
		PostMethod post = new PostMethod(url);
		Gson gson = new Gson();
		String errorMsg = "error~Request Failed";
		try {
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams().setSoTimeout(timeOut);
			client.getHttpConnectionManager().getParams()
					.setConnectionTimeout(timeOut);
			if (requestData == null || requestData.isEmpty()) {
				throw new CustomException("Request data is null");
			}
			if (contentType != null
					&& contentType.equals(CommonHttpClient.CONTENT_TYPE_JSON)) {
				StringRequestEntity requestEntity = new StringRequestEntity(
						gson.toJson(requestData), contentType, "UTF-8");
				post.setRequestEntity(requestEntity);
			} else {
				// SET REQUEST PARAMETER
				for (Map.Entry<String, Object> entry : requestData.entrySet()) {
					post.addParameter(entry.getKey(),
							String.valueOf(entry.getValue()));
				}
			}
			// SET REQUEST HEADER
			if (headerMap != null) {
				for (Map.Entry<String, String> entry : headerMap.entrySet()) {
					post.addRequestHeader(entry.getKey(), entry.getValue());
				}
			}
			int status = client.executeMethod(post);
			// System.out.println("URL:" + url);
			// System.out.println("\n REQUEST HEADERS:");
			// Header[] requestHeaders = post.getRequestHeaders();
			// for (Header header : requestHeaders) {
			// System.out.println(header.getName() + "=" + header.getValue());
			// }
			// System.out.println("\n RESPONSE HEADERS:");
			// Header[] responseHeaders = post.getResponseHeaders();
			// for (Header header : responseHeaders) {
			// System.out.println(header.getName() + "=" + header.getValue());
			// }
			// System.out.println(post.getStatusText());
			// System.out.println(post.getStatusLine().getReasonPhrase());
			// System.out.println(post.getResponseBodyAsString());
			if (status == HttpStatus.SC_OK) {
				return post.getResponseBodyAsString();
			} else if (status == HttpStatus.SC_TEMPORARY_REDIRECT) {
				Header header = post.getResponseHeader("Location");
				if (retry != 1) {
					errorMsg = executePOSTRequestV1(header.getValue(),
							requestData, headerMap, contentType, timeOut,
							retry++);
				}
			} else {
				errorMsg += ",HttpStatus:" + status;
			}
		} catch (Exception ex) {
			logger.error("executePOSTRequestV1 url: " + url + ", Parameters: "
					+ requestData, ex);
			errorMsg = errorMsg + ":" + ex.getMessage();
		} finally {
			post.releaseConnection();
		}
		return errorMsg;
	}
}
