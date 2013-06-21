--IN APIGEE DATA DB
--make sure refresh_token table has no dupe tokens
create TEMPORARY table dupe_refresh as (select refresh_token from o_two_refresh_tokens group by refresh_token having count(1) > 1);
delete from o_two_refresh_tokens where refresh_token IN (select refresh_token from dupe_refresh);
drop table dupe_refresh;

--populate refresh_token with client id
alter table o_two_refresh_tokens add column `clientid` varchar(255) DEFAULT NULL;
alter table o_two_refresh_tokens add column `auth_code` varchar(255) DEFAULT NULL;

--create temporary table clientid_users as (
create table clientid_users as (
select ci.clientid, ci.userid, ci.auth_code, count(1) as consent_cnt from consent_informations ci
LEFT JOIN o_two_refresh_tokens rt on ci.userid=rt.userid
group by ci.clientid,ci.userid
);

update o_two_refresh_tokens rt, clientid_users cu set rt.clientid = cu.clientid, rt.auth_code=cu.auth_code
where rt.userid=cu.userid and cu.consent_cnt=1;

drop table clientid_users;
