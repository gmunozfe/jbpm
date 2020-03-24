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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.jbpm.kie.services.impl.model.ProcessInstanceWithVarsDesc;
import org.jbpm.kie.services.impl.model.UserTaskInstanceWithPotOwnerDesc;
import org.jbpm.shared.services.impl.QueryManager;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.QueryNameCommand;
import org.kie.api.runtime.query.QueryContext;

import static java.util.Collections.singletonMap;

public abstract class AbstractAdvanceRuntimeDataServiceImpl {

    private EntityManagerFactory emf;
    private TransactionalCommandService commandService;

    public AbstractAdvanceRuntimeDataServiceImpl() {
        QueryManager.get().addNamedQueries("META-INF/Servicesorm.xml");
    }
    public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }


    public List<org.jbpm.services.api.model.ProcessInstanceWithVarsDesc> queryProcessByVariables(Map<String, Object> attributes,
                                                                                                 Map<String, Object> variables,
                                                                                                 int processType,
                                                                                                 String varPrefix,
                                                                                                 QueryContext queryContext) {
        EntityManager entityManager = emf.createEntityManager();

        // first step is to filter the data creating a derived tables and pivoting var - rows to columns (only the variables we are interested to filter)
        StringBuilder where = new StringBuilder();
        StringBuilder derivedTables = new StringBuilder();
        for (Map.Entry<String, Object> var : variables.entrySet()) {
            String alias = "TABLE_" + var.getKey();
            String varName = var.getKey();

            derivedTables.append("LEFT JOIN (" +
                                 "SELECT A1.processId, A1.processInstanceId, A1.variableId AS NAME, A1.value AS VALUE \n" +
                                 "FROM VariableInstanceLog A1 \n" +
                                 "LEFT JOIN VariableInstanceLog A2 ON A1.processId = A2.processId AND A1.processInstanceId = A2.processInstanceId AND A1.variableInstanceId = A2.variableInstanceId AND A2.log_date < A1.log_date  \n" +
                                 "WHERE A2.log_date IS NULL \n" +
                                 ") " + alias + " ON " + alias + ".processId = pil.processId AND " + alias + ".processInstanceId = pil.processInstanceId \n");

            addWhereExpression(where);
            where.append(alias + ".NAME = :NAME_" + varName + " AND " + alias + ".VALUE= :VALUE_" + varName + "\n");
        }


        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            addWhereExpression(where);
            where.append(" pil." + entry.getKey() + " = :ATTR_" + entry.getKey() + " ");
        }

        addWhereExpression(where);
        where.append(" pil.processType = :processType ");

        String procSQLString = "SELECT DISTINCT pil.processInstanceId FROM ProcessInstanceLog pil \n " + derivedTables + where + " ORDER BY pil.processInstanceId ASC ";
        procSQLString += (queryContext.getCount() > 0) ? " LIMIT " + queryContext.getCount() + " OFFSET " + queryContext.getOffset() + " \n" : "";

        Query query = entityManager.createNativeQuery(procSQLString);
        for (Map.Entry<String, Object> var : variables.entrySet()) {
            query.setParameter("NAME_" + var.getKey(), varPrefix + var.getKey());
            query.setParameter("VALUE_" + var.getKey(), var.getValue());
        }
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            query.setParameter("ATTR_" + entry.getKey(), entry.getValue());
        }
        query.setParameter("processType", processType);

        List<Number> ids = query.getResultList();
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        // now we get the information

        List<Object[]> procRows = commandService.execute(new QueryNameCommand<List<Object[]>>("GeProcessInstanceByIdList", singletonMap("idList", ids)));
        List<Object[]> varRows = commandService.execute(new QueryNameCommand<List<Object[]>>("GeVariablesByProcessInstanceIdList", singletonMap("idList", ids)));

        int currentVarIdx = 0;
        List<org.jbpm.services.api.model.ProcessInstanceWithVarsDesc> data = new ArrayList<>();
        for (Object[] row : procRows) {
            ProcessInstanceWithVarsDesc pwv = toProcessInstanceWithVarsDesc(row);

            Map<String, Object> vars = new HashMap<>();
            pwv.setVariables(vars);
            while (currentVarIdx < varRows.size() && row[0].equals(varRows.get(currentVarIdx)[0])) {
                String name = (String) varRows.get(currentVarIdx)[1];
                if (name.startsWith(varPrefix)) {
                    vars.put(name.substring(varPrefix.length()), varRows.get(currentVarIdx)[2]);
                }
                currentVarIdx++;
            }
            data.add(pwv);
        }

        entityManager.close();
        return data;
    }

    private ProcessInstanceWithVarsDesc toProcessInstanceWithVarsDesc(Object[] row) {
        return new ProcessInstanceWithVarsDesc(((Number) row[0]).longValue(),
                                               (String) row[1],
                                               (String) row[2],
                                               (String) row[3],
                                               ((Number) row[4]).intValue(),
                                               (String) row[5],
                                               (Date) row[6],
                                               (String) row[7],
                                               (String) row[8]);
    }
    private void addWhereExpression(StringBuilder where) {
        where.append(where.length() == 0 ? "WHERE " : " AND ");
    }

    public List<org.jbpm.services.api.model.UserTaskInstanceWithPotOwnerDesc> queryUserTasksByVariables(Map<String, Object> attributes,
                                                                                                        Map<String, Object> variables,
                                                                                                        Map<String, Object> processVariables,
                                                                                                        List<String> owners,
                                                                                                        int processType,
                                                                                                        String varPrefix,
                                                                                                        QueryContext queryContext) {
        EntityManager entityManager = emf.createEntityManager();

        StringBuilder where = new StringBuilder();
        StringBuilder derivedTables = new StringBuilder();
        for (Map.Entry<String, Object> var : variables.entrySet()) {
            String alias = "V_TABLE_" + var.getKey();
            String varName = var.getKey();
            derivedTables.append("INNER JOIN (\n" +
                                  "SELECT taskId, name, value \n" +
                                  "FROM TaskVariableImpl \n" +
                                  "WHERE type = 0\n" +
                                 ") " + alias + " ON " + alias + ".taskId = task.id  \n");


            addWhereExpression(where);
            where.append(alias + ".NAME = :V_NAME_" + varName + " AND " + alias + ".VALUE= :V_VALUE_" + varName + "\n");
        }

        for (Map.Entry<String, Object> var : processVariables.entrySet()) {
            String alias = "P_TABLE_" + var.getKey();
            String varName = var.getKey();

            derivedTables.append("LEFT JOIN (" +
                                 "SELECT A1.processId, A1.processInstanceId, A1.variableId AS NAME, A1.value AS VALUE \n" +
                                 "FROM VariableInstanceLog A1 \n" +
                                 "LEFT JOIN VariableInstanceLog A2 ON A1.processId = A2.processId AND A1.processInstanceId = A2.processInstanceId AND A1.variableInstanceId = A2.variableInstanceId AND A2.log_date < A1.log_date  \n" +
                                 "WHERE A2.log_date IS NULL \n" +
                                 ") " + alias + " ON " + alias + ".processId = pil.processId AND " + alias + ".processInstanceId = pil.processInstanceId \n");

            addWhereExpression(where);
            where.append(alias + ".NAME = :P_NAME_" + varName + " AND " + alias + ".VALUE= :P_VALUE_" + varName + "\n");
        }

        if (!owners.isEmpty()) {
            derivedTables.append("INNER JOIN ( \n" +
                             "           SELECT DISTINCT po.task_id \n" +
                             "           FROM PeopleAssignments_PotOwners po \n" +
                             "           WHERE po.entity_id IN (:owners) \n" +
                             "           GROUP BY po.task_id \n" +
                             "           HAVING COUNT(po.entity_id) = :num_owners \n" +
                                 ") AS pot ON pot.task_id = task.id ");
        }

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            addWhereExpression(where);
            where.append(" task." + entry.getKey() + " = :ATTR_" + entry.getKey() + " ");
        }

        addWhereExpression(where);
        where.append(" pil.processType = :processType ");

        String procSQLString = "SELECT DISTINCT task.id FROM Task task INNER JOIN ProcessInstanceLog pil ON pil.processInstanceId = task.processInstanceId \n " + derivedTables + where + " ORDER BY id ASC ";
        procSQLString += (queryContext.getCount() > 0) ? " LIMIT " + queryContext.getCount() + " OFFSET " + queryContext.getOffset() + "\n" : "";

        Query query = entityManager.createNativeQuery(procSQLString);
        for (Map.Entry<String, Object> var : variables.entrySet()) {
            query.setParameter("V_NAME_" + var.getKey(), var.getKey());
            query.setParameter("V_VALUE_" + var.getKey(), var.getValue());
        }
        for (Map.Entry<String, Object> var : processVariables.entrySet()) {
            query.setParameter("P_NAME_" + var.getKey(), varPrefix + var.getKey());
            query.setParameter("P_VALUE_" + var.getKey(), var.getValue());
        }
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            query.setParameter("ATTR_" + entry.getKey(), entry.getValue());
        }
        if (!owners.isEmpty()) {
            query.setParameter("num_owners", owners.size());
            query.setParameter("owners", owners);
        }
        query.setParameter("processType", processType);

        List<Number> ids = query.getResultList();
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }


        // query data
        List<Object[]> taskRows = commandService.execute(new QueryNameCommand<List<Object[]>>("GetTasksByIdList", singletonMap("idList", ids)));
        List<Object[]> varRows = commandService.execute(new QueryNameCommand<List<Object[]>>("GetTaskVariablesByTaskIdList", singletonMap("idList", ids)));
        List<Object[]> potRows = commandService.execute(new QueryNameCommand<List<Object[]>>("GetPotentialOwnersByTaskIdList", singletonMap("idList", ids)));
        List<Object[]> varProcSQLRows = commandService.execute(new QueryNameCommand<List<Object[]>>("GeProcessVariablesByTaskIdList", singletonMap("idList", ids)));

        int currentVarIdx = 0;
        int currentPotIdx = 0;
        int currentVarProcIdx = 0;
        List<org.jbpm.services.api.model.UserTaskInstanceWithPotOwnerDesc> data = new ArrayList<>();
        for (Object[] row : taskRows) {
            UserTaskInstanceWithPotOwnerDesc pwv = toUserTaskInstanceWithPotOwnerDesc(row);

            while (currentVarIdx < varRows.size() && row[0].equals(varRows.get(currentVarIdx)[0])) {
                if (((Number) varRows.get(currentVarIdx)[1]).intValue() == 0) {
                    pwv.addInputdata((String) varRows.get(currentVarIdx)[2], varRows.get(currentVarIdx)[3]);
                } else {
                    pwv.addOutputdata((String) varRows.get(currentVarIdx)[2], varRows.get(currentVarIdx)[3]);
                }
                currentVarIdx++;
            }

            pwv.getPotentialOwners().clear();
            while (currentPotIdx < potRows.size() && row[0].equals(potRows.get(currentPotIdx)[0])) {
                pwv.addPotOwner((String) potRows.get(currentPotIdx)[1]);
                currentPotIdx++;
            }

            while (currentVarProcIdx < varProcSQLRows.size() && row[0].equals(varProcSQLRows.get(currentVarProcIdx)[0])) {
                String name = (String) varProcSQLRows.get(currentVarProcIdx)[1];
                Object value = varProcSQLRows.get(currentVarProcIdx)[2];
                if (!varPrefix.isEmpty() && name.startsWith(varPrefix)) {
                    pwv.addExtraData(name.substring(varPrefix.length()), value);
                } else {
                    pwv.addProcessVariable(name, value);
                }
                currentVarProcIdx++;
            }

            data.add(pwv);
        }

        entityManager.close();
        return data;
        

    }

    private UserTaskInstanceWithPotOwnerDesc toUserTaskInstanceWithPotOwnerDesc(Object[] row) {
        return new UserTaskInstanceWithPotOwnerDesc(
                                                    ((Number) row[0]).longValue(), // id
                                                    (String) row[1], // task name
                                                    (String) row[2], // formName
                                                    (String) row[3], // subject
                                                    (String) row[4], // actualOwner_id
                                                    (String) null, // potOwner
                                                    (String) row[5], // correlationKey
                                                    (Date) row[6], // createdOn
                                                    (String) row[7], // createdBy
                                                    (Date) row[8], // expiration time
                                                    (Date) null, // lastModificationDate
                                                    (String) null, // lastModificationUser
                                                    ((Number) row[9]).intValue(), //priority
                                                    ((String) row[10]), // Status
                                                    ((Number) row[11]).longValue(), // processInstanceId
                                                    (String) row[12], // processId
                                                    (String) row[13], // deployment Id
                                                    (String) row[14] // instance description
        );
    }


}
