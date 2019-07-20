package com.ccnu.bbs.service.Impl;

import com.ccnu.bbs.entity.Attention;
import com.ccnu.bbs.entity.User;
import com.ccnu.bbs.enums.AttentionEnum;
import com.ccnu.bbs.forms.AttentionForm;
import com.ccnu.bbs.repository.AttentionRepository;
import com.ccnu.bbs.service.AttentionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class AttentionServiceImpl implements AttentionService {

    @Autowired
    private AttentionRepository attentionRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Integer isUserAttention(String attentionUserId, String attentionFollowerId) {
        Attention attention;
        if (redisTemplate.hasKey("Attention::" + attentionUserId + '-' + attentionFollowerId)){
            attention = (Attention) redisTemplate.opsForValue().get("Attention::" + attentionUserId + '-' + attentionFollowerId);
        }
        else {
            attention = attentionRepository.findAttention(attentionUserId, attentionFollowerId);
        }
        if (attention != null){
            redisTemplate.opsForValue().set("Attention::" + attentionUserId + '-' + attentionFollowerId, attention, 1, TimeUnit.HOURS);
            return attention.getIsAttention();
        }
        return AttentionEnum.NOT_ATTENTION.getCode();
    }

    @Override
    public Attention updateAttention(AttentionForm attentionForm, String attentionFollowerId) {
        Attention attention;
        String attentionUserId = attentionForm.getAttentionUserId();
        // 1.先查看redis中有没有关注信息
        if (redisTemplate.hasKey("Attention::" + attentionUserId + '-' + attentionFollowerId)){
            attention = (Attention) redisTemplate.opsForValue().get("Attention::" + attentionUserId + '-' + attentionFollowerId);
        }
        // 2.若没有,则去数据库查询关注信息
        else {
            attention = attentionRepository.findAttention(attentionForm.getAttentionUserId(), attentionFollowerId);
        }
        // 3.如果均没有则新建关注信息，设置被关注者id和关注者id
        if (attention == null){
            attention = new Attention();
            attention.setAttentionUserId(attentionUserId);
            attention.setAttentionFollowerId(attentionFollowerId);
        }
        // 4.更新被关注人的粉丝数和关注人的关注数
        User attentionUser = userService.getUser(attentionUserId);
        User attentionFollower = userService.getUser(attentionFollowerId);
        // 如果关注信息信息为空或者为未关注且要更新的关注信息为已关注，则用户粉丝数+1，关注数+1
        if (attention.getIsAttention() == null || attention.getIsAttention().equals(AttentionEnum.NOT_ATTENTION.getCode())){
            if (attentionForm.getIsAttention().equals(AttentionEnum.ATTENTION.getCode())){
                attentionUser.setUserFansNum(attentionUser.getUserFansNum() + 1);
                attentionFollower.setUserAttentionNum(attentionFollower.getUserAttentionNum() + 1);
            }
        }
        // 如果关注信息为已关注且要更新的关注信息为未关注，则用户粉丝数-1，关注数-1
        else {
            if (attentionForm.getIsAttention().equals(AttentionEnum.NOT_ATTENTION.getCode())){
                attentionUser.setUserFansNum(attentionUser.getUserFansNum() - 1);
                attentionFollower.setUserAttentionNum(attentionFollower.getUserAttentionNum() - 1);
            }
        }
        userService.saveUser(attentionUser);
        userService.saveUser(attentionFollower);
        // 5.设置关注标志并保存
        attention.setIsAttention(attentionForm.getIsAttention());
        // 6.将关注存入redis中
        redisTemplate.opsForValue().set("Attention::" + attentionUserId + '-' + attentionFollowerId, attention, 1, TimeUnit.HOURS);
        return attention;
    }

    @Override
    public void updateAttentionDatabase() {
        // 1.找到所有有关关注的key
        Set<String> attentionKeys = redisTemplate.keys("Attention::*");
        // 2.保存数据到数据库并清除redis中数据
        for (String attentionKey : attentionKeys){
            Attention attention = (Attention) redisTemplate.opsForValue().get(attentionKey);
            attention = attentionRepository.save(attention);
            // 注意redis中的数据没有主键，必须存一次数据库有了主键后再存redis
            redisTemplate.opsForValue().set(attentionKey, attention, redisTemplate.getExpire(attentionKey), TimeUnit.SECONDS);
//            redisTemplate.delete(attentionKey);
        }
        return;
    }
}
