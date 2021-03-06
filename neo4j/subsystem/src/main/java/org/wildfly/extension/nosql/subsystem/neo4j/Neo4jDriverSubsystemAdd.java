/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.extension.nosql.subsystem.neo4j;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.operations.validation.ParametersValidator;
import org.jboss.as.txn.service.TransactionManagerService;
import org.jboss.as.txn.service.TransactionSynchronizationRegistryService;
import org.wildfly.nosql.common.DriverDependencyProcessor;
import org.wildfly.nosql.common.DriverScanDependencyProcessor;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.Phase;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;

/**
 * Neo4jDriverSubsystemAdd
 *
 * @author Scott Marlow
 */
public class Neo4jDriverSubsystemAdd extends AbstractBoottimeAddStepHandler {

    public static final Neo4jDriverSubsystemAdd INSTANCE = new Neo4jDriverSubsystemAdd();
    private final ParametersValidator runtimeValidator = new ParametersValidator();

    private Neo4jDriverSubsystemAdd() {
        super(Neo4jDriverDefinition.DRIVER_SERVICE_CAPABILITY);
    }

    protected void populateModel(ModelNode operation, ModelNode model) throws OperationFailedException {
        for (AttributeDefinition def : Neo4jDriverDefinition.INSTANCE.getAttributes()) {
            def.validateAndSet(operation, model);
        }
    }

    protected void performBoottime(final OperationContext context, final ModelNode operation, final ModelNode model) throws
            OperationFailedException {

        runtimeValidator.validate(operation.resolve());
        context.addStep(new AbstractDeploymentChainStep() {
            protected void execute(DeploymentProcessorTarget processorTarget) {
                final int PARSE_NEO4J_DRIVER                          = 0x4C00;
                final int DEPENDENCIES_NEO4J_DRIVER                   = 0x1F10;
                // TODO: use Phase.PARSE_NEO4J_DRIVER
                processorTarget.addDeploymentProcessor(Neo4jDriverExtension.SUBSYSTEM_NAME, Phase.PARSE, PARSE_NEO4J_DRIVER, new DriverScanDependencyProcessor("neo4jsubsystem"));
                // TODO: use Phase.DEPENDENCIES_NEO4J_DRIVER
                processorTarget.addDeploymentProcessor(Neo4jDriverExtension.SUBSYSTEM_NAME, Phase.DEPENDENCIES, DEPENDENCIES_NEO4J_DRIVER, DriverDependencyProcessor.getInstance());
            }
        }, OperationContext.Stage.RUNTIME);

        startNeo4jDriverSubsysteService(context);

    }

    private void startNeo4jDriverSubsysteService(final OperationContext context) {
        Neo4jSubsystemService neo4jSubsystemService = new Neo4jSubsystemService();
        context.getServiceTarget().addService(Neo4jSubsystemService.serviceName(), neo4jSubsystemService).setInitialMode(ServiceController.Mode.ACTIVE)
        .addDependency(TransactionManagerService.SERVICE_NAME, TransactionManager.class, neo4jSubsystemService.getTransactionManagerInjector())
        .addDependency(TransactionSynchronizationRegistryService.SERVICE_NAME, TransactionSynchronizationRegistry.class, neo4jSubsystemService.getTxSyncRegistryInjector())
                .install();
    }


}
