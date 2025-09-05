package com.example.mallapi.sample.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SampleApiController.class)
class SampleApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test /hello endpoint returns 'hello'")
    void hello() throws Exception {
        mockMvc.perform(get("/api/v1/sample/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello"));
    }

    @Test
    @DisplayName("Test /hello2 endpoint returns a JSON array")
    void hello2() throws Exception {
        mockMvc.perform(get("/api/v1/sample/hello2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("Hello"))
                .andExpect(jsonPath("$[1]").value("World"));
    }
}