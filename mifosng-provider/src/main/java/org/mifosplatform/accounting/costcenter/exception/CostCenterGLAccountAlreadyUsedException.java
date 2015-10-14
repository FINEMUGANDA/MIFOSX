/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.costcenter.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * A {@link RuntimeException} thrown when trying to fetch accounts belonging to
 * an Invalid Usage Type
 */
public class CostCenterGLAccountAlreadyUsedException extends AbstractPlatformDomainRuleException {

    public CostCenterGLAccountAlreadyUsedException(final String glAccountCode) {
        super("error.msg.costcenter.glaccount.already.assinged", "The following GL Account with code '" + glAccountCode + "' already assigned to another cost center.");
    }
}