/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.persistence.scripts;

import java.util.Arrays;
import java.util.Collection;

import org.jbpm.test.persistence.scripts.DatabaseType;
import org.jbpm.test.persistence.scripts.PersistenceUnit;
import org.jbpm.test.persistence.scripts.ScriptsBase;
import org.jbpm.test.persistence.scripts.TestPersistenceContextBase;
import org.jbpm.test.persistence.scripts.util.ScriptFilter;
import org.jbpm.test.persistence.scripts.util.ScriptFilter.Option;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.jbpm.persistence.scripts.TestPersistenceContext.createAndInitContext;
import static org.jbpm.test.persistence.scripts.PersistenceUnit.DB_QUARTZ_VALIDATE;
import static org.jbpm.test.persistence.scripts.PersistenceUnit.DB_TESTING_VALIDATE;
import static org.jbpm.test.persistence.scripts.util.ScriptFilter.filter;
import static org.junit.Assume.assumeTrue;

/**
 * Contains tests that test DDL scripts.
 */
@RunWith(Parameterized.class)
public class DDLScriptsTest extends ScriptsBase {

    @Parameters
    public static Collection<ScriptFilter[]> data() {
        ScriptFilter[] standard = new ScriptFilter[]{ScriptFilter.init(false, true),
                                                     ScriptFilter.init(false, false)};

        ScriptFilter[] sbOracle = new ScriptFilter[]{filter("oracle-springboot-jbpm-schema.sql",
                                                            "quartz_tables_oracle.sql").setSupportedDatabase(DatabaseType.ORACLE)
                                                                                         .setOptions(Option.DISALLOW_EMTPY_RESULTS, Option.THROW_ON_SCRIPT_ERROR),
                                                     filter("oracle-springboot-jbpm-drop-schema.sql",
                                                            "quartz_tables_drop_oracle.sql")};

        ScriptFilter[] sbPg = new ScriptFilter[]{filter("postgresql-springboot-jbpm-schema.sql",
                                                        "quartz_tables_postgres.sql").setSupportedDatabase(DatabaseType.POSTGRESQL)
                                                                                     .setOptions(Option.DISALLOW_EMTPY_RESULTS, Option.THROW_ON_SCRIPT_ERROR),
                                                 filter("postgresql-springboot-jbpm-drop-schema.sql",
                                                        "quartz_tables_drop_postgres.sql")};


        ScriptFilter[] pqlBytea = new ScriptFilter[]{filter("postgresql-bytea-jbpm-schema.sql",
                                                            "quartz_tables_postgres.sql")
                                                                 .setSupportedDatabase(DatabaseType.POSTGRESQL)
                                                                 .setOptions(Option.DISALLOW_EMTPY_RESULTS, Option.THROW_ON_SCRIPT_ERROR),
                                                     filter("postgresql-bytea-jbpm-drop-schema.sql",
                                                            "quartz_tables_drop_postgres.sql")};

        ScriptFilter[] pqlSpringBootBytea = new ScriptFilter[]{filter("postgresql-springboot-bytea-jbpm-schema.sql",
                                                                      "quartz_tables_postgres.sql")
                                                                           .setSupportedDatabase(DatabaseType.POSTGRESQL)
                                                                           .setOptions(Option.DISALLOW_EMTPY_RESULTS, Option.THROW_ON_SCRIPT_ERROR),
                                                               filter("postgresql-springboot-bytea-jbpm-drop-schema.sql",
                                                                      "quartz_tables_drop_postgres.sql")};

        return Arrays.asList(standard, sbOracle, sbPg, pqlBytea, pqlSpringBootBytea);
    }

    private ScriptFilter createScript;
    private ScriptFilter dropScript;

    public DDLScriptsTest(ScriptFilter createScript, ScriptFilter dropScript) {
        this.createScript = createScript;
        this.dropScript = dropScript;
    }

    @Before
    public void prepare() {
        TestPersistenceContextBase scriptRunnerContext = createAndInitContext(PersistenceUnit.SCRIPT_RUNNER);
        DatabaseType dbType = scriptRunnerContext.getDatabaseType();
        assumeTrue("Scripts test not supported this database " + dbType, createScript.isSupportedDatabase(dbType));
    }
    /**
     * Tests that DB schema is created properly using DDL scripts.
     */
    @Test
    public void createAndDropSchemaUsingDDLs() throws Exception {
        executeScriptRunner(DB_DDL_SCRIPTS_RESOURCE_PATH, createScript);
        validateAndPersistProcess();
        validateQuartz();
        executeScriptRunner(DB_DDL_SCRIPTS_RESOURCE_PATH, dropScript);
    }

    protected void validateAndPersistProcess() {
        final TestPersistenceContext dbTestingContext = createAndInitContext(DB_TESTING_VALIDATE);
        try {
            dbTestingContext.startAndPersistSomeProcess(TEST_PROCESS_ID);
            Assert.assertTrue(dbTestingContext.getStoredProcessesCount() == 1);
        } finally {
            dbTestingContext.clean();
        }
    }

    protected void validateQuartz() {
        final TestPersistenceContext dbquartzContext = createAndInitContext(DB_QUARTZ_VALIDATE);
        dbquartzContext.clean();
    }
}