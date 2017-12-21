package de.qaware.web.util;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 *
 */
public class HttpServletRequestUtil {

	private HttpServletRequestUtil() {
		//utility class
	}

	/**
	 * Reconstructs full url: e.g. http://server:port/foo/bar;xxx=yyy?param1=value1&param2=value2
	 * <pre>
	 * request.getRequestURL() = ttp://server:port/foo/bar;xxx=yyy
	 * request.getQueryString() = param1=value1&param2=value2
	 * request.getRequestURL() + "?" + request.getQueryString();
	 * </pre>
	 * <p>
	 * Difference of this method to {@link HttpServletRequest#getRequestURI()} <br/>
	 * {@link HttpServletRequest#getRequestURI()} returns: /foo/bar;xxx=yyy?param1=value1&param2=value2
	 *
	 * @param request {@see HttpServletRequest}
	 * @return request.getRequestURL() + "?" + request.getQueryString();
	 */
	public static URI getURI(HttpServletRequest request) {
		try {
			StringBuffer url = request.getRequestURL();
			String query = request.getQueryString();
			if (!isBlank(query)) {
				url.append('?').append(query);
			}
			return new URI(url.toString());
		} catch (URISyntaxException ex) {
			throw new IllegalStateException("Could not get HttpServletRequest URI: " + ex.getMessage(), ex);
		}
	}


	public static HttpHeaders getHeaders(HttpServletRequest servletRequest) {
		HttpHeaders headers = new HttpHeaders();
		setHeaderNames(headers, servletRequest);
		setContentLength(headers, servletRequest);
		return headers;
	}

	private static void setHeaderNames(HttpHeaders headers, HttpServletRequest servletRequest) {
		for (Enumeration<?> headerNames = servletRequest.getHeaderNames(); headerNames.hasMoreElements(); ) {
			String headerName = (String) headerNames.nextElement();
			for (Enumeration<?> headerValues = servletRequest.getHeaders(headerName);
			     headerValues.hasMoreElements(); ) {
				String headerValue = (String) headerValues.nextElement();
				headers.add(headerName, headerValue);
			}
		}
	}

	private static void setContentLength(HttpHeaders headers, HttpServletRequest servletRequest) {
		if (headers.getContentLength() < 0) {
			int requestContentLength = servletRequest.getContentLength();
			if (requestContentLength != -1) {
				headers.setContentLength(requestContentLength);
			}
		}
	}


}