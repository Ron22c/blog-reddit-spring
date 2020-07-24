package com.cranajit.Blog.controllers;

import com.cranajit.Blog.dto.SubredditDTO;
import com.cranajit.Blog.services.SubredditService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/subreddit")
public class SubredditController {

    private final SubredditService subredditService;

    @PostMapping(value = "/save")
    public ResponseEntity<SubredditDTO> saveSubReddit(@RequestBody SubredditDTO subredditDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subredditService.save(subredditDTO));
    }

    @GetMapping
    public ResponseEntity<List<SubredditDTO>> getAllSubreddit() {
        return ResponseEntity.ok().body(subredditService.getAllSubreddit());
    }

//    @GetMapping(value = "/{id}")
//    public ResponseEntity<SubredditDTO> getSubredditById(@PathVariable Long id) {
//
//    }
}
