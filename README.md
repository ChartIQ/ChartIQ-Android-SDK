# ChartIQ-Android-SDK

Native Android SDK for the [ChartIQ JavaScript library](https://documentation.chartiq.com).

The ChartIQ Android SDK supports a basic charting application. The SDK can be extended to support more elaborate implementations by adding code to invoke ChartIQ library functions directly or by creating a bridge file similar to *nativeSdkBridge.js* (in the *mobile/js* folder of your ChartIQ library).

Contact us at <support@chartiq.com> to request sample code and guidance on how to extend the SDK.

## Requirements

- Version 9.0.1 or later of the ChartIQ library

  For previous version support, please refer to the [Releases](https://github.com/ChartIQ/ChartIQ-Android-SDK/releases) section.

  To obtain an evaluation version of the ChartIQ library, contact your account manager or visit our <a href="https://pages.marketintelligence.spglobal.com/ChartIQ-Follow-up-Request.html" target="_blank">Request Follow Up site</a> to get in contact with us!

- Android 8.1 Oreo (API level 27) or later

## Installation

### Gradle

Add the following dependency to your `build.gradle` file:

```gradle
implementation 'io.github.chartiq:sdk:3.1.0'
```

## App

The [demo](https://github.com/ChartIQ/ChartIQ-Android-SDK/tree/main/demo) folder of this repository contains an app that was built using the SDK. Customize the app to quickly create your own Android charting application.

The app is also available on Google Play.

**App screen shots**

<table>
  <tr>
    <td><img src="https://github.com/ChartIQ/ChartIQ-Android-SDK/blob/main/screenshots/Candle_Chart.png?raw=true" alt="Candle chart" width="200" height="440"/></td>
    <td><img src="https://github.com/ChartIQ/ChartIQ-Android-SDK/blob/main/screenshots/Chart_with_Studies.png?raw=true" alt="Chart with studies" width="200" height="440"/></td>
    <td><img src="https://github.com/ChartIQ/ChartIQ-Android-SDK/blob/main/screenshots/Chart_Styles_and_Types.png?raw=true" alt="Chart styles and types" width="200" height="440"/></td>
  </tr>
</table>

**End of legacy app support**

The Android app has been totally reengineered for improved usability and functionality using Kotlin. As a result, there is no upgrade path from the [legacy app](https://github.com/ChartIQ/Charting-Library---Android-Sample-App-Legacy) (which is compatible with ChartIQ versions 7.0.5&ndash;7.5.0) to the new app/SDK. To take advantage of the major improvements offered by the new mobile app, upgrade to Version 8.2.0 or later of the library and reimplement any custom functionality from your legacy app in the new app.

**End of ChartIQ feature upgrades**
As of ChartIQ version 8.8.0, only the `single_page_demo` demo will be updated with the latest ChartIQ features. `tabs_demo` will still be supported but not have the [latest](https://documentation.chartiq.com/tutorial-Changelog_Notices.html) released features integrated into the demo.

## Getting started

See the [Getting Started on Mobile: Android](https://documentation.chartiq.com/tutorial-Starting%20on%20Android.html) tutorial for instructions on installing the app and using the SDK.

## Customization

For information on changing the appearance of the app, see our [customization](https://documentation.chartiq.com/tutorial-Mobile%20App%20Customization%20Android.html) tutorial.

## API documentation

- [App/SDK](https://documentation.chartiq.com/android-sdk/chartiq/)

- [ChartIQ JavaScript library](https://documentation.chartiq.com)

## Questions and support

Contact our development support team at <support@chartiq.com>.

## Contributing to this project

Contribute to this project. Fork it and send us a pull request. We'd love to see what you can do with our charting tools on Android!

## License

This project is licensed under the Apache-2.0 License - see the LICENSE.md file for details.



