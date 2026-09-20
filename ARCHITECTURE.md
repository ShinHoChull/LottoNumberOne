# LottoNumberOne Architecture

## 개요

| 항목 | 값 |
|------|-----|
| 패키지 | `com.squirrel.lottonumberone` |
| 모듈 | 단일 `:app` |
| 패턴 | MVVM (LiveData + ViewModel) |
| UI | ViewBinding, RecyclerView, BottomSheet, DialogFragment |
| 카메라 / OCR | CameraX 1.4.1, ML Kit Text Recognition 16.0.1 |
| 저장소 | 메모리 기반 (앱 종료 시 초기화) |

---

## 패키지 구조

```
app/src/main/java/com/squirrel/lottonumberone/
├── base/
│   ├── BaseActivity.kt                       # 기본 Activity 템플릿
│   └── BaseBottomSheetDialogFragment.kt      # 기본 BottomSheet 템플릿
├── config/
│   ├── Defines.kt                            # 디버그 로그 설정
│   └── Etc.kt                                # RecyclerView ViewType 구분
├── utils/
│   ├── LottoNumberParser.kt                  # ML Kit OCR 텍스트에서 로또 번호(6개) 파싱
│   ├── SingleEvent.kt                        # 일회성 이벤트 Wrapper
│   ├── SingleLiveEvent.kt                    # SingleLiveEvent 구현
│   └── ViewExt.kt                            # UI 확장함수 (Toast, Snackbar, EditText 등)
└── ui/
    ├── home/
    │   ├── MainActivity.kt                   # 메인 화면 (번호 목록 및 섞기/스캔 실행)
    │   ├── HomeViewModel.kt                  # 메인 비즈니스 로직 및 등록/섞기 관리
    │   └── adapter/MainListAdapter.kt        # 등록 번호 목록 + [+] 추가 버튼 어댑터
    ├── number_select/
    │   ├── NumberCheckFragment.kt            # 직접 6개 번호 선택 (BottomSheet)
    │   ├── NumberViewModel.kt                # 선택 중인 번호 상태 관리
    │   └── adapter/NumberAdapter.kt          # 1~45 번호 선택 그리드 어댑터
    ├── scan/
    │   └── CameraScanActivity.kt             # CameraX + ML Kit 기반 카메라 촬영 및 번호 인식
    └── dialog/
        ├── MixNumbersDialog.kt               # 섞기 결과 표시 다이얼로그
        ├── ScanResultDialog.kt              # 카메라 스캔 결과 확인/재시도 다이얼로그
        └── adapter/MixNumberAdapter.kt       # 섞기/스캔 결과 행 표시 어댑터
```

---

## 화면 흐름 (UI & Feature Flow)

```
[MainActivity]
  │
  ├─ [+] 버튼 → [NumberCheckFragment] (BottomSheet)
  │                └─ 1~45 중 6개 번호 직접 선택 → HomeViewModel.setNumberArray()
  │                └─ MainListAdapter 목록에 행 추가
  │
  ├─ [카메라 스캔] → [CameraScanActivity]
  │                      └─ CameraX 촬영 + ML Kit OCR 인식
  │                      └─ LottoNumberParser.parse()
  │                      └─ [ScanResultDialog] 결과 확인
  │                      └─ 확인 시 HomeViewModel.setNumberArray() 호출 및 등록
  │
  ├─ [등록번호 섞기] → HomeViewModel.setIncludingMixNumber()
  │                      └─ [MixNumbersDialog] (등록 번호 포함 6세트 표시)
  │
  └─ [등록번호 제외 후 섞기] → HomeViewModel.setExcludingMixNumber()
                                 └─ [MixNumbersDialog] (등록 번호 제외 6세트 표시)
```

---

## ViewModel & 컴포넌트 역할

### 1. HomeViewModel
- **책임**: 등록된 로또 번호 세트 목록 관리 및 섞기 알고리즘 수행
- **주요 LiveData**:
  - `numberArray`: 등록된 로또 세트 목록 (`List<List<Int>>`)
  - `includingMixNumberArray`: 등록 번호를 포함하여 조합한 6세트 결과
  - `excludingMixNumberArray`: 등록 번호를 제외하고 조합한 6세트 결과
- **주요 알고리즘**:
  - `mixMachine(list, mixCount)`: 리스트 셔플/스왑 알고리즘
  - `pushMixNumber(mixNumbers)`: 1~45 범위에서 6개 번호 세트를 생성 (오름차순 정렬)

### 2. NumberViewModel
- **책임**: `NumberCheckFragment` 범위에서 직접 선택 중인 번호 상태 관리
- `setNumberArray(num: Int)`: 번호 추가/제거 및 6개 선택 제약 조건 처리

### 3. LottoNumberParser (Utility)
- **책임**: ML Kit Text Recognition으로 추출된 OCR 텍스트에서 1~45 범위의 중복 없는 6개 숫자 조합을 검색하고 파싱
- **동작**:
  - 연속된 숫자 패턴 분석
  - 1~45 범위 검증 및 중복 제거 후 정확히 6개 숫자가 일치하는 행을 반환

---

## Base 클래스

### BaseActivity
- Lifecycle Template: `onCreate` → `setUpInit()` → `observeViewModel()` → `setUpListener()` 구조 제공

### BaseBottomSheetDialogFragment
- BottomSheet의 디폴트 확장 상태(`STATE_EXPANDED`) 설정
- `setUpInit()`, `observeViewModel()` 등 라이프사이클 훅 제공

---

## 주요 화면 및 다이얼로그

| 화면/다이얼로그 | 역할 및 특징 |
|----------------|--------------|
| `MainActivity` | 등록된 로또 번호 리스트 출력, 번호 추가/카메라 스캔/번호 섞기 트리거 |
| `CameraScanActivity` | CameraX 프리뷰 및 사진 촬영, ML Kit Text Recognition 실행 |
| `ScanResultDialog` | 카메라 인식 결과 번호 6개를 확인하거나 재촬영 선택 |
| `NumberCheckFragment` | 1~45 그리드 버튼에서 사용자가 직접 번호 6개를 수동 선택 |
| `MixNumbersDialog` | 조합 알고리즘에 의해 생성된 로또 번호 세트들을 결과창으로 표시 |

---

## 주요 의존성 (Dependencies)

- **AndroidX & UI**: `core-ktx`, `appcompat`, `constraintlayout`, `activity`, `fragment-ktx`
- **Material Design**: `material` (BottomSheet, Snackbar 등)
- **CameraX**: `camera-core`, `camera-camera2`, `camera-lifecycle`, `camera-view` (1.4.1)
- **ML Kit**: `com.google.mlkit:text-recognition` (16.0.1)
- **JSON**: `gson` (2.10.1)

---

## 확장 및 개선 가능 항목 (Roadmap)

- **데이터 영속화**: Room 또는 DataStore를 통한 등록 번호/이력 저장
- **내비게이션**: Jetpack Navigation Component 적용
- **의존성 주입**: Hilt / Koin 적용
- **비동기 데이터 흐름**: Kotlin Coroutines + Flow/StateFlow 마이그레이션
