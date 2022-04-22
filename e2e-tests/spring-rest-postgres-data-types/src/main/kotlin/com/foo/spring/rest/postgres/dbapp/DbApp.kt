package com.foo.spring.rest.postgres.dbapp

import com.foo.spring.rest.postgres.SwaggerConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import springfox.documentation.swagger2.annotations.EnableSwagger2
import javax.persistence.EntityManager

/**
 * Created by jgaleotti on 18-Apr-22.
 */
@EnableSwagger2
@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
@RequestMapping(path = ["/api/postgres"])
open class DbApp : SwaggerConfiguration() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(DbApp::class.java, *args)
        }
    }

    @Autowired
    private lateinit var em: EntityManager


    @GetMapping
    open fun get(): ResponseEntity<Any> {

        val query = em.createNativeQuery("select 1 from IntegerTypes where integerColumn=0")
        val res = query.resultList

        val status: Int
        if (res.isNotEmpty()) {
            status = 200
        } else {
            status = 400
        }

        return ResponseEntity.status(status).build<Any>()
    }
}

