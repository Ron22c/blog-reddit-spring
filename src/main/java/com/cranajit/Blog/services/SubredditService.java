package com.cranajit.Blog.services;

import com.cranajit.Blog.dto.SubredditDTO;
import com.cranajit.Blog.models.Post;
import com.cranajit.Blog.models.Subreddit;
import com.cranajit.Blog.reposetories.SubredditRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SubredditService {

    private final SubredditRepository subredditRepository;

    public SubredditDTO save (SubredditDTO subredditDTO) {
        Subreddit subreddit = Subreddit.builder().name(subredditDTO.getName())
                .description(subredditDTO.getDescription())
                .posts(new ArrayList<Post>(subredditDTO.getNoOfPosts()))
                .build();
        Subreddit save = subredditRepository.save(subreddit);
        subredditDTO.setId(save.getId());
        return subredditDTO;
    }

    @Transactional(readOnly = true)
    public List<SubredditDTO> getAllSubreddit() {
        return subredditRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public SubredditDTO mapToDto(Subreddit subreddit) {
        return SubredditDTO.builder().id(subreddit.getId())
                .name(subreddit.getName())
                .description(subreddit.getDescription())
                .noOfPosts(subreddit.getPosts().size())
                .build();
    }
}
