# ChartIQ-Android-SDK

Native Android SDK for the [ChartIQ JavaScript library](https://documentation.chartiq.com).

<table>
  <tr>
    <td>:construction: <b>BETA Release</b>. Please visit us again starting April 15, 2021 to download the final release of this SDK and accompanying mobile application.</td>
  </tr>
</table>

The ChartIQ Android SDK supports a basic charting application. The SDK can be extended to support more elaborate implementations by adding code to invoke ChartIQ library functions directly or by creating a bridge file similar to *nativeSdkBridge.js* (in the *mobile/js* folder of your ChartIQ library).

Contact us at <support@chartiq.com> to request sample code and guidance on how to extend the SDK.

## Requirements

- Version 8.2.0 or later of the ChartIQ library

  Go to our <a href="https://cosaic.io/chartiq-sdk-library-download/" target="_blank">download site</a> to obtain a free 30-day trial version of the library, or send us an email at <info@cosaic.io>, and we'll send you an evaluation version.

- Android 8.1 Oreo (API level 27) or later

## Sample app

The [demo](https://github.com/ChartIQ/ChartIQ-Android-SDK/tree/main/demo) folder of this repository contains a sample app that was built using the SDK. Customize the app to quickly create your own Android charting application.

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
