    create table Attachment (
        id bigint not null auto_increment,
        accessType integer,
        attachedAt datetime,
        -- attachedAt datetime(6), to be used with mysql 5.6.4 that supports millis precision
        attachmentContentId bigint not null,
        contentType varchar(255),
        name varchar(255),
        attachment_size integer,
        attachedBy_id varchar(255),
        TaskData_Attachments_Id bigint,
        primary key (id)
    );

    create table AuditTaskImpl (
        id bigint not null auto_increment,
        activationTime datetime,
        -- activationTime datetime(6), to be used with mysql 5.6.4 that supports millis precision
        actualOwner varchar(255),
        createdBy varchar(255),
        createdOn datetime,
        -- createdOn datetime(6), to be used with mysql 5.6.4 that supports millis precision
        deploymentId varchar(255),
        description varchar(255),
        dueDate datetime,
        -- dueDate datetime(6), to be used with mysql 5.6.4 that supports millis precision
        name varchar(255),
        parentId bigint not null,
        priority integer not null,
        processId varchar(255),
        processInstanceId bigint not null,
        processSessionId bigint not null,
        status varchar(255),
        taskId bigint,
        workItemId bigint,
        lastModificationDate datetime,
        primary key (id)
    );

    create table BAMTaskSummary (
        pk bigint not null auto_increment,
        createdDate datetime,
        -- createdDate datetime(6), to be used with mysql 5.6.4 that supports millis precision
        duration bigint,
        endDate datetime,
        -- endDate datetime(6), to be used with mysql 5.6.4 that supports millis precision
        processInstanceId bigint not null,
        startDate datetime,
        -- startDate datetime(6), to be used with mysql 5.6.4 that supports millis precision
        status varchar(255),
        taskId bigint not null,
        taskName varchar(255),
        userId varchar(255),
        OPTLOCK integer,
        primary key (pk)
    );

    create table BooleanExpression (
        id bigint not null auto_increment,
        expression longtext,
        type varchar(255),
        Escalation_Constraints_Id bigint,
        primary key (id)
    );
    
    create table CaseIdInfo (
        id bigint not null auto_increment,
        caseIdPrefix varchar(255),
        currentValue bigint,
        primary key (id)
    );
    
    create table CaseFileDataLog (
        id bigint not null auto_increment,
        caseDefId varchar(255),
        caseId varchar(255),
        itemName varchar(255),
        itemType varchar(255),
        itemValue varchar(255),
        lastModified datetime,
        lastModifiedBy varchar(255),
        primary key (id)
    );

    create table CaseRoleAssignmentLog (
        id bigint not null auto_increment,
        caseId varchar(255),
        entityId varchar(255),
        processInstanceId bigint not null,
        roleName varchar(255),
        type integer not null,
        primary key (id)
    );

    create table Content (
        id bigint not null auto_increment,
        content longblob,
        primary key (id)
    );

    create table ContextMappingInfo (
        mappingId bigint not null auto_increment,
        CONTEXT_ID varchar(255) not null,
        KSESSION_ID bigint not null,
        OWNER_ID varchar(255),
        OPTLOCK integer,
        primary key (mappingId)
    );

    create table CorrelationKeyInfo (
        keyId bigint not null auto_increment,
        name varchar(255),
        processInstanceId bigint not null,
        OPTLOCK integer,
        primary key (keyId)
    );

    create table CorrelationPropertyInfo (
        propertyId bigint not null auto_increment,
        name varchar(255),
        value varchar(255),
        OPTLOCK integer,
        correlationKey_keyId bigint,
        primary key (propertyId)
    );

    create table Deadline (
        id bigint not null auto_increment,
        deadline_date datetime,
        -- deadline_date datetime(6), to be used with mysql 5.6.4 that supports millis precision
        escalated smallint,
        Deadlines_StartDeadLine_Id bigint,
        Deadlines_EndDeadLine_Id bigint,
        primary key (id)
    );

    create table Delegation_delegates (
        task_id bigint not null,
        entity_id varchar(255) not null
    );

    create table DeploymentStore (
        id bigint not null auto_increment,
        attributes varchar(255),
        DEPLOYMENT_ID varchar(255),
        deploymentUnit longtext,
        state integer,
        updateDate datetime,
        -- updateDate datetime(6), to be used with mysql 5.6.4 that supports millis precision
        primary key (id)
    );

    create table ErrorInfo (
        id bigint not null auto_increment,
        message varchar(255),
        stacktrace varchar(5000),
        timestamp datetime,
        -- timestamp datetime(6), to be used with mysql 5.6.4 that supports millis precision
        REQUEST_ID bigint not null,
        primary key (id)
    );

    create table Escalation (
        id bigint not null auto_increment,
        name varchar(255),
        Deadline_Escalation_Id bigint,
        primary key (id)
    );

    create table EventTypes (
        InstanceId bigint not null,
        element varchar(255)
    );
    
    create table ExecutionErrorInfo (
        id bigint not null auto_increment,
        ERROR_ACK smallint,
        ERROR_ACK_AT datetime,
        ERROR_ACK_BY varchar(255),
        ACTIVITY_ID bigint,
        ACTIVITY_NAME varchar(255),
        DEPLOYMENT_ID varchar(255),
        ERROR_INFO longtext,
        ERROR_DATE datetime,
        ERROR_ID varchar(255),
        ERROR_MSG varchar(255),
        INIT_ACTIVITY_ID bigint,
        JOB_ID bigint,
        PROCESS_ID varchar(255),
        PROCESS_INST_ID bigint,
        ERROR_TYPE varchar(255),
        primary key (id)
    );

    create table I18NText (
        id bigint not null auto_increment,
        language varchar(255),
        shortText varchar(255),
        text longtext,
        Task_Subjects_Id bigint,
        Task_Names_Id bigint,
        Task_Descriptions_Id bigint,
        Reassignment_Documentation_Id bigint,
        Notification_Subjects_Id bigint,
        Notification_Names_Id bigint,
        Notification_Documentation_Id bigint,
        Notification_Descriptions_Id bigint,
        Deadline_Documentation_Id bigint,
        primary key (id)
    );

    create table NodeInstanceLog (
        id bigint not null auto_increment,
        connection varchar(255),
        log_date datetime,
        -- log_date datetime(6), to be used with mysql 5.6.4 that supports millis precision
        externalId varchar(255),
        nodeId varchar(255),
        nodeInstanceId varchar(255),
        nodeName varchar(255),
        nodeType varchar(255),
        processId varchar(255),
        processInstanceId bigint not null,
        sla_due_date datetime,
        -- sla_due_date datetime(6), to be used with mysql 5.6.4 that supports millis precision
        slaCompliance integer,
        type integer not null,
        workItemId bigint,
        nodeContainerId varchar(255),
        referenceId bigint,
        primary key (id)
    );

    create table Notification (
        DTYPE varchar(31) not null,
        id bigint not null auto_increment,
        priority integer not null,
        Escalation_Notifications_Id bigint,
        primary key (id)
    );

    create table Notification_BAs (
        task_id bigint not null,
        entity_id varchar(255) not null
    );

    create table Notification_Recipients (
        task_id bigint not null,
        entity_id varchar(255) not null
    );

    create table Notification_email_header (
        Notification_id bigint not null,
        emailHeaders_id bigint not null,
        mapkey varchar(255) not null,
        primary key (Notification_id, mapkey)
    );

    create table OrganizationalEntity (
        DTYPE varchar(31) not null,
        id varchar(255) not null,
        primary key (id)
    );

    create table PeopleAssignments_BAs (
        task_id bigint not null,
        entity_id varchar(255) not null
    );

    create table PeopleAssignments_ExclOwners (
        task_id bigint not null,
        entity_id varchar(255) not null
    );

    create table PeopleAssignments_PotOwners (
        task_id bigint not null,
        entity_id varchar(255) not null
    );

    create table PeopleAssignments_Recipients (
        task_id bigint not null,
        entity_id varchar(255) not null
    );

    create table PeopleAssignments_Stakeholders (
        task_id bigint not null,
        entity_id varchar(255) not null
    );

    create table ProcessInstanceInfo (
        InstanceId bigint not null auto_increment,
        lastModificationDate datetime,
        -- lastModificationDate datetime(6), to be used with mysql 5.6.4 that supports millis precision
        lastReadDate datetime,
        -- lastReadDate datetime(6), to be used with mysql 5.6.4 that supports millis precision
        processId varchar(255),
        processInstanceByteArray longblob,
        startDate datetime,
        -- startDate datetime(6), to be used with mysql 5.6.4 that supports millis precision
        state integer not null,
        OPTLOCK integer,
        primary key (InstanceId)
    );

    create table ProcessInstanceLog (
        id bigint not null auto_increment,
        correlationKey varchar(255),
        duration bigint,
        end_date datetime,
        -- end_date datetime(6), to be used with mysql 5.6.4 that supports millis precision
        externalId varchar(255),
        user_identity varchar(255),
        outcome varchar(255),
        parentProcessInstanceId bigint,
        processId varchar(255),
        processInstanceDescription varchar(255),
        processInstanceId bigint not null,
        processName varchar(255),
        processType integer,
        processVersion varchar(255),
        sla_due_date datetime,
        -- sla_due_date datetime(6), to be used with mysql 5.6.4 that supports millis precision
        slaCompliance integer,
        start_date datetime,
        -- start_date datetime(6), to be used with mysql 5.6.4 that supports millis precision
        status integer,
        primary key (id)
    );

    create table QueryDefinitionStore (
        id bigint not null auto_increment,
        qExpression longtext,
        qName varchar(255),
        qSource varchar(255),
        qTarget varchar(255),
        primary key (id)
    );

    create table Reassignment (
        id bigint not null auto_increment,
        Escalation_Reassignments_Id bigint,
        primary key (id)
    );

    create table Reassignment_potentialOwners (
        task_id bigint not null,
        entity_id varchar(255) not null
    );

    create table RequestInfo (
        id bigint not null auto_increment,
        commandName varchar(255),
        deploymentId varchar(255),
        executions integer not null,
        businessKey varchar(255),
        message varchar(255),
        owner varchar(255),
        priority integer not null,
        processInstanceId bigint,
        requestData longblob,
        responseData longblob,
        retries integer not null,
        status varchar(255),
        timestamp datetime,
        -- timestamp datetime(6), to be used with mysql 5.6.4 that supports millis precision
        primary key (id)
    );

    create table SessionInfo (
        id bigint not null auto_increment,
        lastModificationDate datetime,
        -- lastModificationDate datetime(6), to be used with mysql 5.6.4 that supports millis precision
        rulesByteArray longblob,
        startDate datetime,
        -- startDate datetime(6), to be used with mysql 5.6.4 that supports millis precision
        OPTLOCK integer,
        primary key (id)
    );

    create table Task (
        id bigint not null auto_increment,
        archived smallint,
        allowedToDelegate varchar(255),
        description varchar(255),
        formName varchar(255),
        name varchar(255),
        priority integer not null,
        subTaskStrategy varchar(255),
        subject varchar(255),
        activationTime datetime,
        -- activationTime datetime(6), to be used with mysql 5.6.4 that supports millis precision
        createdOn datetime,
        -- createdOn datetime(6), to be used with mysql 5.6.4 that supports millis precision
        deploymentId varchar(255),
        documentAccessType integer,
        documentContentId bigint not null,
        documentType varchar(255),
        expirationTime datetime,
        -- expirationTime datetime(6), to be used with mysql 5.6.4 that supports millis precision
        faultAccessType integer,
        faultContentId bigint not null,
        faultName varchar(255),
        faultType varchar(255),
        outputAccessType integer,
        outputContentId bigint not null,
        outputType varchar(255),
        parentId bigint not null,
        previousStatus integer,
        processId varchar(255),
        processInstanceId bigint not null,
        processSessionId bigint not null,
        skipable boolean not null,
        status varchar(255),
        workItemId bigint not null,
        taskType varchar(255),
        OPTLOCK integer,
        taskInitiator_id varchar(255),
        actualOwner_id varchar(255),
        createdBy_id varchar(255),
        primary key (id)
    );

    create table TaskDef (
        id bigint not null auto_increment,
        name varchar(255),
        priority integer not null,
        primary key (id)
    );

    create table TaskEvent (
        id bigint not null auto_increment,
        logTime datetime,
        -- logTime datetime(6), to be used with mysql 5.6.4 that supports millis precision
        message varchar(255),
        processInstanceId bigint,
        taskId bigint,
        type varchar(255),
        userId varchar(255),
        OPTLOCK integer,
        workItemId bigint,
        primary key (id)
    );

    create table TaskVariableImpl (
        id bigint not null auto_increment,
        modificationDate datetime,
        -- modificationDate datetime(6), to be used with mysql 5.6.4 that supports millis precision
        name varchar(255),
        processId varchar(255),
        processInstanceId bigint,
        taskId bigint,
        type integer,
        value varchar(4000),
        primary key (id)
    );

    create table VariableInstanceLog (
        id bigint not null auto_increment,
        log_date datetime,
        -- log_date datetime(6), to be used with mysql 5.6.4 that supports millis precision
        externalId varchar(255),
        oldValue varchar(255),
        processId varchar(255),
        processInstanceId bigint not null,
        value varchar(255),
        variableId varchar(255),
        variableInstanceId varchar(255),
        primary key (id)
    );

    create table WorkItemInfo (
        workItemId bigint not null auto_increment,
        creationDate datetime,
        -- creationDate datetime(6), to be used with mysql 5.6.4 that supports millis precision
        name varchar(255),
        processInstanceId bigint not null,
        state bigint not null,
        OPTLOCK integer,
        workItemByteArray longblob,
        primary key (workItemId)
    );

    create table email_header (
        id bigint not null auto_increment,
        body longtext,
        fromAddress varchar(255),
        language varchar(255),
        replyToAddress varchar(255),
        subject varchar(255),
        primary key (id)
    );

    create table task_comment (
        id bigint not null auto_increment,
        addedAt datetime,
        -- addedAt datetime(6), to be used with mysql 5.6.4 that supports millis precision
        text longtext,
        addedBy_id varchar(255),
        TaskData_Comments_Id bigint,
        primary key (id)
    );

    alter table DeploymentStore 
        add constraint UK_85rgskt09thd8mkkfl3tb0y81 unique (DEPLOYMENT_ID);

    alter table QueryDefinitionStore 
        add constraint UK_4ry5gt77jvq0orfttsoghta2j unique (qName);

    alter table Attachment 
        add index IDX_Attachment_Id (attachedBy_id), 
        add constraint FKd5xpm81gxg8n40167lbu5rbfb 
        foreign key (attachedBy_id) 
        references OrganizationalEntity (id);

    alter table Attachment 
        add index IDX_Attachment_DataId (TaskData_Attachments_Id), 
        add constraint FKjj9psk52ifamilliyo16epwpc 
        foreign key (TaskData_Attachments_Id) 
        references Task (id);

    alter table BooleanExpression 
        add index IDX_BoolExpr_Id (Escalation_Constraints_Id), 
        add constraint FKqth56a8k6d8pv6ngsu2vjp4kj 
        foreign key (Escalation_Constraints_Id) 
        references Escalation (id);
        
    alter table CaseIdInfo 
        add constraint UK_CaseIdInfo_1 unique (caseIdPrefix);        

    alter table CorrelationPropertyInfo 
        add index IDX_CorrPropInfo_Id (correlationKey_keyId), 
        add constraint FKbchyl7kb8i6ghvi3dbr86bgo0 
        foreign key (correlationKey_keyId) 
        references CorrelationKeyInfo (keyId);

    alter table Deadline 
        add index IDX_Deadline_StartId (Deadlines_StartDeadLine_Id), 
        add constraint FK361ggw230po88svgfasg36i2w 
        foreign key (Deadlines_StartDeadLine_Id) 
        references Task (id);

    alter table Deadline 
        add index IDX_Deadline_EndId (Deadlines_EndDeadLine_Id), 
        add constraint FKpeiadnoy228t35213t63c3imm 
        foreign key (Deadlines_EndDeadLine_Id) 
        references Task (id);

    alter table Delegation_delegates 
        add index IDX_Delegation_EntityId (entity_id), 
        add constraint FKewkdyi0wrgy9byp6abyglpcxq 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table Delegation_delegates 
        add index IDX_Delegation_TaskId (task_id), 
        add constraint FK85x3trafk3wfbrv719cafr591 
        foreign key (task_id) 
        references Task (id);

    alter table ErrorInfo 
        add index IDX_ErrorInfo_Id (REQUEST_ID), 
        add constraint FKdarp6ushq06q39jmij3fdpdbs 
        foreign key (REQUEST_ID) 
        references RequestInfo (id);

    alter table Escalation 
        add index IDX_Escalation_Id (Deadline_Escalation_Id), 
        add constraint FK37v8ova8ti6jiblda7n6j298e 
        foreign key (Deadline_Escalation_Id) 
        references Deadline (id);

    alter table I18NText 
        add index IDX_I18NText_SubjId (Task_Subjects_Id), 
        add constraint FKcd6eb4q62d9ab8p0di8pb99ch 
        foreign key (Task_Subjects_Id) 
        references Task (id);

    alter table I18NText 
        add index IDX_I18NText_NameId (Task_Names_Id), 
        add constraint FKiogka67sji8fk4cp7a369895i 
        foreign key (Task_Names_Id) 
        references Task (id);

    alter table I18NText 
        add index Task_Descriptions_Id (Task_Descriptions_Id), 
        add constraint FKrisdlmalotmk64mdeqpo4s5m0 
        foreign key (Task_Descriptions_Id) 
        references Task (id);

    alter table I18NText 
        add index IDX_I18NText_ReassignId (Reassignment_Documentation_Id), 
        add constraint FKqxgws3fnukyqlaet11tivqg5u 
        foreign key (Reassignment_Documentation_Id) 
        references Reassignment (id);

    alter table I18NText 
        add index IDX_I18NText_NotSubjId (Notification_Subjects_Id), 
        add constraint FKthf8ix3t3opf9hya1s04hwsx8 
        foreign key (Notification_Subjects_Id) 
        references Notification (id);

    alter table I18NText 
        add index IDX_I18NText_NotNamId (Notification_Names_Id), 
        add constraint FKg2jsybeuc8pbj8ek8xwxutuyo 
        foreign key (Notification_Names_Id) 
        references Notification (id);

    alter table I18NText 
        add index IDX_I18NText_NotDocId (Notification_Documentation_Id), 
        add constraint FKp0m7uhipskrljktvfeubdgfid 
        foreign key (Notification_Documentation_Id) 
        references Notification (id);

    alter table I18NText 
        add index IDX_I18NText_NotDescrId (Notification_Descriptions_Id), 
        add constraint FK6k8hmfvhko069970eghiy2ifp 
        foreign key (Notification_Descriptions_Id) 
        references Notification (id);

    alter table I18NText 
        add index IDX_I18NText_DeadDocId (Deadline_Documentation_Id), 
        add constraint FK8wn7sw34q6bifsi1pvl2b1yyb 
        foreign key (Deadline_Documentation_Id) 
        references Deadline (id);

    alter table Notification 
        add index IDX_Not_EscId (Escalation_Notifications_Id), 
        add constraint FKoxq5uqfg4ylwyijsg2ubyflna 
        foreign key (Escalation_Notifications_Id) 
        references Escalation (id);

    alter table Notification_BAs 
        add index IDX_NotBAs_Entity (entity_id), 
        add constraint FK378pb1cvjv54w4ljqpw99s3wr 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table Notification_BAs 
        add index IDX_NotBAs_Task (task_id), 
        add constraint FKb123fgeomc741s9yc014421yy 
        foreign key (task_id) 
        references Notification (id);

    alter table Notification_Recipients 
        add index IDX_NotRec_Entity (entity_id), 
        add constraint FKot769nimyq1jvw0m61pgsq5g3 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table Notification_Recipients 
        add index IDX_NotRec_Task (task_id), 
        add constraint FKn7v944d0hw37bh0auv4gr3hsf 
        foreign key (task_id) 
        references Notification (id);

    alter table Notification_email_header 
        add constraint IDX_NotEmail_Header unique  (emailHeaders_id), 
        add constraint FKd74pdu41avy2f7a8qyp7wn2n 
        foreign key (emailHeaders_id) 
        references email_header (id);

    alter table Notification_email_header 
        add index IDX_NotEmail_Not (Notification_id), 
        add constraint FKfdnoyp8rl0kxu29l4pyaa5566 
        foreign key (Notification_id) 
        references Notification (id);

    alter table PeopleAssignments_BAs 
        add index IDX_PAsBAs_Entity (entity_id), 
        add constraint FKa90cdfgc4km384n1ataqigq67 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table PeopleAssignments_BAs 
        add index IDX_PAsBAs_Task (task_id), 
        add constraint FKt4xs0glwhbsa0xwg69r6xduv9 
        foreign key (task_id) 
        references Task (id);

    alter table PeopleAssignments_ExclOwners 
        add index IDX_PAsExcl_Entity (entity_id), 
        add constraint FK5ituvd6t8uvp63hsx6282xo6h 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table PeopleAssignments_ExclOwners 
        add index IDX_PAsExcl_Task (task_id), 
        add constraint FKqxbjm1b3dl7w8w2f2y6sk0fv8 
        foreign key (task_id) 
        references Task (id);

    alter table PeopleAssignments_PotOwners 
        add index IDX_PAsPot_Entity (entity_id), 
        add constraint FKsa3rrrjsm1qw98ajbbu2s7cjr 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table PeopleAssignments_PotOwners 
        add index IDX_PAsPot_Task (task_id), 
        add constraint FKh8oqmk4iuh2pmpgby6g8r3jd1 
        foreign key (task_id) 
        references Task (id);

    alter table PeopleAssignments_Recipients 
        add index IDX_PAsRecip_Entity (entity_id), 
        add constraint FKrd0h9ud1bhs9waf2mdmiv6j2r 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table PeopleAssignments_Recipients 
        add index IDX_PAsRecip_Task (task_id), 
        add constraint FK9gnbv6bplxkxoedj35vg8n7ir 
        foreign key (task_id) 
        references Task (id);

    alter table PeopleAssignments_Stakeholders 
        add index IDX_PAsStake_Entity (entity_id), 
        add constraint FK9uy76cu650rg1nnkrtjwj1e9t 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table PeopleAssignments_Stakeholders 
        add index IDX_PAsStake_Task (task_id), 
        add constraint FKaeyk4nwslvx0jywjomjq7083i 
        foreign key (task_id) 
        references Task (id);

    alter table Reassignment 
        add index IDX_Reassign_Esc (Escalation_Reassignments_Id), 
        add constraint FKessy30safh44b30f1cfoujv2k 
        foreign key (Escalation_Reassignments_Id) 
        references Escalation (id);

    alter table Reassignment_potentialOwners 
        add index IDX_ReassignPO_Entity (entity_id), 
        add constraint FKsqrmpvehlc4qe9i0km22nmkjl 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table Reassignment_potentialOwners 
        add index IDX_ReassignPO_Task (task_id), 
        add constraint FKftegfexshix752bh2jfxf6bnh 
        foreign key (task_id) 
        references Reassignment (id);

    alter table Task 
        add index IDX_Task_Initiator (taskInitiator_id), 
        add constraint FK48d1bfgwf0jqow1yk8ku4xcpi 
        foreign key (taskInitiator_id) 
        references OrganizationalEntity (id);

    alter table Task 
        add index IDX_Task_ActualOwner (actualOwner_id), 
        add constraint FKpmkxvqq63aed2y2boruu53a0s 
        foreign key (actualOwner_id) 
        references OrganizationalEntity (id);

    alter table Task 
        add index IDX_Task_CreatedBy (createdBy_id), 
        add constraint FKexuboqnbla7jfyyesyo61ucmb 
        foreign key (createdBy_id) 
        references OrganizationalEntity (id);

    alter table task_comment 
        add index IDX_TaskComments_CreatedBy (addedBy_id), 
        add constraint FKqb4mkarf209y9546w7n75lb7a 
        foreign key (addedBy_id) 
        references OrganizationalEntity (id);

    alter table task_comment 
        add index IDX_TaskComments_Id (TaskData_Comments_Id), 
        add constraint FKm2mwc1ukcpdsiqwgkoroy6ise 
        foreign key (TaskData_Comments_Id) 
        references Task (id);

    create index IDX_Task_processInstanceId on Task(processInstanceId);
    create index IDX_Task_processId on Task(processId);
    create index IDX_Task_status on Task(status);
    create index IDX_Task_archived on Task(archived);
    create index IDX_Task_workItemId on Task(workItemId);
    
    create index IDX_EventTypes_element ON EventTypes(element);
    create index IDX_EventTypes_compound ON EventTypes(InstanceId, element);

    create index IDX_CMI_Context ON ContextMappingInfo(CONTEXT_ID);    
    create index IDX_CMI_KSession ON ContextMappingInfo(KSESSION_ID);    
    create index IDX_CMI_Owner ON ContextMappingInfo(OWNER_ID);
    
    create index IDX_RequestInfo_timestamp ON RequestInfo(timestamp);
    create index IDX_RequestInfo_owner ON RequestInfo(owner);
    
    create index IDX_BAMTaskSumm_createdDate on BAMTaskSummary(createdDate);
    create index IDX_BAMTaskSumm_duration on BAMTaskSummary(duration);
    create index IDX_BAMTaskSumm_endDate on BAMTaskSummary(endDate);
    create index IDX_BAMTaskSumm_pInstId on BAMTaskSummary(processInstanceId);
    create index IDX_BAMTaskSumm_startDate on BAMTaskSummary(startDate);
    create index IDX_BAMTaskSumm_status on BAMTaskSummary(status);
    create index IDX_BAMTaskSumm_taskId on BAMTaskSummary(taskId);
    create index IDX_BAMTaskSumm_taskName on BAMTaskSummary(taskName);
    create index IDX_BAMTaskSumm_userId on BAMTaskSummary(userId);
    
    create index IDX_PInstLog_duration on ProcessInstanceLog(duration);
    create index IDX_PInstLog_end_date on ProcessInstanceLog(end_date);
    create index IDX_PInstLog_extId on ProcessInstanceLog(externalId);
    create index IDX_PInstLog_user_identity on ProcessInstanceLog(user_identity);
    create index IDX_PInstLog_outcome on ProcessInstanceLog(outcome);
    create index IDX_PInstLog_parentPInstId on ProcessInstanceLog(parentProcessInstanceId);
    create index IDX_PInstLog_pId on ProcessInstanceLog(processId);
    create index IDX_PInstLog_pInsteDescr on ProcessInstanceLog(processInstanceDescription);
    create index IDX_PInstLog_pInstId on ProcessInstanceLog(processInstanceId);
    create index IDX_PInstLog_pName on ProcessInstanceLog(processName);
    create index IDX_PInstLog_pVersion on ProcessInstanceLog(processVersion);
    create index IDX_PInstLog_start_date on ProcessInstanceLog(start_date);
    create index IDX_PInstLog_status on ProcessInstanceLog(status);
    create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);

    create index IDX_VInstLog_pInstId on VariableInstanceLog(processInstanceId);
    create index IDX_VInstLog_varId on VariableInstanceLog(variableId);
    create index IDX_VInstLog_pId on VariableInstanceLog(processId);

    create index IDX_NInstLog_pInstId on NodeInstanceLog(processInstanceId);
    create index IDX_NInstLog_nodeType on NodeInstanceLog(nodeType);
    create index IDX_NInstLog_pId on NodeInstanceLog(processId);

    create index IDX_ErrorInfo_pInstId on ExecutionErrorInfo(PROCESS_INST_ID);
    create index IDX_ErrorInfo_errorAck on ExecutionErrorInfo(ERROR_ACK);

    create index IDX_AuditTaskImpl_taskId on AuditTaskImpl(taskId);
    create index IDX_AuditTaskImpl_pInstId on AuditTaskImpl(processInstanceId);
    create index IDX_AuditTaskImpl_workItemId on AuditTaskImpl(workItemId);
    create index IDX_AuditTaskImpl_name on AuditTaskImpl(name);
    create index IDX_AuditTaskImpl_processId on AuditTaskImpl(processId);
    create index IDX_AuditTaskImpl_status on AuditTaskImpl(status);

    create index IDX_TaskVariableImpl_taskId on TaskVariableImpl(taskId);
    create index IDX_TaskVariableImpl_pInstId on TaskVariableImpl(processInstanceId);
    create index IDX_TaskVariableImpl_processId on TaskVariableImpl(processId);
