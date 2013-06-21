/*
drop table acl_entry;
drop table acl_object_identity;
drop table acl_sid;
drop table acl_class;

drop table authorities;
drop table group_authorities;
drop table group_members;
drop table groups;
drop table oauth_access_token;
drop table oauth_code;
drop table oauth_refresh_token;
drop table users;
drop table oauth_client_details;
drop table persistent_logins;
*/

--remove all foreign keys so can do table manipulation
alter table authorities drop foreign key fk_users_username;
alter table group_authorities drop foreign key fk_grp_auth_grp_id;
alter table group_members drop foreign key fk_grp_member_grp_id;
alter table oauth2_access_token drop foreign key fk_access_authorization_id;
alter table oauth2_authentication drop foreign key fk_auth_id_authorization_id;
alter table oauth2_authorization_request drop foreign key fk_authorization_auth_id;
alter table oauth2_authorization_request drop foreign key fk_authorization_client_id;
alter table oauth2_refresh_token drop foreign key fk_refresh_authorization_id;


DROP TABLE users;
CREATE TABLE users ( username varchar(50) NOT NULL, password varchar(50) NOT NULL, enabled tinyint(1) NOT NULL, PRIMARY KEY (username) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
insert into users (username, password, enabled) values ('default_user', 'NULL_VALUE', true);

DROP TABLE authorities;
CREATE TABLE authorities ( username varchar(50) NOT NULL, authority varchar(50) NOT NULL, CONSTRAINT authorities_idx_1 UNIQUE (username, authority) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
insert into authorities (username, authority) values ('default_user', 'ROLE_USER');

DROP TABLE oauth_code;
CREATE TABLE oauth_code ( code varchar(255) NOT NULL, authentication blob NOT NULL, PRIMARY KEY (code) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE oauth_client_details;
CREATE TABLE oauth_client_details ( client_id varchar(256) NOT NULL, resource_ids varchar(1024), client_secret varchar(256), scope varchar(256), authorized_grant_types varchar(256), web_server_redirect_uri varchar(1024), authorities varchar(256), access_token_validity int DEFAULT '0', refresh_token_validity int DEFAULT '0', additional_information varchar(4096), PRIMARY KEY (client_id) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE persistent_logins;
CREATE TABLE persistent_logins ( username varchar(64) NOT NULL, series varchar(64) NOT NULL, token varchar(64) NOT NULL, last_used timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY (series) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE groups;
CREATE TABLE groups ( id bigint unsigned NOT NULL AUTO_INCREMENT, group_name varchar(50) NOT NULL, PRIMARY KEY (id) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
insert into groups (id, group_name) values (1, 'default_group');

DROP TABLE group_authorities;
CREATE TABLE group_authorities ( group_id bigint unsigned NOT NULL, authority varchar(50) NOT NULL, INDEX group_id (group_id) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE group_members;
CREATE TABLE group_members ( id bigint unsigned NOT NULL AUTO_INCREMENT, username varchar(50) NOT NULL, group_id bigint unsigned NOT NULL, PRIMARY KEY (id), INDEX group_id (group_id) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE oauth2_access_token;
CREATE TABLE oauth2_access_token ( id int NOT NULL AUTO_INCREMENT, access_token varchar(256), token_type varchar(256), scope varchar(256), expiration datetime, refresh_token varchar(256), authentication_id varchar(64) NOT NULL, is_encrypted tinyint(1), additional_info blob, created_date datetime, updated_date datetime, PRIMARY KEY (id), CONSTRAINT unq_access_token UNIQUE (access_token), CONSTRAINT unq_at_refresh_token UNIQUE (refresh_token), INDEX fk_access_authorization_id (authentication_id) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE oauth2_authentication;
CREATE TABLE oauth2_authentication ( oauth2AuthenticationId varchar(64) NOT NULL, username varchar(256) NOT NULL, authenticated tinyint(1), principal blob, nimble_token varchar(64) NOT NULL, authorities varchar(256), created_date datetime DEFAULT CURRENT_TIMESTAMP, updated_date datetime DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (oauth2AuthenticationId)) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE oauth2_authorization;
CREATE TABLE oauth2_authorization ( id varchar(64) NOT NULL, authenticated tinyint(1), details blob, created_date datetime DEFAULT CURRENT_TIMESTAMP, updated_date datetime DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE oauth2_authorization_request;
CREATE TABLE oauth2_authorization_request ( id varchar(64) NOT NULL, client_id varchar(256) NOT NULL, username varchar(256) NOT NULL, approved tinyint(1), scope varchar(256), resource_ids varchar(256), authorities varchar(256), redirect_uri varchar(256), auth_params blob, approve_params blob, created_date datetime DEFAULT CURRENT_TIMESTAMP, updated_date datetime DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id), INDEX fk_authorization_client_id (client_id) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE oauth2_refresh_token;
CREATE TABLE oauth2_refresh_token ( id int NOT NULL AUTO_INCREMENT, refresh_token varchar(256), expiration datetime, authentication_id varchar(64) NOT NULL, times_used int DEFAULT '0', created_date datetime DEFAULT CURRENT_TIMESTAMP, updated_date datetime DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id), CONSTRAINT unq_refresh_token UNIQUE (refresh_token), INDEX fk_refresh_authorization_id (authentication_id) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE oauth_client_details;
CREATE TABLE oauth_client_details ( client_id varchar(256) NOT NULL, name varchar(64), app_type varchar(64), description text, resource_ids varchar(1024), client_secret varchar(256), scope varchar(256), authorized_grant_types varchar(256), web_server_redirect_uri varchar(1024), authorities varchar(256), access_token_validity int DEFAULT '0', refresh_token_validity int DEFAULT '0', additional_information varchar(4096), created datetime, updated datetime, PRIMARY KEY (client_id) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE oauth_code;
CREATE TABLE oauth_code ( code varchar(255) NOT NULL, authentication blob NOT NULL, PRIMARY KEY (code) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE persistent_logins;
CREATE TABLE persistent_logins ( username varchar(64) NOT NULL, series varchar(64) NOT NULL, token varchar(64) NOT NULL, last_used timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY (series) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE users;
CREATE TABLE users ( username varchar(50) NOT NULL, password varchar(50) NOT NULL, enabled tinyint(1) NOT NULL, PRIMARY KEY (username) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
insert into users (username, password, enabled) values ('default_user', 'NULL_VALUE', true);



--add all foreign keys
alter table authorities add constraint fk_users_username foreign key (username) references  users (username);
alter table group_authorities add constraint fk_grp_auth_grp_id foreign key (group_id) references  groups (id);
alter table group_members add constraint fk_grp_member_grp_id foreign key (group_id) references  groups (id);
alter table oauth2_access_token add constraint fk_access_authorization_id foreign key (authentication_id) references oauth2_authorization (id);
alter table oauth2_authentication add constraint fk_auth_id_authorization_id foreign key (oauth2AuthenticationId) references oauth2_authorization (id);
alter table oauth2_authorization_request add constraint fk_authorization_auth_id foreign key (id) references oauth2_authorization (id);
alter table oauth2_authorization_request add constraint fk_authorization_client_id foreign key (client_id) references oauth_client_details (client_id);
alter table oauth2_refresh_token add constraint fk_refresh_authorization_id foreign key (authentication_id) references oauth2_authorization (id);


--triggers
DROP TRIGGER ACCESS_TOKEN_ON_BEFORE_CREATE;
--/
CREATE TRIGGER ACCESS_TOKEN_ON_BEFORE_CREATE
BEFORE INSERT ON
oauth2_access_token
FOR EACH ROW BEGIN
set NEW.created_date=current_timestamp;
set NEW.updated_date=current_timestamp;
END
/
DROP TRIGGER ACCESS_TOKEN_ON_BEFORE_UPDATE;
--/
CREATE TRIGGER ACCESS_TOKEN_ON_BEFORE_UPDATE
BEFORE UPDATE ON
oauth2_access_token
FOR EACH ROW set NEW.updated_date=current_timestamp
/
DROP TRIGGER AUTHORIZATION_ON_BEFORE_CREATE;
--/
CREATE TRIGGER AUTHORIZATION_ON_BEFORE_CREATE
BEFORE INSERT ON
oauth2_authorization
FOR EACH ROW BEGIN
set NEW.created_date=current_timestamp;
set NEW.updated_date=current_timestamp;
END
/
DROP TRIGGER AUTHORIZATION_ON_BEFORE_UPDATE;
--/
CREATE TRIGGER AUTHORIZATION_ON_BEFORE_UPDATE
BEFORE UPDATE ON
oauth2_authorization
FOR EACH ROW set NEW.updated_date=current_timestamp
/
DROP TRIGGER AUTHORIZATION_REQ_ON_BEFORE_CREATE;
--/
CREATE TRIGGER AUTHORIZATION_REQ_ON_BEFORE_CREATE
BEFORE INSERT ON
oauth2_authorization_request
FOR EACH ROW BEGIN
set NEW.created_date=current_timestamp;
set NEW.updated_date=current_timestamp;
END
/
DROP TRIGGER AUTHORIZATION_REQ_ON_BEFORE_UPDATE;
--/
CREATE TRIGGER AUTHORIZATION_REQ_ON_BEFORE_UPDATE
BEFORE UPDATE ON
oauth2_authorization_request
FOR EACH ROW set NEW.updated_date=current_timestamp
/
DROP TRIGGER AUTH_ON_BEFORE_CREATE;
--/
CREATE TRIGGER AUTH_ON_BEFORE_CREATE
BEFORE INSERT ON
oauth2_authentication
FOR EACH ROW BEGIN
set NEW.created_date=current_timestamp;
set NEW.updated_date=current_timestamp;
END
/
DROP TRIGGER AUTH_ON_BEFORE_UPDATE;
--/
CREATE TRIGGER AUTH_ON_BEFORE_UPDATE
BEFORE UPDATE ON
oauth2_authentication
FOR EACH ROW set NEW.updated_date=current_timestamp
/
DROP TRIGGER REFRESH_TOKEN_ON_BEFORE_CREATE;
--/
CREATE TRIGGER REFRESH_TOKEN_ON_BEFORE_CREATE
BEFORE INSERT ON
oauth2_refresh_token
FOR EACH ROW BEGIN
set NEW.created_date=current_timestamp;
set NEW.updated_date=current_timestamp;
END
/
DROP TRIGGER REFRESH_TOKEN_ON_BEFORE_UPDATE;
--/
CREATE TRIGGER REFRESH_TOKEN_ON_BEFORE_UPDATE
BEFORE UPDATE ON
oauth2_refresh_token
FOR EACH ROW set NEW.updated_date=current_timestamp
/

--test client account
insert into oauth_client_details (client_id, name, app_type, description, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, created, updated) values ('nimble', 'Migration Test App', 'web', null, null, 'n1mbl3s3cr37', 'read,write', 'authorization_code,refresh_token', 'http://localhost:8080/', 'ROLE_CLIENT', 600, 7776000, null, null, null);

--MAKE SURE TO UPDATE THE APIGEE DB NAME IF NEED BE BEFORE RUNNING
--oauth_client_details
insert into oauth_client_details
(select k.key as client_id, ca.name as name, ca.app_type, ca.description, null as resource_ids,  k.secret as client_secret, 'read,write' as scope, 'authorization_code,refresh_token' as authorized_grant_types, cav.value as redirect_uri,
'ROLE_CLIENT' as authorities, 600 as access_token_validity, 7776000 as refresh_token_validity, null as additional_information,
ca.created_at as created, ca.updated_at as updated
from apigeerailsdb.client_applications ca
inner join apigeerailsdb.`keys` k on ca.id=k.client_application_id
inner join apigeerailsdb.client_application_label_values cav on k.client_application_id=cav.client_application_id)


