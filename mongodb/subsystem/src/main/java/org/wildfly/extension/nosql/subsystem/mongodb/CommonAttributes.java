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

package org.wildfly.extension.nosql.subsystem.mongodb;

/**
 * CommonAttributes
 *
 * @author Scott Marlow
 */
public interface CommonAttributes {
    String DATABASE = "database";
    String OUTBOUND_SOCKET_BINDING_REF = "outbound-socket-binding-ref";
    String HOST_DEF = "host";
    String ID_NAME = "id";
    String JNDI_NAME = "jndi-name";
    String MODULE_NAME = "module";
    String PROFILE = "mongo";
    String PROPERTIES = "properties";
    String PROPERTY = "property";
    String WRITE_CONCERN = "writeConcern";
    String READ_CONCERN = "readConcern";
    String SECURITY_DOMAIN = "security-domain";
    String AUTH_TYPE = "auth-type";
    String SSL = "ssl";
}
