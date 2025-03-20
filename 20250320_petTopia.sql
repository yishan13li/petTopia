
use petTopia

-- 使用者基本資料表
CREATE TABLE users (
  id INT IDENTITY(1,1) PRIMARY KEY,   -- 用戶ID，主鍵，自動遞增
    password NVARCHAR(255) NOT NULL,    -- 用戶密碼，必填
    email NVARCHAR(255) NOT NULL,    -- 電子郵件，必填
    user_role NVARCHAR(50) NOT NULL,    -- 用戶角色：MEMBER(會員)、VENDOR(商家)、ADMIN(管理員)
    email_verified BIT DEFAULT 0,   -- 郵件是否已驗證，預設為否(0)
    verification_token NVARCHAR(255),    -- 郵件驗證token
    token_expiry DATETIME2,    -- token過期時間
    is_super_admin BIT DEFAULT 0,    -- 是否為超級管理員，預設為否(0)
    admin_level INT DEFAULT 0,    -- 管理員等級，預設為0
    CONSTRAINT CHK_admin_level CHECK (admin_level BETWEEN 0 AND 1),    -- 只限制admin_level的值
provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL' --識別本地帳號(LOCAL)還是第三方(google,facebook)
);
-- 添加 local_enabled 欄位到 Users 表
ALTER TABLE Users ADD local_enabled BIT NOT NULL DEFAULT 0; 

 --會員詳細資料
CREATE TABLE member (
    id INT PRIMARY KEY REFERENCES users(id), -- 會員ID，對應到 Users 表中的 UserId
    [name] NVARCHAR(100) , -- 會員姓名，非必填	
    phone NVARCHAR(20), -- 會員電話，非必填
    birthdate DATETIME, -- 會員生日，非必填
    gender bit DEFAULT 0, -- 會員性別，預設值為 male  , 1為female，非必填
    [address] NVARCHAR(255), -- 會員地址，非必填
    profile_photo VARBINARY(MAX), -- 會員照片，非必填
    [status] BIT DEFAULT 0 not null,-- 會員認證狀態，預設為未認證 (0) 跟商家TABLE相同保持BIT，非必填
    updated_date DATETIME DEFAULT GETDATE(), -- 更新時間，預設為當前系統時間
   );

ALTER TABLE member 
ALTER COLUMN birthdate DATE;  -- 更改會員生日為DATE.util而不是LOCALDATETMIE


 --管理員詳細資料
CREATE TABLE [admin] (
    id INT PRIMARY KEY REFERENCES users (id) , -- 管理ID，自動遞增主鍵
    [name] NVARCHAR(100) not null, -- 管理員姓名
    [role] NVARCHAR(20) CHECK (role IN ('SA', 'admin', 'employee')) NOT NULL,
    registration_date DATETIME DEFAULT GETDATE(), -- 註冊日期，預設當前系統時間
);

-- 店家類別表
CREATE TABLE vendor_category (
id INT PRIMARY KEY IDENTITY(1,1),
[name] NVARCHAR(255) NOT NULL unique --餐廳,旅店,其他
);

-- 店家主表 (3/19修正) 
CREATE TABLE vendor ( 
id INT PRIMARY KEY REFERENCES users([id]),
[name] NVARCHAR(255), 
[description] NVARCHAR(255), 
logo_img VARBINARY(MAX), -- 儲存圖片 URL 
[address] NVARCHAR(255), 
phone NVARCHAR(255), 
contact_email NVARCHAR(255) , -- 改成聯絡人mail，可以跟帳號的不一樣
contact_person NVARCHAR(255) , 
taxid_number NVARCHAR(20), -- 商家統一編號 
[status] BIT DEFAULT 0 , -- 商家認證狀態，預設為未認證 (0)
vendor_category_id INT FOREIGN KEY REFERENCES vendor_category(id),
registration_date DATETIME DEFAULT GETDATE(), 
updated_date DATETIME DEFAULT GETDATE(),
event_count INT DEFAULT 0 , -- 店家活動數 
total_rating float DEFAULT 0 , -- 總評分星級 
review_count INT DEFAULT 0 , -- 評論數量 
vendor_level NVARCHAR(50) DEFAULT '普通', -- 預設店家等級
avg_rating_environment float DEFAULT 0 , -- 自動計算平均評分 
avg_rating_price float DEFAULT 0 , -- 自動計算平均評分 
avg_rating_service float DEFAULT 0); -- 自動計算平均評分 

--店家認證標語
CREATE TABLE certification_tag ( 
id INT PRIMARY KEY IDENTITY(1,1), -- 自動遞增的標語ID 
tag_name NVARCHAR(255) NOT NULL UNIQUE );-- 認證標語名稱 


--店家認證申請
CREATE TABLE vendor_certification ( 
id INT PRIMARY KEY IDENTITY(1,1), 
vendor_id INT NOT NULL FOREIGN KEY REFERENCES vendor(id), certification_status NVARCHAR(50) DEFAULT '申請中' NOT NULL, -- 申請中、已認證、未通過 
reason NVARCHAR(1000), -- 認證原因 (例如：評論分析結果) 
request_date DATETIME DEFAULT GETDATE(), -- 申請時間 
approved_date DATETIME ); -- 審核通過時間 

-- 關聯表：店家申請的標語 (允許同一店家多次申請不同認證)
CREATE TABLE vendor_certification_tag (
id INT PRIMARY KEY IDENTITY(1,1),
certification_id INT NOT NULL FOREIGN KEY REFERENCES vendor_certification(id) ON DELETE CASCADE,
tag_id INT NOT NULL FOREIGN KEY REFERENCES certification_tag(id) ON DELETE CASCADE,
meets_standard BIT DEFAULT 0 NOT NULL, -- 0: 不符合, 1: 符合
UNIQUE(certification_id, tag_id) -- 這是針對該次申請唯一，不影響未來新申請
);


-- 活動類型表
CREATE TABLE activity_type (
id INT PRIMARY KEY IDENTITY(1,1),
[name] NVARCHAR(50) NOT NULL unique
);

-- 店家圖片子表
CREATE TABLE vendor_images (
id INT PRIMARY KEY IDENTITY(1,1),
vendor_id INT FOREIGN KEY REFERENCES vendor(id),
[image] VARBINARY(MAX) NOT NULL
);

-- 店家活動表
CREATE TABLE vendor_activity (
id INT PRIMARY KEY IDENTITY(1,1),
vendor_id INT FOREIGN KEY REFERENCES vendor(id),
[name] NVARCHAR(255) NOT NULL,
[description] NVARCHAR(255) NOT NULL,
start_time DATETIME NOT NULL ,
end_time DATETIME NOT NULL,
is_registration_required BIT DEFAULT 0 not null, -- 是否需要報名
activity_type_id INT FOREIGN KEY REFERENCES activity_type(id), -- 活動類型外鍵
registration_date DATETIME DEFAULT GETDATE(),
number_visitor int not null default 0, --訪問人數
[address] varchar(255) not null
);

-- 行事曆事件表 (顯示在行事曆上的事件)
CREATE TABLE calendar_event (
id INT PRIMARY KEY IDENTITY(1,1),
vendor_id INT FOREIGN KEY REFERENCES vendor(id),
event_title NVARCHAR(255) NOT NULL, -- 事件名稱
start_time DATETIME NOT NULL ,
end_time DATETIME NOT NULL,
vendor_activity_id INT FOREIGN KEY REFERENCES vendor_activity(id), -- 關聯的活動ID
created_at DATETIME DEFAULT GETDATE(), -- 創建時間
updated_at DATETIME DEFAULT GETDATE(), -- 最後更新時間
color varchar(7)
);



-- 活動圖片子表
CREATE TABLE vendor_activity_images (
id INT PRIMARY KEY IDENTITY(1,1),
vendor_activity_id INT FOREIGN KEY REFERENCES vendor_activity(id),
[image] VARBINARY(MAX) NOT NULL
);

-- 活動人數表
CREATE TABLE activity_people_number (
id INT PRIMARY KEY IDENTITY(1,1),
vendor_activity_id INT FOREIGN KEY REFERENCES vendor_activity(id),
max_participants INT NOT NULL, -- 活動最大人數
current_participants INT NOT NULL DEFAULT 0 -- 目前參與人數
)

-- (3/16更改約束)
CREATE TABLE activity_registration (
    id INT PRIMARY KEY IDENTITY(1,1),
    vendor_activity_id INT FOREIGN KEY REFERENCES vendor_activity(id),
    member_id INT FOREIGN KEY REFERENCES member(id),
    registration_time DATETIME DEFAULT GETDATE(),
    [status] NVARCHAR(50) DEFAULT 'pending' CHECK ([status] IN ('pending', 'confirmed', 'canceled')), -- 限制 status 的有效值
    CONSTRAINT unique_member_activity UNIQUE (member_id, vendor_activity_id) -- 保证一个 member 只能对一个活动提交一次
);


-- 活動收藏表
CREATE TABLE activity_like (
id INT PRIMARY KEY IDENTITY(1,1),
member_id INT FOREIGN KEY REFERENCES member(id),
vendor_activity_id INT FOREIGN KEY REFERENCES vendor_activity(id)
);
-- 活動評論表 ( 3/6更新 )
CREATE TABLE vendor_activity_review (
id INT PRIMARY KEY IDENTITY(1,1),
vendor_id INT FOREIGN KEY REFERENCES vendor(id) NOT NULL,
member_id INT FOREIGN KEY REFERENCES member(id) NOT NULL,
review_time DATETIME DEFAULT GETDATE() NOT NULL,
review_content NVARCHAR(255) NOT NULL,
vendor_activity_id INT FOREIGN KEY REFERENCES vendor_activity(id) NOT NULL
);

-- 店家評論表 ( 2/28更新 )
CREATE TABLE vendor_review (
id INT PRIMARY KEY IDENTITY(1,1),
vendor_id INT FOREIGN KEY REFERENCES vendor(id) NOT NULL,
member_id INT FOREIGN KEY REFERENCES member(id) NOT NULL,
review_time DATETIME DEFAULT GETDATE(), --可為null
review_content NVARCHAR(255),
rating_environment INT CHECK (rating_environment BETWEEN 1 AND 5),
rating_price INT CHECK (rating_price BETWEEN 1 AND 5),
rating_service INT CHECK (rating_service BETWEEN 1 AND 5)
);

-- 店家收藏表 (2/28新增)
CREATE TABLE vendor_like (
id INT PRIMARY KEY IDENTITY(1,1),
member_id INT FOREIGN KEY REFERENCES member(id),
vendor_id INT FOREIGN KEY REFERENCES vendor(id)
);

-- 店家評論圖片表
CREATE TABLE review_photo (
id INT PRIMARY KEY IDENTITY(1,1),
vendor_review_id INT FOREIGN KEY REFERENCES vendor_review(id),
photo VARBINARY(MAX) NOT NULL
);

-- 活動通知
CREATE TABLE notification (
id INT PRIMARY KEY IDENTITY(1,1),
member_id INT FOREIGN KEY REFERENCES member(id), -- 接收通知的會員
vendor_id INT FOREIGN KEY REFERENCES vendor(id), -- 發送通知的店家
vendor_activity_id INT FOREIGN KEY REFERENCES vendor_activity(id), -- 對應的活動
notification_title NVARCHAR(255) NOT NULL, -- 通知標題
notification_content NVARCHAR(1000) NOT NULL, -- 通知內容
is_read BIT DEFAULT 0 NOT NULL, -- 是否已讀 (0: 未讀, 1: 已讀)
sent_time DATETIME DEFAULT GETDATE() -- 發送時間
);

-- 友善店家座標
CREATE TABLE friendly_shop( 
id INT IDENTITY(1,1) PRIMARY KEY not null, -- 自動增量的主鍵 
[name] NVARCHAR(255) NOT NULL, -- 商店名稱,
vendor_id INT FOREIGN KEY REFERENCES vendor(id)  unique,
vendor_category_id INT REFERENCES vendor_category(id),--店家類型
[address] NVARCHAR(255) NOT NULL, -- 地址 
longitude DECIMAL(10, 7) NOT NULL, -- 經度 
latitude DECIMAL(10, 7) NOT NULL -- 緯度
 );

--商品種類
 CREATE TABLE product_category (
  id int PRIMARY KEY identity(1,1),
  [name] nvarchar(100) not null unique --食品保健,日常用品,服飾,玩具,其他
);

--商品細節
CREATE TABLE [product_detail] (
  id int PRIMARY KEY identity(1,1),
  product_category_id int REFERENCES product_category(id),
  [name] nvarchar(255) NOT NULL unique, 
  [description] nvarchar(255));

--商品顏色
CREATE TABLE product_color ( 
  id INT PRIMARY KEY IDENTITY(1,1), 
  [name] NVARCHAR(50) NOT NULL unique); 

--商品尺寸
CREATE TABLE product_size ( 
  id INT PRIMARY KEY IDENTITY(1,1), 
  [name] NVARCHAR(50) NOT NULL unique);--S,M,L

--商品
CREATE TABLE [product] ( 
  id INT PRIMARY KEY IDENTITY(1,1), 
  product_detail_id INT REFERENCES product_detail(id) NOT NULL, 
  product_size_id INT REFERENCES product_size(id),
  product_color_id INT REFERENCES product_color(id), 
  stock_quantity INT NOT NULL default 0, 
  unit_price decimal(10,2) NOT NULL, -- 如果不同組合有不同價格
  discount_price decimal(10,2),--null表示沒有特價
  created_time datetime default getdate(),
  [status] BIT DEFAULT 0 not null,--上下架
  photo varbinary(max)
  UNIQUE(product_detail_id, product_color_id, product_size_id) -- 確保同一商品不會有重複的顏色和尺寸組合 
  );

--商品評論
CREATE TABLE product_review (
  id int PRIMARY KEY identity(1,1),
  product_id int REFERENCES [product](id),
  member_id int REFERENCES [member](id),
  rating int not null check(rating between 1 and 5),
  review_description nvarchar(255),
  review_time datetime default getdate()
  UNIQUE (product_id, member_id) --一個人只能評論一次同一個商品
);

--商品評論照片
CREATE TABLE product_review_photo ( 
  id INT PRIMARY KEY IDENTITY(1,1), 
  product_review_id INT REFERENCES product_review(id), 
  review_photo varbinary(max) not null );

--管理員商品評論回覆
CREATE TABLE admin_product_reply (
  id int PRIMARY KEY identity(1,1),
  product_review_id int REFERENCES product_review(id),
  admin_id int REFERENCES [admin](id),
  reply_text nvarchar(255) not null,
  reply_time datetime default getdate()
);

-- 優惠券表
CREATE TABLE coupons (
    id INT PRIMARY KEY IDENTITY(1,1), -- 優惠券ID，自動遞增
name nvarchar(50) not null unique,
    discount_type bit default 0 not null, -- 優惠券折價方式，0為固定扣額，1為打折
    discount_value DECIMAL(10,2) NOT NULL CHECK (discount_value > 0), -- 折扣值，例如20.00表示20元，或20%表示20%折扣。都是台幣計算
    min_order_value DECIMAL(10,2) default 0 not null CHECK (min_order_value >= 0), -- 最低訂單金額限制，例如訂單需滿299元才可使用
    limit_count INT NOT NULL CHECK (limit_count > 0), -- 每個會員可以使用的優惠券數量，必須為正數
    valid_start DATETIME not null, -- 優惠券啟用時間，預設為當前時間
    valid_end DATETIME not null, -- 優惠券截止時間，預設為當前時間
    [status] BIT DEFAULT 0 not null-- 優惠券狀態，0 表示「未啟用」，1 表示「啟用」
);

-- 每個會員的優惠券表
CREATE TABLE member_coupon (
    member_id INT references member(id) not null,
    coupons_id INT references coupons(id) not null,
   usage_count INT DEFAULT 0,
   status bit default 1,
PRIMARY KEY (member_id, coupons_id))
;

--訂單狀態
CREATE TABLE order_status ( 
  id INT PRIMARY KEY IDENTITY(1,1), 
  [name] NVARCHAR(50) NOT NULL UNIQUE )
-- 待出貨,配送中,待收貨,已完成,已取消

--訂單
CREATE TABLE [order] (
  id int PRIMARY KEY identity(1,1),
  member_id int REFERENCES [member](id),
 subtotal decimal(10,2) not null, --商品總金額
coupon_id int REFERENCES coupons(id),
discount_amount decimal(10,2) default 0 not null,--金額折扣
shipping_fee decimal(10,2) default 0 not null,--運費
total_amount decimal(10,2) not null, -- 訂單總金額
 order_status_id int references order_status(id),
 created_time datetime default getdate(),
updated_date DATETIME DEFAULT GETDATE(),
note nvarchar(255)
);

--付款狀態
CREATE TABLE payment_status ( 
  id INT PRIMARY KEY IDENTITY(1,1), 
  [name] NVARCHAR(50) NOT NULL UNIQUE);-- 待付款, 已付款,付款失敗 

--訂單細節
CREATE TABLE order_details (
  id int PRIMARY KEY identity(1,1),
  order_id int REFERENCES [order](id),
  product_id int REFERENCES [product](id),
  quantity int not null,
  unit_price decimal(10,2) not null,--商品單價
  discount_price decimal(10,2), --商品單價特價的價格，null表示該商品沒有特價
  total_price decimal(10,2) not null, --商品總價


);

--購物車
CREATE TABLE cart (
  id int PRIMARY KEY identity(1,1),
  member_id int REFERENCES [member](id),
  product_id int REFERENCES [product](id),
  quantity int not null,
created_date DATETIME DEFAULT GETDATE(),
);

--運送方式
CREATE TABLE shipping_category (
  id int PRIMARY KEY identity(1,1),
  [name] nvarchar(100) not null unique,
  shipping_cost DECIMAL(10,2) not null default 0, 
  shipping_day int not null default 7-- 預估配送天數，預設7天
);  --宅配,快遞  

--付款方式
CREATE TABLE payment_category (
  id int PRIMARY KEY identity(1,1),
  [name] nvarchar(100) not null unique  --信用卡付款 貨到付款
);

--付款
CREATE TABLE payment (
  id int PRIMARY KEY identity(1,1),
  order_id int REFERENCES [order](id),
  payment_amount decimal(10,2),  --改成可以null
  payment_category_id int REFERENCES payment_category(id),
  payment_status_id INT REFERENCES payment_status(id),
trade_no nvarchar(255),
  payment_date datetime default getdate(),
  updated_date datetime default getdate(),
);

--運送地址
  CREATE TABLE shipping_address ( 
  id INT PRIMARY KEY IDENTITY(1,1), 
  member_id INT REFERENCES member(id), -- 會員ID
  city NVARCHAR(100) not null,
  street NVARCHAR(255) not null, 
  is_current BIT DEFAULT 0 -- 標記此地址是否為當前地址，1 為當前地址，0 為舊地址 );
)

--運送
CREATE TABLE shipping ( 
  id int PRIMARY KEY identity(1,1),
  order_id int REFERENCES [order](id),
  shipping_address_id INT REFERENCES shipping_address(id),
  shipping_category_id int REFERENCES shipping_category(id),
  receiver_name NVARCHAR(100) not null ,--收件人姓名
  receiver_phone NVARCHAR(20) NOT NULL ,--收件人電話 09XXXXXXXX
  shipping_date datetime default getdate(),
 updated_time datetime default getdate()
);

-- 聊天室訊息
CREATE TABLE [messages] (
  id int PRIMARY KEY identity(1,1),
  sender_id int REFERENCES users([id]),
  receiver_id int REFERENCES users([id]),
  content_text nvarchar(255),
  send_time datetime default getdate()
);






