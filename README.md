
[![Old chat https://gitter.im/AppKillerManager](https://badges.gitter.im/AppKillerManager.svg)](https://gitter.im/AppKillerManager?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0.html)
[ ![Download](https://api.bintray.com/packages/indrih17/app-killer-manager/app-killer-manager/images/download.svg?version=0.1.0) ](https://bintray.com/indrih17/app-killer-manager/app-killer-manager/0.1.0/link)
# AppKillerManager

[Telegram chat](https://github.com/indrih17/AppKillerManager)

Android library to handle App killer manager, agressive power saving mode or battery optimization (Xiaomi, Huawei, letv, ...) and prevent from : not showing notification, services not start at boot, etc

This library will open the right settings of the user phone and prompt him to add your app to whitelist.

Android Custom Roms made sometimes your apps unfunctional due to :

* Your App is killed when it's not in foreground
* Notification message do not appear
* Your services is killed by agressive power saving mode

###If you want to help me do not hesitate to test on your phone and add issue if somethings not work properly

### Please if you have a ZTE, Meizu, Oppo, OnePlus, HTC, Letv test this library to help me, Thanks a lot ! =)

## Current Compatibility :

* Samsung (<span style="color:green">TESTED</span>)
* Huawei (<span style="color:green">TESTED</span>)
* Xiaomi (<span style="color:green">TESTED</span>)
* Meizu (<span style="color:red">TESTED</span>)
* OnePlus (<span style="color:red">TESTED</span>)
* Letv (<span style="color:red">NOT TESTED</span>)
* HTC (<span style="color:red">NOT TESTED</span>)
* Asus (<span style="color:green">TESTED</span>)
* ZTE (<span style="color:red">NOT TESTED</span>)
* VIVO (<span style="color:red">NOT TESTED</span>)
* OPPO (<span style="color:red">NOT TESTED</span>)
* Lenovo (<span style="color:green">TESTED</span>)

### TODO

* Add Tests auto with avd and intent testing
* Add hability to customize dialog
* Add "Tutorial Activity type"
* Add screenshot and "settings path" of the intent killerManagerAction for all phones on ReadMe
* Add a table of possiblities and function unavailable/ not necessary on each device

## Usage
### Step 1

##### Add it on your Android app

```groovy
implementation 'indrih17.app-killer-manager:app-killer-manager:0.1.0'
```

### Step 2

Introduce in your app. [Example](https://github.com/indrih17/AppKillerManager/blob/develop_v4.0/app/src/main/java/com/thelittlefireman/appkillermanager_exemple/MainActivity.kt)

## Maintainers
[thelittlefireman](https://github.com/thelittlefireman)
[Indrih](https://github.com/indrih17)

## TODO :
  - Test on all devices
  - Add differents settings for autostartservice/notifications/permissions
  - Add custom messages for more explaination on what user need to do on manufacturer "settings Activity"
  - Add custom image for each device and each languages
## DEBUG/HELPING INFORMATIONS :

###Get the current activity name :

```shell
$> adb shell
$> dumpsys activity activities | grep mFocusedActivity
or to get more result
$> dumpsys activity activities | grep Activity
```

###Get all permissions
```shell
$> adb shell pm list permissions
```

###Start an activity :

```shell
$> adb shell
$> #by component name
$> am start -n com.samsung.memorymanager/com.samsung.memorymanager.RamActivity  --user 0
$> #by killerManagerAction
$> am start -a com.example.Action --user 0
```
more information http://imsardine.simplbug.com/note/android/adb/commands/am-start.html

### Phone tested :
(EasyMode) = Go directly to package (app) ?

PHONE | ANDROID OS | CUSTOM ROM
--- | --- | --- 
Huawei HONOR 4X | Android 4.4 | EMUI 3.0.1
Huawei P9 LITE | Android 6.0 | EMUI 4.1 
Samsung S7 edge | Android 7.0 |
Samsung Xcover | Android 5.1 |
Samsung S4 mini | Android 4.4 |
Xiaomi Mi mix | Android 6.0.1 | MIUI 8.0

## THANKS TO:
Sylvain BORELLI

[dirkam](https://github.com/dirkam)

[henrichg](https://github.com/henrichg)

## SOURCES/HELPING TOOLS :
[backgroundable-android](https://github.com/dirkam/backgroundable-android)

[TamingTask](https://github.com/YougaKing/TamingTask)

[CRomAppWhitelist](https://github.com/WanghongLin/CRomAppWhitelist)

[permission](https://github.com/by123/permission)

[AndroidPopWinPermission](https://programtalk.com/vs/?source=AndroidPopWinPermission/permssion/src/main/java/io/github/bunnbylue/permssion/)