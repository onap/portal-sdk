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
package org.onap.portalsdk.core.onboarding.rest;

import java.io.IOException;

/**
 * Provides a convenience method for fetching the favorites for a user from the
 * ECOMP Portal.
 */
public class FavoritesClient {

	/**
	 * Fetches the favorites data from portal
	 * 
	 * @param userId
	 *            userId value that it should be using to fetch the
	 *            data
	 * @param appName
	 *            Application name for logging etc.
	 * @param requestId
	 *            128-bit UUID value to uniquely identify the transaction; if null, a new one is generated.
	 * @param appUserName
	 *            REST API user-name
	 * @param appPassword
	 *            REST API decrypted password
	 * @return JSON with favorites
	 * @throws IOException
	 *             on any failure
	 */
	public static String getFavorites(String userId, String appName, String requestId, String appUserName,
			String appPassword) throws IOException {
		return RestWebServiceClient.getInstance().getPortalContent("/getFavorites", userId, appName, requestId, appUserName,
				appPassword,true);
	}
}