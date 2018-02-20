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
package org.onap.portalsdk.core.web.support;

import static org.junit.Assert.*;

import org.junit.Test;

public class MessagesListTest {

	public MessagesList  mockMessagesList (){
		MessagesList messagesList  = new MessagesList();
		
		messagesList.setExceptionMessages(null);
		messagesList.setIncludeCauseInCustomExceptions(false);
		messagesList.setSuccessMessageDisplayed(false);
		messagesList.setSuccessMessages(null);
		
		return messagesList;
	}
	
	@Test
	public void messagesListTest(){
		MessagesList messagesList1  = mockMessagesList();
		
		MessagesList messagesList  = new MessagesList();
		messagesList.setExceptionMessages(null);
		messagesList.setIncludeCauseInCustomExceptions(false);
		messagesList.setSuccessMessageDisplayed(false);
		messagesList.setSuccessMessages(null);
		
		assertEquals(messagesList.getExceptionMessages(), messagesList1.getExceptionMessages());
		assertEquals(messagesList.getSuccessMessages(), messagesList1.getSuccessMessages());
		assertEquals(messagesList.isIncludeCauseInCustomExceptions(), messagesList1.isIncludeCauseInCustomExceptions());
		assertEquals(messagesList.isSuccessMessageDisplayed(), messagesList1.isSuccessMessageDisplayed());
		
	}
}
