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

package org.jbpm.casemgmt.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.services.api.model.ProcessInstanceWithVarsDesc;
import org.jbpm.services.api.model.UserTaskInstanceWithPotOwnerDesc;
import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.Test;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.OrganizationalEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AdvanceCaseRuntimeDataServiceImplTest extends AbstractCaseServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(AdvanceCaseRuntimeDataServiceImplTest.class);

    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<String>();
        processes.add("cases/EmptyCase.bpmn2");
        processes.add("cases/UserTaskCase.bpmn2");
        processes.add("processes/UserTaskProcess.bpmn2");
        return processes;
    }

    /*
     * Case instance queries
     */
    @Test
    public void testSearchByVariable() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "my first case");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, data);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, caseFile);
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "my first case");
        
        List<ProcessInstanceWithVarsDesc> process = advanceCaseRuntimeDataService.queryCaseByVariables(emptyMap(), vars, new QueryContext());
        assertEquals(process.size(), 1);
        assertEquals(process.get(0).getVariables().get("name"), "my first case");

        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        if (caseId != null) {
            caseService.cancelCase(caseId);
        }

    }

    @Test
    public void testSearchUserByVariable() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl(USER));

        Map<String, Object> data = new HashMap<>();
        data.put("name", "my first case");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_CASE_P_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_CASE_P_ID, caseFile);

        List<UserTaskInstanceWithPotOwnerDesc> process = advanceCaseRuntimeDataService.queryUserTasksByVariables(emptyMap(), emptyMap(), emptyMap(), emptyList(), new QueryContext());
        assertEquals(1, process.size());
        assertEquals(process.get(0).getExtraData().get("name"), "my first case");

        assertNotNull(caseId);
        assertEquals(HR_CASE_ID, caseId);
        if (caseId != null) {
            caseService.cancelCase(caseId);
        }

    }

}