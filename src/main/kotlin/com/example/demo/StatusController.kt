package com.example.demo

import org.influxdb.annotation.Column
import org.influxdb.annotation.Measurement
import org.influxdb.dto.BoundParameterQuery.QueryBuilder;
import org.influxdb.impl.InfluxDBMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@RequestMapping
@RestController
class TestController(val influxDb: InfluxDBMapper) {

    @GetMapping("/")
    fun status(): ResponseEntity<StatusEntity> {
        val status = StatusEntity()
        status.status = HttpStatus.OK.toString()
        status.time = Instant.now()

        influxDb.save(status)
        return ResponseEntity.ok(status)
    }

    @GetMapping("/all")
    fun statusesForDate(date: LocalDate): ResponseEntity<List<StatusEntity>> {
        val start: Instant = date.atStartOfDay().toInstant(ZoneOffset.UTC)
        val end = start.plus(1, ChronoUnit.DAYS)

        val query = QueryBuilder.newQuery("SELECT * FROM status WHERE time > \$start AND time < \$end")
            .bind("start", start)
            .bind("end", end)
            .create()

        return ResponseEntity.ok(influxDb.query(query, StatusEntity::class.java))
    }
}

@Measurement(name = "status", database = "database")
class StatusEntity {
    @Column(name = "time")
    lateinit var time: Instant

    @Column(name = "status")
    lateinit var status: String
}
