package com.foo.rest.examples.spring.openapi.v3.jackson.extended

import com.auth0.client.auth.AuthAPI
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/api/jackson"])
class ExtendedJacksonRest {

    @GetMapping(path = ["/byte/{s}"])
    fun byteArrayExample(@PathVariable("s") s: String): ResponseEntity<String> {
        val sampleJSON = "{\"message\":\"%s\"}".format(s)

        val objectMapper = ObjectMapper()
        val response = objectMapper.readValue(sampleJSON.toByteArray(), DummyResponse::class.java)

        if (response.message != null && response.message.equals("foo")) {
            return ResponseEntity.status(200).body("Working")
        }
        return ResponseEntity.status(200).body("Failed")
    }
}