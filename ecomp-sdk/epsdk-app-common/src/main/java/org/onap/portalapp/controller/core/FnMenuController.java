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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.domain.Menu;
import org.onap.portalsdk.core.domain.MenuData;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.service.FnMenuService;
import org.onap.portalsdk.core.service.FunctionalMenuListService;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A controller for Admin to add/edit/delete menu items from FN_MENU.
 */
@Controller
@RequestMapping("/")
public class FnMenuController extends RestrictedBaseController {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(FnMenuController.class);

	@Autowired
	FnMenuService service;

	@Autowired
	FunctionalMenuListService functionalMenuListService;

	private String viewName;

	@RequestMapping(value = { "/admin_fn_menu/get_parent_list" }, method = RequestMethod.GET)
	public void getParentList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		try {
			response.getWriter().write(mapper.writeValueAsString(service.getParentList()));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getParentListFailed", e);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write("An error occurred in the getParentList () ");
		}
	}

	@RequestMapping(value = { "/admin_fn_menu/get_function_cd_list" }, method = RequestMethod.GET)
	public void getFunctionCDList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		try {
			response.getWriter().write(mapper.writeValueAsString(functionalMenuListService.getFunctionCDList(request)));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getFunctionCDList", e);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write("An error occurred in the getFunctionCDList ()");
		}

	}

	@RequestMapping(value = { "/admin_fn_menu" }, method = RequestMethod.GET)
	public void getFnMenuList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> model = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		List<MenuData> temp = null;
		List<List<MenuData>> childItemList = new ArrayList<>();
		List<MenuData> parentList = new ArrayList<>();

		try {
			temp = service.getFnMenuItems();
			for (MenuData menu : temp) {
				MenuData parentData = new MenuData();
				parentData.setId(menu.getId());
				parentData.setLabel(menu.getLabel());
				if (menu.getParentMenu() != null) {
					parentData.setParentId(menu.getParentMenu().getId());
				}
				parentData.setAction(menu.getAction());
				parentData.setFunctionCd(menu.getFunctionCd());
				parentData.setImageSrc(menu.getImageSrc());
				parentData.setSortOrder(menu.getSortOrder());
				parentData.setActive(menu.isActive());
				parentData.setServlet(menu.getServlet());
				parentData.setQueryString(menu.getQueryString());
				parentData.setExternalUrl(menu.getExternalUrl());
				parentData.setTarget(menu.getTarget());
				parentData.setMenuSetCode(menu.getMenuSetCode());
				parentData.setSeparator(menu.isSeparator());
				parentData.setImageSrc(menu.getImageSrc());
				parentList.add(parentData);
				List<MenuData> tempList = new ArrayList<>();
				childItemList.add(tempList);
			}
			model.put("fnMenuItems", parentList);
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getFnMenuList failed", e);
		}

	}

	@RequestMapping(value = { "/admin_fn_menu/updateFnMenu" }, method = RequestMethod.POST)
	public ModelAndView updateFnMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Menu fnMenuItem = mapper.readValue(root.get("availableFnMenuItem").toString(), Menu.class);
			service.saveFnMenu(fnMenuItem);
			request.getSession()
					.removeAttribute(SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_ATTRIBUTE_NAME));
			request.getSession().removeAttribute(SystemProperties.LEFT_MENU_CHILDREND);
			request.getSession().removeAttribute(SystemProperties.LEFT_MENU_PARENT);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(service.getMenuItem(fnMenuItem.getId()));

			out.write(responseString);

			return null;

		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "updateFnMenu failed", e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write("An error occurred in the updateFnMenu () ");
		}
		return null;

	}

	@RequestMapping(value = { "/admin_fn_menu/removeMenuItem" }, method = RequestMethod.POST)
	public ModelAndView removeFnMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Menu fnMenuItem = mapper.readValue(root.get("fnMenuItem").toString(), Menu.class);
			Menu fnMenuItemRow = service.getMenuItemRow(fnMenuItem.getId());
			service.removeMenuItem(fnMenuItemRow);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(service.getMenuItem(fnMenuItem.getId()));
			out.write(responseString);

			return null;

		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "removeFnMenu failed", e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write("An error occurred in the removeFnMenu ()");
		}
		return null;

	}

	@Override
	public String getViewName() {
		return viewName;
	}

	@Override
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

}
