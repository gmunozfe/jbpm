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

package org.jbpm.kie.services.impl;

import java.util.List;
import java.util.Map;

import org.jbpm.services.api.AdvanceRuntimeDataService;
import org.kie.api.runtime.query.QueryContext;

import static org.jbpm.workflow.core.WorkflowProcess.PROCESS_TYPE;

public class AdvanceRuntimeDataServiceImpl extends AbstractAdvanceRuntimeDataServiceImpl implements AdvanceRuntimeDataService {


    @Override
    public List<org.jbpm.services.api.model.ProcessInstanceWithVarsDesc> queryProcessByVariables(Map<String, Object> attributes,
                                                                                                 Map<String, Object> variables,
                                                                                                 QueryContext queryContext) {
        return queryProcessByVariables(attributes, variables, PROCESS_TYPE, "", queryContext);

    }


    @Override
    public List<org.jbpm.services.api.model.UserTaskInstanceWithPotOwnerDesc> queryUserTasksByVariables(Map<String, Object> attributes,
                                                                                                        Map<String, Object> variables,
                                                                                                        Map<String, Object> processVariables,
                                                                                                        List<String> owners,
                                                                                                        QueryContext queryContext) {

        return queryUserTasksByVariables(attributes, variables, processVariables, owners, PROCESS_TYPE, "", queryContext);
    }

}
