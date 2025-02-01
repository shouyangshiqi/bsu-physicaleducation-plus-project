CREATE TABLE oms_order (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
                           user_id BIGINT NOT NULL COMMENT '用户ID',
                           order_sn CHAR(64) NOT NULL COMMENT '订单号',
                           address_id BIGINT COMMENT '地址ID',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           username VARCHAR(200) COMMENT '用户名',
                           total_amount DECIMAL(18,4) NOT NULL COMMENT '订单总金额',
                           pay_amount DECIMAL(18,4) NOT NULL COMMENT '应付总额',
                           freight_amount DECIMAL(18,4) COMMENT '运费金额',
                           coupon_amount DECIMAL(18,4) COMMENT '优惠券抵扣金额',
                           pay_type TINYINT COMMENT '支付方式 [1-支付宝，2-微信，3-银联，4-货到付款]',
                           status TINYINT COMMENT '订单状态 [0-待付款，1-待发货，2-已发货，3-已完成，4-已关闭，5-无效订单]',
                           updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '订单表';

CREATE TABLE oms_order_item (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
                                order_id BIGINT NOT NULL COMMENT '订单ID',
                                order_sn CHAR(64) NOT NULL COMMENT '订单号',
                                product_id BIGINT COMMENT '商品ID',
                                product_name VARCHAR(255) COMMENT '商品名称',
                                product_pic VARCHAR(500) COMMENT '商品图片',
                                category_id BIGINT COMMENT '商品分类ID',
                                product_quantity INT COMMENT '商品购买数量',
                                product_attrs_vals VARCHAR(500) COMMENT '商品销售属性组合（JSON格式）',
                                product_price DECIMAL(18, 4) COMMENT '销售价格',
                                coupon_amount DECIMAL(18,4) COMMENT '优惠券优惠分解金额',
                                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '订单商品表';

CREATE TABLE oms_payment_info (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '支付记录ID',
                                  order_sn CHAR(32) NOT NULL COMMENT '订单号 (对外业务号)',
                                  order_id BIGINT NOT NULL COMMENT '订单ID',
                                  alipay_trade_no VARCHAR(50) COMMENT '支付宝交易流水号',
                                  total_amount DECIMAL(18,4) NOT NULL COMMENT '支付总金额',
                                  subject VARCHAR(200) COMMENT '交易内容',
                                  payment_status VARCHAR(20) COMMENT '支付状态',
                                  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  confirm_time DATETIME COMMENT '确认时间',
                                  callback_content VARCHAR(4000) COMMENT '回调内容',
                                  callback_time DATETIME COMMENT '回调时间',
                                  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '支付信息表';