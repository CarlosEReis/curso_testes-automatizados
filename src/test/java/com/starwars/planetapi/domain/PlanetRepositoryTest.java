package com.starwars.planetapi.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.starwars.planetapi.common.PlanetConstants.PLANET;
import static com.starwars.planetapi.common.PlanetConstants.TATOOINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
class PlanetRepositoryTest {

    @Autowired private PlanetRepository planetRepository;
    @Autowired private TestEntityManager testEntityManager;

    @AfterEach
    void afterEach() {
        PLANET.setId(null);
    }

    @Test
    void createPlanet_WithValidData_ReturnsPlanet() {
        Planet planet = planetRepository.save(PLANET);
        Planet sut = testEntityManager.find(Planet.class, planet.getId());
        assertThat(sut)
            .isNotNull()
            .isEqualTo(planet);
    }

        @ParameterizedTest
    @MethodSource("providesInvalidPlanets")
    void createPlanet_WithInvalidData_ThrowsException(Planet planet) {
        assertThatThrownBy(() -> planetRepository.save(planet)).isInstanceOf(RuntimeException.class);
    }

    private static Stream<Arguments> providesInvalidPlanets() {
        return Stream.of(
            Arguments.of(new Planet(null, "climate", "terrain")),
            Arguments.of(new Planet("name", null, "terrain")),
            Arguments.of(new Planet("name", "climate", null)),
            Arguments.of(new Planet("name", "climate", "")),
            Arguments.of(new Planet("name", "", "terrain")),
            Arguments.of(new Planet("", "climate", "terrain")),
            Arguments.of(new Planet("", "", "")),
            Arguments.of(new Planet("", "climate", "")),
            Arguments.of(new Planet("name", "", "")),
            Arguments.of(new Planet(null, null, null)),
            Arguments.of(new Planet("", null, null)),
            Arguments.of(new Planet(null, "", null)),
            Arguments.of(new Planet(null, null, "")),
            Arguments.of(new Planet("name", null, null)),
            Arguments.of(new Planet("name", "", null))
        );
    }

    @Test
    void createPlanet_WithNameExisting_ThrowsException() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        testEntityManager.detach(planet);
        planet.setId(null);
        assertThatThrownBy(() -> planetRepository.save(planet)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void getPlanet_ByExistingId_ReturnsPlanet() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        testEntityManager.detach(planet);
        Optional<Planet> sut = planetRepository.findById(planet.getId());
        assertThat(sut)
            .isNotEmpty()
            .isEqualTo(Optional.of(planet));
    }

    @Test
    void getPlanet_ByUnexistingId_ReturnsEmpty() {
        Optional<Planet> sut = planetRepository.findById(1L);
        assertThat(sut)
            .isEmpty();
    }

    @Test
    void getPlanet_ByExistingName_ReturnsPlanet() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        Optional<Planet> sut = planetRepository.findByName(planet.getName());
        assertThat(sut)
            .isNotEmpty()
            .isEqualTo(Optional.of(planet));
    }

    @Test
    void getPlanet_ByUnexistingName_ReturnsNotFound() {
        Optional<Planet> sut = planetRepository.findByName("nome_aleatorio");
        assertThat(sut).isEmpty();
    }

    @Sql(scripts = {"/imports_planets.sql"})
    @Test
    void listPlanets_ReturnsFilteredPlanets() {
        Example<Planet> queryWithoutFilters = QueryBuilder.makeQuery(new Planet());
        Example<Planet> queryWithFilters = QueryBuilder.makeQuery(new Planet(TATOOINE.getTerrain(), TATOOINE.getClimate()));

        List<Planet> responseWithoutFilters = planetRepository.findAll(queryWithoutFilters);
        List<Planet> responseWithFilters = planetRepository.findAll(queryWithFilters);

        assertThat(responseWithoutFilters)
        .isNotEmpty()
        .hasSize(3);

        assertThat(responseWithFilters)
        .isNotEmpty()
        .hasSize(1);
    }

    @Test
    void listPlanets_ReturnNoPlanets() {
        Example<Planet> query = QueryBuilder.makeQuery(new Planet());
        List<Planet> response = planetRepository.findAll(query);

        assertThat(response).isEmpty();
    }

    @Test
    void removePlanet_WithExistingId_RemovePlanetFromDatabase() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        planetRepository.deleteById(planet.getId());
        Optional<Planet> sut = planetRepository.findById(planet.getId());
        assertThat(sut).isEmpty();
    }

}
