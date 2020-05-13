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
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.api.model.UserTaskInstanceWithPotOwnerDesc;
import org.jbpm.services.api.query.model.QueryParam;
import org.junit.After;
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
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.jbpm.services.api.AdvanceRuntimeDataService.TASK_ATTR_NAME;
import static org.jbpm.services.api.query.model.QueryParam.notEqualsTo;
import static org.jbpm.services.api.query.model.QueryParam.equalsTo;
import static org.jbpm.services.api.query.model.QueryParam.isNotNull;
import static org.jbpm.services.api.query.model.QueryParam.list;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class AdvanceRuntimeDataServiceImpl2Test extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(AdvanceRuntimeDataServiceImpl2Test.class);

    private List<DeploymentUnit> units = new ArrayList<>();

    private List<Long> processIds;
    private KModuleDeploymentUnit deploymentUnit = null;
    
    private List<QueryParam> attributes;
    private List<QueryParam> processVariables;
    private List<QueryParam> taskVariables;
    private List<String> potOwners;


    @Before
    public void prepare() {

        configureServices();
        logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<>();
        processes.add("repo/processes/general/SingleHumanTaskWithVarsA.bpmn2");
        processes.add("repo/processes/general/SingleHumanTaskWithVarsB.bpmn2");
        processes.add("repo/processes/general/SingleHumanTaskWithVarsC.bpmn2");

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

        Map<String, Object> inputsA1 = new HashMap<>();
        inputsA1.put("var_a", "somethingelse");
        inputsA1.put("var_b", "othervalue");
        processIds.add(processService.startProcess(deploymentUnit.getIdentifier(), "test.test_A", inputsA1));

        Map<String, Object> inputsB1 = new HashMap<>();
        inputsB1.put("var_a", "somethingelse");
        inputsB1.put("var_b", "b_var");
        processIds.add(processService.startProcess(deploymentUnit.getIdentifier(), "test.test_B", inputsB1));

        Map<String, Object> inputsC1 = new HashMap<>();
        inputsC1.put("var_c", "somethingelse");
        inputsC1.put("var_b", "b_var");
        processIds.add(processService.startProcess(deploymentUnit.getIdentifier(), "test.test_C", inputsC1));

        completeTasks();
        
        attributes = Collections.emptyList();
        processVariables = Collections.emptyList();
        taskVariables = Collections.emptyList();
        potOwners = Collections.emptyList();
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
    public void testQueryProcessTaskByVariablesWithOwners() {
        attributes = list(equalsTo(TASK_ATTR_NAME, "CustomTask"));
        processVariables = list(equalsTo("var_b", "3"));
        taskVariables = list(equalsTo("task_in_a1", "somethingelse"));
        potOwners = Collections.singletonList("kieserver");

        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, new QueryContext());
        assertThat(data.size(), is(1));
        assertThat(data.get(0).getVariables().get("var_b"), is("3"));

        List<Long> taksIds = data.stream().map(ProcessInstanceWithVarsDesc::getId).map(id -> runtimeDataService.getTasksByProcessInstanceId(id)).flatMap(List::stream).collect(toList());
        for (Long taskId : taksIds) {
            UserTaskInstanceDesc userTask = runtimeDataService.getTaskById(taskId);
            assertThat(userTask.getName(), is("CustomTask"));
        }
    }
    
    @Test
    public void testQueryProcessTaskByVariablesWithOwners_NullAttributes() {
        attributes = null;
        processVariables = list(equalsTo("var_b", "3"));
        taskVariables = list(equalsTo("task_in_a1", "somethingelse"));
        
        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, new QueryContext());
        assertThat(data.size(), is(2));
    }
    
    @Test
    public void testQueryProcessTaskByVariablesWithOwners_NullProcessVariables() {
        processVariables = null;
        taskVariables = list(equalsTo("task_in_a1", "somethingelse"));
        
        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, new QueryContext());
        assertThat(data.size(), is(2));
    }
    
    @Test
    public void testQueryProcessTaskByVariablesWithOwners_NullTaskVariables() {
        taskVariables = null;
        potOwners = Collections.singletonList("kieserver");
        
        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, new QueryContext());
        assertThat(data.size(), is(2));
    }
    
    @Test
    public void testQueryProcessTaskByVariablesWithOwners_NullPotOwners() {
        taskVariables = list(equalsTo("task_in_a1", "somethingelse"));
        potOwners = null;

        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, new QueryContext());
        assertThat(data.size(), is(2));
    }
    
    @Test
    public void testQueryProcessTaskByVariablesWithOwners_EmptyPotOwners() {
        taskVariables = list(equalsTo("task_in_a1", "somethingelse"));
        
        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, new QueryContext());
        assertThat(data.size(), is(2));
    }
    
    @Test
    public void testQueryProcessTaskByVariablesWithOwners_Pagination() {
        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, new QueryContext(0,1));
        assertThat(data.size(), is(1));
    }
    
    @Test
    public void testQueryProcessTaskByVariablesWithOwners_MultipleProcessVariablesRepeated() {
        processVariables = list(equalsTo("var_b", "3"), equalsTo("var_c", "somethingelse"), isNotNull("var_c"));
        
        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, new QueryContext());
        assertThat(data.size(), is(1));
    }
    
    @Test
    public void testQueryProcessTaskByVariablesWithOwners_MultipleProcessVariablesRepeatedBis() {
        processVariables = list(equalsTo("var_b", "3"), notEqualsTo("var_c","fake"), isNotNull("var_c"));
        //
        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, new QueryContext());
        assertThat(data.size(), is(1));
        System.out.println("@@ pr:"+data.get(0).getProcessId());
        System.out.println("@@ var_c:"+data.get(0).getVariables().get("var_c"));
        System.out.println("@@ var_b:"+data.get(0).getVariables().get("var_b"));
    }
    @Test
    public void testQueryProcessTaskByVariablesWithOwners_MultipleProcessVariablesRepeated3() {
        processVariables = list(equalsTo("var_b", "3"), equalsTo("var_c","somethingelse"), isNotNull("var_c"));
        
        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, new QueryContext());
        assertThat(data.size(), is(1));
        
        System.out.println("@@ pr:"+data.get(0).getProcessId());
        System.out.println("@@ var_c:"+data.get(0).getVariables().get("var_c"));
        System.out.println("@@ var_b:"+data.get(0).getVariables().get("var_b"));
    }
    
    @Test
    public void testQueryProcessTaskByVariablesWithOwners_MultipleTasksVariablesRepeated() {
        taskVariables = list(equalsTo("task_in_a2", "b_var"), equalsTo("task_in_a1", "somethingelse")
                             , isNotNull("task_in_a1"));
        
        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, new QueryContext());
        assertThat(data.size(), is(1));
        
        System.out.println("@@ pr:"+data.get(0).getProcessId());
        //System.out.println("@@ pr:"+data.get(1).getProcessId());
        
    }
    
    @Test
    public void testQueryProcessTaskByVariablesWithOwners_ProcessVariablesNotNull() {
        processVariables = list(equalsTo("var_b", "3"), isNotNull("var_a"));
        
        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, new QueryContext());
        assertThat(data.size(), is(1));
    }
    
    @Test
    public void testQueryProcessTaskByVariablesWithOwners_ProcessVariableIsEmpty() {
        processVariables = list(equalsTo("var_b", ""));
        
        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, new QueryContext());
        assertThat(data.size(), is(1));
    }

    @Test
    public void testQueryProcessTaskByVariablesWithOwners_ProcessVariableNotEqualsTo() {
        processVariables = list(notEqualsTo("var_b", "3"));
        
        List<ProcessInstanceWithVarsDesc> data = advanceVariableDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, new QueryContext());
        assertThat(data.size(), is(1));
    }
    
    private void completeTasks() {
        List<UserTaskInstanceWithPotOwnerDesc> userTasks = advanceVariableDataService.queryUserTasksByVariables(emptyList(), emptyList(), emptyList(), emptyList(), new QueryContext());

        for (UserTaskInstanceWithPotOwnerDesc userTask : userTasks) {
            Long taskId = userTask.getTaskId();
            String user = userTask.getPotentialOwners().get(0);
            userTaskService.start(taskId, user);
            Map<String, Object> inputs = userTaskService.getTaskInputContentByTaskId(taskId);
            Map<String, Object> output = new HashMap<>(inputs);
            output.put("task_out_a1", 3);
            output.put("task_out_a2", "h2");
            userTaskService.saveContentFromUser(taskId, user, output);
            userTaskService.complete(taskId, user, output);
        }
    }
}
