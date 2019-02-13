package com.wipro.www.sonhms;

import static org.junit.Assert.assertEquals;

import com.wipro.www.sonhms.ConfigPolicy;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


public class ConfigPolicyTest {

    @Test
    public void configPolicyTest() {
        ConfigPolicy configPolicy = ConfigPolicy.getInstance();
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("policyName", "pcims_policy");
        configPolicy.setConfig(config);
        assertEquals(config, configPolicy.getConfig());
    }
}
