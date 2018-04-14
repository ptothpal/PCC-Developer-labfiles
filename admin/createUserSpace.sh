#######################################################################################
# Script designed to initialized spaces and users in a logical way where the
# space is the same as the user.
# 
# This script can be run from anywhere that the PCF API Endpoint can be reached from
# It will be necessary to configure a couple of values in this script for the specific
# PCF Environment that was set up.
#
## Set these parameters from the setup of the PCF Cluster
PAS_API_ENDPOINT=https://api.sys.msecrist.gcp.pivotaledu.io
PAS_ADMIN_USER=admin
PAS_ADMIN_PWD=ZK_4OW8EoSNaYMFpwKztSadZHzJ-CgcN
#
######################################################################################

USAGE="Usage: ${0} USER PASSWORD"

if [ -z $1 ]; then
   echo $USAGE
   exit -1
else
   if [ -z $2 ]; then
      echo $USAGE
      exit -1
   else
      user="${1}"
      passwd="${2}"
      space=$user
      cf login -a $PAS_API_ENDPOINT --skip-ssl-validation -u $PAS_ADMIN_USER -p $PAS_ADMIN_PWD -o system -s system
      
      # Create User and Space 
      cf create-space $space -o pivotal-edu 
      cf create-user $user $passwd

      # Map user to space as: cf set-space-role USERNAME ORG SPACE ROLE
      cf set-space-role $user pivotal-edu $space SpaceAuditor
      cf set-space-role $user pivotal-edu $space SpaceManager
      cf set-space-role $user pivotal-edu $space SpaceDeveloper

      echo "Space and user created for: $user"
      exit 0
   fi
fi


