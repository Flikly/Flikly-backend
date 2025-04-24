com.yourname.netflixclone/
├── global/                  # 애플리케이션 전반에 걸친 공통 요소
│   ├── config/              # 애플리케이션 설정 클래스들
│   ├── error/               # 글로벌 예외 처리
│   ├── security/            # 보안 관련 설정 및 구현
│   ├── util/                # 유틸리티 클래스들
│   └── common/              # 공통 엔티티, 상수 등
│
├── domain/                  # 핵심 비즈니스 도메인들
│   ├── user/                # 사용자 관련 기능
│   │   ├── controller/      # API 엔드포인트
│   │   ├── service/         # 비즈니스 로직
│   │   ├── repository/      # 데이터 접근 계층
│   │   ├── dto/             # 데이터 전송 객체
│   │   ├── entity/          # JPA 엔티티
│   │   ├── exception/       # 도메인 특화 예외
│   │   └── mapper/          # DTO-엔티티 변환
│   │
│   ├── content/             # 영화/TV 쇼 콘텐츠 관련
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── exception/
│   │   └── mapper/
│   │
│   ├── recommendation/      # 추천 알고리즘 관련
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── exception/
│   │   └── strategy/        # 다양한 추천 알고리즘 전략
│   │
│   ├── watching/            # 시청 기록 및 진행 상태 관련
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── dto/
│   │   ├── entity/
│   │   └── exception/
│   │
│   └── payment/             # 결제 및 구독 관련
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── dto/
│       ├── entity/
│       └── exception/
│
├── infrastructure/          # 외부 서비스 통합
│   ├── aws/                 # AWS 서비스 연동
│   ├── email/               # 이메일 서비스
│   ├── storage/             # 스토리지 서비스
│   └── payment/             # 외부 결제 게이트웨이
│
└── NetflixCloneApplication.java  # 메인 애플리케이션 클래스