/*
 * Copyright (C) 2018-Present Pivotal Software, Inc. All rights reserved.
 * This program and the accompanying materials are made available under
 * the terms of the under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.bookshop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for reading the environment variable
 * <strong>VCAP_SERVICES</strong> present in Cloud Foundry environment.
 * <br></br>
 * This class has 2 main functions
 * <pre>
 *
 * 1. To parse VCAP_SERVICES environment variable
 * 2. To set security properties of Spring Data GemFire
 *</pre>
 *
 * A modification has been made to check to see if VCAP_SERVICES is present. Lack of a property would indicate not running
 * in a PCF environment. As such, this can now be configured to work locally as well as in PCF.
 *
 * @see <a href="https://docs.spring.io/spring-data/gemfire/docs/2.1.0.M1/reference/html/#bootstrap-annotation-config-security-client" >SDG docs</a>
 *
 * @author Pulkit Chandra (Modified by Mark Secrist)
 *
 * @since 1.0
 */
public class VcapEnvParser implements EnvironmentPostProcessor {

    private final static org.apache.logging.log4j.Logger LOG = LogManager.getLogger(VcapEnvParser.class);

    private final String vcapProperties;
    private static final String PROPERTY_SOURCE_NAME = "defaultProperties";

    private Map credentials = new HashMap();

    protected VcapEnvParser(String properties) {
        this.vcapProperties = properties;
    }
    public VcapEnvParser() {
        this.vcapProperties = System.getenv().get("VCAP_SERVICES");
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (this.vcapProperties != null) {
            LOG.info("VCAP_SERVICES found: " + this.vcapProperties);
            try {
                map.put("spring.data.gemfire.security.username", getCredentials().get("username"));
                map.put("spring.data.gemfire.security.password", getCredentials().get("password"));
                map.put("spring.data.gemfire.pool.locators", getLocators());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {

            }
            LOG.info("list of properties sources are :" + environment.getPropertySources().toString());
            LOG.info("List of new properties from VCAP_SERVICES: " + map);
            addOrReplace(environment.getPropertySources(), map);
            LOG.info("list of profiles which are active are :" + environment.getActiveProfiles());
        } else {
            LOG.info("No VCAP_SERVICES available");
        }
    }

    private void addOrReplace(MutablePropertySources propertySources,
                              Map<String, Object> map) {
        MapPropertySource target = null;
        if (propertySources.contains(PROPERTY_SOURCE_NAME)) {
            PropertySource<?> source = propertySources.get(PROPERTY_SOURCE_NAME);
            if (source instanceof MapPropertySource) {
                target = (MapPropertySource) source;
                for (String key : map.keySet()) {
                    if (!target.containsProperty(key)) {
                        target.getSource().put(key, map.get(key));
                    }
                }
            }
        }
        if (target == null) {
            target = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
        }
        if (!propertySources.contains(PROPERTY_SOURCE_NAME)) {
            propertySources.addLast(target);
        }
    }

    private List<String> getLocators() throws IOException, URISyntaxException {
        Map credentials = getCredentials();
        List<String> locators = (List<String>) credentials.get("locators");
        return locators;
    }


    private Map getCredentials() throws IOException {
        if (credentials.size() == 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Map> services = objectMapper.readValue(this.vcapProperties, Map.class);
            List<Map> gemfireService = getGemFireService(services);
            if (gemfireService != null) {
                Map rawCredMap = (Map) gemfireService.get(0).get("credentials");
                credentials.put("locators", rawCredMap.get("locators"));
                List<Map> users = (List) rawCredMap.get("users");
                for (Map entry : users) {
                    List<String> roles = (List<String>) entry.get("roles");
                    if (roles.contains("cluster_operator")) {
                        credentials.put("username", entry.get("username"));
                        credentials.put("password", entry.get("password"));
                    }
                }
            }
        }
        return credentials;

    }

    private List<Map> getGemFireService(Map services) {
        List<Map> l = (List<Map>) services.get("p-cloudcache");
        if (l == null) {
            throw new IllegalStateException("GemFire service is not bound to this application");
        }
        return l;
    }
}