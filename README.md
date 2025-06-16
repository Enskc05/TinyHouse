# Tiny House Kiralama Sistemi

## 🏠 Proje Tanımı
Tiny House Kiralama Sistemi, minimalist yaşam alanlarının kiralanmasını sağlayan bir rezervasyon ve yönetim platformudur. Sistem üç temel rol üzerine kurulmuştur:
- **Kiracılar (Renter)**: İlanları görüntüleyip rezervasyon yapabilir
- **Ev Sahipleri (Owner)**: Kendi ilanlarını yönetebilir
- **Yöneticiler (Admin)**: Tüm sistemi denetleyebilir

## ✨ Temel Özellikler
### 👥 Kullanıcı Yönetimi
- Rol bazlı kayıt sistemi (Admin/Owner/Renter)
- JWT tabanlı kimlik doğrulama
- Profil yönetimi

### 🏡 İlan Yönetimi
- İlan oluşturma/güncelleme/silme
- Detaylı filtreleme ve arama
- Fotoğraf yükleme
- Konum bazlı harita görüntüleme

### 📅 Rezervasyon Sistemi
- Tarih seçimli rezervasyon
- Rezervasyon onay/iptal akışı
- Rezervasyon takip sistemi

### 💳 Ödeme Sistemi
- Online ödeme entegrasyonu
- Ödeme geçmişi görüntüleme
- Yönetici ödeme takibi

## 🛠 Teknoloji Stack'i
### 📱 Mobil Uygulama (Frontend)
- **Android Compose UI**
- **Kotlin** programlama dili
- **MVVM** Mimari
- **StateFlow** ile durum yönetimi
- **Coroutine** tabanlı asenkron işlemler

### 💻 Sunucu Tarafı (Backend)
- **Spring Boot** (REST API)
- **MySQL** Veritabanı
- **Clean Architecture** prensipleri
- JWT tabanlı yetkilendirme

## 📊 Sistem Mimarisi
```mermaid
graph TD
    A[Kullanıcı] --> B{Mobil Uygulama}
    B --> C[ViewModel]
    C --> D[Use Cases]
    D --> E[Repository]
    E --> F[REST API]
    F --> G[Spring Boot]
    G --> H[MySQL]
