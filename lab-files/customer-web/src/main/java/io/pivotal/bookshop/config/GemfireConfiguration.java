package io.pivotal.bookshop.config;

import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication.Locator;
import org.springframework.data.gemfire.config.annotation.EnableSecurity;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

@ClientCacheApplication(name="GemFireClient", locators = {
        @Locator(host = "localhost", port = 41111)
})
@EnableGemfireRepositories(basePackages = "io.pivotal.bookshop.dao" )
@EnableEntityDefinedRegions(basePackages = "io.pivotal.bookshop.domain")
@EnableSecurity
public class GemfireConfiguration {
}