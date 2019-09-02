/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.test.functional.prediction;

import java.util.List;

import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.InternalTaskService;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

public class HumanTaskPredictionTest extends JbpmTestCase {
    
    public HumanTaskPredictionTest() {
        super(true, true);
    }
         
    /*
     * Notice that the system property "org.jbpm.task.prediction.service" 
     * has been set up to "MockPredictionService" in the pom
     */
    
    @Test
    public void testConfidentPredictionService() {
        manager = createRuntimeManager("org/jbpm/test/functional/prediction/humantask.bpmn");
        RuntimeEngine runtimeEngine = getRuntimeEngine(EmptyContext.get());
        KieSession kieSession = runtimeEngine.getKieSession();
        
        ProcessInstance pi = kieSession.startProcess("com.sample.humantask.task1");
     
        assertProcessInstanceCompleted(pi.getId());
        assertNodeTriggered(pi.getId(), "Task 1 PS");
        
        String eval = (String)((WorkflowProcessInstance) pi).getVariable("eval");
        
        assertNotNull(eval);
        
        assertEquals(MockPredictionService.PREDICTED_VALUE, eval); 
    }
   
    @Test
    public void testUnconfidentPredictionService() {
        manager = createRuntimeManager("org/jbpm/test/functional/prediction/humantask2.bpmn" );
        RuntimeEngine runtimeEngine = getRuntimeEngine(EmptyContext.get());
        KieSession kieSession = runtimeEngine.getKieSession();
       
        ProcessInstance pi = kieSession.startProcess("com.sample.humantask.task2");

        TaskService taskService = runtimeEngine.getTaskService();
       
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
       
        assertEquals(1, tasks.size());
       
        assertThat(tasks.get(0).getStatus()).isEqualTo(Status.Reserved);
       
        assertEquals(MockPredictionService.UNCONFIDENT_VALUE, 
           ((InternalTaskService)taskService).getOutputContentMapForUser(tasks.get(0).getId(), "john").get("eval"));
       
        assertProcessInstanceActive(pi.getId(), kieSession);              
    }
    
}
