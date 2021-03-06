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
package org.onap.portalapp.controller.core;

import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.core.controller.UnRestrictedBaseController;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.onap.portalsdk.core.logging.aspect.MetricsLog;

@Controller
@RequestMapping("/")
public class LogoutController extends UnRestrictedBaseController {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(LogoutController.class);

	private User user;

	/**
	 * 
	 * invalidates the current application session, then redirects to portal logout
	 * 
	 * @param request
	 * @return modelView
	 */
	@RequestMapping(value = { "/logout.htm" }, method = RequestMethod.GET)
	public ModelAndView globalLogout(HttpServletRequest request) {
		ModelAndView modelView = null;
		try {
			chatRoomLogout(request);
			request.getSession().invalidate();
			String portalUrl = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL);
			String portalDomain = portalUrl.substring(0, portalUrl.lastIndexOf('/'));
			String redirectUrl = portalDomain + "/logout.htm";
			modelView = new ModelAndView("redirect:" + redirectUrl);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Logout failed", e);
		}
		return modelView;
	}

	/**
	 * 
	 * invalidates the current session (application logout) and redirects user to
	 * Portal.
	 * 
	 * @param request
	 * @return modelView
	 */
	@RequestMapping(value = { "/app_logout.htm" }, method = RequestMethod.GET)
	public ModelAndView appLogout(HttpServletRequest request) {
		ModelAndView modelView = null;
		try {
			chatRoomLogout(request);
			modelView = new ModelAndView(
					"redirect:" + PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL));
			UserUtils.clearUserSession(request);
			request.getSession().invalidate();
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Application Logout failed", e);
		}
		return modelView;
	}

	@MetricsLog
	public void chatRoomLogout(HttpServletRequest request) {
		request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		setUser(UserUtils.getUserSession(request));
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
