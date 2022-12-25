# Giới thiệu

App ghi chú lấy ý tưởng từ Samsung Note. App cho phép soạn ghi chú, truy vấn ghi chú, tạo thư mục và quản lý thư mục. App được xây dựng bằng framework Android SDK. Ngôn ngữ sử dụng: Java. 

Database sử dụng: SQLite tích hợp sẵn trong hệ điều hành Android. App sử dụng library Room nhằm trừu tượng hóa quá trình truy vấn Database, giúp truy vấn dễ dàng và hiệu quả hơn.
[Android SDK](https://vi.wikipedia.org/wiki/Android_SDK)
[Room persistence library](https://developer.android.com/jetpack/androidx/releases/room?gclid=CjwKCAiAhqCdBhB0EiwAH8M_GjzT4z6HhmzL8AryB_FdWU30pwc8FMKZbVNATjKuI3RpH4d4pyLRwBoC0bwQAvD_BwE&gclsrc=aw.ds)

# Hướng dẫn chạy project
Yêu cầu cài sẵn Android studio để chạy thử project.
# Thư viện bổ trợ
##### Các thư viện đã add vào file build.gradle(module:AppGhiChu):
* implementation group: 'org.jsoup', name: 'jsoup', version: '1.7.2'
* implementation 'jp.wasabeef:richeditor-android:2.0.0'
* implementation group: 'commons-io', name: 'commons-io', version: '2.6'
* implementation "androidx.recyclerview:recyclerview:1.2.1"implementation
* androidx.room:room-runtime:$room_version"
* annotationProcessor androidx.room:room-compiler:2.4.3

# Cấu trúc project
