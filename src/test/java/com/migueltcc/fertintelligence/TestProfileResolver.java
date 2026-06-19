package com.migueltcc.fertintelligence;

import org.springframework.test.context.ActiveProfilesResolver;

public class TestProfileResolver implements ActiveProfilesResolver {

    private static final String DEFAULT_TEST_PROFILE = "test";

    @Override
    public String[] resolve(Class<?> testClass) {
        String activeProfiles = System.getProperty("spring.profiles.active");
        if (activeProfiles == null || activeProfiles.isBlank()) {
            activeProfiles = System.getenv("SPRING_PROFILES_ACTIVE");
        }
        if (activeProfiles == null || activeProfiles.isBlank()) {
            return new String[] { DEFAULT_TEST_PROFILE };
        }
        return activeProfiles.split("\\s*,\\s*");
    }
}
