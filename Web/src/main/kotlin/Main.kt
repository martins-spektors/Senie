package lv.ailab.senie

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Should match the project namespace in build config.
 */
const val ROOT_PACKAGE: String = "lv.ailab.senie"

@SpringBootApplication(scanBasePackages = [ROOT_PACKAGE])
@EnableJpaRepositories(basePackages = [ROOT_PACKAGE])
@ConfigurationPropertiesScan(basePackages = [ROOT_PACKAGE])
class Main

fun main() {
    runApplication<Main>()
}
