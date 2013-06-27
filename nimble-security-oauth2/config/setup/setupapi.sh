#/bin/sh

HOST=api2.bill1.ninternal.com;
OAUTH_PROJECT_DIR=/Users/brandonnimble/projects/Nimble/nimble-oauth/nimble-security-oauth2;
JBOSS_LOCAL_DIR=/Users/Shared/coding/servers/jboss/jboss-as-7.1.1.Final;
ENV=prod;

cd $OAUTH_PROJECT_DIR;
rsync -avc bash_profile $HOST:~/.bash_profile;
rsync -avc nimble_profile.$ENV $HOST:~/.nimble_profile;
rsync -avc $JBOSS_LOCAL_DIR --delete --exclude="tmp/*" --exclude="log/*" $HOST:~/

ssh -t $HOST 'sudo rsync -avc ~/jboss-as-7.1.1.Final /usr/local/';
ssh -t $HOST 'sudo ln -s /usr/local/jboss-as-7.1.1.Final /usr/local/jboss';
ssh -t $HOST 'sudo chown jboss.jboss /usr/local/jboss-as-7.1.1.Final';

rsync -avc /usr/local/java/db/drivers/mysql/mysql-connector-java-5.1.24/mysql-connector-java-5.1.24-bin.jar app1.bill1.ninternal.com:~/