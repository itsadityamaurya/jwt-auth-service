package com.aditya.authservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private void register(String email) throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\",\"email\":\"" + email + "\",\"password\":\"Password@123\"}"))
                .andExpect(status().isCreated());
    }

    private String login(String email, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("token").asText();
    }

    @Test
    void registerLoginAndReadProfile() throws Exception {
        register("flow@example.com");
        String token = login("flow@example.com", "Password@123");
        mvc.perform(get("/api/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("flow@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void protectedEndpointWithoutTokenReturns401() throws Exception {
        mvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void wrongPasswordReturns401() throws Exception {
        register("wrong@example.com");
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"wrong@example.com\",\"password\":\"bad-password\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void duplicateEmailReturns409() throws Exception {
        register("dup@example.com");
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\",\"email\":\"dup@example.com\",\"password\":\"Password@123\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidPayloadReturns400() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"not-an-email\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void normalUserCannotAccessAdminEndpoint() throws Exception {
        register("normal@example.com");
        String token = login("normal@example.com", "Password@123");
        mvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanListUsers() throws Exception {
        String token = login("admin@example.com", "Admin@12345");
        mvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
