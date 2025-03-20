BEGIN
USE [petTopia]
END;

--------------------- 會員假資料 ---------------------
--------------------- 商家、會員、店家 ---------------------
BEGIN 

-- 插入 10 筆商家用戶資料
INSERT INTO users (password, email, user_role, email_verified, provider) VALUES
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor1@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor2@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor3@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor4@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor5@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor6@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor7@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor8@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor9@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor10@example.com', 'VENDOR', 1, 'LOCAL');

-- 插入 10 筆會員用戶資料
INSERT INTO users (password, email, user_role, email_verified, provider) VALUES
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member1@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member2@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member3@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member4@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member5@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member6@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member7@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member8@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member9@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member10@example.com', 'MEMBER', 1, 'LOCAL');

-- 插入 10 筆會員資料 (關聯 users 表)
INSERT INTO member (id, name, phone, birthdate, gender, address,status) VALUES
(11, '陳莉絲', '0912345678', '1990-01-01', 1, '台北市中正區仁愛路1號',1),
(12, '王小明', '0923456789', '1991-02-02', 0, '新北市板橋區文化路2號',1),
(13, '林大雄', '0934567890', '1992-03-03', 0, '台中市西屯區台灣大道3段3號',1),
(14, '張志國', '0955678901', '1993-04-04', 0, '台南市中西區成功路4號',1),
(15, '許小美', '0966789012', '1994-05-05', 1, '高雄市苓雅區三多路5號',1),
(16, '楊志福', '0977890123', '1995-06-06', 0, '桃園市中壢區元化路6號',1),
(17, '郭佳慧', '0988901234', '1996-07-07', 1, '新竹市東區光復路7號',1),
(18, '劉建豪', '0910012345', '1997-08-08', 0, '彰化縣員林市中山路8號',1),
(19, '曾小芸', '0921123456', '1998-09-09', 1, '嘉義市西區中興路9號',1),
(20, '蘇志偉', '0932234567', '1999-10-10', 0, '基隆市仁愛區信義路10號',1);

-- 店家種類
INSERT INTO vendor_category ([name]) VALUES
('寵物美容'),
('寵物用品店'),
('寵物醫院'),
('寵物寄宿'),
('寵物餐廳'),
('寵物訓練'),
('水族用品'),
('爬蟲類專門店'),
('寵物攝影'),
('寵物手作工坊'),
('其他');

-- 插入店家（10 家寵物友善店）
INSERT INTO vendor (id, [name], [description], logo_img, [address], phone, contact_email, contact_person, taxid_number, [status], vendor_category_id, registration_date, updated_date, event_count, total_rating, review_count, vendor_level)
VALUES
(1, '毛孩天堂寵物美容', '專業寵物美容與SPA，讓毛孩擁有最舒適的體驗', NULL, '台北市大安區信義路五段100號', '02-1234-5678', 'contact1@example.com', '張小姐', '12345678', 1, 1, GETDATE(), GETDATE(), 0, 0, 0, '普通'),
(2, '汪喵精品寵物用品', '提供各種寵物食品與用品，滿足毛孩需求', NULL, '台中市西屯區台灣大道三段200號', '04-8765-4321', 'contact2@example.com', '李先生', '23456789', 1, 2, GETDATE(), GETDATE(), 0, 0, 0, '普通'),
(3, '安心動物醫院', '專業獸醫團隊，提供最安心的醫療服務', NULL, '新北市板橋區中山路一段300號', '02-5566-7788', 'contact3@example.com', '王醫師', '34567890', 1, 3, GETDATE(), GETDATE(), 0, 0, 0, '普通'),
(4, '毛孩樂園寵物寄宿', '專業照顧，給毛孩一個舒適的家', NULL, '高雄市苓雅區成功一路50號', '07-3344-5566', 'contact4@example.com', '林小姐', '45678901', 1, 4, GETDATE(), GETDATE(), 0, 0, 0, '普通'),
(5, '寵物咖啡館喵喵汪汪', '享受美食與毛孩共度美好時光', NULL, '桃園市中壢區中華路88號', '03-5566-7788', 'contact5@example.com', '陳先生', '56789012', 1, 5, GETDATE(), GETDATE(), 0, 0, 0, '普通'),
(6, '狗狗訓練學院', '專業狗狗訓練課程，讓愛犬變成聽話乖寶寶', NULL, '新竹市東區光復路200號', '03-3344-5566', 'contact6@example.com', '楊教練', '67890123', 1, 6, GETDATE(), GETDATE(), 0, 0, 0, '普通'),
(7, '海洋樂趣水族館', '專營觀賞魚、海水魚與水族設備', NULL, '台南市中西區民族路77號', '06-7788-5566', 'contact7@example.com', '趙先生', '78901234', 1, 7, GETDATE(), GETDATE(), 0, 0, 0, '普通'),
(8, '爬寵世界', '專業飼養爬蟲類，提供高品質飼養環境與用品', NULL, '台北市松山區南京東路100號', '02-8899-6677', 'contact8@example.com', '吳先生', '89012345', 1, 8, GETDATE(), GETDATE(), 0, 0, 0, '普通'),
(9, '毛小孩攝影館', '專為寵物打造美麗回憶的攝影棚', NULL, '台中市南屯區五權西路300號', '04-4455-6677', 'contact9@example.com', '周小姐', '90123456', 1, 9, GETDATE(), GETDATE(), 0, 0, 0, '普通'),
(10, '手作寵物小物', '手工製作寵物衣物與配件，獨一無二的設計', NULL, '彰化市中正路150號', '04-7788-5566', 'contact10@example.com', '戴小姐', '01234567', 1, 10, GETDATE(), GETDATE(), 0, 0, 0, '普通');


END;

--------------------- 店家假資料 ---------------------
--------------------- 店家 ---------------------
BEGIN

-- 更新店家 logo_img 圖片
/*
UPDATE vendor
SET logo_img = (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)
WHERE id = 1;
UPDATE vendor
SET logo_img = (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)
WHERE id = 2;
UPDATE vendor
SET logo_img = (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)
WHERE id = 3;
UPDATE vendor
SET logo_img = (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)
WHERE id = 4;
UPDATE vendor
SET logo_img = (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)
WHERE id = 5;
UPDATE vendor
SET logo_img = (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)
WHERE id = 6;
UPDATE vendor
SET logo_img = (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)
WHERE id = 7;
UPDATE vendor
SET logo_img = (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)
WHERE id = 8;
UPDATE vendor
SET logo_img = (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)
WHERE id = 9;
UPDATE vendor
SET logo_img = (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)
WHERE id = 10;
*/
-- 插入所有店家的圖片（全部使用 p1.jpg）
/*
INSERT INTO vendor_images (vendor_id, [image])
VALUES
(1, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(1, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(2, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(2, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(3, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(3, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(4, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(4, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(5, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(5, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(6, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(6, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(7, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(7, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(8, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(8, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(9, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(9, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(10, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(10, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img));
*/
-- 插入活動類型
INSERT INTO activity_type ([name]) VALUES
('寵物市集'),
('運動會'),
('下午茶'),
('聚餐'),
('DIY活動'),
('寵物攝影日'),
('其他');

-- 插入店家活動
INSERT INTO vendor_activity (vendor_id, [name], [description], start_time, end_time, is_registration_required, activity_type_id, registration_date, number_visitor, [address])
VALUES
(1, '毛孩美容日', '提供免費寵物美容體驗，讓毛孩煥然一新', DATEADD(DAY, 7, GETDATE()), DATEADD(DAY, 7, GETDATE()), 1, 1, GETDATE(), 0, '台北市大安區信義路五段100號'),
(1, 'SPA放鬆體驗', '專業寵物 SPA，舒緩壓力與焦慮', DATEADD(DAY, 15, GETDATE()), DATEADD(DAY, 15, GETDATE()), 1, 3, GETDATE(), 0, '台北市大安區信義路五段100號'),

(2, '新品試吃會', '提供新款寵物食品試吃，讓毛孩找到最愛的口味', DATEADD(DAY, 10, GETDATE()), DATEADD(DAY, 10, GETDATE()), 0, 4, GETDATE(), 0, '台中市西屯區台灣大道三段200號'),
(2, '寵物市集', '各類寵物用品與手作商品展售', DATEADD(DAY, 20, GETDATE()), DATEADD(DAY, 20, GETDATE()), 0, 1, GETDATE(), 0, '台中市西屯區台灣大道三段200號'),

(3, '健康檢查日', '免費寵物健康檢查，提供專業建議', DATEADD(DAY, 5, GETDATE()), DATEADD(DAY, 5, GETDATE()), 1, 7, GETDATE(), 0, '新北市板橋區中山路一段300號'),
(3, '疫苗注射優惠', '特定疫苗施打享優惠價', DATEADD(DAY, 12, GETDATE()), DATEADD(DAY, 12, GETDATE()), 1, 7, GETDATE(), 0, '新北市板橋區中山路一段300號'),

(4, '毛孩運動會', '各種寵物比賽與遊戲，挑戰毛孩體能極限', DATEADD(DAY, 8, GETDATE()), DATEADD(DAY, 8, GETDATE()), 0, 2, GETDATE(), 0, '高雄市苓雅區成功一路50號'),
(4, '住宿體驗日', '免費體驗一天寵物寄宿服務', DATEADD(DAY, 18, GETDATE()), DATEADD(DAY, 18, GETDATE()), 1, 7, GETDATE(), 0, '高雄市苓雅區成功一路50號'),

(5, '寵物下午茶派對', '與毛孩一起享受下午茶時光', DATEADD(DAY, 9, GETDATE()), DATEADD(DAY, 9, GETDATE()), 0, 3, GETDATE(), 0, '桃園市中壢區中華路88號'),
(5, '寵物聚餐趴', '一起與寵物朋友們共享晚餐', DATEADD(DAY, 17, GETDATE()), DATEADD(DAY, 17, GETDATE()), 0, 4, GETDATE(), 0, '桃園市中壢區中華路88號'),

(6, '狗狗行為訓練體驗', '體驗基礎狗狗行為訓練課程', DATEADD(DAY, 6, GETDATE()), DATEADD(DAY, 6, GETDATE()), 1, 7, GETDATE(), 0, '新竹市東區光復路200號'),
(6, '狗狗社交日', '讓狗狗認識新朋友，增強社交能力', DATEADD(DAY, 14, GETDATE()), DATEADD(DAY, 14, GETDATE()), 0, 2, GETDATE(), 0, '新竹市東區光復路200號'),

(7, '水族設備體驗會', '介紹最新水族設備並提供試用', DATEADD(DAY, 11, GETDATE()), DATEADD(DAY, 11, GETDATE()), 0, 7, GETDATE(), 0, '台南市中西區民族路77號'),
(7, '海水魚飼養講座', '專業水族達人分享海水魚養殖技巧', DATEADD(DAY, 16, GETDATE()), DATEADD(DAY, 16, GETDATE()), 0, 7, GETDATE(), 0, '台南市中西區民族路77號'),

(8, '爬蟲飼養工作坊', '專業爬寵飼養知識分享', DATEADD(DAY, 13, GETDATE()), DATEADD(DAY, 13, GETDATE()), 1, 7, GETDATE(), 0, '台北市松山區南京東路100號'),
(8, '親子爬寵體驗日', '讓孩子親近爬蟲，培養對動物的興趣', DATEADD(DAY, 21, GETDATE()), DATEADD(DAY, 21, GETDATE()), 0, 2, GETDATE(), 0, '台北市松山區南京東路100號'),

(9, '寵物攝影日', '專業攝影師捕捉毛孩最美瞬間', DATEADD(DAY, 7, GETDATE()), DATEADD(DAY, 7, GETDATE()), 1, 6, GETDATE(), 0, '台中市南屯區五權西路300號'),
(9, '戶外攝影體驗', '帶毛孩到戶外拍攝自然美景', DATEADD(DAY, 19, GETDATE()), DATEADD(DAY, 19, GETDATE()), 1, 6, GETDATE(), 0, '台中市南屯區五權西路300號'),

(10, '手作寵物飾品課程', 'DIY 製作寵物配件，親手打造專屬小物', DATEADD(DAY, 9, GETDATE()), DATEADD(DAY, 9, GETDATE()), 1, 5, GETDATE(), 0, '彰化市中正路150號'),
(10, '寵物衣物縫紉班', '學習縫製寵物衣物，讓毛孩穿上獨特服裝', DATEADD(DAY, 20, GETDATE()), DATEADD(DAY, 20, GETDATE()), 1, 5, GETDATE(), 0, '彰化市中正路150號');

-- 插入活動圖片資料
/*
INSERT INTO vendor_activity_images (vendor_activity_id, [image])
VALUES
(1, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(1, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(2, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(2, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(3, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(3, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(4, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(4, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(5, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(5, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(6, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(6, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(7, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(7, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(8, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(8, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(9, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(9, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(10, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(10, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(11, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(11, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(12, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(12, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(13, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(13, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(14, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(14, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(15, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(15, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(16, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(16, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(17, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(17, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(18, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(18, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(19, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(19, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),

(20, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img)),
(20, (SELECT BulkColumn FROM OPENROWSET(BULK 'C:\Program Files\Microsoft SQL Server\image\p1.jpg', SINGLE_BLOB) AS img));
*/

-- 插入行事曆事件（包含顏色）
INSERT INTO calendar_event (vendor_id, event_title, start_time, end_time, vendor_activity_id, color, created_at, updated_at)
VALUES
(1, '毛孩美容日', DATEADD(DAY, 7, GETDATE()), DATEADD(DAY, 7, GETDATE()), 1, '#FF5733', GETDATE(), GETDATE()),
(2, 'SPA放鬆體驗', DATEADD(DAY, 15, GETDATE()), DATEADD(DAY, 15, GETDATE()), 2, '#33FF57', GETDATE(), GETDATE()),
(3, '新品試吃會', DATEADD(DAY, 10, GETDATE()), DATEADD(DAY, 10, GETDATE()), 3, '#3357FF', GETDATE(), GETDATE()),
(4, '寵物市集', DATEADD(DAY, 20, GETDATE()), DATEADD(DAY, 20, GETDATE()), 4, '#FF33A1', GETDATE(), GETDATE()),
(5, '健康檢查日', DATEADD(DAY, 5, GETDATE()), DATEADD(DAY, 5, GETDATE()), 5, '#FFFF33', GETDATE(), GETDATE()),
(6, '疫苗注射優惠', DATEADD(DAY, 12, GETDATE()), DATEADD(DAY, 12, GETDATE()), 6, '#33FFF0', GETDATE(), GETDATE()),
(7, '毛孩運動會', DATEADD(DAY, 8, GETDATE()), DATEADD(DAY, 8, GETDATE()), 7, '#FF6F33', GETDATE(), GETDATE()),
(8, '住宿體驗日', DATEADD(DAY, 18, GETDATE()), DATEADD(DAY, 18, GETDATE()), 8, '#33FFAB', GETDATE(), GETDATE()),
(9, '寵物下午茶派對', DATEADD(DAY, 9, GETDATE()), DATEADD(DAY, 9, GETDATE()), 9, '#FF33E1', GETDATE(), GETDATE()),
(10, '寵物聚餐趴', DATEADD(DAY, 17, GETDATE()), DATEADD(DAY, 17, GETDATE()), 10, '#33FF99', GETDATE(), GETDATE());
/*
(11, '狗狗行為訓練體驗', DATEADD(DAY, 6, GETDATE()), DATEADD(DAY, 6, GETDATE()), 11, '#FF5733', GETDATE(), GETDATE()),
(12, '狗狗社交日', DATEADD(DAY, 14, GETDATE()), DATEADD(DAY, 14, GETDATE()), 12, '#33FF73', GETDATE(), GETDATE()),
(13, '水族設備體驗會', DATEADD(DAY, 11, GETDATE()), DATEADD(DAY, 11, GETDATE()), 13, '#3333FF', GETDATE(), GETDATE()),
(14, '海水魚飼養講座', DATEADD(DAY, 16, GETDATE()), DATEADD(DAY, 16, GETDATE()), 14, '#FF33B7', GETDATE(), GETDATE()),
(15, '爬蟲飼養工作坊', DATEADD(DAY, 13, GETDATE()), DATEADD(DAY, 13, GETDATE()), 15, '#33FFB7', GETDATE(), GETDATE()),
(16, '親子爬寵體驗日', DATEADD(DAY, 21, GETDATE()), DATEADD(DAY, 21, GETDATE()), 16, '#FFCD33', GETDATE(), GETDATE()),
(17, '寵物攝影日', DATEADD(DAY, 7, GETDATE()), DATEADD(DAY, 7, GETDATE()), 17, '#FF8C33', GETDATE(), GETDATE()),
(18, '戶外攝影體驗', DATEADD(DAY, 19, GETDATE()), DATEADD(DAY, 19, GETDATE()), 18, '#33FFFC', GETDATE(), GETDATE()),
(19, '手作寵物飾品課程', DATEADD(DAY, 9, GETDATE()), DATEADD(DAY, 9, GETDATE()), 19, '#FF63FF', GETDATE(), GETDATE()),
(20, '寵物衣物縫紉班', DATEADD(DAY, 20, GETDATE()), DATEADD(DAY, 20, GETDATE()), 20, '#FF3366', GETDATE(), GETDATE());
*/
-- 插入提醒事件（每個店家 2 個）
INSERT INTO calendar_event (event_title, start_time, end_time, vendor_activity_id, created_at, updated_at)
VALUES
('活動報名截止提醒', '2025-03-05 10:00:00', '2025-03-05 10:00:00', NULL, '2025-03-05 09:00:00', '2025-03-05 09:00:00'),
('活動開始前通知', '2025-03-05 14:00:00', '2025-03-05 14:00:00', NULL, '2025-03-05 09:00:00', '2025-03-05 09:00:00'),

('活動報名截止提醒', '2025-03-06 10:00:00', '2025-03-06 10:00:00', NULL, '2025-03-06 09:00:00', '2025-03-06 09:00:00'),
('活動開始前通知', '2025-03-06 14:00:00', '2025-03-06 14:00:00', NULL, '2025-03-06 09:00:00', '2025-03-06 09:00:00'),

('活動報名截止提醒', '2025-03-07 10:00:00', '2025-03-07 10:00:00', NULL, '2025-03-07 09:00:00', '2025-03-07 09:00:00'),
('活動開始前通知', '2025-03-07 14:00:00', '2025-03-07 14:00:00', NULL, '2025-03-07 09:00:00', '2025-03-07 09:00:00'),

('活動報名截止提醒', '2025-03-08 10:00:00', '2025-03-08 10:00:00', NULL, '2025-03-08 09:00:00', '2025-03-08 09:00:00'),
('活動開始前通知', '2025-03-08 14:00:00', '2025-03-08 14:00:00', NULL, '2025-03-08 09:00:00', '2025-03-08 09:00:00'),

('活動報名截止提醒', '2025-03-09 10:00:00', '2025-03-09 10:00:00', NULL, '2025-03-09 09:00:00', '2025-03-09 09:00:00'),
('活動開始前通知', '2025-03-09 14:00:00', '2025-03-09 14:00:00', NULL, '2025-03-09 09:00:00', '2025-03-09 09:00:00'),

('活動報名截止提醒', '2025-03-10 10:00:00', '2025-03-10 10:00:00', NULL, '2025-03-10 09:00:00', '2025-03-10 09:00:00'),
('活動開始前通知', '2025-03-10 14:00:00', '2025-03-10 14:00:00', NULL, '2025-03-10 09:00:00', '2025-03-10 09:00:00'),

('活動報名截止提醒', '2025-03-11 10:00:00', '2025-03-11 10:00:00', NULL, '2025-03-11 09:00:00', '2025-03-11 09:00:00'),
('活動開始前通知', '2025-03-11 14:00:00', '2025-03-11 14:00:00', NULL, '2025-03-11 09:00:00', '2025-03-11 09:00:00'),

('活動報名截止提醒', '2025-03-12 10:00:00', '2025-03-12 10:00:00', NULL, '2025-03-12 09:00:00', '2025-03-12 09:00:00'),
('活動開始前通知', '2025-03-12 14:00:00', '2025-03-12 14:00:00', NULL, '2025-03-12 09:00:00', '2025-03-12 09:00:00'),

('活動報名截止提醒', '2025-03-13 10:00:00', '2025-03-13 10:00:00', NULL, '2025-03-13 09:00:00', '2025-03-13 09:00:00'),
('活動開始前通知', '2025-03-13 14:00:00', '2025-03-13 14:00:00', NULL, '2025-03-13 09:00:00', '2025-03-13 09:00:00');

-- 插入活動人數
INSERT INTO activity_people_number (vendor_activity_id, max_participants, current_participants) VALUES (1, 50, 20), (2, 30, 10), (3, 40, 25), (4, 60, 35), (5, 20, 10), (6, 25, 15), (7, 100, 50), (8, 80, 40), (9, 35, 18), (10, 45, 22), (11, 30, 12), (12, 50, 30), (13, 60, 33), (14, 40, 20), (15, 20, 8), (16, 25, 10), (17, 70, 45), (18, 55, 28), (19, 30, 15), (20, 40, 18);

INSERT INTO certification_tag (tag_name)
VALUES
('服務優質'),
('商品值得信賴'),
('顧客滿意'),
('環境整潔'),
('快速反應'),
('專業態度'),
('熱情友善'),
('值得推薦'),
('品質保證'),
('物超所值');

INSERT INTO vendor_certification (vendor_id, certification_status, reason, request_date, approved_date)
VALUES
(1, '申請中', '顧客高度滿意，獲得高評價', GETDATE(), NULL),
(2, '申請中', '環境乾淨整潔，顧客反應良好', GETDATE(), NULL),
(3, '已認證', '顧客評價高，醫療服務專業', GETDATE(), GETDATE()),
(4, '申請中', '顧客回饋滿意，服務專業', GETDATE(), NULL),
(5, '已認證', '優質服務，顧客反饋良好', GETDATE(), GETDATE()),
(6, '申請中', '專業訓練，顧客滿意', GETDATE(), NULL),
(7, '已認證', '顧客評價極高，環境整潔', GETDATE(), GETDATE()),
(8, '申請中', '提供多樣產品，顧客反應良好', GETDATE(), NULL),
(9, '已認證', '寵物攝影服務專業，顧客滿意', GETDATE(), GETDATE()),
(10, '申請中', '顧客滿意，產品設計獨特', GETDATE(), NULL);

INSERT INTO vendor_certification_tag (certification_id, tag_id, meets_standard)
VALUES
(1, 1, 1), -- '服務優質'
(1, 2, 0), -- '商品值得信賴'
(2, 3, 1), -- '顧客滿意'
(2, 4, 1), -- '環境整潔'
(3, 5, 1), -- '快速反應'
(3, 6, 1), -- '專業態度'
(4, 7, 1), -- '熱情友善'
(4, 8, 0), -- '值得推薦'
(5, 9, 1), -- '品質保證'
(5, 10, 0); -- '物超所值'

INSERT INTO notification (member_id, vendor_id, vendor_activity_id, notification_title, notification_content, is_read, sent_time)
VALUES
(11, 1, 1, '活動報名提醒', '您的活動報名即將截止，請儘快完成報名。', 0, GETDATE()),
(12, 2, 2, '活動開始通知', '您的活動將在1小時後開始，請準備好。', 0, GETDATE()),
(13, 3, 3, '活動報名提醒', '活動報名即將截止，請勿錯過報名時間。', 0, GETDATE()),
(14, 4, 4, '活動開始通知', '活動即將開始，請務必準時參加。', 0, GETDATE()),
(15, 5, 5, '活動報名提醒', '報名將於今日結束，請確保您的報名已經完成。', 0, GETDATE()),
(16, 6, 6, '活動開始通知', '活動開始前的提醒，請確保準時參加。', 0, GETDATE()),
(17, 7, 7, '活動報名提醒', '活動即將結束，請儘早報名參加。', 0, GETDATE()),
(18, 8, 8, '活動開始通知', '活動即將開始，記得準時出席！', 0, GETDATE()),
(19, 9, 9, '活動報名提醒', '報名結束前最後的機會，請儘早報名。', 0, GETDATE()),
(20, 10, 10, '活動開始通知', '您的活動即將開始，請不要錯過！', 0, GETDATE());

END;

--------------------- 店家評論、收藏、活動，活動收藏 ---------------------
BEGIN

-- 店家評論
INSERT INTO [dbo].[vendor_review]([vendor_id],[member_id],[review_time],[review_content],[rating_environment],[rating_price],[rating_service])
VALUES
(1, 11, '2025-01-10', '店內環境乾淨，沒有異味，寵物用品種類豐富。', 5, 4, 5),
(2, 12, '2025-01-11', '價格親民，幫狗狗洗澡的美容師很有耐心。', 4, 5, 5),
(3, 13, '2025-01-12', '員工對貓咪很友善，但環境稍微擁擠了些。', 3, 4, 4),
(4, 14, '2025-01-13', '寵物美容技術不錯，但等待時間有點長。', 4, 3, 3),
(5, 15, '2025-01-14', '店員推薦的貓砂很好用，價格合理，會再回購。', 5, 5, 4),
(6, 16, '2025-01-15', '狗狗美容完很可愛，但價格稍貴。', 4, 3, 5),
(7, 17, '2025-01-16', '有很多進口寵物食品，品質很好，值得推薦！', 5, 4, 5),
(8, 18, '2025-01-17', '店內的倉鼠籠選擇多，價格也算合理。', 4, 4, 4),
(9, 19, '2025-01-18', '這間店的貓咪玩具種類多，價格也不貴。', 4, 5, 5),
(10, 20, '2025-01-19', '工作人員專業，但服務態度可以再提升。', 3, 4, 3);

-- 店家收藏
INSERT INTO [dbo].[vendor_like]([member_id],[vendor_id])
VALUES
(11, 1),(12, 2),(13, 3),(14, 4),(15, 5),
(16, 6),(17, 7),(18, 8),(19, 9),(20, 10),
(11, 2),(12, 3),(13, 1),(14, 5),(15, 6),
(16, 7),(17, 4),(18, 9),(19, 10),(20, 8);

-- 活動收藏
INSERT INTO [dbo].[activity_like]([member_id],[vendor_activity_id])
VALUES
(11, 3),(12, 5),(13, 8),(14, 2),(15, 10),
(16, 7),(17, 1),(18, 12),(19, 15),(20, 9),
(11, 6),(12, 14),(13, 11),(14, 19),(15, 4),
(16, 16),(17, 20),(18, 17),(19, 13),(20, 18),
(11, 2),(12, 9),(13, 14),(14, 7),(15, 1),
(16, 12),(17, 5),(18, 3),(19, 6),(20, 8),
(11, 17),(12, 13),(13, 16),(14, 20),(15, 18),
(16, 11),(17, 15),(18, 4),(19, 10),(20, 19);

-- 活動評論
INSERT INTO [dbo].[vendor_activity_review] ([vendor_id], [member_id], [review_time], [review_content], [vendor_activity_id])
VALUES
(2, 12, '2024-07-15', '寵物活動非常有趣，狗狗玩得很開心！', 5),
(7, 18, '2024-09-22', '工作人員很友善，貓咪也感到很放鬆。', 12),
(4, 15, '2024-11-03', '活動場地很乾淨，適合帶寵物來玩。', 8),
(9, 11, '2024-05-19', '我的兔子第一次參加活動，表現得很活躍。', 3),
(1, 20, '2024-08-30', '活動內容豐富，寵物們都玩得很盡興。', 17),
(6, 14, '2024-12-10', '非常推薦這個活動，主人和寵物都能享受。', 9),
(3, 16, '2024-06-25', '活動安排得很周到，狗狗交到了新朋友。', 14),
(8, 13, '2024-10-05', '貓咪在活動中表現得很勇敢，主人也很開心。', 6),
(5, 19, '2024-04-12', '場地設施完善，寵物們都很安全。', 11),
(10, 17, '2024-03-28', '活動結束後，狗狗回家睡得特別香。', 2),
(2, 12, '2024-07-15', '工作人員對寵物很有耐心，活動體驗很棒。', 7),
(7, 18, '2024-09-22', '我的寵物第一次參加活動，表現得很興奮。', 18),
(4, 15, '2024-11-03', '活動中有很多互動環節，寵物們都很投入。', 4),
(9, 11, '2024-05-19', '場地很大，寵物可以自由奔跑，非常適合。', 13),
(1, 20, '2024-08-30', '活動中有專業的寵物訓練師指導，收穫很多。', 10),
(6, 14, '2024-12-10', '我的狗狗在活動中學會了新技能，非常感謝。', 19),
(3, 16, '2024-06-25', '活動氣氛很好，寵物們都玩得很開心。', 1),
(8, 13, '2024-10-05', '工作人員很細心，照顧到每隻寵物的需求。', 15),
(5, 19, '2024-04-12', '活動結束後，寵物們都依依不捨。', 20),
(10, 17, '2024-03-28', '這是我參加過最棒的寵物活動，強烈推薦！', 16),
(2, 12, '2024-07-15', '寵物活動非常有趣，狗狗玩得很開心！', 5),
(7, 18, '2024-09-22', '工作人員很友善，貓咪也感到很放鬆。', 12),
(4, 15, '2024-11-03', '活動場地很乾淨，適合帶寵物來玩。', 8),
(9, 11, '2024-05-19', '我的兔子第一次參加活動，表現得很活躍。', 3),
(1, 20, '2024-08-30', '活動內容豐富，寵物們都玩得很盡興。', 17),
(6, 14, '2024-12-10', '非常推薦這個活動，主人和寵物都能享受。', 9),
(3, 16, '2024-06-25', '活動安排得很周到，狗狗交到了新朋友。', 14),
(8, 13, '2024-10-05', '貓咪在活動中表現得很勇敢，主人也很開心。', 6),
(5, 19, '2024-04-12', '場地設施完善，寵物們都很安全。', 11),
(10, 17, '2024-03-28', '活動結束後，狗狗回家睡得特別香。', 2),
(2, 12, '2024-07-15', '工作人員對寵物很有耐心，活動體驗很棒。', 7),
(7, 18, '2024-09-22', '我的寵物第一次參加活動，表現得很興奮。', 18),
(4, 15, '2024-11-03', '活動中有很多互動環節，寵物們都很投入。', 4),
(9, 11, '2024-05-19', '場地很大，寵物可以自由奔跑，非常適合。', 13),
(1, 20, '2024-08-30', '活動中有專業的寵物訓練師指導，收穫很多。', 10),
(6, 14, '2024-12-10', '我的狗狗在活動中學會了新技能，非常感謝。', 19),
(3, 16, '2024-06-25', '活動氣氛很好，寵物們都玩得很開心。', 1),
(8, 13, '2024-10-05', '工作人員很細心，照顧到每隻寵物的需求。', 15),
(5, 19, '2024-04-12', '活動結束後，寵物們都依依不捨。', 20),
(10, 17, '2024-03-28', '這是我參加過最棒的寵物活動，強烈推薦！', 16);

END;

--------------------- 商品假資料 ---------------------
BEGIN

INSERT INTO [dbo].[product_category] ([name])
VALUES
	('食品保健'),
	('日常用品'),
	('服飾'),
	('玩具'),
	('其他')

INSERT INTO [dbo].[product_color] ([name])
VALUES
	('黑色'),
	('白色'),
	('紅色'),
	('橙色'),
	('黃色'),
	('綠色'),
	('藍色'),
	('紫色')

INSERT INTO [dbo].[product_size] ([name])
VALUES
	('S'),
	('M'),
	('L')

INSERT INTO [dbo].[product_detail]  
           ([product_category_id], [name], [description])  
VALUES  
	(1, '鮮味雞肉凍乾', '高蛋白、低脂肪的凍乾雞肉，適合作為獎勵零食。'),  
	(2, '無塵豆腐貓砂', '環保可沖馬桶，強力吸水凝結，持久除臭。'),  
	(3, '冬季加厚保暖寵物衣', '柔軟保暖，適合寒冷天氣，讓寵物舒適過冬。'),  
	(4, '貓咪逗趣自動旋轉球', '內建感應，隨機變換方向，刺激貓咪狩獵本能。'),  
	(5, '寵物飲水機濾芯組', '高效過濾雜質，確保寵物飲水清潔健康。'),
	(1, '營養貓糧', '含有豐富維生素與礦物質，滿足貓咪日常營養需求。'),
	(2, '抗菌濕巾', '有效抑制細菌，適合清潔寵物用品與身體。'),
	(3, '防水寵物雨衣', '輕便透氣，適合雨天使用。'),
	(4, '智能寵物玩具', '多模式互動，讓寵物保持活力。'),
	(5, '除臭寵物墊', '長效吸附異味，維持環境清新。'),
	(1, '高蛋白牛肉乾', '適合訓練獎勵，提供優質蛋白質。'),
	(2, '強力除臭噴霧', '快速分解異味，適用於寵物環境。'),
	(3, '夏季透氣寵物衣', '涼爽透氣，防止中暑。'),
	(4, '雷射逗貓棒', '互動遊戲，讓貓咪保持活力。'),
	(5, '活性炭濾芯', '高效過濾異味和雜質。'),
	(1, '鮭魚貓零食', '富含Omega-3，促進皮毛健康。'),
	(2, '天然木屑貓砂', '無塵低敏，環保可降解。'),
	(3, '防風防水寵物外套', '適合冬季和雨天使用。'),
	(4, '彈跳寵物球', '內藏零食設計，提高玩樂興趣。'),
	(5, '寵物智能餵食器', '定時餵食，保持良好飲食習慣。'),
	(1, '雞肉風味潔牙棒', '幫助清潔牙齒，預防口臭。'),
	(2, '超吸水寵物毛巾', '柔軟舒適，快速吸乾水分。'),
	(3, '夏季防蚊寵物衣', '有效防止蚊蟲叮咬。'),
	(4, '智慧逗貓器', '可自動運行，吸引貓咪注意。'),
	(5, '可拆洗寵物床', '舒適透氣，易於清潔。');

INSERT INTO [dbo].[product]  
           ([product_detail_id], [product_size_id], [product_color_id],  
            [stock_quantity], [unit_price], [discount_price], [status])  
VALUES
    (1, NULL, NULL, 100, 250, 220, 1),
    (2, NULL, NULL, 150, 320, NULL, 1),
    (3, 1, 1, 40, 500, NULL, 1),
    (3, 1, 3, 35, 500, 400, 1),
    (3, 1, 5, 30, 500, NULL, 1),
    (3, 1, 7, 40, 500, NULL, 1),
    (3, 1, 8, 35, 500, 400, 1),
    (3, 2, 2, 40, 500, NULL, 1),
    (3, 2, 3, 35, 500, 450, 1),
    (3, 2, 4, 30, 500, NULL, 1),
    (3, 2, 5, 40, 500, NULL, 1),
    (3, 3, 8, 35, 500, 480, 1),
    (4, NULL, 1, 20, 450, 420, 1),
    (4, NULL, 2, 30, 450, 420, 1),
    (4, NULL, 3, 40, 450, 420, 1),
    (4, NULL, 4, 50, 450, 420, 1),
    (4, NULL, 5, 60, 450, 420, 1),
    (5, NULL, NULL, 250, 150, NULL, 1),
    (6, 1, NULL, 50, 200, 150, 1),
    (6, 2, NULL, 30, 400, 320, 1),
    (6, 3, NULL, 20, 600, 500, 1),
    (7, NULL, NULL, 260, 250, 230, 1),
    (8, 1, 1, 50, 600, NULL, 1),
    (8, 2, 1, 45, 600, 550, 1),
    (8, 3, 1, 40, 600, NULL, 1),
    (8, 1, 2, 48, 600, NULL, 1),
    (8, 2, 3, 40, 600, 570, 1),
    (8, 3, 3, 38, 600, NULL, 1),
    (9, NULL, NULL, 130, 750, NULL, 1),
    (10, NULL, NULL, 190, 500, NULL, 1),
    (11, NULL, NULL, 140, 300, 280, 1),
    (12, NULL, NULL, 230, 450, 400, 1),
    (13, 1, 3, 35, 550, NULL, 1),
    (13, 2, 3, 30, 550, 500, 1),
    (13, 3, 3, 25, 550, NULL, 1),
    (13, 1, 4, 33, 550, NULL, 1),
    (13, 2, 4, 30, 550, 520, 1),
    (13, 3, 4, 28, 550, NULL, 1),
    (14, NULL, NULL, 170, 420, 400, 1),
    (15, NULL, NULL, 220, 280, 260, 1),
    (16, NULL, NULL, 150, 360, 330, 1),
    (17, NULL, NULL, 180, 280, 260, 1),
    (18, 1, 5, 38, 650, NULL, 1),
    (18, 2, 5, 30, 650, 600, 1),
    (18, 3, 5, 28, 650, NULL, 1),
    (18, 1, 6, 35, 650, NULL, 1),
    (18, 2, 6, 30, 650, 620, 1),
    (18, 3, 6, 28, 650, NULL, 1),
    (19, NULL, NULL, 175, 800, 750, 1),
    (20, NULL, NULL, 190, 520, 480, 1),
    (21, NULL, NULL, 220, 290, 270, 1),
    (22, NULL, NULL, 210, 500, 470, 1),
    (23, 1, 7, 40, 570, NULL, 1),
    (23, 2, 7, 35, 570, 520, 1),
    (23, 3, 7, 30, 570, NULL, 1),
    (23, 1, 8, 38, 570, NULL, 1),
    (23, 2, 8, 35, 570, 540, 1),
    (23, 3, 8, 30, 570, NULL, 1),
    (24, NULL, NULL, 180, 450, 420, 1),
    (25, NULL, NULL, 200, 600, 550, 1);


/*
UPDATE [dbo].[product]  
SET photo = (SELECT BulkColumn FROM Openrowset(Bulk 'C:\petTopia\商品圖片\01.jpg', Single_Blob) AS pic)  
WHERE id BETWEEN 1 AND 100;
*/



END;

--------------------- 商品假資料 ---------------------
BEGIN

--優惠券
INSERT INTO coupons ([name], discount_type, discount_value, min_order_value, limit_count, valid_start, valid_end, [status]) VALUES
('新會員限定優惠50元', 0, 50.00, 200.00, 1, '2025-01-01', '2025-12-31', 1),  -- 固定50元折扣
('周年慶九折爽爽送', 1, 0.10, 100.00, 5, '2025-01-01', '2025-12-31', 1),  -- 10%折扣
('中秋節100元大放送', 0, 100.00, 300.00, 2, '2025-02-01', '2025-06-30', 0);   -- 固定100元折扣

-- 插入訂單狀態
INSERT INTO order_status ([name]) VALUES
('處理中'),
('待出貨'),
('配送中'),
('待收貨'),
('已完成'),
('已取消');

-- 插入付款狀態
INSERT INTO payment_status ([name]) VALUES
('待付款'),
('已付款'),
('付款失敗');

-- 插入運送方式
INSERT INTO shipping_category ([name], shipping_cost, shipping_day) VALUES
('宅配', 50.00, 7),
('快遞', 80.00, 3);

-- 插入付款方式
INSERT INTO payment_category ([name]) VALUES
('信用卡付款'),
('貨到付款');

INSERT INTO member_coupon (member_id, coupons_id, usage_count, status)
VALUES 
    (11, 1, 0, 1),
    (11, 2, 0, 1),
    (11, 3, 0, 1),
    (12, 1, 0, 1),
    (12, 2, 0, 1),
    (12, 3, 0, 1),
    (13, 1, 0, 1),
    (13, 2, 0, 1),
    (13, 3, 0, 1);

-- 插入購物車
INSERT INTO cart (member_id, product_id, quantity) VALUES
(11, 10, 2),
(11, 7, 3),
(12, 7, 1),
(12, 12, 2),
(13, 1, 1),
(13, 7, 2);


END;

