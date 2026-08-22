# Unit Tests for FormAddressActivity.kt

This plan outlines the creation of instrumented tests for `FormAddressActivity` and its primary UI component `FormAddressScreen`. While the user requested "Unit Tests", Activity and Compose testing in Android typically requires an instrumented environment (`androidTest`) or Robolectric. Given the current project setup, we will use `androidTest`.

## Proposed Changes

### [app]

#### [NEW] [FormAddressScreenTest.kt](file:///Users/christoffer/Documents/android-interviews/studying-jetpack-compose-mechanisms/HandlingActivityForResultComposable/app/src/androidTest/java/com/br/handlingactivityforresultcomposable/feature/formaddress/FormAddressScreenTest.kt)
Tests the `FormAddressScreen` composable in isolation:
- Verifies that text fields exist.
- Verifies that validation works (button click triggers `onValidAddress` only when form is valid).
- Verifies that invalid inputs don't trigger the callback.

#### [NEW] [FormAddressActivityTest.kt](file:///Users/christoffer/Documents/android-interviews/studying-jetpack-compose-mechanisms/HandlingActivityForResultComposable/app/src/androidTest/java/com/br/handlingactivityforresultcomposable/feature/formaddress/FormAddressActivityTest.kt)
Tests the `FormAddressActivity` integration:
- Uses `ActivityScenario` to launch the activity.
- Verifies that when a valid address is entered and the button is clicked, the activity sets the correct result (`RESULT_OK`) and includes the serialised `Address` in the intent extras.

## Verification Plan

### Automated Tests
- Run the newly created instrumented tests using:
  ```bash
  ./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.br.handlingactivityforresultcomposable.feature.formaddress.FormAddressScreenTest
  ./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.br.handlingactivityforresultcomposable.feature.formaddress.FormAddressActivityTest
  ```
