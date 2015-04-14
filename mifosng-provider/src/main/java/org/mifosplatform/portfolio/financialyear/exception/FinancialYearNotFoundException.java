/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.financialyear.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when financial year resources are not found.
 */
public class FinancialYearNotFoundException extends AbstractPlatformResourceNotFoundException {

    public FinancialYearNotFoundException(final Long id) {
        super("error.msg.financial.year.id.invalid", "Financial Year with identifier " + id + " does not exist", id);
    }
}