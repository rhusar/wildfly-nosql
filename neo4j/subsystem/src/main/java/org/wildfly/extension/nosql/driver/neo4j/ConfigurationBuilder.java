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

package org.wildfly.extension.nosql.driver.neo4j;

import org.wildfly.extension.nosql.driver.neo4j.transaction.TransactionEnlistmentType;

/**
 * ConfigurationBuilder
 *
 * @author Scott Marlow
 */
public class ConfigurationBuilder {
    private String description; //
    private String JNDIName;    // required global jndi name

    private static final String defaultModuleName = "org.neo4j.driver";
    private String moduleName = // name of static module
            defaultModuleName;
    private TransactionEnlistmentType transactionEnlistment;
    private String securityDomain;

    public void setDescription(String description) {
        this.description = description;
    }

    public void setJNDIName(String JNDIName) {
        this.JNDIName = JNDIName;
    }

    public String getJNDIName() {
        return JNDIName;
    }

    public String getDescription() {
        return description;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setTransactionEnlistment(TransactionEnlistmentType transactionEnlistment) {
        this.transactionEnlistment = transactionEnlistment;
    }

    public TransactionEnlistmentType getTransactionEnlistment() {
        return transactionEnlistment;
    }

    public void setSecurityDomain(String securityDomain) {
        this.securityDomain = securityDomain;
    }

    public String getSecurityDomain() {
        return securityDomain;
    }
}
