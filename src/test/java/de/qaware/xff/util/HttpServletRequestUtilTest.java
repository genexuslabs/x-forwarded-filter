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

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Arjen Poutsma
 */
public class HttpServletRequestUtilTest {


    private MockHttpServletRequest mockRequest;


    @Before
    public void create() {
        mockRequest = new MockHttpServletRequest();
    }

    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InstantiationException {
        Constructor<HttpServletRequestUtil> constructor = HttpServletRequestUtil.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }

    @Test
    public void getURI() throws URISyntaxException {
        URI uri = new URI("http://example.com/path?query");
        mockRequest.setServerName(uri.getHost());
        mockRequest.setServerPort(uri.getPort());
        mockRequest.setRequestURI(uri.getPath());
        mockRequest.setQueryString(uri.getQuery());
        assertEquals("Invalid uri", uri, HttpServletRequestUtil.getURI(mockRequest));
    }

    @Test  // SPR-13876
    public void getUriWithEncoding() throws URISyntaxException {
        URI uri = new URI("https://example.com/%E4%B8%AD%E6%96%87" +
                "?redirect=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework");
        mockRequest.setScheme(uri.getScheme());
        mockRequest.setServerName(uri.getHost());
        mockRequest.setServerPort(uri.getPort());
        mockRequest.setRequestURI(uri.getRawPath());
        mockRequest.setQueryString(uri.getRawQuery());
        assertEquals("Invalid uri", uri, HttpServletRequestUtil.getURI(mockRequest));
    }

    @Test
    public void getHeaders() {
        String headerName = "MyHeader";
        String headerValue1 = "value1";
        String headerValue2 = "value2";
        mockRequest.addHeader(headerName, headerValue1);
        mockRequest.addHeader(headerName, headerValue2);
        mockRequest.setContentType("text/plain");
        mockRequest.setCharacterEncoding("UTF-8");

        HttpHeaders headers = HttpServletRequestUtil.getHeaders(mockRequest);
        assertNotNull("No HttpHeaders returned", headers);
        assertTrue("Invalid headers returned", headers.containsKey(headerName));
        List<String> headerValues = headers.get(headerName);
        assertEquals("Invalid header values returned", 2, headerValues.size());
        assertTrue("Invalid header values returned", headerValues.contains(headerValue1));
        assertTrue("Invalid header values returned", headerValues.contains(headerValue2));
    }

    @Test
    public void getHeadersWithEmptyContentTypeAndEncoding() {
        String headerName = "MyHeader";
        String headerValue1 = "value1";
        String headerValue2 = "value2";
        mockRequest.addHeader(headerName, headerValue1);
        mockRequest.addHeader(headerName, headerValue2);
        mockRequest.setContentType("");
        mockRequest.setCharacterEncoding("");

        HttpHeaders headers = HttpServletRequestUtil.getHeaders(mockRequest);
        assertNotNull("No HttpHeaders returned", headers);
        assertTrue("Invalid headers returned", headers.containsKey(headerName));
        List<String> headerValues = headers.get(headerName);
        assertEquals("Invalid header values returned", 2, headerValues.size());
        assertTrue("Invalid header values returned", headerValues.contains(headerValue1));

        mockRequest.setContent(new byte [2]);
        assertEquals(2,HttpServletRequestUtil.getHeaders(mockRequest).getContentLength());

    }

    @Test
    public void getFirstToken(){
        assertNull(HttpServletRequestUtil.getFirstValueToken(null,","));
        assertEquals("a",HttpServletRequestUtil.getFirstValueToken("a,b",","));
        assertEquals("a,b",HttpServletRequestUtil.getFirstValueToken("a,b","wrongDelim"));
    }
}
