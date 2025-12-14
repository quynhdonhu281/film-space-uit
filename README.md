# FilmSpace 

Đây là ứng dụng Android được phát triển bằng **Java** và **Android Studio**.

## Bắt đầu

### Yêu cầu
- Android Studio (khuyến nghị dùng phiên bản mới nhất)
- Android SDK (cài đặt thông qua Android Studio)
- JDK 

### Cách chạy project
1. Clone repository này về máy
2. Mở **Android Studio**
3. Chọn **Open an existing project**
4. Chọn thư mục gốc của project
5. Chờ **Gradle Sync** hoàn tất
6. Chạy ứng dụng trên **Máy ảo (Emulator)** hoặc **Thiết bị thật**

## Cấu trúc project
- `app/` : Module chính của ứng dụng Android  
- `app/java` : Mã nguồn Java  
- `app/res` : Layout, drawable, values  
- `app/manifests` : File cấu hình `AndroidManifest.xml`  

## Lưu ý
- **KHÔNG** commit file `local.properties`
- Đảm bảo Android Studio nhận đúng đường dẫn Android SDK
- Gradle và JDK được Android Studio quản lý tự động
