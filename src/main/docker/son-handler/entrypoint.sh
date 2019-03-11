#!/bin/bash
#*******************************************************************************
#  ============LICENSE_START=======================================================
#  son-handler
#  ================================================================================
#   Copyright (C) 2019 Wipro Limited.
#   ==============================================================================
#     Licensed under the Apache License, Version 2.0 (the "License");
#     you may not use this file except in compliance with the License.
#     You may obtain a copy of the License at
#  
#          http://www.apache.org/licenses/LICENSE-2.0
#  
#     Unless required by applicable law or agreed to in writing, software
#     distributed under the License is distributed on an "AS IS" BASIS,
#     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#     See the License for the specific language governing permissions and
#     limitations under the License.
#     ============LICENSE_END=========================================================
#  
#*******************************************************************************

sed -i s/DMAAP_SERVER/$DMAAPSERVER/ /etc/config.json
sed -i s/SDNR_SERVICE/$SDNRSERVICE/ /etc/config.json
sed -i s/OOF_SERVICE/$OOFSERVICE/ /etc/config.json
sed -i s/POLICY_SERVICE/$POLICYSERVICE/ /etc/config.json
sed -i s/PCIMS_SERVICE_HOST/$PCIMS_SERVICE_HOST/ /etc/config.json
sed -i s/MANAGER_API_KEY/$MANAGERAPIKEY/ /etc/config.json
sed -i s/MANAGER_SECRET_KEY/$MANAGERSECRETKEY/ /etc/config.json
sed -i s/PCIMS_API_KEY/$PCIMSAPIKEY/ /etc/config.json
sed -i s/PCIMS_SECRET_KEY/$PCIMSSECRETKEY/ /etc/config.json
sed -i s/SDNR_API_KEY/$SDNRAPIKEY/ /etc/config.json
sed -i s/POLICY_API_KEY/$POLICYAPIKEY/ /etc/config.json
java -jar application.jar

