package com.starwars.planetapi.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.starwars.planetapi.common.PlanetConstants.INVALID_PLANET;
import static com.starwars.planetapi.common.PlanetConstants.PLANET;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanetServiceTest {

    /*
        Testes Unitários
     */

    @InjectMocks
    private PlanetService planetService;

    @Mock
    private PlanetRepository planetRepository;

    // operacao_estado_returno
    @Test
    void createPlanet_WithValidData_ReturnsPlanet () {
        // Teste AAA

        // Arrange
        when(planetRepository.save(PLANET)).thenReturn(PLANET);

        // Action
        // system under test
        Planet sut = planetService.create(PLANET);

        // Assert
        assertThat(sut).isEqualTo(PLANET);
    }

    @Test
    void createPlanet_WithInvalidData_ThrowsException() {
        when(planetRepository.save(INVALID_PLANET)).thenThrow(RuntimeException.class);
        assertThatThrownBy(() -> planetService.create(INVALID_PLANET)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void findPlanet_ByExistingId_ReturnsPlanet() {
        when(planetRepository.findById(1L)).thenReturn(Optional.of(PLANET));
        Optional<Planet> sut = planetService.get(1L);
        assertThat(sut)
                .isNotEmpty()
                .isEqualTo(Optional.of(PLANET));
    }

    @Test
    void findPlanet_ByUnexistingId_ReturnEmpty() {
        when(planetRepository.findById(anyLong())).thenReturn(Optional.empty());
        Optional<Planet> sut = planetService.get(anyLong());
        assertThat(sut)
                .isEmpty();
    }

    @Test
    void findPlanet_ByExistingName_ReturnPlanet() {
        when(planetRepository.findByName(PLANET.getName())).thenReturn(Optional.of(PLANET));
        Optional<Planet> sut = planetService.findByName(PLANET.getName());
        assertThat(sut)
                .isNotEmpty()
                .isEqualTo(Optional.of(PLANET));
    }

    @Test
    void findPlanet_ByUnexistingName_ReturnPlanet() {
        when(planetRepository.findByName(anyString())).thenReturn(Optional.empty());
        Optional<Planet> sut = planetService.findByName(anyString());
        assertThat(sut)
                .isEmpty();
    }

    @Test
    void listPlanets_ReturnAllPlanets() {
        List<Planet> planets = List.of(PLANET);
        Example<Planet> query = QueryBuilder.makeQuery(new Planet(PLANET.getTerrain(), PLANET.getClimate()));
        when(planetRepository.findAll(query)).thenReturn(planets);

        List<Planet> sut = planetService.list(PLANET.getTerrain(), PLANET.getClimate());
        assertThat(sut)
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(PLANET);
    }

    @Test
    void listPlanets_ReturnNoPlanets() {
        when(planetRepository.findAll(any(Example.class))).thenReturn(Collections.EMPTY_LIST);
        List<Planet> sut = planetService.list(PLANET.getTerrain(), PLANET.getClimate());
        assertThat(sut).isEmpty();
    }

    @Test
    void removePlanet_ByExistingId_DoesNotThrowAnyException() {
        assertThatCode(() -> planetService.remove(anyLong())).doesNotThrowAnyException();
    }

    @Test
    void removePlanet_ByUnexistingId_DoesNotThrowAnyException() {
        doThrow(RuntimeException.class).when(planetRepository).deleteById(1L);
        assertThatThrownBy(() -> planetService.remove(1L)).isInstanceOf(RuntimeException.class);
    }
}
