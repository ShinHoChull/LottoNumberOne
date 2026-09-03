# Known Issues

수정 시 우선 확인할 알려진 버그·기술 부채 목록입니다.

## 버그

### 1. MixNumbersDialog — mixType 미적용 (높음)

**파일:** `app/src/main/java/com/squirrel/lottonumberone/ui/dialog/MixNumbersDialog.kt`

**증상:** "등록번호 섞기" / "등록번호 제외 후 섞기" 구분 없이 동일 데이터 표시

**원인:**
- `mixType`이 `"exclude"`로 하드코딩됨
- `arguments?.getString("mixType")` 반환값을 변수에 할당하지 않음
- include/exclude 모두 `includingMixNumberArray`만 사용

**수정 방향:**
```kotlin
val mixType = arguments?.getString("mixType") ?: "exclude"
val data = when (mixType) {
    "include" -> viewModel.includingMixNumberArray.value.orEmpty()
    else -> viewModel.excludingMixNumberArray.value.orEmpty()
}
```

---

### 2. MainListAdapter — itemCount / + 버튼 인덱스 (중간)

**파일:** `app/src/main/java/com/squirrel/lottonumberone/ui/home/adapter/MainListAdapter.kt`

**증상:** 데이터가 1개 이상일 때 position 0의 + 버튼 ViewType과 itemCount 불일치 가능

**현재:**
- `getItemViewType(0)` → `ZERO_CONTENT` (+ 버튼)
- `getItemCount()` → `lottoArray.size` (데이터 있을 때 +1 없음)

**수정 방향:** itemCount를 `lottoArray.size + 1`로, bind 시 position 보정

---

### 3. setExcludingMixNumber — null 안전 (중간)

**파일:** `HomeViewModel.kt`

**증상:** 등록 번호 없을 때 `_numberArray.value!!` NPE 가능

**수정 방향:** `setIncludingMixNumber()`와 동일하게 null/empty 체크 추가

---

### 4. MainListAdapter.setLottoRow — 중복/빈 행 (낮음)

**파일:** `MainListAdapter.kt`

**증상:** `lottoArray`가 비어 있을 때 빈 리스트를 먼저 add한 뒤 실제 row add

**수정 방향:** 빈 리스트 선행 add 제거, `notifyItemInserted` 인덱스 정정

---

## 경고 / 코드 품질

| 항목 | 파일 | 내용 |
|------|------|------|
| 미사용 import | `HomeViewModel.kt` | `android.devicelock.DeviceId` |
| 미사용 import | `MainActivity.kt` | `Gson`, `Defines` |
| 미사용 import | `MixNumbersDialog.kt` | `Defines`, `viewModels` 등 |
| 불필요 null 체크 | `NumberCheckFragment`, `NumberAdapter` | LiveData는 init에서 초기화됨 |
| Java 8 target | `app/build.gradle` | JDK 21 빌드 시 deprecation 경고 |

---

## 환경

### Java 버전

- 시스템 기본 Java 8 → **빌드 실패**
- Android Studio JBR (Java 21) 사용 필요

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug
```

### 에뮬레이터

- AVD 부팅에 2분 이상 소요될 수 있음
- `adb devices`에 `device` 상태 확인 후 install/run

---

## 수정하지 말 것 (의도된 범위)

- DB/네트워크 레이어 추가 (요청 없이)
- Navigation Component / Hilt 도입 (요청 없이)
- 섞기 알고리즘을 공식 로또 추첨 방식으로 변경 (현재는 단순 랜덤 스왑)
