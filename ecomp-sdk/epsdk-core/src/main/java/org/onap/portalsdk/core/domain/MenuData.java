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
package org.onap.portalsdk.core.domain;

import java.util.Set;
import java.util.TreeSet;

public class MenuData extends Menu {

	private static final long serialVersionUID = 1L;
	private MenuData parentMenu;
	private Set childMenus = new TreeSet();

	public MenuData() {
	}

	public Set getChildMenus() {
		return childMenus;
	}

	public void setChildMenus(Set childMenus) {
		this.childMenus = childMenus;
	}

	public MenuData getParentMenu() {
		return parentMenu;
	}

	public void setParentMenu(MenuData parentMenu) {
		this.parentMenu = parentMenu;
	}

	public String getActiveAsString() {
		return String.valueOf(isActive());
	}

	public String getParentIdAsString() {
		return String.valueOf(getParentId());
	}

	public String getSeparatorAsString() {
		return String.valueOf(isSeparator());
	}

	@Override
	public int compareTo(Object obj) {
		Short c1 = getSortOrder();
		Short c2 = ((MenuData) obj).getSortOrder();
		return (c1 == null || c2 == null) ? 1 : ((c1.compareTo(c2) == 0) ? 1 : c1.compareTo(c2));
	}

}
