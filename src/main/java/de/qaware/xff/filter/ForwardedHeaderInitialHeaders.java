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
 package de.qaware.xff.filter;

import de.qaware.xff.util.ForwardedHeader;

import javax.servlet.FilterConfig;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ForwardedHeaderInitialHeaders {

    Map<String, String> initialHeaders = new HashMap<>();
    public boolean hasDefaults() {
        return initialHeaders.size() > 0;
    }

    public ForwardedHeaderInitialHeaders(FilterConfig config) {
        Enumeration<String> names = config.getInitParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (ForwardedHeader.isForwardedHeader(name)) {
                String headerValue = config.getInitParameter(name);
                initialHeaders.put(name, headerValue);
            }
        }
    }

    public Map<String, String> getDefaultHeaders() {
        return initialHeaders;
    }
}