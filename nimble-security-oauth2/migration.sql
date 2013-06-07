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


--oauth_client_details
insert into oauth_client_details
(select k.key as client_id, ca.name as name, ca.app_type, ca.description, null as resource_ids,  k.secret as client_secret, 'read,write' as scope, 'authorization_code,refresh_token' as authorized_grant_types, cav.value as redirect_uri,
'ROLE_CLIENT' as authorities, 600 as access_token_validity, 7776000 as refresh_token_validity, null as additional_information,
ca.created_at as created, ca.updated_at as updated
from api_import.client_applications ca
inner join api_import.`keys` k on ca.id=k.client_application_id
inner join api_import.client_application_label_values cav on k.client_application_id=cav.client_application_id)