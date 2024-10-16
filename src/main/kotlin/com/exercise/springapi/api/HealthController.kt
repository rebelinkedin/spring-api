package com.exercise.springapi.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/healthz")
    fun greeting() = "I'm alive"
}