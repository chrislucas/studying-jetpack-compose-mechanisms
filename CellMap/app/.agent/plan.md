# Project Plan

CellMap: An Android app using Jetpack Compose to monitor cell towers.
Features:
1. Real-time monitoring of connected cell tower using TelephonyManager and CellInfo (LTE, 5G NR, WCDMA, GSM).
2. Display metrics: Cell ID, Signal Strength (dBm), Quality Level (0-4), MCC, MNC, LAC/TAC.
3. Map integration: Show the current tower's location on a map using OpenCellID API.
4. Dynamic Map: Show nearby towers as the user moves.
5. Permissions: Handle READ_PHONE_STATE, ACCESS_FINE_LOCATION, and ACCESS_COARSE_LOCATION.
6. UI: Modern Jetpack Compose UI, possibly using Material 3 and adaptive layouts.

Please provide the full project brief in Markdown format, including App Name, Features, Tech Stack, and UI Design description.

## Project Brief

# CellMap Project Brief

## Features
- **Live Cellular Telemetry**: Provides real-time monitoring of the connected cell tower, displaying signal strength (dBm), quality level (0-4), and network type (LTE, 5G NR, WCDMA, GSM).
- **Geospatial Tower Mapping**: Visualizes the current device location and the estimated position of the connected tower on an interactive map using the OpenCellID API.
- **Detailed Network Identity**: Displays essential technical identifiers including Cell ID, MCC, MNC, and LAC/TAC for network analysis.
- **Adaptive Dashboard**: A responsive Material 3 interface that dynamically adjusts its layout for optimized viewing on phones, tablets, and foldables.
- **Contextual Permission Management**: Gracefully handles mandatory system permissions for `READ_PHONE_STATE` and location access to ensure a seamless setup experience.

## High-Level Technical Stack
- **Kotlin**: Primary language for the application logic.
- **Jetpack Compose**: Declarative UI framework for building modern interfaces.
- **Jetpack Navigation 3**: State-driven navigation architecture for robust screen management.
- **Compose Material Adaptive**: Library used to implement adaptive, multi-pane layouts across different form factors.
- **Kotlin Coroutines & Flow**: For reactive handling of live telephony updates and asynchronous API requests.
- **Retrofit**: To facilitate communication with the OpenCellID REST API.
- **Google Play Services Location**: Provides precise positioning for the mapping features.

## Implementation Steps
**Total Duration:** 1h 39m 53s

### Task_1_Setup_Permissions_Telephony: Initialize the project with necessary dependencies (Retrofit, Compose Adaptive, Navigation 3, Google Maps), implement permission handling for READ_PHONE_STATE and ACCESS_FINE_LOCATION, and create a repository to fetch live telephony data using TelephonyManager.
- **Status:** COMPLETED
- **Updates:** Initialized project dependencies, updated manifest with permissions, implemented TelephonyRepository and MainViewModel to expose cell data via Flow. Built initial UI for cell metadata and signal strength.
- **Acceptance Criteria:**
  - Project builds with required libraries
  - Permissions are requested and handled gracefully
  - Telephony repository provides MCC, MNC, LAC/TAC, and signal strength via Flow
- **Duration:** 1h 7m 53s

### Task_2_OpenCellID_Mapping: Integrate OpenCellID API using Retrofit to fetch tower locations and implement an interactive map screen that displays both the user location and the connected cell tower.
- **Status:** COMPLETED
- **Updates:** Integrated OpenCellID API with Retrofit, implemented LocationRepository for user tracking, and created a MapScreen using Google Maps. Also implemented initial adaptive UI with ListDetailPaneScaffold and Navigation 3.
- **Acceptance Criteria:**
  - OpenCellID API integration works with API_KEY integration as a critical acceptance criteria
  - Map displays device location and tower position accurately
  - App fetches tower metadata correctly based on cell identity
- **Duration:** 8m 34s

### Task_3_Adaptive_Dashboard_Navigation: Develop the main dashboard UI using Compose Material 3 and Compose Material Adaptive for multi-pane layouts, and set up Jetpack Navigation 3 for screen management.
- **Status:** COMPLETED
- **Updates:** Refined dashboard UI with Material 3 cards and signal indicators. Implemented nearby towers feature on the map. Finalized adaptive layout and Navigation 3 integration. App now supports edge-to-edge and dynamic color.
- **Acceptance Criteria:**
  - Dashboard shows live signal telemetry and network identity
  - Layout adapts to phone, tablet, and foldable form factors
  - Navigation 3 correctly handles screen transitions
- **Duration:** 2m 58s

### Task_4_Run_Verify: Perform a final run of the application to ensure all features work together seamlessly and the UI meets the project requirements.
- **Status:** BLOCKED
- **Updates:** Verification failed: App crashes when navigating to the map due to a missing Google Maps API key in the AndroidManifest.xml. Triggering refinement loop.
- **Acceptance Criteria:**
  - Application is stable with no crashes
  - Alignment with user requirements confirmed
  - Critic_agent verifies UI stability and reports critical issues
  - All existing tests pass
  - Build passes
- **Duration:** 20m 28s

