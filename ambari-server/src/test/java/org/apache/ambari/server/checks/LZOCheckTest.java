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
package org.apache.ambari.server.checks;

    import java.util.HashMap;
    import java.util.Map;

    import org.apache.ambari.server.configuration.Configuration;
    import org.apache.ambari.server.controller.PrereqCheckRequest;
    import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
    import org.apache.ambari.server.state.Cluster;
    import org.apache.ambari.server.state.Clusters;
    import org.apache.ambari.server.state.Config;
    import org.apache.ambari.server.state.DesiredConfig;
    import org.apache.ambari.server.state.RepositoryType;
    import org.apache.ambari.server.state.Service;
    import org.apache.ambari.server.state.repository.ClusterVersionSummary;
    import org.apache.ambari.server.state.repository.VersionDefinitionXml;
    import org.apache.ambari.server.state.stack.PrereqCheckStatus;
    import org.apache.ambari.server.state.stack.PrerequisiteCheck;
    import org.junit.Assert;
    import org.junit.Before;
    import org.junit.Test;
    import org.junit.runner.RunWith;
    import org.mockito.Mock;
    import org.mockito.Mockito;
    import org.mockito.runners.MockitoJUnitRunner;

    import com.google.inject.Provider;


/* Test for LZOCheck */
@RunWith(MockitoJUnitRunner.class)
public class LZOCheckTest {

  private final Clusters clusters = Mockito.mock(Clusters.class);
  private final LZOCheck lZOCheck = new LZOCheck();

  @Mock
  private ClusterVersionSummary m_clusterVersionSummary;

  @Mock
  private VersionDefinitionXml m_vdfXml;

  @Mock
  private RepositoryVersionEntity m_repositoryVersion;

  final Map<String, Service> m_services = new HashMap<>();

  @Before
  public void setup() throws Exception {
    lZOCheck.clustersProvider = new Provider<Clusters>() {
      @Override
      public Clusters get() {
        return clusters;
      }
    };
    Configuration config = Mockito.mock(Configuration.class);
    lZOCheck.config = config;

    m_services.clear();

    Mockito.when(m_repositoryVersion.getType()).thenReturn(RepositoryType.STANDARD);
    Mockito.when(m_repositoryVersion.getRepositoryXml()).thenReturn(m_vdfXml);
    Mockito.when(m_vdfXml.getClusterSummary(Mockito.any(Cluster.class))).thenReturn(m_clusterVersionSummary);
    Mockito.when(m_clusterVersionSummary.getAvailableServiceNames()).thenReturn(m_services.keySet());
  }

  @Test
  public void testIsApplicable() throws Exception {
    final Cluster cluster = Mockito.mock(Cluster.class);

    Mockito.when(cluster.getServices()).thenReturn(m_services);
    Mockito.when(cluster.getClusterId()).thenReturn(1L);
    Mockito.when(clusters.getCluster("cluster")).thenReturn(cluster);

    PrereqCheckRequest request = new PrereqCheckRequest("cluster");
    request.setTargetRepositoryVersion(m_repositoryVersion);

    Assert.assertTrue(lZOCheck.isApplicable(request));
  }

  @Test
  public void testPerform() throws Exception {
    final Cluster cluster = Mockito.mock(Cluster.class);
    final Map<String, Service> services = new HashMap<>();
    final Service service = Mockito.mock(Service.class);

    Mockito.when(cluster.getServices()).thenReturn(services);
    Mockito.when(cluster.getClusterId()).thenReturn(1L);
    Mockito.when(clusters.getCluster("cluster")).thenReturn(cluster);

    final DesiredConfig desiredConfig = Mockito.mock(DesiredConfig.class);
    Mockito.when(desiredConfig.getTag()).thenReturn("tag");
    Map<String, DesiredConfig> configMap = new HashMap<>();
    configMap.put("core-site", desiredConfig);

    Mockito.when(cluster.getDesiredConfigs()).thenReturn(configMap);
    final Config config = Mockito.mock(Config.class);
    Mockito.when(cluster.getConfig(Mockito.anyString(), Mockito.anyString())).thenReturn(config);
    final Map<String, String> properties = new HashMap<>();
    Mockito.when(config.getProperties()).thenReturn(properties);

    PrerequisiteCheck check = new PrerequisiteCheck(null, null);
    lZOCheck.perform(check, new PrereqCheckRequest("cluster"));
    Assert.assertEquals(PrereqCheckStatus.PASS, check.getStatus());


    properties.put(LZOCheck.IO_COMPRESSION_CODECS,"test," + LZOCheck.LZO_ENABLE_VALUE);
    check = new PrerequisiteCheck(null, null);
    lZOCheck.perform(check, new PrereqCheckRequest("cluster"));
    Assert.assertEquals(PrereqCheckStatus.WARNING, check.getStatus());

    properties.put(LZOCheck.IO_COMPRESSION_CODECS,"test");
    check = new PrerequisiteCheck(null, null);
    lZOCheck.perform(check, new PrereqCheckRequest("cluster"));
    Assert.assertEquals(PrereqCheckStatus.PASS, check.getStatus());

    properties.put(LZOCheck.LZO_ENABLE_KEY, LZOCheck.LZO_ENABLE_VALUE);
    check = new PrerequisiteCheck(null, null);
    lZOCheck.perform(check, new PrereqCheckRequest("cluster"));
    Assert.assertEquals(PrereqCheckStatus.WARNING, check.getStatus());

    properties.put(LZOCheck.LZO_ENABLE_KEY, LZOCheck.LZO_ENABLE_VALUE);
    properties.put(LZOCheck.IO_COMPRESSION_CODECS,"test," + LZOCheck.LZO_ENABLE_VALUE);
    check = new PrerequisiteCheck(null, null);
    lZOCheck.perform(check, new PrereqCheckRequest("cluster"));
    Assert.assertEquals(PrereqCheckStatus.WARNING, check.getStatus());
  }
}
