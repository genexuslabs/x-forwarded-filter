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
package de.qaware.xff.util.uri;

import java.util.Arrays;
import java.util.Iterator;

/**
 * URI template variables backed by a variable argument array.
 */
final class VarArgsTemplateVariables extends UriTemplateVariables {

	private final Iterator<Object> valueIterator;

	VarArgsTemplateVariables(Object... uriVariableValues) {
		this.valueIterator = Arrays.asList(uriVariableValues).iterator();
	}

	@Override
	/*@Nullable*/
	public Object getValue(/*@Nullable*/ String name) {
		if (!this.valueIterator.hasNext()) {
			throw new IllegalArgumentException("Not enough variable values available to expand '" + name + "'");
		}
		return this.valueIterator.next();
	}
}
