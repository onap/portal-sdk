/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ============LICENSE_END============================================
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.portalsdk.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PortalApiProperties.class})
public class CentralAccessConditionTest {

	@InjectMocks
	private CentralAccessCondition cntrlAccessCondition;
	
	@Test
	public void matchesFalseTest() {
		ConditionContext context = Mockito.mock(ConditionContext.class);
		AnnotatedTypeMetadata metadata = Mockito.mock(AnnotatedTypeMetadata.class);
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED)).thenReturn(null);
		boolean isRemote = cntrlAccessCondition.matches(context, metadata);
		Assert.assertFalse(isRemote);
	}
	
	@Test
	public void matchesTrueTest() {
		ConditionContext context = Mockito.mock(ConditionContext.class);
		AnnotatedTypeMetadata metadata = Mockito.mock(AnnotatedTypeMetadata.class);
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED)).thenReturn("remote");
		boolean isRemote = cntrlAccessCondition.matches(context, metadata);
		Assert.assertTrue(isRemote);
	}
}
