BEGIN
USE [petTopia]
END;

--------------------- �|������� ---------------------
--------------------- �Ӯa�B�|���B���a ---------------------
BEGIN 

-- ���J 10 ���Ӯa�Τ���
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

-- ���J 10 ���|���Τ���
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

-- ���J 10 ���|����� (���p users ��)
INSERT INTO member (id, name, phone, birthdate, gender, address,status) VALUES
(11, '������', '0912345678', '1990-01-01', 1, '�x�_�������Ϥ��R��1��',1),
(12, '���p��', '0923456789', '1991-02-02', 0, '�s�_���O���Ϥ�Ƹ�2��',1),
(13, '�L�j��', '0934567890', '1992-03-03', 0, '�x������ٰϥx�W�j�D3�q3��',1),
(14, '�i�Ӱ�', '0955678901', '1993-04-04', 0, '�x�n������Ϧ��\��4��',1),
(15, '�\�p��', '0966789012', '1994-05-05', 1, '�������d���ϤT�h��5��',1),
(16, '���Ӻ�', '0977890123', '1995-06-06', 0, '��饫���c�Ϥ��Ƹ�6��',1),
(17, '���μz', '0988901234', '1996-07-07', 1, '�s�˥��F�ϥ��_��7��',1),
(18, '�B�ػ�', '0910012345', '1997-08-08', 0, '���ƿ����L�����s��8��',1),
(19, '���p��', '0921123456', '1998-09-09', 1, '�Ÿq����Ϥ�����9��',1),
(20, 'Ĭ�Ӱ�', '0932234567', '1999-10-10', 0, '�򶩥����R�ϫH�q��10��',1);

-- ���a����
INSERT INTO vendor_category ([name]) VALUES
('�d�����e'),
('�d���Ϋ~��'),
('�d����|'),
('�d���H�J'),
('�d���\�U'),
('�d���V�m'),
('���ڥΫ~'),
('�������M����'),
('�d����v'),
('�d����@�u�{'),
('��L');

-- ���J���a�]10 �a�d���͵����^
INSERT INTO vendor (id, [name], [description], logo_img, [address], phone, contact_email, contact_person, taxid_number, [status], vendor_category_id, registration_date, updated_date, event_count, total_rating, review_count, vendor_level)
VALUES
(1, '��ĤѰ��d�����e', '�M�~�d�����e�PSPA�A����ľ֦��̵ξA������', NULL, '�x�_���j�w�ϫH�q�����q100��', '02-1234-5678', 'contact1@example.com', '�i�p�j', '12345678', 1, 1, GETDATE(), GETDATE(), 0, 0, 0, '���q'),
(2, '�L�p��~�d���Ϋ~', '���ѦU���d�����~�P�Ϋ~�A������ĻݨD', NULL, '�x������ٰϥx�W�j�D�T�q200��', '04-8765-4321', 'contact2@example.com', '������', '23456789', 1, 2, GETDATE(), GETDATE(), 0, 0, 0, '���q'),
(3, '�w�߰ʪ���|', '�M�~�~��ζ��A���ѳ̦w�ߪ������A��', NULL, '�s�_���O���Ϥ��s���@�q300��', '02-5566-7788', 'contact3@example.com', '����v', '34567890', 1, 3, GETDATE(), GETDATE(), 0, 0, 0, '���q'),
(4, '��ļֶ��d���H�J', '�M�~���U�A����Ĥ@�ӵξA���a', NULL, '�������d���Ϧ��\�@��50��', '07-3344-5566', 'contact4@example.com', '�L�p�j', '45678901', 1, 4, GETDATE(), GETDATE(), 0, 0, 0, '���q'),
(5, '�d���@���]�p�p�L�L', '�ɨ������P��Ħ@�׬��n�ɥ�', NULL, '��饫���c�Ϥ��ظ�88��', '03-5566-7788', 'contact5@example.com', '������', '56789012', 1, 5, GETDATE(), GETDATE(), 0, 0, 0, '���q'),
(6, '�����V�m�ǰ|', '�M�~�����V�m�ҵ{�A���R���ܦ�ť�ܨ��_�_', NULL, '�s�˥��F�ϥ��_��200��', '03-3344-5566', 'contact6@example.com', '���нm', '67890123', 1, 6, GETDATE(), GETDATE(), 0, 0, 0, '���q'),
(7, '���v�ֽ�����]', '�M���[�೽�B�������P���ڳ]��', NULL, '�x�n������ϥ��ڸ�77��', '06-7788-5566', 'contact7@example.com', '������', '78901234', 1, 7, GETDATE(), GETDATE(), 0, 0, 0, '���q'),
(8, '���d�@��', '�M�~�}�i�������A���Ѱ��~��}�i���һP�Ϋ~', NULL, '�x�_���Q�s�ϫn�ʪF��100��', '02-8899-6677', 'contact8@example.com', '�d����', '89012345', 1, 8, GETDATE(), GETDATE(), 0, 0, 0, '���q'),
(9, '��p����v�]', '�M���d�����y���R�^�Ъ���v��', NULL, '�x�����n�ٰϤ��v���300��', '04-4455-6677', 'contact9@example.com', '�P�p�j', '90123456', 1, 9, GETDATE(), GETDATE(), 0, 0, 0, '���q'),
(10, '��@�d���p��', '��u�s�@�d���窫�P�t��A�W�@�L�G���]�p', NULL, '���ƥ�������150��', '04-7788-5566', 'contact10@example.com', '���p�j', '01234567', 1, 10, GETDATE(), GETDATE(), 0, 0, 0, '���q');


END;

--------------------- ���a����� ---------------------
--------------------- ���a ---------------------
BEGIN

-- ��s���a logo_img �Ϥ�
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
-- ���J�Ҧ����a���Ϥ��]�����ϥ� p1.jpg�^
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
-- ���J��������
INSERT INTO activity_type ([name]) VALUES
('�d������'),
('�B�ʷ|'),
('�U�ȯ�'),
('�E�\'),
('DIY����'),
('�d����v��'),
('��L');

-- ���J���a����
INSERT INTO vendor_activity (vendor_id, [name], [description], start_time, end_time, is_registration_required, activity_type_id, registration_date, number_visitor, [address])
VALUES
(1, '��Ĭ��e��', '���ѧK�O�d�����e����A����ķصM�@�s', DATEADD(DAY, 7, GETDATE()), DATEADD(DAY, 7, GETDATE()), 1, 1, GETDATE(), 0, '�x�_���j�w�ϫH�q�����q100��'),
(1, 'SPA���P����', '�M�~�d�� SPA�A�νw���O�P�J�{', DATEADD(DAY, 15, GETDATE()), DATEADD(DAY, 15, GETDATE()), 1, 3, GETDATE(), 0, '�x�_���j�w�ϫH�q�����q100��'),

(2, '�s�~�զY�|', '���ѷs���d�����~�զY�A����ħ��̷R���f��', DATEADD(DAY, 10, GETDATE()), DATEADD(DAY, 10, GETDATE()), 0, 4, GETDATE(), 0, '�x������ٰϥx�W�j�D�T�q200��'),
(2, '�d������', '�U���d���Ϋ~�P��@�ӫ~�i��', DATEADD(DAY, 20, GETDATE()), DATEADD(DAY, 20, GETDATE()), 0, 1, GETDATE(), 0, '�x������ٰϥx�W�j�D�T�q200��'),

(3, '���d�ˬd��', '�K�O�d�����d�ˬd�A���ѱM�~��ĳ', DATEADD(DAY, 5, GETDATE()), DATEADD(DAY, 5, GETDATE()), 1, 7, GETDATE(), 0, '�s�_���O���Ϥ��s���@�q300��'),
(3, '�̭]�`�g�u�f', '�S�w�̭]�I�����u�f��', DATEADD(DAY, 12, GETDATE()), DATEADD(DAY, 12, GETDATE()), 1, 7, GETDATE(), 0, '�s�_���O���Ϥ��s���@�q300��'),

(4, '��ĹB�ʷ|', '�U���d�����ɻP�C���A�D�Ԥ����෥��', DATEADD(DAY, 8, GETDATE()), DATEADD(DAY, 8, GETDATE()), 0, 2, GETDATE(), 0, '�������d���Ϧ��\�@��50��'),
(4, '��J�����', '�K�O����@���d���H�J�A��', DATEADD(DAY, 18, GETDATE()), DATEADD(DAY, 18, GETDATE()), 1, 7, GETDATE(), 0, '�������d���Ϧ��\�@��50��'),

(5, '�d���U�ȯ�����', '�P��Ĥ@�_�ɨ��U�ȯ��ɥ�', DATEADD(DAY, 9, GETDATE()), DATEADD(DAY, 9, GETDATE()), 0, 3, GETDATE(), 0, '��饫���c�Ϥ��ظ�88��'),
(5, '�d���E�\�w', '�@�_�P�d���B�̦ͭ@�ɱ��\', DATEADD(DAY, 17, GETDATE()), DATEADD(DAY, 17, GETDATE()), 0, 4, GETDATE(), 0, '��饫���c�Ϥ��ظ�88��'),

(6, '�����欰�V�m����', '�����¦�����欰�V�m�ҵ{', DATEADD(DAY, 6, GETDATE()), DATEADD(DAY, 6, GETDATE()), 1, 7, GETDATE(), 0, '�s�˥��F�ϥ��_��200��'),
(6, '���������', '�������{�ѷs�B�͡A�W�j�����O', DATEADD(DAY, 14, GETDATE()), DATEADD(DAY, 14, GETDATE()), 0, 2, GETDATE(), 0, '�s�˥��F�ϥ��_��200��'),

(7, '���ڳ]������|', '���г̷s���ڳ]�ƨô��Ѹե�', DATEADD(DAY, 11, GETDATE()), DATEADD(DAY, 11, GETDATE()), 0, 7, GETDATE(), 0, '�x�n������ϥ��ڸ�77��'),
(7, '�������}�i���y', '�M�~���ڹF�H���ɮ������i�ާޥ�', DATEADD(DAY, 16, GETDATE()), DATEADD(DAY, 16, GETDATE()), 0, 7, GETDATE(), 0, '�x�n������ϥ��ڸ�77��'),

(8, '���ι}�i�u�@�{', '�M�~���d�}�i���Ѥ���', DATEADD(DAY, 13, GETDATE()), DATEADD(DAY, 13, GETDATE()), 1, 7, GETDATE(), 0, '�x�_���Q�s�ϫn�ʪF��100��'),
(8, '�ˤl���d�����', '���Ĥl�˪��ΡA���i��ʪ�������', DATEADD(DAY, 21, GETDATE()), DATEADD(DAY, 21, GETDATE()), 0, 2, GETDATE(), 0, '�x�_���Q�s�ϫn�ʪF��100��'),

(9, '�d����v��', '�M�~��v�v������ĳ̬�����', DATEADD(DAY, 7, GETDATE()), DATEADD(DAY, 7, GETDATE()), 1, 6, GETDATE(), 0, '�x�����n�ٰϤ��v���300��'),
(9, '��~��v����', '�a��Ĩ��~����۵M����', DATEADD(DAY, 19, GETDATE()), DATEADD(DAY, 19, GETDATE()), 1, 6, GETDATE(), 0, '�x�����n�ٰϤ��v���300��'),

(10, '��@�d�����~�ҵ{', 'DIY �s�@�d���t��A�ˤ⥴�y�M�ݤp��', DATEADD(DAY, 9, GETDATE()), DATEADD(DAY, 9, GETDATE()), 1, 5, GETDATE(), 0, '���ƥ�������150��'),
(10, '�d���窫�_���Z', '�ǲ��_�s�d���窫�A����Ĭ�W�W�S�A��', DATEADD(DAY, 20, GETDATE()), DATEADD(DAY, 20, GETDATE()), 1, 5, GETDATE(), 0, '���ƥ�������150��');

-- ���J���ʹϤ����
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

-- ���J��ƾ�ƥ�]�]�t�C��^
INSERT INTO calendar_event (vendor_id, event_title, start_time, end_time, vendor_activity_id, color, created_at, updated_at)
VALUES
(1, '��Ĭ��e��', DATEADD(DAY, 7, GETDATE()), DATEADD(DAY, 7, GETDATE()), 1, '#FF5733', GETDATE(), GETDATE()),
(2, 'SPA���P����', DATEADD(DAY, 15, GETDATE()), DATEADD(DAY, 15, GETDATE()), 2, '#33FF57', GETDATE(), GETDATE()),
(3, '�s�~�զY�|', DATEADD(DAY, 10, GETDATE()), DATEADD(DAY, 10, GETDATE()), 3, '#3357FF', GETDATE(), GETDATE()),
(4, '�d������', DATEADD(DAY, 20, GETDATE()), DATEADD(DAY, 20, GETDATE()), 4, '#FF33A1', GETDATE(), GETDATE()),
(5, '���d�ˬd��', DATEADD(DAY, 5, GETDATE()), DATEADD(DAY, 5, GETDATE()), 5, '#FFFF33', GETDATE(), GETDATE()),
(6, '�̭]�`�g�u�f', DATEADD(DAY, 12, GETDATE()), DATEADD(DAY, 12, GETDATE()), 6, '#33FFF0', GETDATE(), GETDATE()),
(7, '��ĹB�ʷ|', DATEADD(DAY, 8, GETDATE()), DATEADD(DAY, 8, GETDATE()), 7, '#FF6F33', GETDATE(), GETDATE()),
(8, '��J�����', DATEADD(DAY, 18, GETDATE()), DATEADD(DAY, 18, GETDATE()), 8, '#33FFAB', GETDATE(), GETDATE()),
(9, '�d���U�ȯ�����', DATEADD(DAY, 9, GETDATE()), DATEADD(DAY, 9, GETDATE()), 9, '#FF33E1', GETDATE(), GETDATE()),
(10, '�d���E�\�w', DATEADD(DAY, 17, GETDATE()), DATEADD(DAY, 17, GETDATE()), 10, '#33FF99', GETDATE(), GETDATE());
/*
(11, '�����欰�V�m����', DATEADD(DAY, 6, GETDATE()), DATEADD(DAY, 6, GETDATE()), 11, '#FF5733', GETDATE(), GETDATE()),
(12, '���������', DATEADD(DAY, 14, GETDATE()), DATEADD(DAY, 14, GETDATE()), 12, '#33FF73', GETDATE(), GETDATE()),
(13, '���ڳ]������|', DATEADD(DAY, 11, GETDATE()), DATEADD(DAY, 11, GETDATE()), 13, '#3333FF', GETDATE(), GETDATE()),
(14, '�������}�i���y', DATEADD(DAY, 16, GETDATE()), DATEADD(DAY, 16, GETDATE()), 14, '#FF33B7', GETDATE(), GETDATE()),
(15, '���ι}�i�u�@�{', DATEADD(DAY, 13, GETDATE()), DATEADD(DAY, 13, GETDATE()), 15, '#33FFB7', GETDATE(), GETDATE()),
(16, '�ˤl���d�����', DATEADD(DAY, 21, GETDATE()), DATEADD(DAY, 21, GETDATE()), 16, '#FFCD33', GETDATE(), GETDATE()),
(17, '�d����v��', DATEADD(DAY, 7, GETDATE()), DATEADD(DAY, 7, GETDATE()), 17, '#FF8C33', GETDATE(), GETDATE()),
(18, '��~��v����', DATEADD(DAY, 19, GETDATE()), DATEADD(DAY, 19, GETDATE()), 18, '#33FFFC', GETDATE(), GETDATE()),
(19, '��@�d�����~�ҵ{', DATEADD(DAY, 9, GETDATE()), DATEADD(DAY, 9, GETDATE()), 19, '#FF63FF', GETDATE(), GETDATE()),
(20, '�d���窫�_���Z', DATEADD(DAY, 20, GETDATE()), DATEADD(DAY, 20, GETDATE()), 20, '#FF3366', GETDATE(), GETDATE());
*/
-- ���J�����ƥ�]�C�ө��a 2 �ӡ^
INSERT INTO calendar_event (event_title, start_time, end_time, vendor_activity_id, created_at, updated_at)
VALUES
('���ʳ��W�I���', '2025-03-05 10:00:00', '2025-03-05 10:00:00', NULL, '2025-03-05 09:00:00', '2025-03-05 09:00:00'),
('���ʶ}�l�e�q��', '2025-03-05 14:00:00', '2025-03-05 14:00:00', NULL, '2025-03-05 09:00:00', '2025-03-05 09:00:00'),

('���ʳ��W�I���', '2025-03-06 10:00:00', '2025-03-06 10:00:00', NULL, '2025-03-06 09:00:00', '2025-03-06 09:00:00'),
('���ʶ}�l�e�q��', '2025-03-06 14:00:00', '2025-03-06 14:00:00', NULL, '2025-03-06 09:00:00', '2025-03-06 09:00:00'),

('���ʳ��W�I���', '2025-03-07 10:00:00', '2025-03-07 10:00:00', NULL, '2025-03-07 09:00:00', '2025-03-07 09:00:00'),
('���ʶ}�l�e�q��', '2025-03-07 14:00:00', '2025-03-07 14:00:00', NULL, '2025-03-07 09:00:00', '2025-03-07 09:00:00'),

('���ʳ��W�I���', '2025-03-08 10:00:00', '2025-03-08 10:00:00', NULL, '2025-03-08 09:00:00', '2025-03-08 09:00:00'),
('���ʶ}�l�e�q��', '2025-03-08 14:00:00', '2025-03-08 14:00:00', NULL, '2025-03-08 09:00:00', '2025-03-08 09:00:00'),

('���ʳ��W�I���', '2025-03-09 10:00:00', '2025-03-09 10:00:00', NULL, '2025-03-09 09:00:00', '2025-03-09 09:00:00'),
('���ʶ}�l�e�q��', '2025-03-09 14:00:00', '2025-03-09 14:00:00', NULL, '2025-03-09 09:00:00', '2025-03-09 09:00:00'),

('���ʳ��W�I���', '2025-03-10 10:00:00', '2025-03-10 10:00:00', NULL, '2025-03-10 09:00:00', '2025-03-10 09:00:00'),
('���ʶ}�l�e�q��', '2025-03-10 14:00:00', '2025-03-10 14:00:00', NULL, '2025-03-10 09:00:00', '2025-03-10 09:00:00'),

('���ʳ��W�I���', '2025-03-11 10:00:00', '2025-03-11 10:00:00', NULL, '2025-03-11 09:00:00', '2025-03-11 09:00:00'),
('���ʶ}�l�e�q��', '2025-03-11 14:00:00', '2025-03-11 14:00:00', NULL, '2025-03-11 09:00:00', '2025-03-11 09:00:00'),

('���ʳ��W�I���', '2025-03-12 10:00:00', '2025-03-12 10:00:00', NULL, '2025-03-12 09:00:00', '2025-03-12 09:00:00'),
('���ʶ}�l�e�q��', '2025-03-12 14:00:00', '2025-03-12 14:00:00', NULL, '2025-03-12 09:00:00', '2025-03-12 09:00:00'),

('���ʳ��W�I���', '2025-03-13 10:00:00', '2025-03-13 10:00:00', NULL, '2025-03-13 09:00:00', '2025-03-13 09:00:00'),
('���ʶ}�l�e�q��', '2025-03-13 14:00:00', '2025-03-13 14:00:00', NULL, '2025-03-13 09:00:00', '2025-03-13 09:00:00');

-- ���J���ʤH��
INSERT INTO activity_people_number (vendor_activity_id, max_participants, current_participants) VALUES (1, 50, 20), (2, 30, 10), (3, 40, 25), (4, 60, 35), (5, 20, 10), (6, 25, 15), (7, 100, 50), (8, 80, 40), (9, 35, 18), (10, 45, 22), (11, 30, 12), (12, 50, 30), (13, 60, 33), (14, 40, 20), (15, 20, 8), (16, 25, 10), (17, 70, 45), (18, 55, 28), (19, 30, 15), (20, 40, 18);

INSERT INTO certification_tag (tag_name)
VALUES
('�A���u��'),
('�ӫ~�ȱo�H��'),
('�U�Ⱥ��N'),
('���Ҿ��'),
('�ֳt����'),
('�M�~�A��'),
('�����͵�'),
('�ȱo����'),
('�~��O��'),
('���W�ҭ�');

INSERT INTO vendor_certification (vendor_id, certification_status, reason, request_date, approved_date)
VALUES
(1, '�ӽФ�', '�U�Ȱ��׺��N�A��o������', GETDATE(), NULL),
(2, '�ӽФ�', '���Ұ��b���A�U�Ȥ����}�n', GETDATE(), NULL),
(3, '�w�{��', '�U�ȵ������A�����A�ȱM�~', GETDATE(), GETDATE()),
(4, '�ӽФ�', '�U�Ȧ^�X���N�A�A�ȱM�~', GETDATE(), NULL),
(5, '�w�{��', '�u��A�ȡA�U�Ȥ��X�}�n', GETDATE(), GETDATE()),
(6, '�ӽФ�', '�M�~�V�m�A�U�Ⱥ��N', GETDATE(), NULL),
(7, '�w�{��', '�U�ȵ��������A���Ҿ��', GETDATE(), GETDATE()),
(8, '�ӽФ�', '���Ѧh�˲��~�A�U�Ȥ����}�n', GETDATE(), NULL),
(9, '�w�{��', '�d����v�A�ȱM�~�A�U�Ⱥ��N', GETDATE(), GETDATE()),
(10, '�ӽФ�', '�U�Ⱥ��N�A���~�]�p�W�S', GETDATE(), NULL);

INSERT INTO vendor_certification_tag (certification_id, tag_id, meets_standard)
VALUES
(1, 1, 1), -- '�A���u��'
(1, 2, 0), -- '�ӫ~�ȱo�H��'
(2, 3, 1), -- '�U�Ⱥ��N'
(2, 4, 1), -- '���Ҿ��'
(3, 5, 1), -- '�ֳt����'
(3, 6, 1), -- '�M�~�A��'
(4, 7, 1), -- '�����͵�'
(4, 8, 0), -- '�ȱo����'
(5, 9, 1), -- '�~��O��'
(5, 10, 0); -- '���W�ҭ�'

INSERT INTO notification (member_id, vendor_id, vendor_activity_id, notification_title, notification_content, is_read, sent_time)
VALUES
(11, 1, 1, '���ʳ��W����', '�z�����ʳ��W�Y�N�I��A�о��֧������W�C', 0, GETDATE()),
(12, 2, 2, '���ʶ}�l�q��', '�z�����ʱN�b1�p�ɫ�}�l�A�зǳƦn�C', 0, GETDATE()),
(13, 3, 3, '���ʳ��W����', '���ʳ��W�Y�N�I��A�Фſ��L���W�ɶ��C', 0, GETDATE()),
(14, 4, 4, '���ʶ}�l�q��', '���ʧY�N�}�l�A�аȥ��Ǯɰѥ[�C', 0, GETDATE()),
(15, 5, 5, '���ʳ��W����', '���W�N�󤵤鵲���A�нT�O�z�����W�w�g�����C', 0, GETDATE()),
(16, 6, 6, '���ʶ}�l�q��', '���ʶ}�l�e�������A�нT�O�Ǯɰѥ[�C', 0, GETDATE()),
(17, 7, 7, '���ʳ��W����', '���ʧY�N�����A�о������W�ѥ[�C', 0, GETDATE()),
(18, 8, 8, '���ʶ}�l�q��', '���ʧY�N�}�l�A�O�o�ǮɥX�u�I', 0, GETDATE()),
(19, 9, 9, '���ʳ��W����', '���W�����e�̫᪺���|�A�о������W�C', 0, GETDATE()),
(20, 10, 10, '���ʶ}�l�q��', '�z�����ʧY�N�}�l�A�Ф��n���L�I', 0, GETDATE());

END;

--------------------- ���a���סB���áB���ʡA���ʦ��� ---------------------
BEGIN

-- ���a����
INSERT INTO [dbo].[vendor_review]([vendor_id],[member_id],[review_time],[review_content],[rating_environment],[rating_price],[rating_service])
VALUES
(1, 11, '2025-01-10', '�������Ұ��b�A�S�������A�d���Ϋ~�����״I�C', 5, 4, 5),
(2, 12, '2025-01-11', '����˥��A�������~�������e�v�ܦ��@�ߡC', 4, 5, 5),
(3, 13, '2025-01-12', '���u��߫}�ܤ͵��A�����ҵy�L�����F�ǡC', 3, 4, 4),
(4, 14, '2025-01-13', '�d�����e�޳N�����A�����ݮɶ����I���C', 4, 3, 3),
(5, 15, '2025-01-14', '�������˪��߬�ܦn�ΡA����X�z�A�|�A�^�ʡC', 5, 5, 4),
(6, 16, '2025-01-15', '�������e���ܥi�R�A������y�Q�C', 4, 3, 5),
(7, 17, '2025-01-16', '���ܦh�i�f�d�����~�A�~��ܦn�A�ȱo���ˡI', 5, 4, 5),
(8, 18, '2025-01-17', '�������ܹ�Ţ��ܦh�A����]��X�z�C', 4, 4, 4),
(9, 19, '2025-01-18', '�o�������߫}��������h�A����]���Q�C', 4, 5, 5),
(10, 20, '2025-01-19', '�u�@�H���M�~�A���A�ȺA�ץi�H�A���ɡC', 3, 4, 3);

-- ���a����
INSERT INTO [dbo].[vendor_like]([member_id],[vendor_id])
VALUES
(11, 1),(12, 2),(13, 3),(14, 4),(15, 5),
(16, 6),(17, 7),(18, 8),(19, 9),(20, 10),
(11, 2),(12, 3),(13, 1),(14, 5),(15, 6),
(16, 7),(17, 4),(18, 9),(19, 10),(20, 8);

-- ���ʦ���
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

-- ���ʵ���
INSERT INTO [dbo].[vendor_activity_review] ([vendor_id], [member_id], [review_time], [review_content], [vendor_activity_id])
VALUES
(2, 12, '2024-07-15', '�d�����ʫD�`����A�������o�ܶ}�ߡI', 5),
(7, 18, '2024-09-22', '�u�@�H���ܤ͵��A�߫}�]�P��ܩ��P�C', 12),
(4, 15, '2024-11-03', '���ʳ��a�ܰ��b�A�A�X�a�d���Ӫ��C', 8),
(9, 11, '2024-05-19', '�ڪ��ߤl�Ĥ@���ѥ[���ʡA��{�o�ܬ��D�C', 3),
(1, 20, '2024-08-30', '���ʤ��e�״I�A�d���̳����o�ܺɿ��C', 17),
(6, 14, '2024-12-10', '�D�`���˳o�Ӭ��ʡA�D�H�M�d������ɨ��C', 9),
(3, 16, '2024-06-25', '���ʦw�Ʊo�ܩP��A�������F�s�B�͡C', 14),
(8, 13, '2024-10-05', '�߫}�b���ʤ���{�o�ܫi���A�D�H�]�ܶ}�ߡC', 6),
(5, 19, '2024-04-12', '���a�]�I�����A�d���̳��ܦw���C', 11),
(10, 17, '2024-03-28', '���ʵ�����A�����^�a�αo�S�O���C', 2),
(2, 12, '2024-07-15', '�u�@�H�����d���ܦ��@�ߡA��������ܴΡC', 7),
(7, 18, '2024-09-22', '�ڪ��d���Ĥ@���ѥ[���ʡA��{�o�ܿ��ġC', 18),
(4, 15, '2024-11-03', '���ʤ����ܦh�������`�A�d���̳��ܧ�J�C', 4),
(9, 11, '2024-05-19', '���a�ܤj�A�d���i�H�ۥѩb�]�A�D�`�A�X�C', 13),
(1, 20, '2024-08-30', '���ʤ����M�~���d���V�m�v���ɡA��ì�ܦh�C', 10),
(6, 14, '2024-12-10', '�ڪ������b���ʤ��Ƿ|�F�s�ޯ�A�D�`�P�¡C', 19),
(3, 16, '2024-06-25', '���ʮ�^�ܦn�A�d���̳����o�ܶ}�ߡC', 1),
(8, 13, '2024-10-05', '�u�@�H���ܲӤߡA���U��C���d�����ݨD�C', 15),
(5, 19, '2024-04-12', '���ʵ�����A�d���̳��̨̤��ˡC', 20),
(10, 17, '2024-03-28', '�o�O�ڰѥ[�L�̴Ϊ��d�����ʡA�j�P���ˡI', 16),
(2, 12, '2024-07-15', '�d�����ʫD�`����A�������o�ܶ}�ߡI', 5),
(7, 18, '2024-09-22', '�u�@�H���ܤ͵��A�߫}�]�P��ܩ��P�C', 12),
(4, 15, '2024-11-03', '���ʳ��a�ܰ��b�A�A�X�a�d���Ӫ��C', 8),
(9, 11, '2024-05-19', '�ڪ��ߤl�Ĥ@���ѥ[���ʡA��{�o�ܬ��D�C', 3),
(1, 20, '2024-08-30', '���ʤ��e�״I�A�d���̳����o�ܺɿ��C', 17),
(6, 14, '2024-12-10', '�D�`���˳o�Ӭ��ʡA�D�H�M�d������ɨ��C', 9),
(3, 16, '2024-06-25', '���ʦw�Ʊo�ܩP��A�������F�s�B�͡C', 14),
(8, 13, '2024-10-05', '�߫}�b���ʤ���{�o�ܫi���A�D�H�]�ܶ}�ߡC', 6),
(5, 19, '2024-04-12', '���a�]�I�����A�d���̳��ܦw���C', 11),
(10, 17, '2024-03-28', '���ʵ�����A�����^�a�αo�S�O���C', 2),
(2, 12, '2024-07-15', '�u�@�H�����d���ܦ��@�ߡA��������ܴΡC', 7),
(7, 18, '2024-09-22', '�ڪ��d���Ĥ@���ѥ[���ʡA��{�o�ܿ��ġC', 18),
(4, 15, '2024-11-03', '���ʤ����ܦh�������`�A�d���̳��ܧ�J�C', 4),
(9, 11, '2024-05-19', '���a�ܤj�A�d���i�H�ۥѩb�]�A�D�`�A�X�C', 13),
(1, 20, '2024-08-30', '���ʤ����M�~���d���V�m�v���ɡA��ì�ܦh�C', 10),
(6, 14, '2024-12-10', '�ڪ������b���ʤ��Ƿ|�F�s�ޯ�A�D�`�P�¡C', 19),
(3, 16, '2024-06-25', '���ʮ�^�ܦn�A�d���̳����o�ܶ}�ߡC', 1),
(8, 13, '2024-10-05', '�u�@�H���ܲӤߡA���U��C���d�����ݨD�C', 15),
(5, 19, '2024-04-12', '���ʵ�����A�d���̳��̨̤��ˡC', 20),
(10, 17, '2024-03-28', '�o�O�ڰѥ[�L�̴Ϊ��d�����ʡA�j�P���ˡI', 16);

END;

--------------------- �ӫ~����� ---------------------
BEGIN

INSERT INTO [dbo].[product_category] ([name])
VALUES
	('���~�O��'),
	('��`�Ϋ~'),
	('�A��'),
	('����'),
	('��L')

INSERT INTO [dbo].[product_color] ([name])
VALUES
	('�¦�'),
	('�զ�'),
	('����'),
	('���'),
	('����'),
	('���'),
	('�Ŧ�'),
	('����')

INSERT INTO [dbo].[product_size] ([name])
VALUES
	('S'),
	('M'),
	('L')

INSERT INTO [dbo].[product_detail]  
           ([product_category_id], [name], [description])  
VALUES  
	(1, '�A�����׭ᰮ', '���J�աB�C�תժ��ᰮ���סA�A�X�@�����y�s���C'),  
	(2, '�L�Ш��G�߬�', '���O�i�R����A�j�O�l�������A���[����C'),  
	(3, '�V�u�[�p�O�x�d����', '�X�n�O�x�A�A�X�H�N�Ѯ�A���d���ξA�L�V�C'),  
	(4, '�߫}�r��۰ʱ���y', '���طP���A�H���ܴ���V�A��E�߫}���y����C'),  
	(5, '�d���������o���', '���ĹL�o����A�T�O�d�������M�䰷�d�C'),
	(1, '��i��³', '�t���״I���ͯ��P�q����A�����߫}��`��i�ݨD�C'),
	(2, '�ܵ���y', '���ħ��ӵߡA�A�X�M���d���Ϋ~�P����C'),
	(3, '�����d���B��', '���K�z��A�A�X�B�ѨϥΡC'),
	(4, '�����d������', '�h�Ҧ����ʡA���d���O�����O�C'),
	(5, '�����d����', '���ħl�������A�������ҲM�s�C'),
	(1, '���J�դ��װ�', '�A�X�V�m���y�A�����u��J�ս�C'),
	(2, '�j�O����Q��', '�ֳt���Ѳ����A�A�Ω��d�����ҡC'),
	(3, '�L�u�z���d����', '�D�n�z��A������C'),
	(4, '�p�g�r�ߴ�', '���ʹC���A���߫}�O�����O�C'),
	(5, '���ʬ��o��', '���ĹL�o�����M����C'),
	(1, '�D���߹s��', '�I�tOmega-3�A�P�i�֤򰷱d�C'),
	(2, '�ѵM��h�߬�', '�L�ЧC�ӡA���O�i���ѡC'),
	(3, '���������d���~�M', '�A�X�V�u�M�B�ѨϥΡC'),
	(4, '�u���d���y', '���ùs���]�p�A�������ֿ���C'),
	(5, '�d������������', '�w�������A�O���}�n�����ߺD�C'),
	(1, '���׭��������', '���U�M������A�w���f��C'),
	(2, '�W�l���d����y', '�X�n�ξA�A�ֳt�l�������C'),
	(3, '�L�u���A�d����', '���Ĩ���A�Υm�r�C'),
	(4, '���z�r�߾�', '�i�۰ʹB��A�l�޿߫}�`�N�C'),
	(5, '�i��~�d����', '�ξA�z��A����M��C');

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
SET photo = (SELECT BulkColumn FROM Openrowset(Bulk 'C:\petTopia\�ӫ~�Ϥ�\01.jpg', Single_Blob) AS pic)  
WHERE id BETWEEN 1 AND 100;
*/



END;

--------------------- �ӫ~����� ---------------------
BEGIN

--�u�f��
INSERT INTO coupons ([name], discount_type, discount_value, min_order_value, limit_count, valid_start, valid_end, [status]) VALUES
('�s�|�����w�u�f50��', 0, 50.00, 200.00, 1, '2025-01-01', '2025-12-31', 1),  -- �T�w50���馩
('�P�~�y�E��n�n�e', 1, 0.10, 100.00, 5, '2025-01-01', '2025-12-31', 1),  -- 10%�馩
('����`100���j��e', 0, 100.00, 300.00, 2, '2025-02-01', '2025-06-30', 0);   -- �T�w100���馩

-- ���J�q�檬�A
INSERT INTO order_status ([name]) VALUES
('�B�z��'),
('�ݥX�f'),
('�t�e��'),
('�ݦ��f'),
('�w����'),
('�w����');

-- ���J�I�ڪ��A
INSERT INTO payment_status ([name]) VALUES
('�ݥI��'),
('�w�I��'),
('�I�ڥ���');

-- ���J�B�e�覡
INSERT INTO shipping_category ([name], shipping_cost, shipping_day) VALUES
('�v�t', 50.00, 7),
('�ֻ�', 80.00, 3);

-- ���J�I�ڤ覡
INSERT INTO payment_category ([name]) VALUES
('�H�Υd�I��'),
('�f��I��');

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

-- ���J�ʪ���
INSERT INTO cart (member_id, product_id, quantity) VALUES
(11, 10, 2),
(11, 7, 3),
(12, 7, 1),
(12, 12, 2),
(13, 1, 1),
(13, 7, 2);


END;

