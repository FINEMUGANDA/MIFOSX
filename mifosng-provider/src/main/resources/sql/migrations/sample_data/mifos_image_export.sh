#!/bin/bash

MYSQL_USER=root
MYSQL_PASS=rootroot

# uat and prod
DIR="/usr/share/tomcat7"

sudo chown $USER $DIR/.mifosx -R
echo "select client_id from old_mifos_image.client_photo" | mysql -u $MYSQL_USER -p$MYSQL_PASS | sed 1d | while read -r client_id
do
    mkdir -p "$DIR/.mifosx/default/images/clients/$client_id"
    echo "update m_client set image_id=null where id=$client_id" | mysql -u $MYSQL_USER -p$MYSQL_PASS mifostenant-default
    echo "delete from m_image where location = '$DIR/.mifosx/default/images/clients/$client_id/profile.jpg'" | mysql -u $MYSQL_USER -p$MYSQL_PASS mifostenant-default
    echo "insert into m_image(location, storage_type_enum) values('$DIR/.mifosx/default/images/clients/$client_id/profile.jpg', 1)" | mysql -u $MYSQL_USER -p$MYSQL_PASS mifostenant-default
    image_id=$(echo "select id from m_image where location = '$DIR/.mifosx/default/images/clients/$client_id/profile.jpg' order by id desc limit 1" | mysql -u $MYSQL_USER -p$MYSQL_PASS mifostenant-default | sed 1d)
    echo "update m_client set image_id=$image_id where id=$client_id"
    echo "update m_client set image_id=$image_id where id=$client_id" | mysql -u $MYSQL_USER -p$MYSQL_PASS mifostenant-default
    #echo "Convert image # $image_id for client  # $client_id"
done

sudo chown mysql $DIR/.mifosx -R

echo "CALL dump_images()" | mysql -u $MYSQL_USER -p$MYSQL_PASS old_mifos_image

sudo chown tomcat7 $DIR/.mifosx -R
