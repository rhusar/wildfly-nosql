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

package org.wildfly.extension.nosql.driver.cassandra;

import static org.wildfly.nosql.common.NoSQLLogger.ROOT_LOGGER;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Set;

import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;

import org.jboss.modules.ModuleIdentifier;
import org.jboss.security.SubjectFactory;
import org.wildfly.nosql.common.MethodHandleBuilder;
import org.wildfly.nosql.common.NoSQLConstants;

/**
 * CassandraInteraction is for interacting with Cassandra without static references to Cassandra classes.
 *
 * @author Scott Marlow
 */
public class CassandraInteraction {

    private final Class clusterClass;
    private final Class sessionClass;

    private final Class clusterBuilderClass;
    private final MethodHandle clusterBuilderMethod;
    private final MethodHandle clusterConnectMethod;
    private final MethodHandle clusterCloseMethod;
    private final MethodHandle builderBuildMethod;
    private final MethodHandle builderWithClusterNameMethod;
    private final MethodHandle builderWithPortMethod;
    private final MethodHandle builderWithCredentials;
    private final MethodHandle builderAddContactPointMethod;
    private final MethodHandle builderwithSSLMethod;
    private final MethodHandle sessionCloseMethod;
    private Object clusterBuilder;
    private volatile SubjectFactory subjectFactory;
    private final String securityDomain;

    public CassandraInteraction(ConfigurationBuilder configurationBuilder) {
        MethodHandleBuilder methodHandleBuilder = new MethodHandleBuilder();
        methodHandleBuilder.classLoader(ModuleIdentifier.fromString(configurationBuilder.getModuleName()));
        clusterBuilderClass = methodHandleBuilder.className(NoSQLConstants.CASSANDRACLUSTERBUILDERCLASS).getTargetClass();
        builderBuildMethod = methodHandleBuilder.method("build");
        builderWithClusterNameMethod = methodHandleBuilder.method("withClusterName", String.class);
        builderWithCredentials = methodHandleBuilder.method("withCredentials", String.class, String.class);
        builderWithPortMethod = methodHandleBuilder.method("withPort", int.class);
        builderAddContactPointMethod = methodHandleBuilder.method("addContactPoint", String.class);
        builderwithSSLMethod = methodHandleBuilder.method("withSSL");

        clusterClass = methodHandleBuilder.className(NoSQLConstants.CASSANDRACLUSTERCLASS).getTargetClass();
        clusterConnectMethod = methodHandleBuilder.method("connect", String.class);
        clusterCloseMethod = methodHandleBuilder.method("close");
        clusterBuilderMethod = methodHandleBuilder.staticMethod("builder", MethodType.methodType(clusterBuilderClass));
        sessionClass = methodHandleBuilder.className(NoSQLConstants.CASSANDRASESSIONCLASS).getTargetClass();
        sessionCloseMethod = methodHandleBuilder.method("close");
        securityDomain = configurationBuilder.getSecurityDomain();
    }

    private Object getBuilder() throws Throwable {
        if (clusterBuilder == null) {
            this.clusterBuilder =  clusterBuilderMethod.invoke(); // Cluster.builder();
        }
        return clusterBuilder;
    }

    protected Object /* Cluster */ build() throws Throwable {
        return builderBuildMethod.invoke(getBuilder());
    }

    protected Object connect(Object cluster, String keySpace) throws Throwable {
        return clusterConnectMethod.invoke(cluster, keySpace);
    }

    protected void withClusterName(String clusterName) throws Throwable {
        builderWithClusterNameMethod.invoke(getBuilder(), clusterName);
    }

    protected void withPort(int port) throws Throwable {
        builderWithPortMethod.invoke(getBuilder(), port);
    }

    protected void withSSL() throws Throwable {
        builderwithSSLMethod.invoke(getBuilder());
    }

    protected void setCredential(String securityDomain) throws Throwable {
        if (securityDomain != null && subjectFactory != null) {
            try {
                Subject subject = subjectFactory.createSubject(securityDomain);
                Set<PasswordCredential> passwordCredentials = subject.getPrivateCredentials(PasswordCredential.class);
                PasswordCredential passwordCredential = passwordCredentials.iterator().next();
                withCredentials(passwordCredential.getUserName(), new String(passwordCredential.getPassword()));
            } catch(Throwable problem) {
                if (ROOT_LOGGER.isTraceEnabled()) {
                    ROOT_LOGGER.tracef(problem,"could not create subject for security domain '%s'",
                            securityDomain);
                }
                throw problem;
            }
        }
    }


    private void withCredentials(String user, String password) throws Throwable {
        builderWithCredentials.invoke(getBuilder(), user, password);
    }

    protected void addContactPoint(String host) throws Throwable {
        builderAddContactPointMethod.invoke(getBuilder(), host);
    }

    protected void clusterClose(Object cluster) throws Throwable {
        clusterCloseMethod.invoke(cluster);
    }

    protected void sessionClose(Object session) throws Throwable {
        sessionCloseMethod.invoke(session);
    }

    public Class getClusterClass() {
        return clusterClass;
    }

    public Class getSessionClass() {
        return sessionClass;
    }

    public void subjectFactory(SubjectFactory subjectFactory) throws Throwable {
        this.subjectFactory = subjectFactory;
        setCredential(securityDomain);
    }
}
