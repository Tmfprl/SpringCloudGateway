좋아요! 요청하신 대로 **영어 버전으로 완전히 통일된 README**를 만들어드릴게요. `final_msa_service` 프로젝트 내 스타일에 맞춰 구성했습니다.

---

```markdown
# Spring Cloud Gateway Learning Service

## 📌 Description
This project was developed to learn **Spring Cloud Gateway**.  
It is included as a service in the `final_msa_service` project.  
The main focus is to practice gateway-based request routing, filters, and JWT authentication token validation.

- **Language**: Java  
- **Framework**: Spring Boot, Spring Cloud Gateway  
- **Development Time**: 2024  

---

## ✨ Features
- Acts as an API Gateway to route client requests  
- JWT token verification and request filtering  
- Understanding microservice communication via Spring Cloud Gateway  
- Logging and request processing practice  

---

## 🛠️ Tech Stack
| Component        | Technology                        |
|-----------------|----------------------------------|
| Framework       | Spring Boot, Spring Cloud Gateway |
| Language        | Java                              |
| Security        | JWT                               |
| Testing/Logging | JUnit, Spring Logging             |

---

## 📂 Project Structure
```

scg\_service
├─ src/main/java/org/example/scg
│  ├─ config        # Gateway configuration, routing, filters
│  ├─ filter        # JWT verification and request filters
│  └─ controller    # Test routing controllers
└─ README.md        # Project documentation

````

---

## ⚙️ Installation
1. Clone the repository:
```bash
git clone [Repository URL]
cd scg_service
````

2. Build and run with Maven:

```bash
./mvnw spring-boot:run
```

3. Test after registering the service in the `final_msa_service` project

---

## 🚀 Usage

* Call the Gateway API via browser or Postman
* Requests with JWT tokens are validated
* Observe routing and filter processing

---

## 📜 License

MIT License

---

## 👨‍💻 Author

GYEONGEUN PARK, 2024



---
KOREAN.ver

좋아요! 위 영어 버전을 기반으로 **한국어 버전 README**를 만들어 드리면 아래와 같습니다.

---

# Spring Cloud Gateway 학습 서비스

## 📌 설명
이 프로젝트는 **Spring Cloud Gateway** 학습을 위해 개발되었습니다.  
`final_msa_service` 프로젝트 내 서비스로 포함되어 있으며,  
주요 목적은 게이트웨이 기반 요청 라우팅, 필터 처리, JWT 인증 토큰 검증을 연습하는 것입니다.

- **언어**: Java  
- **프레임워크**: Spring Boot, Spring Cloud Gateway  
- **개발 기간**: 2024  

---

## ✨ 주요 기능
- 클라이언트 요청을 라우팅하는 API Gateway 역할 수행  
- JWT 토큰 검증 및 요청 필터링  
- Spring Cloud Gateway를 활용한 마이크로서비스 통신 이해  
- 요청 처리 및 로깅 실습  

---

## 🛠️ 기술 스택
| 구성 요소       | 기술                              |
|----------------|---------------------------------|
| 프레임워크      | Spring Boot, Spring Cloud Gateway |
| 언어            | Java                              |
| 보안            | JWT                               |
| 테스트/로깅     | JUnit, Spring Logging             |

---

## 📂 프로젝트 구조
```

scg\_service
├─ src/main/java/org/example/scg
│  ├─ config        # 게이트웨이 설정, 라우팅, 필터
│  ├─ filter        # JWT 검증 및 요청 필터
│  └─ controller    # 테스트용 라우팅 컨트롤러
└─ README.md        # 프로젝트 문서

````

---

## ⚙️ 설치 방법
1. 저장소 클론:
```bash
git clone [Repository URL]
cd scg_service
````

2. Maven으로 빌드 및 실행:

```bash
./mvnw spring-boot:run
```

3. `final_msa_service` 프로젝트에서 서비스 등록 후 테스트

---

## 🚀 사용 방법

* 브라우저 또는 Postman으로 게이트웨이 API 호출
* JWT 토큰이 포함된 요청은 검증됨
* 라우팅 및 필터 처리 확인 가능

---

## 📜 라이선스

MIT License

---

## 👨‍💻 개발자

박경은, 2024




