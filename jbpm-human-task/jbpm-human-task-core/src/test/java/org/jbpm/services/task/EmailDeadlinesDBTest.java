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
package org.jbpm.services.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.identity.DBUserInfoImpl;
import org.jbpm.services.task.identity.DefaultUserInfo;
import org.jbpm.services.task.impl.TaskDeadlinesServiceImpl;
import org.kie.test.util.db.DataSourceFactory;
import org.kie.test.util.db.PoolingDataSourceWrapper;
import org.junit.After;
import org.junit.Before;
import org.kie.internal.task.api.InternalTaskService;


public class EmailDeadlinesDBTest extends EmailDeadlinesBaseTest {

	private PoolingDataSourceWrapper pds;
	private EntityManagerFactory emf;
	
	@Before
	public void setup() {
	    pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory( "org.jbpm.services.task" );
        super.setup();
        
	    prepareDb();
                 
        Properties props = new Properties();
        props.setProperty(DBUserInfoImpl.DS_JNDI_NAME, "jdbc/jbpm-ds");
        props.setProperty(DBUserInfoImpl.NAME_QUERY, "select name from Users where userId = ?");
        props.setProperty(DBUserInfoImpl.EMAIL_QUERY, "select email from Users where userId = ?");
        props.setProperty(DBUserInfoImpl.LANG_QUERY, "select lang from Users where userId = ? or email = ?");
        props.setProperty(DBUserInfoImpl.HAS_EMAIL_QUERY, "select email from UserGroups where groupId = ?");
        props.setProperty(DBUserInfoImpl.MEMBERS_QUERY, "select userId from UserGroups where groupId = ?");
        props.setProperty(DBUserInfoImpl.ID_QUERY, "select userId from Users where email = ?");
        
        DBUserInfoImpl userInfo = new DBUserInfoImpl(props);
                
        this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
                                                .entityManagerFactory(emf)
                                                .userInfo(userInfo)
                                                .getTaskService();

	}
	
    protected void prepareDb() {
        try {
            Connection conn = pds.getConnection();
            String createUserTableSql = "create table Users (userId varchar(255), email varchar(255), lang varchar(255), name varchar(255))";
            PreparedStatement st = conn.prepareStatement(createUserTableSql);
            st.execute();

            String createGroupTableSql = "create table UserGroups (groupId varchar(255), userId varchar(255), email varchar(255))";
            st = conn.prepareStatement(createGroupTableSql);
            st.execute();

            DefaultUserInfo ui = new DefaultUserInfo(true);
            // insert user rows
            String insertUser = "insert into Users (userId, email, lang, name) values (?, ?, ?, ?)";
            st = conn.prepareStatement(insertUser);
            
            st.setString(1, "Darth Vader");
            st.setString(2, "darth@domain.com");
            st.setString(3, "es-ES");
            st.setString(4, "Darth Vader");
            st.execute();
            
            st = conn.prepareStatement(insertUser);
            st.setString(1, "tony@domain.com");
            st.setString(2, "tony@domain.com");
            st.setString(3, "fr-FR");
            st.setString(4, "Tony Stark");
            st.execute();

            // insert group rows
            String insertGroup = "insert into UserGroups (groupId, userId, email) values (?, ?, ?)";
            st = conn.prepareStatement(insertGroup);
            st.setString(1, "PM");
            st.setString(2, "john");
            st.setString(3, "pm@jbpm.org");
            st.execute();


            st.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    protected void cleanDb() {
        try {
            Connection conn = pds.getConnection();
            String dropUserTableSql = "drop table Users";
            PreparedStatement st = conn.prepareStatement(dropUserTableSql);
            st.execute();

            String dropGroupTableSql = "drop table UserGroups";
            st = conn.prepareStatement(dropGroupTableSql);

            st.execute();

            st.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

	
	@After
	public void clean() {
		TaskDeadlinesServiceImpl.reset();
		super.tearDown();
		if (emf != null) {
			emf.close();
		}
		if (pds != null) {
			pds.close();
		}
	}
	
	protected Properties loadDataSourceProperties() {

        InputStream propsInputStream = getClass().getResourceAsStream(DATASOURCE_PROPERTIES);

        Properties dsProps = new Properties();
        if (propsInputStream != null) {
            try {
                dsProps.load(propsInputStream);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return dsProps;
    }
}
