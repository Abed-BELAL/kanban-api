package com.example.kanban_api;

import com.example.kanban_api.model.Role;
import com.example.kanban_api.model.User;
import com.example.kanban_api.repository.CardRepository;
import com.example.kanban_api.repository.KanbanListRepository;
import com.example.kanban_api.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class KanbanApiTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private KanbanListRepository kanbanListRepository;
    @Autowired
    private CardRepository cardRepository;

    @BeforeEach
    void clean() {
        cardRepository.deleteAll();
        kanbanListRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void registerLoginAndPasswordNeverLeaks() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ada@example.com\",\"password\":\"motdepasse\",\"name\":\"Ada\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email").value("ada@example.com"))
                .andExpect(jsonPath("$.role").value("user"))
                .andExpect(contentWithoutPassword("motdepasse"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ada@example.com\",\"password\":\"motdepasse\",\"name\":\"Ada\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"pas-un-email\",\"password\":\"motdepasse\",\"name\":\"Ada\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"court@example.com\",\"password\":\"court\",\"name\":\"Ada\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ada@example.com\",\"password\":\"motdepasse\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"inconnu@example.com\",\"password\":\"motdepasse\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Identifiants invalides"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ada@example.com\",\"password\":\"mauvais\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Identifiants invalides"));
    }

    @Test
    void userProfileAndRights() throws Exception {
        String ada = token("ada@example.com");
        String bob = token("bob@example.com");
        long adaId = userRepository.findByEmail("ada@example.com").orElseThrow().getId();
        long bobId = userRepository.findByEmail("bob@example.com").orElseThrow().getId();

        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + ada))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ada@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());

        mockMvc.perform(patch("/api/users/" + adaId)
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ada Lovelace\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ada Lovelace"))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(patch("/api/users/" + bobId)
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Pirate\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/users/" + adaId)
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"admin\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/users/99999")
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\"}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/users/" + adaId)
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"superuser\"}"))
                .andExpect(status().isBadRequest());

        User adaUser = userRepository.findByEmail("ada@example.com").orElseThrow();
        adaUser.setRole(Role.admin);
        userRepository.save(adaUser);
        String admin = login("ada@example.com");

        mockMvc.perform(patch("/api/users/" + bobId)
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"admin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("admin"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void listsAreScopedToTheirOwner() throws Exception {
        String ada = token("ada@example.com");
        String bob = token("bob@example.com");

        mockMvc.perform(get("/api/lists")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/lists").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"A faire\"}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/lists/1")).andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/lists")
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest());

        MvcResult created = mockMvc.perform(post("/api/lists")
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"A faire\",\"position\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerId").isNumber())
                .andReturn();
        int listId = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/lists").header("Authorization", "Bearer " + bob))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/api/lists").header("Authorization", "Bearer " + ada))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(patch("/api/lists/" + listId)
                        .header("Authorization", "Bearer " + bob)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Volee\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/lists/99999")
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Inconnue\"}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/lists/" + listId)
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/api/lists/" + listId)
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"En cours\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("En cours"));

        mockMvc.perform(delete("/api/lists/" + listId).header("Authorization", "Bearer " + bob))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/lists/99999").header("Authorization", "Bearer " + ada))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/lists/" + listId).header("Authorization", "Bearer " + ada))
                .andExpect(status().isNoContent());
    }

    @Test
    void cardsFollowTheParentListOwner() throws Exception {
        String ada = token("ada@example.com");
        String bob = token("bob@example.com");
        int adaList = list(ada, "Ada");
        int bobList = list(bob, "Bob");

        mockMvc.perform(get("/api/lists/99999/cards").header("Authorization", "Bearer " + ada))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/lists/" + bobList + "/cards").header("Authorization", "Bearer " + ada))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/lists/" + adaList + "/cards")
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/lists/99999/cards")
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"X\"}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/lists/" + bobList + "/cards")
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Intrus\"}"))
                .andExpect(status().isForbidden());

        MvcResult created = mockMvc.perform(post("/api/lists/" + adaList + "/cards")
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Carte\",\"description\":\"Details\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.listId").value(adaList))
                .andReturn();
        int cardId = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/lists/" + adaList + "/cards").header("Authorization", "Bearer " + ada))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/api/cards/" + cardId).header("Authorization", "Bearer " + ada))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Carte"));
        mockMvc.perform(get("/api/cards/" + cardId).header("Authorization", "Bearer " + bob))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/cards/99999").header("Authorization", "Bearer " + ada))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/cards/" + cardId)
                        .header("Authorization", "Bearer " + bob)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Non\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/cards/" + cardId)
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"listId\":" + bobList + "}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/cards/99999")
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Inconnue\"}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/cards/" + cardId)
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"listId\":99999}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/cards/" + cardId)
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest());

        int adaOther = list(ada, "Autre");
        mockMvc.perform(patch("/api/cards/" + cardId)
                        .header("Authorization", "Bearer " + ada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Deplacee\",\"listId\":" + adaOther + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listId").value(adaOther));

        mockMvc.perform(delete("/api/cards/" + cardId)).andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/cards/" + cardId).header("Authorization", "Bearer " + bob))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/cards/99999").header("Authorization", "Bearer " + ada))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/cards/" + cardId).header("Authorization", "Bearer " + ada))
                .andExpect(status().isNoContent());
    }

    @Test
    void swaggerIsServedOnApi() throws Exception {
        mockMvc.perform(get("/api"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }

    private String token(String email) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"motdepasse\",\"name\":\"Nom\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist());
        return login(email);
    }

    private String login(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"motdepasse\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
    }

    private int list(String token, String title) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/lists")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + title + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
    }

    private org.springframework.test.web.servlet.ResultMatcher contentWithoutPassword(String secret) {
        return result -> {
            String body = result.getResponse().getContentAsString();
            if (body.contains(secret) || body.contains("\"password\"")) {
                throw new AssertionError("le mot de passe ne doit pas apparaître : " + body);
            }
        };
    }
}
