/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.service;

import org.mifosplatform.useradministration.data.PermissionExpressionData;

import java.util.Collection;

public interface PermissionExpressionReadPlatformService {

    Collection<PermissionExpressionData> retrieveAllRolePermissionExpressions(Long roleId);
}