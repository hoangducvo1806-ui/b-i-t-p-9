# BÀI TẬP TUẦN 9: SPRING BOOT 4 + SPRING SECURITY 7 + MAPSTRUCT + THYMELEAF

**Trường Đại học Sư phạm Kỹ thuật TP.HCM (HCMUTE)**  
**Khoa Công nghệ Thông tin**  
**Môn học:** Lập trình Web / Chuyên đề Java  
**Sinh viên thực hiện:** Võ Lê Hoàng Đức  
**Mã số sinh viên (MSSV):** 24133013  
**Repository GitHub:** [https://github.com/hoangducvo1806-ui/b-i-t-p-9](https://github.com/hoangducvo1806-ui/b-i-t-p-9)

---

## 📌 MỤC LỤC & CẤU TRÚC REPOSITORY

Dự án bao gồm 3 bài tập ví dụ được phân chia thành 3 thư mục độc lập:

```text
b-i-t-p-9/
├── ViDu1/                    # Dự án springboot1-8: Login cơ bản bằng Email (Thymeleaf không dùng Dialect)
├── ViDu2/                    # Dự án springboot1-9: Custom Login bằng Username/Email (Thymeleaf Layout Dialect)
├── ViDu3/                    # Dự án shop-springboot-4-1-1: Hệ thống lớn với OTP, Cloudinary, CRUD & Query
├── .gitignore                # File loại bỏ các thư mục rác (target/, .idea/, .env, ...)
└── README.md                 # Tài liệu hướng dẫn chi tiết toàn bộ bài tập
```

---

## 🚀 CHI TIẾT TỪNG VÍ DỤ

### 1. VÍ DỤ 1: LOGIN CƠ BẢN VỚI EMAIL (`ViDu1/`)
- **Yêu cầu:** Cho bảng `User`, `Role`, xây dựng chức năng đăng nhập bằng email, thông tin user hiển thị ở `header.html`. Sử dụng Spring Boot 4.1.1, Spring Security 7, MapStruct 1.6.3 và Thymeleaf thuần (không dùng Layout Dialect).
- **Điểm nổi bật:**
  - Cấu hình `SecurityConfig` chuẩn mới nhất với Lambda DSL, cấu hình `formLogin()` nhận diện trường `email` qua `.usernameParameter("email")`.
  - Class `CustomUserDetailsService` truy vấn tài khoản qua `findByEmailWithRole(email)` kết hợp `JOIN FETCH` tránh N+1 query.
  - Sử dụng cơ chế `th:fragment` và `th:replace` chuẩn của Thymeleaf kết hợp `thymeleaf-extras-springsecurity6` để hiển thị email người dùng và quyền truy cập (`sec:authentication="name"`, `sec:authentication="principal.authorities"`).
  - Tự động nạp dữ liệu mẫu ban đầu qua `DataInitializer`:
    - Admin: `trungnh@hcmute.edu.vn` / Mật khẩu: `123456`
    - User: `user01@gmail.com` / Mật khẩu: `123456`

---

### 2. VÍ DỤ 2: CUSTOM LOGIN BẰNG USERNAME HOẶC EMAIL (`ViDu2/`)
- **Yêu cầu:** Nâng cấp hệ thống cho phép đăng nhập bằng cả **Username** hoặc **Email**, hiển thị `fullName` và `images` (avatar) lên thẻ header sử dụng **Thymeleaf Layout Dialect**.
- **Điểm nổi bật:**
  - Phương thức `findByUsernameOrEmail(String username, String email)` trong `UserRepository` giúp người dùng có thể nhập bất kỳ hình thức định danh nào vào cùng 1 ô input.
  - Xây dựng class `CustomUserDetails` implement `UserDetails` lưu trữ các trường dữ liệu tùy biến: `id`, `username`, `email`, `fullName`, `images`, `role`, `enabled`.
  - Sử dụng thư viện `thymeleaf-layout-dialect` với file template chuẩn `layouts/layout.html` (`layout:decorate` và `layout:fragment="content"`).
  - Trích xuất thông tin Principal mở rộng trên Header:
    - Avatar: `${#authentication.principal.images}` kèm logic fallback hiển thị ảnh mặc định nếu chưa cập nhật.
    - Tên đầy đủ: `${#authentication.principal.fullName}`.
    - Tài khoản demo khởi tạo sẵn qua `CommandLineRunner`:
      - Đăng nhập Username: `user01` / Mật khẩu: `123456`
      - Đăng nhập Email: `user01@gmail.com` / Mật khẩu: `123456`

---

### 3. VÍ DỤ 3: HỆ THỐNG SHOPPING QUẢN LÝ USER & PRODUCT VỚI OTP VÀ CLOUDINARY (`ViDu3/`)
- **Yêu cầu:** Xây dựng hệ thống MVC hoàn chỉnh với kiến trúc tầng (Controller - Service - Repository - Entity - DTO - Mapper) quản lý các thực thể: `User`, `Role`, `OtpToken`, `Product` (quan hệ 1 User - N Products).
- **Các module cốt lõi:**
  1. **Module Xác thực OTP & Gmail SMTP:**
     - Sinh mã OTP 6 số ngẫu nhiên bằng `SecureRandom`.
     - Mã hóa OTP bằng BCrypt trước khi lưu vào bảng `otp_tokens`.
     - Giới hạn thời gian hiệu lực OTP 5 phút, giới hạn số lần nhập sai tối đa 5 lần (chống tấn công brute-force).
     - Hỗ trợ 2 luồng: **Đăng ký tài khoản** (gửi OTP kích hoạt) và **Quên mật khẩu** (gửi OTP đổi mật khẩu mới).
  2. **Module Cloudinary Image Storage:**
     - Tích hợp Cloudinary SDK (`cloudinary-http5`).
     - Tải ảnh sản phẩm lên Cloudinary trong thư mục `shop/products`.
     - Lưu trữ database dưới định dạng `secure_url|public_id` để xóa ảnh cũ trên đám mây khi cập nhật hoặc xóa sản phẩm.
  3. **Module JPA / Query & Phân trang:**
     - Tìm kiếm User theo từ khóa (username, email, họ tên) có phân trang.
     - Tìm kiếm Product theo tên và mô tả có phân trang và `JOIN FETCH` để tránh Lazy load N+1.
     - Đếm tổng số User trong hệ thống (`userRepository.count()`).
     - Đếm tổng số Product theo từng User (`countProductsByUserId`).
  4. **Module MapStruct 1.6.3:**
     - Ánh xạ tự động DTO <-> Entity ở compile-time, tối ưu hóa hiệu năng, tách biệt hoàn toàn tầng dữ liệu và tầng biểu diễn.

---

## 🛠 CÔNG NGHỆ SỬ DỤNG

| Thành phần | Công nghệ / Thư viện |
| :--- | :--- |
| **Java Platform** | Java 26 / JDK 26 |
| **Framework** | Spring Boot 4.1.1, Spring Security 7.1.x |
| **ORM / Persistence** | Spring Data JPA, Hibernate, Jakarta Persistence |
| **Database** | SQL Server (MSSQL JDBC) / H2 in-memory |
| **View Engine** | Thymeleaf, Thymeleaf Layout Dialect, Thymeleaf Security Extras 6 |
| **Object Mapping** | MapStruct 1.6.3, Lombok |
| **Email Service** | Spring Mail (Gmail SMTP) |
| **Cloud Storage** | Cloudinary Java SDK |
| **Build Tool** | Apache Maven 3.9.x |

---

## 📋 HƯỚNG DẪN CÀI ĐẶT VÀ CHẠY DỰ ÁN

### 1. Chuẩn bị môi trường
- Đã cài đặt JDK 26 (hoặc Java 17+ tương thích).
- Đã cài đặt Maven 3.9+.
- Khởi động SQL Server và tạo cơ sở dữ liệu:
  ```sql
  -- Dành cho Ví dụ 1
  CREATE DATABASE webst4;
  
  -- Dành cho Ví dụ 2
  CREATE DATABASE webst9;
  
  -- Dành cho Ví dụ 3
  CREATE DATABASE webst3;
  ```

### 2. Cấu hình biến môi trường (`.env`)
Mỗi thư mục dự án (`ViDu1`, `ViDu2`, `ViDu3`) đều có file cấu hình `.env` mẫu. Bạn có thể điều chỉnh thông số cho phù hợp với máy của mình:
```properties
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=webst3;encrypt=false;trustServerCertificate=true;sslProtocol=TLSv1.2;characterEncoding=UTF-8
DB_USERNAME=sa
DB_PASSWORD=your_password

# Cấu hình Gmail SMTP (đối với Ví dụ 3)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Cấu hình Cloudinary (đối với Ví dụ 3)
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

### 3. Biên dịch và Chạy từng dự án

- **Chạy Ví dụ 1 (Cổng 8088):**
  ```bash
  cd ViDu1
  mvn spring-boot:run
  ```
  Truy cập: [http://localhost:8088](http://localhost:8088)

- **Chạy Ví dụ 2 (Cổng 8081):**
  ```bash
  cd ViDu2
  mvn spring-boot:run
  ```
  Truy cập: [http://localhost:8081](http://localhost:8081)

- **Chạy Ví dụ 3 (Cổng 8080):**
  ```bash
  cd ViDu3
  mvn spring-boot:run
  ```
  Truy cập: [http://localhost:8080](http://localhost:8080)
