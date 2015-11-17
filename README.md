# DroidWifiScan

This is a project to scan all the wifi access points and present the evolution of their signal strength over time them in a graphical way. A web.py based REST server ([see rest_server](https://github.com/pcoder/DroidWifiScan/tree/master/rest_server)) that allows storing the access point (AP) details on a server using the REST API. In the absense of this server, the scanned AP details are stored on the device. They are uploaded once the server is reachable.

####[Download apk here.](https://github.com/pcoder/DroidWifiScan/blob/master/apks/app-release.apk?raw=true)


![Screenshot](https://raw.githubusercontent.com/pcoder/DroidWifiScan/master/screenshots/Screenshot_2015-11-16-00-16-13.png "Screenshot of the app")


The start screen of the app prompts a screen as shown in the screenshot below. It requires: (i) an application key for identification and authentification and (ii) the url of the rest server that can receive the regular updates about the signal strengths of the access points.

![Login](https://raw.githubusercontent.com/pcoder/DroidWifiScan/master/screenshots/Screenshot_2015-11-16-00-29-59.png)


### Android Permissions used in the app

	1. `android.permission.ACCESS_WIFI_STATE`
		For continuous scanning of various access points
	2. `android.permission.WRITE_EXTERNAL_STORAGE`
		For storing the wifi signal data locally in the absence of a 
		server or internet connectivity.
	3. `android.permission.ACCESS_NETWORK_STATE`
		To check whether network is accessible or not
	4. `android.permission.INTERNET`
		To post the WIFI signal data using the REST api


### Application Dependencies

1. `com.android.support:appcompat-v7:22.1.0`
2. `com.androidplot:androidplot-core:0.6.1` A graph plot library for android


### TODO

1. Currently, the evolution of the WIFI signals of various APs are stored in the internal storage of the android device. Need to extend this to store them on the external storage if available.
2. A settings activity, where various settings that are currently stored in [Constants.java](https://github.com/pcoder/DroidWifiScan/blob/master/app/src/main/java/pcoder/net/droidwifiscan/Constants.java) can be edited and persisted.
3. Write mockup Test cases.
4. Update UI
  1. Too many access points makes the graph clutter.
  2. Show corresponding legend of a graph on selection
  3. Highlight the graph that is currently selected
5. To test on different versions of android and on different devices.



