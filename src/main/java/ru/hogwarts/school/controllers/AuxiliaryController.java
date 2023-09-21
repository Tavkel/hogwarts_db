package ru.hogwarts.school.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/aux")
public class AuxiliaryController {
    @GetMapping(path = "/v1")
    public ResponseEntity<Long[]> getValue() {
        var start = System.currentTimeMillis();
        Long sum = Long.valueOf(Stream.iterate(1, a -> a + 1)
                .limit(10_000_000)
                .reduce(0, (a, b) -> a + b));
        var end = System.currentTimeMillis();
        return new ResponseEntity<>(new Long[]{sum, end - start}, HttpStatus.OK);
    }

    @GetMapping(path = "/v2")
    public ResponseEntity<Long[]> getValue2() {
        var start = System.currentTimeMillis();
        Long sum = Long.valueOf(Stream.iterate(1, a -> a + 1).parallel()
                .limit(10_000_000)
                .reduce(0, (a, b) -> a + b));
        var end = System.currentTimeMillis();
        return new ResponseEntity<>(new Long[]{sum, end - start}, HttpStatus.OK);
    }

    @GetMapping(path = "/v3")
    public ResponseEntity<Long[]> getValue3() { // <-- best
        var start = System.currentTimeMillis();
        long sum = IntStream.rangeClosed(1, 10_000_000)
                .reduce(0, (a, b) -> a + b);
        var end = System.currentTimeMillis();
        return new ResponseEntity<>(new Long[]{sum, end - start}, HttpStatus.OK);
    }

    @GetMapping(path = "/v4")
    public ResponseEntity<Long[]> getValue4() {
        var start = System.currentTimeMillis();
        long sum = IntStream.rangeClosed(1, 10_000_000).parallel()
                .reduce(0, (a, b) -> a + b);
        var end = System.currentTimeMillis();
        return new ResponseEntity<>(new Long[]{sum, end - start}, HttpStatus.OK);
    }

// Параллель сильно проигрывает. Мб надо еще больше элементов, или более тяжелые операции
}
