
# 2조 - 매진임박(MaeJinImBack)
## 🎫  서비스 소개 (Momentix)
### ⭐️ 서비스 개요
### "특별한 순간을 예매한다"
#### Moment + Tix (Tickets) : “순간 + 티켓” → 특별한 순간을 위한 티켓. 
Momentix는 '특별한 순간(Moment)'을 위한 '티켓(Tix)', 그리고 그 순간을 위한 관람 문화 정보를 제공하고 그 순간을 함께하기 위한 티켓 예매를 지원하는 서비스입니다.

다양한 문화 콘텐츠를 모아 보여주며, 원하는 순간을 간편하게 예매할 수 있습니다.

## 목차
1. [🔲 Wireframe](#-wireframe)
2. [🗄 ERD](#-erd)
3. [🧾 API명세서](#-api명세서)
4. [🛠기술 스택](#-기술-스택)
5. [👥 팀원 소개 ](#-팀원-소개)

## 🔲 Wireframe

## 🗄 ERD
<img width="1382" height="845" alt="Image" src="https://github.com/user-attachments/assets/834cd14e-c16c-46de-b4fa-627b4b5230a0" />

## 🧾 API명세서
- [API 명세서](https://www.notion.so/teamsparta/API-2622dc3ef5148038969ddf25075059a1)
## 🛠 기술 스택
#### 📋 Languages
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)

#### 💻 IDEs/Editors
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)

#### 🎋 ORM & Frameworks
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)

#### ☁️ Cloud & Infrastructure
![Amazon S3](https://img.shields.io/badge/Amazon%20S3-FF9900?style=for-the-badge&logo=amazons3&logoColor=white)

#### 🔐 Security
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

### 💾 Databases & Search Engine
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![Elasticsearch](https://img.shields.io/badge/elasticsearch-%230377CC.svg?style=for-the-badge&logo=elasticsearch&logoColor=white)

#### 🎨 Design & Collaboration Tools
![Canva](https://img.shields.io/badge/Canva-%2300C4CC.svg?style=for-the-badge&logo=Canva&logoColor=white)
![Figma](https://img.shields.io/badge/figma-%23F24E1E.svg?style=for-the-badge&logo=figma&logoColor=white)
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white)
![Slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white)
![Zoom](https://img.shields.io/badge/Zoom-2D8CFF?style=for-the-badge&logo=zoom&logoColor=white)
<img src="https://img.shields.io/badge/erd cloud-7B00FF?style=for-the-badge&logo=erd&logoColor=white"> 

#### 🖥️ Backend Development
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)

#### 📟 Test
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)

## 기술적 의사결정

<details><summary> 🙈성인 인증</summary>

#### 프로젝트 요구사항
- 일반 공연 연령 제한 법적 기준 일관 부재 → 사전 연령·성인 여부 확인 필요함
- 초기 비용·운영 부담 최소화 요구, 추후 인증으로 확장할 수 있어야 함

| 비교사항| 네이버 소셜 <br>로그인 회원정보| KG 이니시스 | 네이버 인증|
| :---: | :---: | :---: |:---:|
| 본인인증 비용 | 기본제공 | 40원/건 | 30원/건|

</details>


<details><summary>📚MySQL
</summary>

#### 프로젝트 요구사항

- 티켓 예매 시스템에서 좌석 관리, 예매 내역, 경제 정보 등
- 정합성과 트랜잭션 안정성이 중요한 데이터 처리 필요


| 비교사항| MySQL| PostgreSQL| 
| :--- | :--- | :--- |
|트랜잭션 지원|InnoDB기반 ACID지원  | ACID준수, 고급 트랜잭션 기능  | 
|데이터 정합성|외래키·제약조건으로 충분히 보장  | 복잡한 제약조건과 고급 데이터 타입 지원 | 
|성능|중소형 규모  |  대규모 시스템| 
|확장성| 수직 확장 중심 |수평 확장 및 병렬 처리 지원  | 

</details>


<details><summary> 📜OpenCSV </summary>

#### 프로젝트 요구사항
- 관리자가 공연장의 좌석 기본 배치 등록
- 공연별 좌석 등급, 가격 구역 등록



| 비교 항목| OpenCSV| Apache Commons CSV|
| :--- | :--- | :--- |
| POJO(Plan Old Java Object) 매핑| CsvBindByName 등<br> 어노테이션 지원 | 수작업 매핑 필요|
| 설정 및 통합|  Spring Boot 통합 간단| 코드 작성 필요|
| 데이터 처리 규모| 중소 규모 처리| 대규모 / 스트리밍 처리|


</details>


<details><summary> 📁AWS S3
</summary>

#### 프로젝트 요구사항
- 확장성 : 이미지 수가 증가하더라도 안정적으로 서비스를 운영할 수 있어야 함.
- 서버 성능 : 이미지 파일 트래픽이 서버의 성능에 영향을 주지 않아야 함.

| 비교 항목| 서버에 직접 저장| 데이터베이스에 저장| AWS S3 |
| --- | --- | --- |--- |
| 확장성/안정성| 서버 확장 시 데이터 불일치|DB가 무거워져 백업/복구 비효율 |뛰어난 확장성/안정성|
| 서버 성능| 이미지 요청↑, 서버 부하|DB가 커지며 심각한 성능 저하 |URL 주소만 DB에 저장 
서버 부담 X|
| 비용 효율성| 비효율적 비용| DB 저장 공간 고비용|스토리지 비용 저비용|
</details>

<details><summary>🔒JWT
</summary>

#### 프로젝트 요구사항
 - 동시 접속자가 급증하는 티켓팅 환경에서도 서버 부하를 최소화해야 함
 - 클라이언트와 서버 간 인증 정보를 안전하게 전달할 수 있어야 함

| 비교 항목| Session| JWT|
| --- | --- | --- |
| 상태 관리|서버 메모리|클라이언트 쿠키/헤더에 저장|
|인증 처리|매 요청 DB/세션 조회 필요 |토큰 서명 검증만으로 인증|
|확장성/부하|서버 확장 시 세션 공유 필요|별도 공유 필요 없음|

</details>



## 👥 팀원 소개
| 이름| 직책 | Github | blog |
| :---: | :---: | :---: | :---: |
| 곽지훈 | 팀원| [Gwakjihun](https://github.com/Gwakjihun) |[rhkrwlgns](https://rhkrwlgns.tistory.com/)|
| 전재민 | 부팀장 | [Beforejamni](https://github.com/Beforejamni) |[beforejamn1](https://beforejamn1.tistory.com/)|
| 최재혁 | 팀장 | [Gemini-kei](https://github.com/Gemini-kei)|[keigemini](https://velog.io/@keigemini/posts)|
| 최한솔 | 서기 | [hansolChoi29](https://github.com/hansolChoi29) |[winwin0219](https://winwin0219.tistory.com/)|
