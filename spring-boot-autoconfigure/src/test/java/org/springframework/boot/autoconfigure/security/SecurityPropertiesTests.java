/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.security;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SecurityProperties}.
 *
 * @author Dave Syer
 */
public class SecurityPropertiesTests {

	private SecurityProperties security = new SecurityProperties();

	@Test
	public void testBindingIgnoredSingleValued() {
		bind("security.ignored", "/css/**");
		assertThat(this.security.getIgnored()).hasSize(1);
	}

	@Test
	public void testBindingIgnoredEmpty() {
		bind("security.ignored", "");
		assertThat(this.security.getIgnored()).isEmpty();
	}

	@Test
	public void testBindingIgnoredDisable() {
		bind("security.ignored", "none");
		assertThat(this.security.getIgnored()).hasSize(1);
	}

	@Test
	public void testBindingIgnoredMultiValued() {
		bind("security.ignored", "/css/**,/images/**");
		assertThat(this.security.getIgnored()).hasSize(2);
	}

	@Test
	public void testBindingIgnoredMultiValuedList() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("security.ignored[0]", "/css/**");
		map.put("security.ignored[1]", "/foo/**");
		MapConfigurationPropertySource source = new MapConfigurationPropertySource(map);
		bind(source);
		assertThat(this.security.getIgnored()).hasSize(2);
		assertThat(this.security.getIgnored().contains("/foo/**")).isTrue();
	}

	@Test
	public void testDefaultPasswordAutogeneratedIfUnresolvedPlaceholder() {
		bind("security.user.password", "${ADMIN_PASSWORD}");
		assertThat(this.security.getUser().isDefaultPassword()).isTrue();
	}

	@Test
	public void testDefaultPasswordAutogeneratedIfEmpty() {
		bind("security.user.password", "");
		assertThat(this.security.getUser().isDefaultPassword()).isTrue();
	}

	@Test
	public void testRoles() {
		bind("security.user.role", "USER,ADMIN");
		assertThat(this.security.getUser().getRole().toString())
				.isEqualTo("[USER, ADMIN]");
	}

	@Test
	public void testRole() {
		bind("security.user.role", "ADMIN");
		assertThat(this.security.getUser().getRole().toString()).isEqualTo("[ADMIN]");
	}

	private void bind(String name, String value) {
		bind(new MapConfigurationPropertySource(Collections.singletonMap(name, value)));
	}

	private void bind(ConfigurationPropertySource source) {
		new Binder(source).bind("security", Bindable.ofInstance(this.security));
	}

}
