# PetTopia

## 專案簡介
**PetTopia** 是一個提供寵物商城、搜尋寵物友善店家與活動的綜合平台，讓飼主能夠增進與毛小孩的生活與關係。

## 技術架構
(前期使用 MVC，後期考慮分離)
- **後端**：Spring Boot + Hibernate + MSSQL
- **前端**：HTML / CSS / JavaScript + Vue.js
- **API**：
  - Google Maps API
  - 第三方支付 API
  - RESTful API
- **驗證方式**：JWT（JSON Web Token）

## 後端環境
- 安裝 spring tool suite, lombok, (postman, axios)

## Git 分支管理規則
我們使用 Git Feature Branch Workflow，主要分為以下幾個分支：
- **main**：穩定版本，只有依珊可以合併
- **dev**：開發版本，只有依珊跟品媃可以合併
  - **mssql**：SQL 檔
  - **meeting**：用於每週會議和會議記錄，大家將要討論的程式碼與進度上傳到此分支
  - **f1/user**：會員系統 >> 放已完成的（該功能）
    - **f1/lai**：自己的分支（可以放未完成的程式碼）
  - **f2/shop**：
    - **f2/melody**
    - **f2/yon**
  - **f3/vendor**：
    - **f3/danny**
    - **f3/luo**

## Git - Push權限

| 人員 | 依珊 | 品媃 | 柏丞 | 政陽 | 天意 |
|------|------|------|------|------|------|
| 負責任務 | 商城&會員前台git | 店家&會員後台git | 權限 | 資料庫 | 前端優化 |
| 負責功能 | 商城 | 店家 | 會員 | 商城 | 店家 |
| main | ✔ |  |  |  |  |
| dev | ✔ | ✔ |  |  |  |
| database |✔  |  |  | ✔ |  |
| meeting | ✔ | ✔ | ✔ | ✔ | ✔ |
| f1/user | ✔ | ✔ | ✔ |  |  |
| f1/lai |✔  |  | ✔ |  |  |
| f2/shop | ✔ | ✔ |  | ✔ |  |
| f2/yon | ✔ |  |  | ✔ |  |
| f2/melody | ✔ |  |  |  |  |
| f3/vendor | ✔ | ✔ |  |  | ✔ |
| f3/danny | ✔ |  |  |  | ✔ |
| f3/luo | ✔ | ✔ |  |  |  |

## Git 流程
*****貼上的快捷鍵是shift+ctrl+insert
*****git bash 一次只能寫一行
### 若尚未安裝 Git
1. 安裝 Git
2. 打開 Git Bash
3. 設定用戶名與郵箱：
   ```bash
   git config --global user.name "你的名字"
   git config --global user.email "你的郵件@example.com"
### 建立專案
1. 在c槽建立一個petTopia資料夾，裡面建workspace資料夾
2. 將遠端專案clone到本地端指定位置
    ```bash
    cd "C:\petTopia\workspace”
    git clone https://github.com/yishan13li/petTopia.git
3. 確認workspace是否有專案匯入
4. 用IDE開啟workspace並匯入專案，測試是否可run as

### 提交程式碼
1. 先到專案的位置，並status確認哪些檔案有變動
    ```bash
    cd "C:\petTopia\workspace\petTopia”  
    git status 
2. 把所有變動檔案"都"提交檔案到遠端:
    ```bash
    git add .
    git commit -m “提交的原因”
    git push origin 要提交到遠端的分支名稱
3. 只想上傳某檔案到遠端:
    ```bash
    git add 某檔案名稱.檔案類型
    git commit -m “提交的原因”
    git push origin 要提交到遠端的分支名稱
