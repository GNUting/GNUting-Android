# 🌸 GNUting, 지누팅 

기간: 2024.03.01 ~ 2024.05.22 <p/>
한 줄 소개: 경상국립대학교 재학생들을 위한 과팅 커뮤니티 앱 <p/>
팀 구성: 안드로이드 앱 개발 1명, Ios 앱 개발자 1명, 백엔드 2명, 디자이너 1명

![](https://github.com/GNUting/GNUting-Android/assets/86148926/93c3a729-1634-4aad-90fa-4800d8d7f23f)

## 🧩 Google Play
구글 플레이 스토어에서 지누팅을 다운로드 받을 수 있습니다.<br/>
<a href="https://play.google.com/store/apps/details?id=com.changs.android.gnuting_android"><img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" width="20%"></a>

## 🖥 Development

### Features
- 회원가입/회원탈퇴/로그인/로그아웃
- 게시물 작성/수정/삭제
- 게시물 조회/검색
- FCM push 알림
- 채팅

## ℹ️ 시연 영상

[http://naver.me/Fcurc14A](http://naver.me/Fcurc14A)

## 📖 상세 내용

지누팅은 경상국립대학교 학생들을 위한 전용 과팅 매칭 애플리케이션이며, 학우들이 쉽고 빠르게 과팅을 구성하고 새로운 만남을 경험할 수 있는 플랫폼입니다. 이 앱을 통해 같은 대학의 학생들과 소통하며 흥미로운 대화와 즐거운 만남을 가질 수 있습니다.

### 로그인 화면
<img width="330" src="https://github.com/GNUting/GNUting-Android/assets/86148926/e2f5f1bd-a79c-4b8a-8442-7a7b3683a571">

### 메인 화면
<img width="330" src="https://github.com/GNUting/GNUting-Android/assets/86148926/3ea8db9e-328d-4516-acb6-fb19885d7866">
    
### 과팅 게시판
<img width="330" src="https://github.com/GNUting/GNUting-Android/assets/86148926/1274fb79-377f-41fa-90f6-5e114781498a">

<img width="330" src="https://github.com/GNUting/GNUting-Android/assets/86148926/27d2371b-0494-4d19-a7e8-f5a9e25af4ac">


### 과팅 신청 현황
<img width="330" src="https://github.com/GNUting/GNUting-Android/assets/86148926/64649363-3d4c-4aef-9851-fba871430fac">

### 푸시 알림
<img width="330" src="https://github.com/GNUting/GNUting-Android/assets/86148926/660d64ce-bd7c-4a7f-a4f0-d5a080e973f9">

    
### 알림 설정
<img width="330" src="https://github.com/GNUting/GNUting-Android/assets/86148926/26764c2f-c90d-44ba-9993-34713c45eb23">
    

### 실시간 단체 채팅
<img width="330" src="https://github.com/GNUting/GNUting-Android/assets/86148926/248c6bd1-516f-4fe4-a03d-448c94363c9c">


## 🛠️ 사용 기술 및 라이브러리

- Kotlin
- **Coroutines & Flow**
- **MVVM 패턴**
- Retrofit2
- OkHttp3
- ViewModel, LiveData, Room, Navigation Component
- Glide
- **Timber**
- **Paging3**
- **Data Store**
- **Hilt - 의존성 주입**
- **JWT - Access Token & Refresh Token**
- **StompProtocolAndroid - 실시간 채팅 구현**
- **FCM - 푸시 알림**
- **Swipe refresh layout**
- **In-APP Update**
- **android proguard - 앱 축소 및 난독화**

### Architecture
GNUting is based on MVVM design pattern and repository pattern
<p align="center">
  <img src="https://gnuting.github.io/GNUting-PrivacyPolicy/gunting_arc.webp" width="50%">
</p>

