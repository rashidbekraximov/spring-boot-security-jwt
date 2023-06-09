package com.example.demo.integration;

import com.example.demo.model.Movie;
import com.example.demo.repository.MovieRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class MoviesIntegrationTest {


    @Value("${server.port}")
    private int port;

    private String baseUrl = "http://localhost";

    private static RestTemplate restTemplate;

    private Movie avatarMovie;
    private Movie titanicMovie;

    @Autowired
    private MovieRepository movieRepository;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void beforeSetup() {

        avatarMovie = new Movie();
        avatarMovie.setName("Avatar");
        avatarMovie.setGenera("Action");
        avatarMovie.setReleaseDate(LocalDate.of(2000, Month.APRIL, 23));

        titanicMovie = new Movie();
        titanicMovie.setName("Titanic");
        titanicMovie.setGenera("Romance");
        titanicMovie.setReleaseDate(LocalDate.of(2004, Month.JANUARY, 10));

        avatarMovie = movieRepository.save(avatarMovie);
        titanicMovie = movieRepository.save(titanicMovie);
    }

    @AfterEach
    public void afterSetup() {
        movieRepository.deleteAll();
    }

    @Test
    void shouldCreateMovieTest() {
        baseUrl = baseUrl + ":" + port + "/movie/create";

        Movie hangoverMovie = new Movie();
        hangoverMovie.setName("Hangover");
        hangoverMovie.setGenera("Comedy");
        hangoverMovie.setReleaseDate(LocalDate.of(2004, Month.DECEMBER, 31));

        Movie newMoive = restTemplate.postForObject(baseUrl, hangoverMovie, Movie.class);

        assertNotNull(newMoive);
        assertThat(newMoive.getId()).isNotNull();
    }

    @Test
    void shouldFetchMoviesTest() {
        baseUrl = baseUrl + ":" + port + "/movie/all";

        List list = restTemplate.getForObject(baseUrl, List.class);

        assert list != null;
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void shouldFetchOneMovieByIdTest() {

        Movie existingMovie = restTemplate.getForObject(baseUrl+":"+port+"/movie/read/"+avatarMovie.getId(), Movie.class);

        assertNotNull(existingMovie);
        assertEquals("Avatar", existingMovie.getName());
    }

    @Test
    void shouldDeleteMovieTest() {

        restTemplate.delete(baseUrl+":"+port+"/movie/delete/"+avatarMovie.getId());

        int count = movieRepository.findAll().size();

        assertEquals(1, count);
    }

    @Test
    void shouldUpdateMovieTest() {

        avatarMovie.setGenera("Fantacy");

        restTemplate.put(baseUrl+":"+port+"/movie/update/{id}", avatarMovie, avatarMovie.getId());

        Movie existingMovie = restTemplate.getForObject(baseUrl+":"+port+"/movie/read/"+avatarMovie.getId(), Movie.class);

        assertNotNull(existingMovie);
        assertEquals("Fantacy", existingMovie.getGenera());
    }
}
