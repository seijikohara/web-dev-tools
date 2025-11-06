package io.github.seijikohara.devtools.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanCreationException
import org.springframework.beans.factory.InjectionPoint
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

/**
 * Configuration class for Logger dependency injection.
 *
 * Enables automatic Logger injection with the appropriate class context.
 */
@Configuration
class LoggerInjectionConfig {
    /**
     * Creates a Logger instance for the requesting class.
     *
     * The Logger is created with the class name of the injection point,
     * allowing for proper categorization in logging frameworks.
     *
     * @param injectionPoint Injection point metadata provided by Spring
     * @return Logger instance configured for the requesting class
     * @throws BeanCreationException if the injection point type cannot be determined
     */
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    fun logger(injectionPoint: InjectionPoint): Logger =
        (
            injectionPoint.methodParameter?.containingClass
                ?: injectionPoint.field?.declaringClass
                ?: throw BeanCreationException("Cannot find type for Logger")
        ).let(LoggerFactory::getLogger)
}
