/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.costcenter.handler;

import org.mifosplatform.accounting.costcenter.command.CostCenterCommand;
import org.mifosplatform.accounting.costcenter.service.CostCenterWritePlatformService;
import org.mifosplatform.accounting.glaccount.service.GLAccountWritePlatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteCostCenterCommandHandler implements NewCommandSourceHandler {

    private final CostCenterWritePlatformService writePlatformService;

    @Autowired
    public DeleteCostCenterCommandHandler(final CostCenterWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.deleteCostCenter(command.entityId(), command);
    }
}