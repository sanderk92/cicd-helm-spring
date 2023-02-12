package com.example.demo.config

import jakarta.annotation.PostConstruct
import mu.two.KotlinLogging
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.influxdb.impl.InfluxDBMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@ConfigurationProperties(prefix = "influxdb")
data class InfluxDbProperties(
    val url: String,
    val username: String,
    val password: String,
    val database: String,
)

@Configuration
@EnableConfigurationProperties(value = [InfluxDbProperties::class])
class InfluxDbConfig(private val properties: InfluxDbProperties) {

    @Bean
    fun influxDb(): InfluxDB = try {
        InfluxDBFactory
            .connect(properties.url, properties.username, properties.password)
            .setDatabase(properties.database)
            .enableBatch()
            .use { return it }
    } catch (e: Throwable) {
        throw IllegalStateException("Could not start application due to InfluxDB startup failure", e)
    }

    @Bean
    fun influxDbMapper(influxDB: InfluxDB): InfluxDBMapper {
        return InfluxDBMapper(influxDB)
    }
}

@Service
@ConditionalOnProperty(name = ["influxdb.create-database"], havingValue = "true")
class DatabaseInitializer(private val influxDb: InfluxDB, private val properties: InfluxDbProperties) {
    @PostConstruct
    fun createDatabase() {
        log.info("creating database '${properties.database}'")
        influxDb.createDatabase(properties.database)
        log.info("created database '${properties.database}'")
    }
}
