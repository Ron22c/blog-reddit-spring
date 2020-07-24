package com.cranajit.Blog.reposetories;

import com.cranajit.Blog.models.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteReposetory extends JpaRepository<Vote, Long> {
}
