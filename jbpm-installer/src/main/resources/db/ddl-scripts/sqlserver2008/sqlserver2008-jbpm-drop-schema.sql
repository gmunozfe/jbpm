alter table Attachment drop constraint FKd5xpm81gxg8n40167lbu5rbfb;
alter table Attachment drop constraint FKjj9psk52ifamilliyo16epwpc;
alter table BooleanExpression drop constraint FKqth56a8k6d8pv6ngsu2vjp4kj;
alter table CorrelationPropertyInfo drop constraint FKbchyl7kb8i6ghvi3dbr86bgo0;
alter table Deadline drop constraint FK361ggw230po88svgfasg36i2w;
alter table Deadline drop constraint FKpeiadnoy228t35213t63c3imm;
alter table Delegation_delegates drop constraint FKewkdyi0wrgy9byp6abyglpcxq;
alter table Delegation_delegates drop constraint FK85x3trafk3wfbrv719cafr591;
alter table ErrorInfo drop constraint FKdarp6ushq06q39jmij3fdpdbs;
alter table Escalation drop constraint FK37v8ova8ti6jiblda7n6j298e;
alter table EventTypes drop constraint FKj0o3uve2nqo5yrjwrkc9jfttq;
alter table I18NText drop constraint FKcd6eb4q62d9ab8p0di8pb99ch;
alter table I18NText drop constraint FKiogka67sji8fk4cp7a369895i;
alter table I18NText drop constraint FKrisdlmalotmk64mdeqpo4s5m0;
alter table I18NText drop constraint FKqxgws3fnukyqlaet11tivqg5u;
alter table I18NText drop constraint FKthf8ix3t3opf9hya1s04hwsx8;
alter table I18NText drop constraint FKg2jsybeuc8pbj8ek8xwxutuyo;
alter table I18NText drop constraint FKp0m7uhipskrljktvfeubdgfid;
alter table I18NText drop constraint FK6k8hmfvhko069970eghiy2ifp;
alter table I18NText drop constraint FK8wn7sw34q6bifsi1pvl2b1yyb;
alter table Notification drop constraint FKoxq5uqfg4ylwyijsg2ubyflna;
alter table Notification_BAs drop constraint FK378pb1cvjv54w4ljqpw99s3wr;
alter table Notification_BAs drop constraint FKb123fgeomc741s9yc014421yy;
alter table Notification_email_header drop constraint FKd74pdu41avy2f7a8qyp7wn2n;
alter table Notification_email_header drop constraint FKfdnoyp8rl0kxu29l4pyaa5566;
alter table Notification_Recipients drop constraint FKot769nimyq1jvw0m61pgsq5g3;
alter table Notification_Recipients drop constraint FKn7v944d0hw37bh0auv4gr3hsf;
alter table PeopleAssignments_BAs drop constraint FKa90cdfgc4km384n1ataqigq67;
alter table PeopleAssignments_BAs drop constraint FKt4xs0glwhbsa0xwg69r6xduv9;
alter table PeopleAssignments_ExclOwners drop constraint FK5ituvd6t8uvp63hsx6282xo6h;
alter table PeopleAssignments_ExclOwners drop constraint FKqxbjm1b3dl7w8w2f2y6sk0fv8;
alter table PeopleAssignments_PotOwners drop constraint FKsa3rrrjsm1qw98ajbbu2s7cjr;
alter table PeopleAssignments_PotOwners drop constraint FKh8oqmk4iuh2pmpgby6g8r3jd1;
alter table PeopleAssignments_Recipients drop constraint FKrd0h9ud1bhs9waf2mdmiv6j2r;
alter table PeopleAssignments_Recipients drop constraint FK9gnbv6bplxkxoedj35vg8n7ir;
alter table PeopleAssignments_Stakeholders drop constraint FK9uy76cu650rg1nnkrtjwj1e9t;
alter table PeopleAssignments_Stakeholders drop constraint FKaeyk4nwslvx0jywjomjq7083i;
alter table Reassignment drop constraint FKessy30safh44b30f1cfoujv2k;
alter table Reassignment_potentialOwners drop constraint FKsqrmpvehlc4qe9i0km22nmkjl;
alter table Reassignment_potentialOwners drop constraint FKftegfexshix752bh2jfxf6bnh;
alter table Task drop constraint FK48d1bfgwf0jqow1yk8ku4xcpi;
alter table Task drop constraint FKpmkxvqq63aed2y2boruu53a0s;
alter table Task drop constraint FKexuboqnbla7jfyyesyo61ucmb;
alter table task_comment drop constraint FKqb4mkarf209y9546w7n75lb7a;
alter table task_comment drop constraint FKm2mwc1ukcpdsiqwgkoroy6ise;
drop table Attachment;
drop table AuditTaskImpl;
drop table BAMTaskSummary;
drop table BooleanExpression;
drop table CaseFileDataLog;
drop table CaseIdInfo;
drop table CaseRoleAssignmentLog;
drop table Content;
drop table ContextMappingInfo;
drop table CorrelationKeyInfo;
drop table CorrelationPropertyInfo;
drop table Deadline;
drop table Delegation_delegates;
drop table DeploymentStore;
drop table email_header;
drop table ErrorInfo;
drop table Escalation;
drop table EventTypes;
drop table ExecutionErrorInfo;
drop table I18NText;
drop table NodeInstanceLog;
drop table Notification;
drop table Notification_BAs;
drop table Notification_email_header;
drop table Notification_Recipients;
drop table OrganizationalEntity;
drop table PeopleAssignments_BAs;
drop table PeopleAssignments_ExclOwners;
drop table PeopleAssignments_PotOwners;
drop table PeopleAssignments_Recipients;
drop table PeopleAssignments_Stakeholders;
drop table ProcessInstanceInfo;
drop table ProcessInstanceLog;
drop table QueryDefinitionStore;
drop table Reassignment;
drop table Reassignment_potentialOwners;
drop table RequestInfo;
drop table SessionInfo;
drop table Task;
drop table task_comment;
drop table TaskDef;
drop table TaskEvent;
drop table TaskVariableImpl;
drop table VariableInstanceLog;
drop table WorkItemInfo;
