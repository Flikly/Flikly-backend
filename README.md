```Mermaid

sequenceDiagram
    %% 주요 참여자들
    actor Client as 사용자/관리자
    participant AdminUI as 관리자 UI
    participant UserUI as 사용자 UI
    participant AuthController as 인증 컨트롤러
    participant AdminController as 관리자 대시보드 컨트롤러
    participant UserController as 사용자 컨트롤러
    participant BookController as 도서 관리 컨트롤러
    participant BranchController as 지점 관리 컨트롤러
    participant LoanController as 대출 관리 컨트롤러
    participant AuthService as 인증 서비스
    participant AdminService as 관리자 서비스
    participant UserService as 사용자 서비스
    participant BookService as 도서 서비스
    participant BranchService as 지점 서비스
    participant LoanService as 대출 서비스
    participant OtpService as OTP 서비스
    participant EmailService as 이메일 서비스
    participant DB as 데이터베이스
    
    %% 관리자 로그인/회원가입 흐름
    Note over Client, DB: 관리자 로그인/회원가입 기능 (AUTH-001~AUTH-005)
    
    %% 관리자 로그인
    Client->>AdminUI: 관리자 로그인 페이지 요청
    AdminUI->>Client: 로그인 폼 제공
    Client->>AdminUI: 이메일, 비밀번호 입력
    AdminUI->>AuthController: 로그인 요청
    AuthController->>AuthService: 인증 요청(이메일, 비밀번호)
    AuthService->>DB: 관리자 정보 조회
    DB-->>AuthService: 관리자 정보 반환
    AuthService->>AuthService: 비밀번호 검증
    
    alt 인증 성공
        AuthService-->>AuthController: 인증 성공 응답(JWT 토큰)
        AuthController-->>AdminUI: 인증 성공, 토큰 및 사용자 정보 반환
        AdminUI->>AdminUI: 세션/토큰 저장
        AdminUI->>Client: 관리자 대시보드로 리다이렉트
    else 인증 실패
        AuthService-->>AuthController: 인증 실패 응답(에러)
        AuthController-->>AdminUI: 인증 실패 메시지
        AdminUI->>Client: 오류 메시지 표시
    end
    
    %% 관리자 회원가입
    Client->>AdminUI: 관리자 회원가입 페이지 요청
    AdminUI->>Client: 회원가입 폼 제공
    Client->>AdminUI: 이름, 이메일, 비밀번호 입력
    AdminUI->>AuthController: 회원가입 요청
    AuthController->>AuthService: 회원가입 처리 요청
    AuthService->>DB: 이메일 중복 확인
    
    alt 이메일 사용가능
        DB-->>AuthService: 중복 없음 확인
        AuthService->>AuthService: 비밀번호 암호화
        AuthService->>DB: 관리자 계정 생성
        DB-->>AuthService: 생성 성공
        AuthService-->>AuthController: 회원가입 성공 응답
        AuthController-->>AdminUI: 회원가입 성공 응답
        AdminUI->>Client: 로그인 페이지로 리다이렉트
    else 이메일 중복
        DB-->>AuthService: 중복 알림
        AuthService-->>AuthController: 회원가입 실패 응답(이메일 중복)
        AuthController-->>AdminUI: 회원가입 실패 메시지
        AdminUI->>Client: 오류 메시지 표시
    end
    
    %% 비밀번호 재설정 흐름
    Client->>AdminUI: 비밀번호 찾기 요청
    AdminUI->>Client: 이메일 입력 폼 제공
    Client->>AdminUI: 이메일 주소 입력
    AdminUI->>AuthController: 비밀번호 재설정 요청
    AuthController->>AuthService: 이메일 존재 확인
    AuthService->>DB: 이메일 조회
    
    alt 이메일 존재
        DB-->>AuthService: 이메일 존재 확인
        AuthService->>OtpService: OTP 생성 요청
        OtpService->>OtpService: 랜덤 OTP 생성
        OtpService->>DB: OTP 저장
        OtpService->>EmailService: OTP 이메일 발송 요청
        EmailService-->>Client: 이메일로 OTP 발송
        OtpService-->>AuthService: OTP 생성 성공
        AuthService-->>AuthController: 요청 성공 응답
        AuthController-->>AdminUI: OTP 입력 화면 요청
        AdminUI->>Client: OTP 입력 폼 제공
        
        %% OTP 검증
        Client->>AdminUI: OTP 코드 입력
        AdminUI->>AuthController: OTP 검증 요청
        AuthController->>OtpService: OTP 검증 요청
        OtpService->>DB: OTP 조회
        DB-->>OtpService: OTP 정보 반환
        
        alt OTP 유효
            OtpService->>OtpService: OTP 만료여부 확인
            OtpService->>DB: OTP 검증 완료 처리
            OtpService-->>AuthController: OTP 검증 성공
            AuthController-->>AdminUI: 비밀번호 재설정 화면 요청
            AdminUI->>Client: 새 비밀번호 입력 폼 제공
            
            %% 비밀번호 업데이트
            Client->>AdminUI: 새 비밀번호, 확인 비밀번호 입력
            AdminUI->>AuthController: 비밀번호 업데이트 요청
            AuthController->>AuthController: 비밀번호 일치 확인
            
            alt 비밀번호 일치
                AuthController->>AuthService: 비밀번호 업데이트 요청
                AuthService->>AuthService: 비밀번호 암호화
                AuthService->>DB: 비밀번호 업데이트
                DB-->>AuthService: 업데이트 성공
                AuthService-->>AuthController: 업데이트 성공 응답
                AuthController-->>AdminUI: 비밀번호 변경 성공
                AdminUI->>Client: 로그인 페이지로 리다이렉트
            else 비밀번호 불일치
                AuthController-->>AdminUI: 비밀번호 불일치 오류
                AdminUI->>Client: 오류 메시지 표시
            end
        else OTP 만료 또는 잘못됨
            OtpService-->>AuthController: OTP 검증 실패
            AuthController-->>AdminUI: OTP 검증 실패
            AdminUI->>Client: 오류 메시지 표시
        end
    else 이메일 없음
        DB-->>AuthService: 이메일 없음 응답
        AuthService-->>AuthController: 이메일 없음 오류
        AuthController-->>AdminUI: 이메일 없음 메시지
        AdminUI->>Client: 오류 메시지 표시
    end
    
    %% 관리자 대시보드 접근
    Note over Client, DB: 관리자 대시보드 기능 (DASH-001~DASH-013)
    
    Client->>AdminUI: 대시보드 접근 요청
    AdminUI->>AdminController: 대시보드 데이터 요청
    AdminController->>AdminService: 사용자 통계 요청
    AdminController->>BookService: 도서 통계 요청
    AdminController->>BranchService: 지점 통계 요청
    AdminController->>LoanService: 대출/반납 통계 요청
    LoanService->>DB: 연체자 목록 조회
    AdminService->>DB: 관리자 목록 조회
    BookService->>DB: 도서 총수 조회
    BranchService->>DB: 지점 목록 조회
    
    DB-->>AdminService: 사용자 통계 데이터 반환
    DB-->>BookService: 도서 통계 데이터 반환
    DB-->>BranchService: 지점 목록 데이터 반환
    DB-->>LoanService: 연체자 목록 데이터 반환
    
    AdminService-->>AdminController: 사용자 통계 반환
    BookService-->>AdminController: 도서 통계 반환
    BranchService-->>AdminController: 지점 통계 반환
    LoanService-->>AdminController: 대출/연체 통계 반환
    
    AdminController-->>AdminUI: 대시보드 데이터 제공
    AdminUI->>Client: 대시보드 화면 표시
    
    %% 관리자 대출자 대시보드
    Note over Client, DB: 관리자 대출자 대시보드 기능 (LOAN-001~LOAN-012)
    
    Client->>AdminUI: 대출자 대시보드 요청
    AdminUI->>LoanController: 대출 내역 요청
    LoanController->>LoanService: 대출 내역 조회 요청
    LoanService->>DB: 대출 내역 조회
    DB-->>LoanService: 대출 내역 반환
    LoanService-->>LoanController: 대출 내역 데이터 반환
    LoanController-->>AdminUI: 대출 내역 제공
    AdminUI->>Client: 대출 내역 목록 표시
    
    %% 연체자 필터링
    Client->>AdminUI: 연체자 탭 클릭
    AdminUI->>LoanController: 연체자 목록 요청
    LoanController->>LoanService: 연체자 목록 조회 요청
    LoanService->>DB: 연체자 목록 조회
    DB-->>LoanService: 연체자 목록 반환
    LoanService-->>LoanController: 연체자 목록 데이터 반환
    LoanController-->>AdminUI: 연체자 목록 제공
    AdminUI->>Client: 연체자 목록 표시
    
    %% 대출 상세 조회
    Client->>AdminUI: 특정 대출 상세 보기 요청
    AdminUI->>LoanController: 대출 상세 정보 요청
    LoanController->>LoanService: 대출 상세 정보 조회
    LoanService->>DB: 대출 도서 목록 조회
    DB-->>LoanService: 대출 도서 목록 반환
    LoanService-->>LoanController: 대출 상세 정보 반환
    LoanController-->>AdminUI: 대출 상세 정보 제공
    AdminUI->>Client: 대출 상세 정보 팝업 표시
    
    %% 관리자 도서관리 대시보드
    Note over Client, DB: 관리자 도서관리 기능 (BOOK-001~BOOK-023)
    
    Client->>AdminUI: 도서 관리 페이지 요청
    AdminUI->>BookController: 도서 목록 요청
    BookController->>BookService: 도서 목록 조회 요청
    BookService->>DB: 도서 목록 조회
    DB-->>BookService: 도서 목록 반환
    BookService-->>BookController: 도서 목록 데이터 반환
    BookController-->>AdminUI: 도서 목록 제공
    AdminUI->>Client: 도서 목록 표시
    
    %% 도서 등록
    Client->>AdminUI: 도서 추가 버튼 클릭
    AdminUI->>Client: 도서 등록 팝업 표시
    Client->>AdminUI: 도서 정보 입력
    AdminUI->>BookController: 도서 등록 요청
    BookController->>BookService: 도서 등록 요청
    BookService->>DB: 도서 중복 검사
    
    alt 중복 없음
        DB-->>BookService: 중복 없음 확인
        BookService->>DB: 도서 저장
        DB-->>BookService: 저장 성공
        BookService-->>BookController: 등록 성공 응답
        BookController-->>AdminUI: 등록 성공 알림
        AdminUI->>AdminUI: 도서 목록 갱신
        AdminUI->>Client: 성공 메시지 표시
    else 중복 있음
        DB-->>BookService: 중복 확인
        BookService-->>BookController: 중복 오류 응답
        BookController-->>AdminUI: 중복 오류 알림
        AdminUI->>Client: 오류 메시지 표시
    end
    
    %% 도서 수정
    Client->>AdminUI: 도서 수정 아이콘 클릭
    AdminUI->>BookController: 도서 정보 요청
    BookController->>BookService: 도서 조회 요청
    BookService->>DB: 도서 조회
    DB-->>BookService: 도서 정보 반환
    BookService-->>BookController: 도서 정보 반환
    BookController-->>AdminUI: 도서 정보 제공
    AdminUI->>Client: 도서 수정 팝업 표시
    
    Client->>AdminUI: 수정된 도서 정보 입력
    AdminUI->>BookController: 도서 수정 요청
    BookController->>BookService: 도서 수정 요청
    BookService->>DB: 도서 갱신
    DB-->>BookService: 갱신 성공
    BookService-->>BookController: 수정 성공 응답
    BookController-->>AdminUI: 수정 성공 알림
    AdminUI->>AdminUI: 도서 목록 갱신
    AdminUI->>Client: 성공 메시지 표시
    
    %% 도서 삭제
    Client->>AdminUI: 도서 삭제 아이콘 클릭
    AdminUI->>Client: 삭제 확인 팝업 표시
    Client->>AdminUI: 삭제 확인
    AdminUI->>BookController: 도서 삭제 요청
    BookController->>BookService: 도서 삭제 요청
    BookService->>DB: 도서 삭제
    DB-->>BookService: 삭제 성공
    BookService-->>BookController: 삭제 성공 응답
    BookController-->>AdminUI: 삭제 성공 알림
    AdminUI->>AdminUI: 도서 목록 갱신
    AdminUI->>Client: 성공 메시지 표시
    
    %% 관리자 지점 관리
    Note over Client, DB: 관리자 지점 관리 기능 (BRANCH-001~BRANCH-008)
    
    Client->>AdminUI: 지점 관리 페이지 요청
    AdminUI->>BranchController: 지점 목록 요청
    BranchController->>BranchService: 지점 목록 조회 요청
    BranchService->>DB: 지점 목록 조회
    DB-->>BranchService: 지점 목록 반환
    BranchService-->>BranchController: 지점 목록 데이터 반환
    BranchController-->>AdminUI: 지점 목록 제공
    AdminUI->>Client: 지점 목록 표시
    
    %% 지점 등록/수정/삭제 (도서와 유사한 흐름)
    
    %% 사용자 로그인/회원가입
    Note over Client, DB: 사용자 로그인/회원가입 기능 (USER-AUTH-001~USER-AUTH-009)
    
    %% 사용자 로그인
    Client->>UserUI: 사용자 로그인 페이지 요청
    UserUI->>Client: 로그인 폼 제공
    Client->>UserUI: 이메일, 비밀번호 입력
    UserUI->>AuthController: 로그인 요청
    AuthController->>AuthService: 인증 요청(이메일, 비밀번호)
    AuthService->>DB: 사용자 정보 조회
    DB-->>AuthService: 사용자 정보 반환
    AuthService->>AuthService: 비밀번호 검증
    
    alt 인증 성공
        AuthService-->>AuthController: 인증 성공 응답(JWT 토큰)
        AuthController-->>UserUI: 인증 성공, 토큰 및 사용자 정보 반환
        UserUI->>UserUI: 세션/토큰 저장
        UserUI->>Client: 사용자 대시보드로 리다이렉트
    else 인증 실패
        AuthService-->>AuthController: 인증 실패 응답(에러)
        AuthController-->>UserUI: 인증 실패 메시지
        UserUI->>Client: 오류 메시지 표시
    end
    
    %% 사용자 비밀번호 재설정 (관리자와 유사한 흐름)
    
    %% 사용자 대시보드
    Note over Client, DB: 사용자 대시보드 기능 (USER-DASH-001~USER-DASH-008)
    
    Client->>UserUI: 대시보드 접근 요청
    UserUI->>UserController: 대시보드 데이터 요청
    UserController->>UserService: 사용자 도서 이용 통계 요청
    UserService->>DB: 사용자 대출/반납 내역 조회
    DB-->>UserService: 대출/반납 데이터 반환
    UserService-->>UserController: 사용자 통계 반환
    UserController-->>UserUI: 대시보드 데이터 제공
    UserUI->>Client: 대시보드 화면 표시
    
    %% 사용자 도서 대출
    Note over Client, DB: 사용자 도서 대출 기능 (USER-BORROW-001~USER-BORROW-008)
    
    Client->>UserUI: 도서 검색 페이지 요청
    UserUI->>BookController: 대출 가능 도서 목록 요청
    BookController->>BookService: 대출 가능 도서 조회 요청
    BookService->>DB: 대출 가능 도서 조회
    DB-->>BookService: 도서 목록 반환
    BookService-->>BookController: 도서 목록 데이터 반환
    BookController-->>UserUI: 도서 목록 제공
    UserUI->>Client: 도서 목록 표시
    
    %% 도서 대출 처리
    Client->>UserUI: 도서 선택 후 대출 버튼 클릭
    UserUI->>LoanController: 도서 대출 요청
    LoanController->>LoanService: 도서 대출 처리 요청
    LoanService->>DB: 도서 상태 확인
    DB-->>LoanService: 도서 상태 반환
    
    alt 대출 가능
        LoanService->>DB: 대출 정보 저장
        LoanService->>DB: 도서 상태 업데이트
        DB-->>LoanService: 처리 성공
        LoanService-->>LoanController: 대출 성공 응답
        LoanController-->>UserUI: 대출 성공 알림
        UserUI->>Client: 성공 메시지 및 대출 내역 안내
    else 대출 불가
        LoanService-->>LoanController: 대출 불가 오류
        LoanController-->>UserUI: 대출 불가 알림
        UserUI->>Client: 오류 메시지 표시
    end
    
    %% 사용자 도서 반납
    Note over Client, DB: 사용자 도서 반납 기능 (USER-RETURN-001~USER-RETURN-007)
    
    Client->>UserUI: 대출 내역 페이지 요청
    UserUI->>LoanController: 현재 대출 중인 도서 목록 요청
    LoanController->>LoanService: 대출 중인 도서 조회 요청
    LoanService->>DB: 사용자의 대출 내역 조회
    DB-->>LoanService: 대출 내역 반환
    LoanService-->>LoanController: 대출 내역 데이터 반환
    LoanController-->>UserUI: 대출 내역 제공
    UserUI->>Client: 대출 내역 목록 표시
    
    %% 도서 반납 처리
    Client->>UserUI: 특정 대출 항목 반납 버튼 클릭
    UserUI->>LoanController: 도서 반납 요청
    LoanController->>LoanService: 도서 반납 처리 요청
    LoanService->>DB: 대출 상태 확인
    DB-->>LoanService: 대출 상태 반환
    
    alt 반납 가능
        LoanService->>DB: 반납 정보 업데이트
        LoanService->>DB: 도서 상태 업데이트
        DB-->>LoanService: 처리 성공
        LoanService-->>LoanController: 반납 성공 응답
        LoanController-->>UserUI: 반납 성공 알림
        UserUI->>Client: 성공 메시지 및 반납 확인
    else 반납 불가
        LoanService-->>LoanController: 반납 불가 오류
        LoanController-->>UserUI: 반납 불가 알림
        UserUI->>Client: 오류 메시지 표시
    end
    
    %% 사용자 반납 내역 조회
    Note over Client, DB: 사용자 도서 내역 조회 기능 (USER-HISTORY-001~USER-HISTORY-007)
    
    Client->>UserUI: 반납 내역 페이지 요청
    UserUI->>LoanController: 반납 내역 요청
    LoanController->>LoanService: 반납 내역 조회 요청
    LoanService->>DB: 사용자의 반납 내역 조회
    DB-->>LoanService: 반납 내역 반환
    LoanService-->>LoanController: 반납 내역 데이터 반환
    LoanController-->>UserUI: 반납 내역 제공
    UserUI->>Client: 반납 내역 목록 표시
    
    %% 반납 상세 조회
    Client->>UserUI: 특정 반납 건 상세 보기 요청
    UserUI->>LoanController: 반납 상세 정보 요청
    LoanController->>LoanService: 반납 상세 정보 조회
    LoanService->>DB: 반납 도서 목록 조회
    DB-->>LoanService: 반납 도서 목록 반환
    LoanService-->>LoanController: 반납 상세 정보 반환
    LoanController-->>UserUI: 반납 상세 정보 제공
    UserUI->>Client: 반납 상세 정보 팝업 표시


```
