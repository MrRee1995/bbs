package com.ccnu.bbs.repository;

import com.ccnu.bbs.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

public interface MessageRepository extends JpaRepository<Message, BigInteger> {

    // 按照消息类型查找全部消息
    @Query("select m from Message m where m.receiverUserId = ?1 and m.messageType = ?2 order by m.messageTime desc")
    Page<Message> findMessage(String receiverUserId, Integer messageType, Pageable pageable);

    // 按照消息类型查找是否有新消息
    @Query("select count(m) from Message m where m.receiverUserId = ?1 and m.messageType = ?2 and m.isRead = 0")
    Integer haveNewMessage(String receiverUserId, Integer messageType);

    @Transactional
    void deleteByMessageId(BigInteger messageId);
}
