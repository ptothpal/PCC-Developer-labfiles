#############################################################################
#
# Script designed to be run on the jumpbox. The person setting this up
# will need to obtain their PIVNET UAA token.
# 
# A good recommendation for the COURSE_SUBDOMAIN would be to use the customer
# combined with the delivery date as: jpmc-051018
#
###########################################################################
USAGE="Usage: $0 PIVNET_UAA_TOKEN COURSE_SUBDOMAIN REGION: [US, EMEA, APJ]"

if [ $# -ne 3 ]; then
  echo $USAGE
  exit -1
fi

PIVNET_UAA_TOKEN=$1
PCC_DOMAIN_NAME=gcp.pivotaledu.io
PCC_SUBDOMAIN_NAME=$2

if [ "${3}" = "US" ]; then
   region=us-central1
else 
if [ "${3}" = "EMEA" ];then
   region="europe-west1"
else 
if [ "${3}" = "APJ" ];then
   region="asia-east1"
else
   echo "Unrecognized region type"
   echo $USAGE
   exit -1
fi
fi
fi

region_az1="${region}-a"
region_az2="${region}-b"
region_az3="${region}-c"
echo $region
echo $region_az1
echo $region_az2
echo $region_az3
exit 0;

cat > ${HOME}/.env <<-EOF
PCF_PIVNET_UAA_TOKEN=$PIVNET_UAA_TOKEN
PCF_DOMAIN_NAME=$PCC_DOMAIN_NAME
PCF_SUBDOMAIN_NAME=$PCC_SUBDOMAIN_NAME
PCF_OPSMAN_ADMIN_USER=admin
PCF_OPSMAN_ADMIN_PASSWD=CHANGE_ME_OPSMAN_ADMIN_PASSWD # e.g. for simplicity, recycle your PCF_PIVNET_UAA_TOKEN 
PCF_REGION=$region
PCF_AZ_1=$region_az1
PCF_AZ_2=$region_az2
PCF_AZ_3=$region_az3

PCF_OPSMAN_IMAGE=ops-manager-us/pcf-gcp-2.0-build.213.tar.gz        # PLEASE DON'T CHANGE ME!
PCF_PROJECT_ID=$(gcloud config get-value core/project 2> /dev/null) # e.g. cso-education-cls99env66
PCF_OPSMAN_FQDN=pcf.\${PCF_SUBDOMAIN_NAME}.\${PCF_DOMAIN_NAME} # change "pcf" to "opsman" if necessary
EOF
