package com.ccnu.bbs.repository;

import com.ccnu.bbs.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Integer> {

    @Query("select t from Topic t where t.topicId <> 0")
    List<Topic> topicList();
}
