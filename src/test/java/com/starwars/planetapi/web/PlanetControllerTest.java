package com.starwars.planetapi.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starwars.planetapi.domain.Planet;
import com.starwars.planetapi.domain.PlanetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.starwars.planetapi.common.PlanetConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanetController.class)
class PlanetControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private PlanetService planetService;

    @Test
    void createPlanet_WithValidData_ReturnsCreated() throws Exception {
        when(planetService.create(PLANET)).thenReturn(PLANET);
        String planetJson = objectMapper.writeValueAsString(PLANET);
        mockMvc.perform(
            post("/planets")
                .content(planetJson)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    void createPlanet_WithInvalidData_ReturnsBadRequest() throws Exception {
        Planet emptyPlanet = new Planet();
        Planet invalidPlanet = new Planet("", "", "");
        mockMvc.perform(
            post("/planets")
                .content(objectMapper.writeValueAsString(emptyPlanet))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(
            post("/planets")
                .content(objectMapper.writeValueAsString(invalidPlanet))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createPlanet_WithExistingName_ReturnConflit() throws Exception {
        when(planetService.create(any())).thenThrow(DataIntegrityViolationException.class);
        mockMvc.perform(
            post("/planets")
                .content(objectMapper.writeValueAsString(PLANET))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict());
    }

    @Test
    void getPlanet_ByExistingId_ReturnsPlanet() throws Exception {
        when(planetService.get(1L)).thenReturn(Optional.of(PLANET));
        mockMvc.perform(
            get("/planets/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    void getPlanet_ByUnexistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(
            get("/planets/1"))
        .andExpect(status().isNotFound());
    }

    @Test
    void getPlanet_ByExistingName_ReturnsPlanet() throws Exception {
        when(planetService.findByName(PLANET.getName())).thenReturn(Optional.of(PLANET));
        mockMvc.perform(
            get("/planets/name/".concat(PLANET.getName())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    void getPlanet_ByUnexistingName_ReturnsNotFound() throws Exception {
        mockMvc.perform(
            get("/planets/name/".concat("nome_aleatorio")))
        .andExpect(status().isNotFound());
    }

    @Test
    void listPlanets_ReturnsFilteredPlanets() throws Exception{
        when(planetService.list(null, null)).thenReturn(PLANETS);
        when(planetService.list(TATOOINE.getTerrain(), TATOOINE.getClimate())).thenReturn(List.of(TATOOINE));

        mockMvc.perform(
            get("/planets"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(
            get("/planets?".concat(String.format("terrain=%s&climate=%s", TATOOINE.getTerrain(), TATOOINE.getClimate()))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]").value(TATOOINE));
    }

    @Test
    void listPlanets_ReturnNoPlanets() throws Exception{
        when(planetService.list(null, null)).thenReturn(Collections.EMPTY_LIST);
        mockMvc.perform(
            get("/planets"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void removePlanet_ByExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(
            delete("/planets/1"))
        .andExpect(status().isNoContent());
    }

    @Test
    void removePlanet_ByUnexistingId_ReturnsNoContent() throws Exception {
        final Long planetIdInvalid = 999L;
        doThrow(new EmptyResultDataAccessException(1)).when(planetService).remove(planetIdInvalid);
        mockMvc.perform(
            delete("/planets/" + planetIdInvalid))
        .andExpect(status().isNotFound());
    }
}
