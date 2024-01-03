# Changelog for ChartIQ SDK

All notable changes to "ChartIQ SDK" will be documented in this file.

## [3.3.1] - Jan 4, 2024

Requires ChartIQ version 8.8.0 or later
### Fixed

- Crosshair functionality on/off issues


## [3.3.0] - Dec 20, 2023

Requires ChartIQ version 8.8.0 or later

### Changed

- Made updates to ```ChartIQHandler.kt``` related to Gson and study inputs, outputs, and parameters. These changes are
  aimed at improving the handling and processing of study data in the SDK.
- Added new drawing tools, including the Measurement Line (```DrawingTool.MEASUREMENTLINE```) and Trend Line (
  ```DrawingTool.TREND_LINE```), to enhance the drawing capabilities of the SDK.
- Enhancements on existing features, and introduce new functionalities in the SDK and demo app.

## [3.2.0] - Sept 12, 2023

Requires ChartIQ version 8.8.0 or later

### Changed

- Introduction of the method ```setDrawingParameter(parameterName: Any, value: Any)``` to allow setting drawing
  parameters with diverse types, replacing the earlier method.

## [3.1.0] - Oct 18, 2022

Requires ChartIQ version 8.8.0 or later

### Fixed

- Various other bug fixes

### Changed

- Update study scripts to use inputs, outputs, parameters when adding a study.
- Update language codes to valid IANA format.
- Upgrade Settings view in order to stop app crashing.

---

## [3.0.0] - Oct 06, 2022

Requires ChartIQ version 8.2.1 or later
---

## [2.0.0] - Apr 29, 2021

Requires ChartIQ version 8.2.1 or later

---

## [1.0.0] - Apr 29, 2021

Requires ChartIQ version 8.2.0


---

