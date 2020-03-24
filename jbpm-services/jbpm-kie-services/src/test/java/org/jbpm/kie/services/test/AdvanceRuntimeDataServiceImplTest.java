/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.kie.services.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessInstanceWithVarsDesc;
import org.jbpm.services.api.model.UserTaskInstanceWithPotOwnerDesc;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.query.QueryContext;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class AdvanceRuntimeDataServiceImplTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(AdvanceRuntimeDataServiceImplTest.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    protected String correctUser = "testUser";
    protected String wrongUser = "wrongUser";

    private List<Long> processIds;
    private KModuleDeploymentUnit deploymentUnit = null;

    @Before
    public void prepare() {

        configureServices();
        logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/SingleHumanTaskWithVarsA.bpmn2");
        processes.add("repo/processes/general/SingleHumanTaskWithVarsB.bpmn2");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try (FileOutputStream fs = new FileOutputStream(pom);) {
            fs.write(getPom(releaseId).getBytes());
        } catch (Exception e) {

        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);

        assertNotNull(deploymentService);

        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        assertNotNull(processService);

        processIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> inputsA1 = new HashMap<>();
            inputsA1.put("var_a", "a" + (i % 3));
            inputsA1.put("var_b", (i % 3));
            processIds.add(processService.startProcess(deploymentUnit.getIdentifier(), "test.test_A", inputsA1));
        }
        for (int i = 0; i < 10; i++) {
            Map<String, Object> inputsB1 = new HashMap<>();
            inputsB1.put("var_a", "b" + (i % 3));
            inputsB1.put("var_b", (i % 3));
            processIds.add(processService.startProcess(deploymentUnit.getIdentifier(), "test.test_B", inputsB1));
        }
    }

    @After
    public void cleanup() {
        for (Long processInstanceId : processIds) {
            try {
                // let's abort process instance to leave the system in clear state
                processService.abortProcessInstance(processInstanceId);

                ProcessInstance pi = processService.getProcessInstance(processInstanceId);
                assertNull(pi);
            } catch (ProcessInstanceNotFoundException e) {
                // ignore it as it was already completed/aborted
            }
        }
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                try {
                    deploymentService.undeploy(unit);
                } catch (Exception e) {
                    // do nothing in case of some failed tests to avoid next test to fail as well
                }
            }
            units.clear();
        }
        close();
    }

    @Test
    public void testQueryProcessByVariables() {
        Map<String, Object> variables = Collections.<String, Object> singletonMap("var_a", "a1");
        Map<String, Object> attributes = Collections.<String, Object> singletonMap("processId", "test.test_A");
        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariables(attributes, variables, new QueryContext());
        Assert.assertEquals(3, data.size());
        for (ProcessInstanceWithVarsDesc p : data) {
            Assert.assertEquals("a1", p.getVariables().get("var_a"));
            Assert.assertEquals("test.test_A", p.getProcessId());
        }
    }

    @Test
    public void testQueryProcessByVariablesWithPagination() {
        Map<String, Object> variables = Collections.<String, Object> singletonMap("var_a", "a1");
        Map<String, Object> attributes = Collections.<String, Object> singletonMap("processId", "test.test_A");
        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariables(attributes, variables, new QueryContext(0, 2));
        Assert.assertEquals(2, data.size());
        for (ProcessInstanceWithVarsDesc p : data) {
            Assert.assertEquals("a1", p.getVariables().get("var_a"));
            Assert.assertEquals("test.test_A", p.getProcessId());
        }
    }

    @Test
    public void testQueryTaskByVariables() {
        Map<String, Object> variables = Collections.<String, Object> singletonMap("task_in_a1", "a0");
        Map<String, Object> attributes = Collections.<String, Object> singletonMap("deploymentId", "org.jbpm.test:test-module:1.0.0");
        List<String> potOwners = emptyList();
        List<UserTaskInstanceWithPotOwnerDesc> data = advanceVariableDataService.queryUserTasksByVariables(attributes, variables, emptyMap(), potOwners, new QueryContext());
        Assert.assertEquals(4, data.size());
        for (UserTaskInstanceWithPotOwnerDesc p : data) {
            Assert.assertEquals("a0", p.getInputdata().get("task_in_a1"));
            Assert.assertEquals("org.jbpm.test:test-module:1.0.0", p.getDeploymentId());
        }
    }

    @Test
    public void testQueryTaskByVariablesWithPagination() {
        Map<String, Object> variables = Collections.<String, Object> singletonMap("task_in_a1", "a0");
        Map<String, Object> attributes = Collections.<String, Object> singletonMap("deploymentId", "org.jbpm.test:test-module:1.0.0");
        List<String> potOwners = emptyList();
        List<UserTaskInstanceWithPotOwnerDesc> data = advanceVariableDataService.queryUserTasksByVariables(attributes, variables, emptyMap(), potOwners, new QueryContext(0, 2));
        Assert.assertEquals(2, data.size());
        for (UserTaskInstanceWithPotOwnerDesc p : data) {
            Assert.assertEquals("a0", p.getInputdata().get("task_in_a1"));
            Assert.assertEquals("org.jbpm.test:test-module:1.0.0", p.getDeploymentId());
        }
    }

    @Test
    public void testQueryTaskByVariablesWithOwners() {
        Map<String, Object> variables = emptyMap();
        Map<String, Object> attributes = emptyMap();
        List<String> potOwners = Collections.singletonList("katy");
        List<UserTaskInstanceWithPotOwnerDesc> data = advanceVariableDataService.queryUserTasksByVariables(attributes, variables, emptyMap(), potOwners, new QueryContext(0, 2));
        Assert.assertEquals(2, data.size());
        for (UserTaskInstanceWithPotOwnerDesc p : data) {
            Assert.assertTrue(p.getPotentialOwners().contains("katy"));
        }
    }

    @Test
    public void testQueryTaskByVariablesWithAllOwners() {
        Map<String, Object> variables = emptyMap();
        Map<String, Object> attributes = emptyMap();
        List<String> potOwners = Arrays.asList("katy", "nobody");
        List<UserTaskInstanceWithPotOwnerDesc> data = advanceVariableDataService.queryUserTasksByVariables(attributes, variables, emptyMap(), potOwners, new QueryContext(0, 2));
        Assert.assertEquals(0, data.size());

    }

    @Test
    public void testQueryTaskByVariablesWithByProcessVar() {
        Map<String, Object> variables = emptyMap();
        Map<String, Object> attributes = emptyMap();
        Map<String, Object> processVariables = Collections.singletonMap("var_a", "a1");
        List<String> potOwners = Collections.emptyList();
        List<UserTaskInstanceWithPotOwnerDesc> data = advanceVariableDataService.queryUserTasksByVariables(attributes, variables, processVariables, potOwners, new QueryContext(0, 2));
        Assert.assertEquals(2, data.size());

    }

}
