# LottoNumberOne Architecture

## 개요

| 항목 | 값 |
|------|-----|
| 패키지 | `com.squirrel.lottonumberone` |
| 모듈 | 단일 `:app` |
| 패턴 | MVVM (LiveData + ViewModel) |
| UI | ViewBinding, RecyclerView, BottomSheet, DialogFragment |
| 저장소 | 없음 (메모리 only, 앱 종료 시 초기화) |

## 패키지 구조

```
app/src/main/java/com/squirrel/lottonumberone/
├── base/
│   ├── BaseActivity.kt
│   └── BaseBottomSheetDialogFragment.kt
├── config/
│   ├── Defines.kt          # DEBUG, log()
│   └── Etc.kt              # RecyclerView ViewType
├── utils/
│   ├── SingleEvent.kt
│   ├── SingleLiveEvent.kt
│   └── ViewExt.kt          # Toast, Snackbar, EditText 확장
└── ui/
    ├── home/
    │   ├── MainActivity.kt
    │   ├── HomeViewModel.kt
    │   └── adapter/MainListAdapter.kt
    ├── number_select/
    │   ├── NumberCheckFragment.kt
    │   ├── NumberViewModel.kt
    │   └── adapter/NumberAdapter.kt
    └── dialog/
        ├── MixNumbersDialog.kt
        └── adapter/MixNumberAdapter.kt
```

## 화면 흐름

```
MainActivity
  │
  ├─ [+] 버튼 → NumberCheckFragment (BottomSheet)
  │                └─ 6개 선택 → HomeViewModel.setNumberArray()
  │                └─ MainListAdapter에 행 추가
  │
  ├─ [등록번호 섞기] → HomeViewModel.setIncludingMixNumber()
  │                      └─ MixNumbersDialog (include)
  │
  └─ [등록번호 제외 후 섞기] → HomeViewModel.setExcludingMixNumber()
                                 └─ MixNumbersDialog (exclude)
```

## ViewModel 책임

### HomeViewModel

| LiveData | 설명 |
|----------|------|
| `numberArray` | 등록된 로또 세트 목록 `List<List<Int>>` |
| `includingMixNumberArray` | 등록번호 포함 섞기 결과 (6세트) |
| `excludingMixNumberArray` | 등록번호 제외 섞기 결과 (6세트) |

**섞기 알고리즘**

1. `mixMachine(list, mixCount)`: 리스트 정렬 후 랜덤 스왑 반복
2. `pushMixNumber(mixNumbers)`: 6번 반복해 6세트 × 6개 번호 생성 (각 세트 오름차순)

### NumberViewModel

- Fragment scope, 선택 중인 6개 번호만 관리
- `setNumberArray(num: Int)`: 번호 추가

## Base 클래스

### BaseActivity

생명주기: `onCreate` → `setUpInit()` → `observeViewModel()` → `setUpListener()`

### BaseBottomSheetDialogFragment

- 전체 화면 높이로 확장 (`STATE_EXPANDED`)
- `setUpInit()`, `observeViewModel()` 훅 제공

## 레이아웃

| 파일 | 용도 |
|------|------|
| `activity_main.xml` | 번호 목록 RecyclerView + 섞기 버튼 2개 |
| `fragment_number_check.xml` | 1~45 번호 그리드 + 저장 버튼 |
| `item_main_list.xml` | 등록된 번호 6개 표시 |
| `item_main_list_plus_button.xml` | + 버튼 행 |
| `dialog_mix_numbers.xml` | 섞기 결과 Dialog |
| `item_lotto_row.xml` | 섞기 결과 한 줄 (동적 TextView) |

## 의존성

- AndroidX: core-ktx, appcompat, activity, fragment-ktx, constraintlayout
- Material: BottomSheet, Snackbar
- Gson: import만 존재, **미사용**

## 확장 시 참고 (현재 미구현)

- 데이터 영속화: Room 또는 DataStore
- 화면 전환: Navigation Component
- DI: Hilt
- 비동기: Coroutines + StateFlow
