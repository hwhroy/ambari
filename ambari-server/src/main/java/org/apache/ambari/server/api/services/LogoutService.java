/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.api.services;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.ambari.server.audit.AuditLogger;
import org.apache.ambari.server.audit.event.LogoutAuditEvent;
import org.apache.ambari.server.security.authorization.AuthorizationHelper;
import org.apache.ambari.server.utils.RequestUtils;

import com.google.inject.Inject;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Service performing logout of current user
 */
@Path("/logout")
public class LogoutService {

  private static AuditLogger auditLogger;

  @Inject
  public static void init(AuditLogger logger) {
    auditLogger = logger;
  }

  @GET
  @Produces("text/plain")
  public Response performLogout(@Context HttpServletRequest servletRequest) {
    auditLog(servletRequest);
    SecurityContextHolder.clearContext();
    servletRequest.getSession().invalidate();
    return Response.status(Response.Status.OK).build();
  }

  private void auditLog(HttpServletRequest servletRequest) {
    LogoutAuditEvent logoutEvent = LogoutAuditEvent.builder()
      .withTimestamp(System.currentTimeMillis())
      .withRemoteIp(RequestUtils.getRemoteAddress(servletRequest))
      .withUserName(AuthorizationHelper.getAuthenticatedName())
      .build();
    auditLogger.log(logoutEvent);
  }
}
