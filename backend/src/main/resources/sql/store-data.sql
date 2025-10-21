USE `store`;

-- 初始化角色数据
INSERT INTO `role` (`role_name`, `description`)
VALUES ('consumer', '普通消费者'),
       ('admin', '超级管理员'),
       ('merchant', '商家');