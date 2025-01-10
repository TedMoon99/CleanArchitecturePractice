# Clean Architecture for Android

Android의 Layer(계층)는 크게 3가지로 나눌 수 있다.
- Presentation Layer
- Domain Layer
- Data Layer

## Presentation Layer

- View : 직접적으로 플랫폼 의존적인 구현, 즉 **UI 화면표시**와 **사용자 입력**을 담당 / 단순하게 프레젠터가 명령하는 일만 수행
- Presenter : MVVM의 ViewModel과 같이 사용자 입력이 들어왔을 때 어떤 반응을 해야 하는지에 대한 판단을 하는 영역 / 무엇을 그려야 할지도 알고 있는 영역

## Domain Layer
- UseCase : 비즈니스 로직이 들어 있는 영역
- Entity(model) : 앱의 실질적인 데이터

## Data Layer
- Repository : UseCase가 필요로 하는 데이터의 저장 및 수정 등의 기능을 제공하는 영역 / DataSource를 `interface`로 참조하여, **local DB**와 **Network 통신**을 자유롭게 할 수 있음
- Data Source : 실제 데이터의 입출력이 여기서 실행

## Data Flow
데이터의 흐름은 다음과 같다.

- 사용자의 인터렉션이 발생하면 이벤트는 위에서 아래로, 아래서 위로 흐른다. 사용자가 버튼을 클릭하면 **UI -> Presenter -> UseCase -> Entity -> Repository -> Data Source**로 이동한다
- Domain Layer의 Model이 Translater를 거쳐 Data Layer의 Entity가 된다. 반대로, Data Layer의 Entity가 Domain Layer의 Translater를 거쳐 Model이 된다
- 실제로 Domain Layer는 Data Layer를 참조하지 않는다. 이는 Repository에서 이루어지는 DIP(Dependency Inversion Principle) 때문이다.
- 간단히 말하면, interface로 만들고, Domain Layer에서 interface를 참조하면 된다

## 실제 코드로의 적용

이제 실제 코드로 클린 아키텍처의 주요 계층인 Presentation Layer, Domain Layer, Data Layer 프로젝트 구조를 구현해보자

### 1. Domain Module 생성

안드로이드 스튜디오에서는 각 계층을 별도의 모듈로 구성하여 의존성을 관리한다.
Domain Layer용 모듈을 생성할 때는 Android Framework나 Android Library에 의존하지 않는 Java or Kotlin Library를 선택한다

Domain Layer는 어떤 플랫폼에도 종속되지 않는 계층이기 때문에 Android Library로 생성하지 않고 **Java or Kotlin Library**로 생성한다

#### 1-1. Domain Layer에서 Java or Kotlin Library를 선택하는 이유

Domain Layer를 순수 Kotlin/Java 라이브러리로 구현하는 이유는 다음과 같다.
- 원인 : 비즈니스 로직을 플랫폼으로부터 독립적으로 유지
- 장점1. : 테스트 용이성 증가
- 장점2. : 코드의 재사용성, 유지보수성 향상
- 결과 : 비즈니스 규칙이 UI나 데이터 저장소의 구현 세부 사항으로부터 분리 & 애플리케이션의 다른 부분이 변경되어도 비즈니스 로직에 영향을 주지 않음

### 2. Data 모듈 생성

Data Layer에서는 데이터 저장소와의 상호작용을 담당한다. 안드로이드 플랫폼에 특화된 기능과 API를 사용하기 위해서 반드시 **Android Library**를 선택하여 모듈을 생성한다

#### 2-1. Data Layer에서 `Android Library`를 선택하는 이유

Data Layer를 Android Library로 생성하는 이유는 안드로이드 기능과 API를 사용하기 위함
Data Layer에서 주로 사용하는 안드로이드 기능과 API의 예시 : **로컬 데이터베이스 접근(Room, SQLite)**, **네트워크 통신(Retrofit2, OkHttp3)**, **백그라운드 작업(WorkManager, JobScheduler)**, **안드로이드의 Context 접근**
이러한 기능들은 순수 Kotlin/Java Library에서는 사용할 수 없기 때문에 데이터 관리와 관련된 작업을 효과적으로 수행하기 위해 Android Library가 필요하다

### 3. Presentation 모듈 생성

Presentation Layer도 Data Layer와 마찬가지로 Android Library를 사용하여 모듈을 생성해준다

### 4. Clean Architecture 계층별 의존성 설정

Clean Architecture에서 의존성은 하위 계층에서 상위 계층으로 향한다. 상위 계층은 하위 계층에 대해 알지 못하며, 하위 계층은 상위 계층의 interface를 통해 데이터를 주고받는다.

#### 4-1. Domain Layer 의존성 설정
Domain Layer는 애플리케이션의 비즈니스 로직을 담당하며, 다른 계층에 의존하지 않는다. 따라서 Domain Layer의 build.gradle.kts 파일에는 **다른 계층에 대한 의존성이 포함되지 않는다**

#### 4-2. Data Layer 의존성 설정
Data Layer는 네트워크 통신, 로컬 데이터베이스 접근 등 데이터를 처리하는 로직을 담당하며, Domain Layer에 정의된 interface를 구현한다. 따라서 Data Layer는 Domain Layer에 의존한다

#### 4-3. Presentation Layer 의존성 설정
Presentation Layer는 사용자에게 데이터를 표시하고, 사용자 입력을 처리한다.
Domain Layer에 정의된 UseCase를 사용하여 사용자와의 상호작용을 기반으로 비즈니스 로직을 실행한다.
Presentation Layer는 `사용자 인터페이스 처리`라는 단일 책임만 가져야 하므로 Presentation Layer에서 Data Layer에 의존하게 되면 SRP를 지킬 수 없다. 그러므로 Presentation Layer는 Domain Layer에만 의존한다
