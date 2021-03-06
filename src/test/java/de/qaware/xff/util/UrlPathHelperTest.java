/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.qaware.xff.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link UrlPathHelper}.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Costin Leau
 */
public class UrlPathHelperTest {

	private static final String WEBSPHERE_URI_ATTRIBUTE = "com.ibm.websphere.servlet.uri_non_decoded";

	private final UrlPathHelper helper = new UrlPathHelper();

	private final MockHttpServletRequest request = new MockHttpServletRequest();


	@Test
	public void getPathWithinApplication() {
		request.setContextPath("/petclinic");
		request.setRequestURI("/petclinic/welcome.html");

		assertEquals("Incorrect path returned", "/welcome.html", helper.getPathWithinApplication(request));
	}

	@Test
	public void getPathWithinApplicationForRootWithNoLeadingSlash() {
		request.setContextPath("/petclinic");
		request.setRequestURI("/petclinic");

		assertEquals("Incorrect root path returned", "/", helper.getPathWithinApplication(request));
	}

	@Test
	public void getPathWithinApplicationForSlashContextPath() {
		request.setContextPath("/");
		request.setRequestURI("/welcome.html");

		assertEquals("Incorrect path returned", "/welcome.html", helper.getPathWithinApplication(request));
	}

	@Test
	public void getPathWithinApplicationSemicolonContent() {
		request.setContextPath("/asdf");
		request.setRequestURI("/welcome.html?foo=bar&bar=foo;jsessionid=asdf");
		assertEquals("Incorrect path returned", "/welcome.html?foo=bar&bar=foo", helper.getPathWithinApplication(request));
	}

	@Test
	public void getPathWithinApplicationSemicolonContent2() {
		request.setContextPath("/asdf;jsessionid=asdf");
		request.setRequestURI("/welcome.html?foo=bar&bar=foo;jsessionid=asdf");
		assertEquals("Incorrect path returned", "/welcome.html?foo=bar&bar=foo", helper.getPathWithinApplication(request));
	}

	@Test
	public void getRequestUri() {
		request.setRequestURI("/welcome.html");
		assertEquals("Incorrect path returned", "/welcome.html", helper.getRequestUri(request));

		request.setRequestURI("/foo%20bar");
		assertEquals("Incorrect path returned", "/foo bar", helper.getRequestUri(request));

		request.setRequestURI("/foo+bar");

		assertEquals("Incorrect path returned", "/foo+bar", helper.getRequestUri(request));


	}

	@Test
	public void getRequestRemoveSemicolonContent() throws UnsupportedEncodingException {
		helper.setRemoveSemicolonContent(true);

		request.setRequestURI("/foo;f=F;o=O;o=O/bar;b=B;a=A;r=R");
		assertEquals("/foo/bar", helper.getRequestUri(request));

		// SPR-13455

		request.setServletPath("/foo/1");
		request.setRequestURI("/foo/;test/1");

		assertEquals("/foo/1", helper.getRequestUri(request));
	}

	@Test
	public void getRequestKeepSemicolonContent() throws UnsupportedEncodingException {
		helper.setRemoveSemicolonContent(false);

		request.setRequestURI("/foo;a=b;c=d");
		assertEquals("/foo;a=b;c=d", helper.getRequestUri(request));

		request.setRequestURI("/foo;jsessionid=c0o7fszeb1");
		assertEquals("jsessionid should always be removed", "/foo", helper.getRequestUri(request));

		request.setRequestURI("/foo;a=b;jsessionid=c0o7fszeb1;c=d");
		assertEquals("jsessionid should always be removed", "/foo;a=b;c=d", helper.getRequestUri(request));

		// SPR-10398

		request.setRequestURI("/foo;a=b;JSESSIONID=c0o7fszeb1;c=d");
		assertEquals("JSESSIONID should always be removed", "/foo;a=b;c=d", helper.getRequestUri(request));
	}

	@Test
	public void setDefaultEncoding() {
		helper.setDefaultEncoding("UTF-8");
		assertEquals("UTF-8", helper.getDefaultEncoding());
		helper.setDefaultEncoding("ISO-8859-1");
		assertEquals("ISO-8859-1", helper.getDefaultEncoding());
	}

	@Test
	public void getContextPath() {
		request.setContextPath("/asdf");
		request.setRequestURI("/welcome.html?foo=bar&bar=foo;jsessionid=asdf");
		assertEquals("/asdf", helper.getContextPath(request));
	}

	@Test
	public void badRequestEncodingWithCorrectDefault_silentFallback() throws UnsupportedEncodingException {
		helper.setDefaultEncoding("UTF-8");
		String uri = "/welcome.html?foo=" + URLEncoder.encode("äöü", "UTF-8") + ";jsessionid=asdf";
		request.setContextPath("/asdf");
		request.setRequestURI(uri);
		request.setCharacterEncoding("fooBarCoding");

		assertEquals("/welcome.html?foo=äöü", helper.getRequestUri(request));
	}

	@Test
	public void badRequestEncodingWithWrongDefault_silentFallback() throws UnsupportedEncodingException {
		helper.setDefaultEncoding("ISO-8859-1");
		String uri = "/welcome.html?foo=" + URLEncoder.encode("äöü", "UTF-8") + ";jsessionid=asdf";
		request.setContextPath("/asdf");
		request.setRequestURI(uri);
		request.setCharacterEncoding("fooBarCoding");

		//BROKEN ENCODING IS INTENTIONAL
		assertEquals("/welcome.html?foo=Ã¤Ã¶Ã¼", helper.getRequestUri(request));
	}


	@Test
	public void noRequestEncodingAndWrongDefault() throws UnsupportedEncodingException {
		helper.setDefaultEncoding("fooBarCoding");
		String uri = "/welcome.html?foo=" + URLEncoder.encode("äöü", "UTF-8") + ";jsessionid=asdf";
		request.setContextPath("/asdf");
		request.setRequestURI(uri);
		request.setCharacterEncoding("fooBarCoding");

		Assertions.assertThatThrownBy(() -> helper.getRequestUri(request))//
				.isInstanceOf(AssertionError.class)//
				.hasCauseInstanceOf(UnsupportedEncodingException.class);
	}

}