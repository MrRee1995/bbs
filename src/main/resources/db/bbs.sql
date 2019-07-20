/*
 Navicat Premium Data Transfer

 Source Server         : MyDatabase
 Source Server Type    : MySQL
 Source Server Version : 80015
 Source Host           : localhost:3306
 Source Schema         : bbs

 Target Server Type    : MySQL
 Target Server Version : 80015
 File Encoding         : 65001

 Date: 20/07/2019 23:48:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `article_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子主键id',
  `article_topic_type` int(11) NULL DEFAULT NULL COMMENT '帖子所属版块',
  `article_user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '帖子作者id',
  `article_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '帖子标题',
  `article_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '帖子正文',
  `article_keywords` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '帖子关键词',
  `article_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '帖子图片',
  `article_create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '帖子创建时间',
  `article_view_num` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '帖子浏览量',
  `article_comment_num` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '帖子评论数',
  `article_hot_num` double(30, 20) NULL DEFAULT NULL COMMENT '帖子热度',
  `article_like_num` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '帖子点赞数',
  `article_is_delete` int(1) NULL DEFAULT NULL COMMENT '帖子是否被删除',
  PRIMARY KEY (`article_id`) USING BTREE,
  INDEX `article_user_index`(`article_user_id`) USING BTREE,
  INDEX `article_topic_index`(`article_topic_type`) USING BTREE,
  CONSTRAINT `article_topic_index` FOREIGN KEY (`article_topic_type`) REFERENCES `topic` (`topic_type`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `article_user_index` FOREIGN KEY (`article_user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for attention
-- ----------------------------
DROP TABLE IF EXISTS `attention`;
CREATE TABLE `attention`  (
  `attention_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '关注id',
  `attention_user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '被关注人id',
  `attention_follower_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关注人id',
  `is_attention` int(1) NULL DEFAULT NULL COMMENT '是否关注',
  PRIMARY KEY (`attention_id`) USING BTREE,
  INDEX `attention_user_index`(`attention_user_id`) USING BTREE,
  INDEX `attention_follower_index`(`attention_follower_id`) USING BTREE,
  CONSTRAINT `attention_follower_index` FOREIGN KEY (`attention_follower_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `attention_user_index` FOREIGN KEY (`attention_user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '关注表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for authority
-- ----------------------------
DROP TABLE IF EXISTS `authority`;
CREATE TABLE `authority`  (
  `authority_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限id',
  `authority_article` int(1) NULL DEFAULT NULL COMMENT '发表文章权限',
  `authority_comment` int(1) NULL DEFAULT NULL COMMENT '发表评论权限',
  PRIMARY KEY (`authority_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for authorization
-- ----------------------------
DROP TABLE IF EXISTS `authorization`;
CREATE TABLE `authorization`  (
  `authorization_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色授权id',
  `authorization_role_id` int(11) NULL DEFAULT NULL COMMENT '角色id',
  `authorization_authority_id` int(11) NULL DEFAULT NULL COMMENT '权限id',
  PRIMARY KEY (`authorization_id`) USING BTREE,
  INDEX `authorization_authority_index`(`authorization_authority_id`) USING BTREE,
  INDEX `authorization_role_index`(`authorization_role_id`) USING BTREE,
  CONSTRAINT `authorization_authority_index` FOREIGN KEY (`authorization_authority_id`) REFERENCES `authority` (`authority_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `authorization_role_index` FOREIGN KEY (`authorization_role_id`) REFERENCES `role` (`role_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色授权表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for collect
-- ----------------------------
DROP TABLE IF EXISTS `collect`;
CREATE TABLE `collect`  (
  `collect_id` int(11) NOT NULL AUTO_INCREMENT,
  `collect_article_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收藏文章id',
  `collect_user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收藏用户id',
  `is_collect` int(1) NULL DEFAULT NULL COMMENT '是否收藏',
  PRIMARY KEY (`collect_id`) USING BTREE,
  INDEX `collect_user_index`(`collect_user_id`) USING BTREE,
  INDEX `collect_article_index`(`collect_article_id`) USING BTREE,
  CONSTRAINT `collect_article_index` FOREIGN KEY (`collect_article_id`) REFERENCES `article` (`article_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `collect_user_index` FOREIGN KEY (`collect_user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 279 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `comment_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `comment_article_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章id',
  `comment_user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论用户id',
  `comment_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论正文',
  `comment_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  `comment_like_num` int(11) NULL DEFAULT NULL COMMENT '评论点赞数',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `comment_article_index`(`comment_article_id`) USING BTREE,
  INDEX `comment_user_index`(`comment_user_id`) USING BTREE,
  CONSTRAINT `comment_article_index` FOREIGN KEY (`comment_article_id`) REFERENCES `article` (`article_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `comment_user_index` FOREIGN KEY (`comment_user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
  `department_id` int(11) UNSIGNED NOT NULL COMMENT '学院id',
  `department_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学院名称',
  PRIMARY KEY (`department_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for like_article
-- ----------------------------
DROP TABLE IF EXISTS `like_article`;
CREATE TABLE `like_article`  (
  `like_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评论点赞id',
  `like_article_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章id',
  `like_user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户id',
  `is_like` int(1) NULL DEFAULT NULL COMMENT '是否点赞',
  PRIMARY KEY (`like_id`) USING BTREE,
  INDEX `like_article_index`(`like_article_id`) USING BTREE,
  INDEX `like_user_index_1`(`like_user_id`) USING BTREE,
  CONSTRAINT `like_article_index` FOREIGN KEY (`like_article_id`) REFERENCES `article` (`article_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `like_user_index_1` FOREIGN KEY (`like_user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1012 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for like_comment
-- ----------------------------
DROP TABLE IF EXISTS `like_comment`;
CREATE TABLE `like_comment`  (
  `like_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评论点赞id',
  `like_comment_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论id',
  `like_user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户id',
  `is_like` int(1) NULL DEFAULT NULL COMMENT '是否点赞',
  PRIMARY KEY (`like_id`) USING BTREE,
  INDEX `like_comment_index`(`like_comment_id`) USING BTREE,
  INDEX `like_user_index_2`(`like_user_id`) USING BTREE,
  CONSTRAINT `like_comment_index` FOREIGN KEY (`like_comment_id`) REFERENCES `comment` (`comment_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `like_user_index_2` FOREIGN KEY (`like_user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 263 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `message_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `message_type` int(1) NULL DEFAULT NULL,
  `article_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `comment_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `receiver_user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sender_user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `replied_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `message_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_read` int(1) NULL DEFAULT NULL,
  `message_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `message_article_index`(`article_id`) USING BTREE,
  INDEX `message_comment_id`(`comment_id`) USING BTREE,
  INDEX `message_receive_index`(`receiver_user_id`) USING BTREE,
  INDEX `message_sender_index`(`sender_user_id`) USING BTREE,
  CONSTRAINT `message_article_index` FOREIGN KEY (`article_id`) REFERENCES `article` (`article_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `message_comment_id` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`comment_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `message_receive_index` FOREIGN KEY (`receiver_user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `message_sender_index` FOREIGN KEY (`sender_user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 154 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for reply
-- ----------------------------
DROP TABLE IF EXISTS `reply`;
CREATE TABLE `reply`  (
  `reply_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回复id',
  `reply_comment_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论id',
  `reply_user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '回复用户id',
  `reply_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '回复内容',
  `reply_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '回复时间',
  PRIMARY KEY (`reply_id`) USING BTREE,
  INDEX `reply_comment_index`(`reply_comment_id`) USING BTREE,
  INDEX `reply_user_index`(`reply_user_id`) USING BTREE,
  CONSTRAINT `reply_comment_index` FOREIGN KEY (`reply_comment_id`) REFERENCES `comment` (`comment_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `reply_user_index` FOREIGN KEY (`reply_user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '回复表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `role_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色名',
  `role_type` int(11) NULL DEFAULT NULL COMMENT '角色类型',
  PRIMARY KEY (`role_id`) USING BTREE,
  INDEX `role_type`(`role_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for slideshow
-- ----------------------------
DROP TABLE IF EXISTS `slideshow`;
CREATE TABLE `slideshow`  (
  `slideshow_id` int(11) NOT NULL AUTO_INCREMENT,
  `topic_type` int(11) NULL DEFAULT NULL,
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`slideshow_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for topic
-- ----------------------------
DROP TABLE IF EXISTS `topic`;
CREATE TABLE `topic`  (
  `topic_id` int(11) NOT NULL COMMENT '版块id',
  `topic_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '版块名',
  `topic_type` int(11) NOT NULL COMMENT '版块编号',
  `topic_create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '版块创建时间',
  `topic_update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '版块更新时间',
  PRIMARY KEY (`topic_id`) USING BTREE,
  INDEX `topic_id`(`topic_id`, `topic_type`) USING BTREE,
  INDEX `topic_type`(`topic_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户id',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `user_role_type` int(11) NULL DEFAULT NULL COMMENT '用户角色id',
  `user_gender` int(1) NULL DEFAULT NULL COMMENT '用户性别：0 男 1 女',
  `user_department` int(11) UNSIGNED NULL DEFAULT 0 COMMENT '用户所在院系',
  `user_ex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户经验',
  `user_article_num` int(11) UNSIGNED NULL DEFAULT 0 COMMENT '用户发帖数',
  `user_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户注册时间',
  `user_emotion` int(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '用户情感状态',
  `user_show` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户签名',
  `user_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像',
  `user_fans_num` int(11) NULL DEFAULT NULL COMMENT '用户粉丝数',
  `user_attention_num` int(11) NULL DEFAULT NULL COMMENT '用户关注数',
  `user_city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户所在城市',
  `user_province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户所在省份',
  `user_country` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户所在国家',
  PRIMARY KEY (`user_id`) USING BTREE,
  INDEX `user_role_index`(`user_role_type`) USING BTREE,
  INDEX `user_department_index`(`user_department`) USING BTREE,
  CONSTRAINT `user_department_index` FOREIGN KEY (`user_department`) REFERENCES `department` (`department_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `user_role_index` FOREIGN KEY (`user_role_type`) REFERENCES `role` (`role_type`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
