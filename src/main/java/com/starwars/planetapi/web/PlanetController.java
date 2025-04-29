package com.starwars.planetapi.web;

import com.starwars.planetapi.domain.Planet;
import com.starwars.planetapi.domain.PlanetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planets")
public class PlanetController {

    @Autowired
    private PlanetService planetService;

    @PostMapping
    public ResponseEntity<Planet> create(@RequestBody @Valid Planet planet) {
        planet = planetService.create(planet);
        return ResponseEntity.status(HttpStatus.CREATED).body(planet);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Planet> findById(@PathVariable Long id) {
        return planetService.get(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Planet> findByName(@PathVariable String name) {
        return planetService.findByName(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Planet>> findByFilter(@RequestParam(required = false) String terrain, @RequestParam(required = false) String climate) {
        List<Planet> planets = planetService.list(terrain, climate);
        return ResponseEntity.ok(planets);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        planetService.remove(id);
        return ResponseEntity.noContent().build();
    }

}
