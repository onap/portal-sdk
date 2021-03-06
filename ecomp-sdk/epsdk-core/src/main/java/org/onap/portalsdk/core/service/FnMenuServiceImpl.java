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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.onap.portalsdk.core.domain.Menu;
import org.onap.portalsdk.core.domain.MenuData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("fnMenuService")
@Transactional
public class FnMenuServiceImpl implements FnMenuService {

	@Autowired
	private DataAccessService dataAccessService;

	@Override
	@SuppressWarnings("unchecked")
	public List<MenuData> getFnMenuItems() {
		return getDataAccessService().getList(MenuData.class, null, "1", null);
	}

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	@Override
	public void saveFnMenuData(MenuData domainFnMenu) {
		getDataAccessService().saveDomainObject(domainFnMenu, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getParentId(String label) {
		Map<String, String> params = new HashMap<>();
		params.put("paramLabel", label);
		return getDataAccessService().executeNamedQuery("IdForLabelList", params, null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<List> getParentList() {
		return getDataAccessService().executeNamedQuery("parentList", null, null);
	}

	@Override
	public void removeMenuItem(MenuData domainFnMenu) {
		getDataAccessService().deleteDomainObject(domainFnMenu, null);
	}

	@Override
	public void removeMenuItem(Menu domainFnMenu) {
		getDataAccessService().deleteDomainObject(domainFnMenu, null);
	}

	@Override
	public MenuData getMenuItemRow(Long id) {
		return (MenuData) getDataAccessService().getDomainObject(MenuData.class, id, null);
	}

	@Override
	public Menu getMenuItem(Long id) {
		return (Menu) getDataAccessService().getDomainObject(Menu.class, id, null);
	}

	@Override
	public void saveFnMenu(Menu domainFnMenu) {
		getDataAccessService().saveDomainObject(domainFnMenu, null);
	}

	@Override
	public void setMenuDataStructure(List<List<MenuData>> childItemList, List<MenuData> parentList,
			Set<MenuData> menuResult) {
		for (MenuData menu : menuResult) {
			MenuData parentData = new MenuData();
			parentData.setLabel(menu.getLabel());
			parentData.setAction(menu.getAction());
			parentData.setImageSrc(menu.getImageSrc());
			parentList.add(parentData);
			List<MenuData> tempList = new ArrayList<>();
			for (Object o : menu.getChildMenus()) {
				MenuData m = (MenuData) o;
				MenuData data = new MenuData();
				data.setLabel(m.getLabel());
				data.setAction(m.getAction());
				data.setImageSrc(m.getImageSrc());
				tempList.add(data);
			}
			childItemList.add(tempList);
		}
	}

}
