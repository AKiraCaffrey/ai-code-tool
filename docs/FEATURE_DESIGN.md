# 功能设计文档（Feature Design Document）

> 面向电商场景的业务功能设计，覆盖用户、商品、订单、支付、评价与售后全链路。

## 1. 模块清单与功能说明
- **用户模块**：注册、登录、OAuth、个人信息、地址簿、账号安全。
- **商品模块**：类目、品牌、SKU/SPU、价格、库存、上下架、搜索与推荐。
- **购物车模块**：加购、变更数量、选择优惠、勾选结算、合并购物车。
- **订单模块**：创建订单、拆单、运费/优惠计算、订单查询、取消/关闭、物流跟踪。
- **支付模块**：多渠道（支付宝/微信/银行卡）、支付二维码/跳转、回调校验、退款。
- **优惠券与营销模块**：领券、核券、满减、限时折扣、活动白名单/黑名单。
- **评价模块**：评价提交、追加评价、图片上传、敏感词过滤、申诉。
- **售后模块**：退货、换货、仅退款、逆向物流、客服沟通、仲裁。

## 2. 模块业务流程图
### 2.1 用户模块
```mermaid
flowchart LR
    A[注册/登录] --> B[实名/安全校验]
    B --> C[获取用户信息]
    C --> D[维护地址簿]
    D --> E[下单时选择地址]
```

### 2.2 商品模块
```mermaid
flowchart LR
    C1[商品管理后台] --> C2[创建/编辑 SPU]
    C2 --> C3[配置 SKU 价格库存]
    C3 --> C4[上下架发布]
    C4 --> C5[前台展示/搜索]
```

### 2.3 购物车模块
```mermaid
flowchart LR
    G1[用户浏览商品] --> G2[加入购物车]
    G2 --> G3[变更数量/勾选]
    G3 --> G4[优惠试算]
    G4 --> G5[提交结算]
```

### 2.4 订单模块
```mermaid
flowchart LR
    O1[提交订单] --> O2[预扣库存]
    O2 --> O3[生成订单记录]
    O3 --> O4[等待支付]
    O4 -->|支付成功| O5[更新状态/扣减库存]
    O5 --> O6[发货/物流]
    O6 --> O7[收货完成]
```

### 2.5 支付模块
```mermaid
flowchart LR
    P1[下单] --> P2[调用支付网关]
    P2 --> P3[生成支付链接/二维码]
    P3 --> P4[用户支付]
    P4 --> P5[支付回调验签]
    P5 -->|成功| P6[更新订单/记账]
    P5 -->|失败| P7[通知失败/重试]
```

### 2.6 优惠券与营销模块
```mermaid
flowchart LR
    M1[领取优惠券] --> M2[校验有效期/可用范围]
    M2 --> M3[下单时核券]
    M3 -->|通过| M4[抵扣金额]
    M3 -->|未通过| M5[提示不可用]
```

### 2.7 评价模块
```mermaid
flowchart LR
    R1[收货后] --> R2[提交评价]
    R2 --> R3[敏感词过滤]
    R3 --> R4[存储评价]
    R4 --> R5[前台展示/统计]
```

### 2.8 售后模块
```mermaid
flowchart LR
    S1[用户申请售后] --> S2[审核受理]
    S2 -->|通过| S3[用户寄回/免邮]
    S3 --> S4[质检/确认]
    S4 --> S5[退款/补发/换货]
    S2 -->|拒绝| S6[通知原因]
```

## 3. 模块状态机
- **订单状态**：`待支付 -> 待发货 -> 待收货 -> 已完成 -> 已关闭`，中间可穿插`退款中/已退款`。
- **支付状态**：`未支付 -> 支付中 -> 已支付 -> 支付失败/超时`。
- **售后状态**：`待审核 -> 待寄回/待上门取件 -> 质检中 -> 待退款/待补发 -> 完成`，可能存在`拒绝/取消`分支。
- **库存状态**：`可售 -> 预扣 -> 已扣减 -> 回补`。

## 4. 关键字段与接口
- **用户**：`id`、`phone/email`、`password_hash`、`status`、`roles`。
- **商品**：`spu_id`、`sku_id`、`title`、`price`、`stock`、`status`、`attributes`。
- **订单**：`order_id`、`user_id`、`total_amount`、`pay_amount`、`status`、`pay_channel`、`pay_time`、`coupon_id`、`address_id`。
- **支付**：`payment_id`、`order_id`、`channel`、`trade_no`、`amount`、`status`、`notify_payload`。
- **售后**：`after_sale_id`、`order_id`、`type`、`reason`、`status`、`evidence`、`refund_amount`。

**关键接口（示例）**
- `/api/auth/login`：账号登录，返回 Token 与用户信息。
- `/api/products/{id}`：获取商品详情（含 SKU 列表、库存、价格）。
- `/api/cart`：管理购物车项（增删改查）。
- `/api/orders`：创建订单、查询订单列表与详情、取消订单。
- `/api/pay/notify`：支付回调验签并更新订单。
- `/api/aftersale`：提交/查询售后申请，上传物流单号。

## 5. 模块依赖关系图
```mermaid
flowchart LR
    User[用户模块]
    Product[商品模块]
    Cart[购物车模块]
    Order[订单模块]
    Pay[支付模块]
    Coupon[优惠券/营销]
    Review[评价模块]
    AfterSale[售后模块]

    User --> Cart
    Cart --> Order
    Coupon --> Order
    Product --> Cart
    Product --> Order
    Order --> Pay
    Pay --> Order
    Order --> Review
    Order --> AfterSale
    AfterSale --> Pay
```
