package com.ccnu.bbs.repository;

import com.ccnu.bbs.entity.Slideshow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SlideshowRepository extends JpaRepository<Slideshow, Integer> {

    /** 找出对应版块的轮播图. */
    @Query("select s.imgUrl from Slideshow s where s.topicType = ?1")
    List<String> findTopicImg(Integer topicType);
}
