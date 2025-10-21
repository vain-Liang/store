CREATE DATABASE IF NOT EXISTS `store`
    DEFAULT CHARACTER SET `utf8mb4`
    COLLATE `utf8mb4_unicode_ci`;

USE `store`;

-- 角色表（先创建，因为用户表需要外键约束）
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
                        `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                        `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称: consumer, admin, merchant',
                        `description` VARCHAR(200) DEFAULT '' COMMENT '角色描述',
                        `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_role_name` (`role_name`),
                        KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                        `username` VARCHAR(50) NOT NULL COMMENT '用户名',
                        `password` VARCHAR(255) NOT NULL COMMENT '加密密码',
                        `email` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '邮箱',
                        `phone` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '手机号',
                        `balance` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
                        `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-正常, 0-冻结',
                        `last_login` TIMESTAMP NULL DEFAULT NULL COMMENT '最后登录时间',
                        `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_username` (`username`),
                        UNIQUE KEY `uk_email` (`email`),
                        KEY `idx_status` (`status`),
                        KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户-角色关系表
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
                             `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
                             `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
                             `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`user_id`, `role_id`),
                             KEY `idx_role_id` (`role_id`),
                             CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                             CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-角色关系表';

-- 商品表
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
                           `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商品ID',
                           `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
                           `description` TEXT COMMENT '商品描述',
                           `image_url` VARCHAR(512) NULL DEFAULT NULL COMMENT '商品主图URL',
                           `price` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '售价',
                           `cost_price` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '成本价',
                           `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
                           `category_id` INT NOT NULL DEFAULT 0 COMMENT '分类ID',
                           `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-在售, 2-下架, 3-缺货',
                           `merchant_id` BIGINT UNSIGNED NOT NULL COMMENT '商家ID',
                           `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           KEY `idx_merchant_id` (`merchant_id`),
                           KEY `idx_category` (`category_id`),
                           KEY `idx_status` (`status`),
                           KEY `idx_price` (`price`),
                           FULLTEXT KEY `ft_name_desc` (`name`, `description`),
                           CONSTRAINT `fk_product_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 购物车表
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item` (
                             `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
                             `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
                             `product_id` BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
                             `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
                             `selected` TINYINT NOT NULL DEFAULT 1 COMMENT '是否选中: 1-是, 0-否',
                             `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
                             KEY `idx_user_id` (`user_id`),
                             CONSTRAINT `fk_cart_item_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                             CONSTRAINT `fk_cart_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- 订单主表
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
                          `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
                          `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
                          `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
                          `total_amount` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
                          `pay_amount` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '实际支付金额',
                          `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-待支付, 2-已支付, 3-已发货, 4-已完成, 5-已取消, 6-退款中, 7-已退款',
                          `address` TEXT NOT NULL COMMENT '收货地址',
                          `consignee` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '收货人',
                          `phone` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '联系电话',
                          `pay_time` TIMESTAMP NULL DEFAULT NULL COMMENT '支付时间',
                          `delivery_time` TIMESTAMP NULL DEFAULT NULL COMMENT '发货时间',
                          `complete_time` TIMESTAMP NULL DEFAULT NULL COMMENT '完成时间',
                          `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `uk_order_no` (`order_no`),
                          KEY `idx_user_id` (`user_id`),
                          KEY `idx_status` (`status`),
                          KEY `idx_created_time` (`created_time`),
                          CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单主表';

-- 订单商品表
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
                              `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
                              `order_id` BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
                              `product_id` BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
                              `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称（快照）',
                              `product_price` DECIMAL(15,2) NOT NULL COMMENT '商品单价（快照）',
                              `quantity` INT NOT NULL COMMENT '购买数量',
                              `subtotal` DECIMAL(15,2) NOT NULL COMMENT '小计金额',
                              `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              PRIMARY KEY (`id`),
                              KEY `idx_order_id` (`order_id`),
                              KEY `idx_product_id` (`product_id`),
                              CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                              CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单商品表';

-- 交易流水表
DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
                               `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '交易ID',
                               `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
                               `order_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联订单ID',
                               `amount` DECIMAL(15,2) NOT NULL COMMENT '交易金额',
                               `type` TINYINT NOT NULL COMMENT '类型: 1-充值, 2-消费, 3-退款',
                               `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-成功, 2-失败, 3-处理中',
                               `transaction_no` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '第三方交易号',
                               `remark` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '交易备注',
                               `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`id`),
                               KEY `idx_user_id` (`user_id`),
                               KEY `idx_order_id` (`order_id`),
                               KEY `idx_type` (`type`),
                               KEY `idx_created_time` (`created_time`),
                               CONSTRAINT `fk_transaction_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                               CONSTRAINT `fk_transaction_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交易流水表';

-- 消息表 (拓展功能时再创建)
-- DROP TABLE IF EXISTS `message`;
-- CREATE TABLE `message` (
--                            `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息ID',
--                            `sender_id` BIGINT UNSIGNED NOT NULL COMMENT '发送者ID',
--                            `receiver_id` BIGINT UNSIGNED NOT NULL COMMENT '接收者ID',
--                            `content` TEXT NOT NULL COMMENT '消息内容',
--                            `msg_type` TINYINT NOT NULL DEFAULT 1 COMMENT '消息类型: 1-文本, 2-图片, 3-商品链接',
--                            `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读: 1-是, 0-否',
--                            `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--                            `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--                            PRIMARY KEY (`id`),
--                            KEY `idx_sender` (`sender_id`),
--                            KEY `idx_receiver` (`receiver_id`),
--                            KEY `idx_created_time` (`created_time`),
--                            CONSTRAINT `fk_message_sender` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
--                            CONSTRAINT `fk_message_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 初始化角色数据
INSERT INTO `role` (`role_name`, `description`)
VALUES ('consumer', '普通消费者'),
       ('admin', '超级管理员'),
       ('merchant', '商家');