CREATE DATABASE IF NOT EXISTS `store`
    DEFAULT CHARACTER SET `utf8`
    COLLATE `utf8mb4_unicode_ci`;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户id',
                      username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
                      password VARCHAR(255) NOT NULL COMMENT '用户密码',
                      email VARCHAR(100) NULL DEFAULT '' COMMENT '用户邮箱',
                      balance DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期'
) ENGINE = InnoDB COMMENT = '用户表';

-- 角色表
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      role_name VARCHAR(50) UNIQUE NOT NULL COMMENT 'consumer, admin, merchant'
) ENGINE = InnoDB COMMENT '角色表';

-- 用户-角色关系表
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
                           user_id BIGINT,
                           role_id BIGINT,
                           PRIMARY KEY(user_id, role_id),
                           FOREIGN KEY(user_id) REFERENCES user(id),
                           FOREIGN KEY(role_id) REFERENCES role(id)
);

-- 商品表
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(100) NOT NULL,
                         description TEXT,
                         price DECIMAL(10,2) NOT NULL,
                         stock INT NOT NULL,
                         status TINYINT DEFAULT 1 COMMENT '1=在售,0=下架',
                         merchant_id BIGINT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (merchant_id) REFERENCES user(id)
);

-- 购物车
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item` (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           user_id BIGINT,
                           product_id BIGINT,
                           quantity INT NOT NULL,
                           FOREIGN KEY(user_id) REFERENCES user(id),
                           FOREIGN KEY(product_id) REFERENCES product(id)
);

-- 用户订单
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        user_id BIGINT,
                        total_price DECIMAL(10,2) NOT NULL,
                        status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, PAID, SHIPPED, COMPLETED, REFUNDED
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY(user_id) REFERENCES user(id)
);

-- 订单
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            order_id BIGINT,
                            product_id BIGINT,
                            quantity INT NOT NULL,
                            price DECIMAL(10,2) NOT NULL,
                            FOREIGN KEY(order_id) REFERENCES orders(id),
                            FOREIGN KEY(product_id) REFERENCES product(id)
);

-- 支付
DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             user_id BIGINT,
                             amount DECIMAL(10,2) NOT NULL,
                             type VARCHAR(20) NOT NULL COMMENT 'RECHARGE, PURCHASE, REFUND',
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY(user_id) REFERENCES user(id)
);

-- 消息
CREATE TABLE `message` (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         sender_id BIGINT,
                         receiver_id BIGINT,
                         content TEXT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY(sender_id) REFERENCES user(id),
                         FOREIGN KEY(receiver_id) REFERENCES user(id)
);