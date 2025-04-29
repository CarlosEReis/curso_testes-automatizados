package com.starwars.planetapi.domain;

import com.starwars.planetapi.jacoco.ExcludeFromJacocoGeneratedReport;
import jakarta.annotation.Generated;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.Objects;

@Entity
@Table(name = "planets")
public class Planet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(nullable = false, unique = true)
    private String name;

    @NotEmpty
    @Column(nullable = false)
    private String climate;

    @NotEmpty
    @Column(nullable = false)
    private String terrain;

    public Planet() {}

    public Planet(Long id, String name, String climate, String terrain) {
        this.id = id;
        this.name = name;
        this.climate = climate;
        this.terrain = terrain;
    }

    public Planet(String name, String climate, String terrain) {
        this.name = name;
        this.climate = climate;
        this.terrain = terrain;
    }

    public Planet(String terrain, String climate) {
        this.terrain = terrain;
        this.climate = climate;
    }

    public String getClimate() {
        return climate;
    }

    public void setClimate(String climate) {
        this.climate = climate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTerrain() {
        return terrain;
    }

    public void setTerrain(String terrain) {
        this.terrain = terrain;
    }

    @ExcludeFromJacocoGeneratedReport
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Planet planet = (Planet) o;
        return Objects.equals(id, planet.id) && Objects.equals(name, planet.name) && Objects.equals(climate, planet.climate) && Objects.equals(terrain, planet.terrain);
    }

    @ExcludeFromJacocoGeneratedReport
    @Override
    public int hashCode() {
        return Objects.hash(id, name, climate, terrain);
    }
}
