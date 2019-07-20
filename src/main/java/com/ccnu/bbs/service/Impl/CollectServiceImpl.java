package com.ccnu.bbs.service.Impl;

import com.ccnu.bbs.entity.Collect;
import com.ccnu.bbs.enums.CollectEnum;
import com.ccnu.bbs.forms.CollectForm;
import com.ccnu.bbs.repository.CollectRepository;
import com.ccnu.bbs.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class CollectServiceImpl implements CollectService {

    @Autowired
    private CollectRepository collectRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    /**
     * 查看帖子是否被收藏
     */
    public Boolean isArticleCollect(String articleId, String userId) {
        Collect collect;
        if (redisTemplate.hasKey("Collect::" + articleId + '-' + userId)){
            collect = (Collect) redisTemplate.opsForValue().get("Collect::" + articleId + '-' + userId);
        }
        else {
            collect = collectRepository.findCollect(articleId, userId);
        }
        if (collect != null){
            redisTemplate.opsForValue().set("Collect::" + articleId + '-' + userId, collect, 1, TimeUnit.HOURS);
            return collect.getIsCollect().equals(CollectEnum.COLLECT.getCode());
        }
        else return false;
    }

    @Override
    /**
     * 帖子收藏更新
     */
    public Collect updateCollectArticle(CollectForm collectForm, String userId) {
        Collect collect;
        String articleId = collectForm.getCollectArticleId();
        // 1.先查看redis中有没有收藏信息
        if (redisTemplate.hasKey("Collect::" + articleId + '-' + userId)){
            collect = (Collect) redisTemplate.opsForValue().get("Collect::" + articleId + '-' + userId);
        }
        // 2.若没有,则去数据库查询收藏信息
        else {
            collect = collectRepository.findCollect(collectForm.getCollectArticleId(), userId);
        }
        // 3.如果均没有则新建收藏信息，设置帖子id和收藏用户id
        if (collect == null){
            collect = new Collect();
            collect.setCollectArticleId(collectForm.getCollectArticleId());
            collect.setCollectUserId(userId);
        }
        // 3.设置收藏标志并保存
        collect.setIsCollect(collectForm.getIsCollect());
        // 4.将收藏存入redis中
        redisTemplate.opsForValue().set("Collect::" + articleId + '-' + userId, collect, 1, TimeUnit.HOURS);
        return collect;
    }

    @Override
    /**
     * 从redis更新数据库
     */
    @Transactional
    public void updateCollectDatabase() {
        // 1.找到所有有关收藏的key
        Set<String> collectKeys = redisTemplate.keys("Collect::*");
        // 2.保存数据到数据库并清除redis中数据
        for (String collectKey : collectKeys){
            Collect collect = (Collect) redisTemplate.opsForValue().get(collectKey);
            collect = collectRepository.save(collect);
            // 注意redis中的数据没有主键，必须存一次数据库有了主键后再存redis
            redisTemplate.opsForValue().set(collectKey, collect, redisTemplate.getExpire(collectKey), TimeUnit.SECONDS);
//            redisTemplate.delete(collectKey);
        }
        return;
    }
}
