package com.ccnu.bbs.repository;

import com.ccnu.bbs.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReplyRepository extends JpaRepository<Reply, String> {

    // 查找单个回复
    @Query("select r from Reply r where r.replyId = ?1")
    Reply findReply(String replyId);

    // 查看某个评论的回复(按照回复时间升序排列)
    @Query("select r from Reply r where r.replyCommentId = ?1 order by r.replyTime asc")
    Page<Reply> findCommentReply(String commentId, Pageable pageable);

    // 查看某个用户的回复(按照回复时间降序排列)
    @Query("select r from Reply r where r.replyUserId = ?1 order by r.replyTime desc")
    Page<Reply> findUserReply(String userId, Pageable pageable);

}
