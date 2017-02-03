# React Native Wrapper for Latest LinkedIn Mobile SDK for Sign-In / Auth and API Access.

React Native wrapper for the latest [LinkedIn SDK](https://developer.linkedin.com/docs/android-sdk).

For iOS, this library bridges [tonyli508/LinkedinSwift](https://github.com/tonyli508/LinkedinSwift).

For Google Sign-In SDK, check out [joonhocho/react-native-google-sign-in](https://github.com/joonhocho/react-native-google-sign-in).

## Getting started

See [Tested Environments](#tested-environments).

`$ react-native install react-native-linkedin-sdk`


## Android
Download the latest release of [LinkedIn's Mobile SDK for Android](https://developer.linkedin.com/downloads#androidsdk).

Move `li-android-sdk/linkedin-sdk` to `{YourApp}/android/linkedin-sdk`.


Follow instructions from [Official Guide](https://developer.linkedin.com/docs/android-sdk) to:
 - [Create an application on LinkedIn](https://www.linkedin.com/secure/developer?newapp=).
 - Generate a key hash value.
 - Configure package name and hash values on your [LinkedIn application settings](https://www.linkedin.com/developer/apps).

Modify the following files under your `{YourApp}/android`. Some of them should be automatically done by `react-native install`. If not, do it yourself:
 - Modify your `{YourApp}/android/settings.gradle`:
```
include ':linkedin-sdk'
include ':react-native-linkedin-sdk'
project(':react-native-linkedin-sdk').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-linkedin-sdk/android')
```

 - Modify your `{YourApp}/android/app/build.gradle`:
```
dependencies {
    compile project(':linkedin-sdk')
    compile project(':react-native-linkedin-sdk')
}
```

 - Modify your `{YourApp}/android/app/src/main/java/com/{YourApp}/MainApplication.java`:
```
import com.reactlibrary.linkedinsdk.RNLinkedInSessionManagerPackage; // Add this.

...in your class MainApplication...
        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new RNLinkedInSessionManagerPackage(), // Add this.
                    ...other packages...
            );
        }
```


## iOS

Follow instructions from [Official Guide](https://developer.linkedin.com/docs/ios-sdk) to:
 - [Create an application on LinkedIn](https://www.linkedin.com/secure/developer?newapp=), if haven't done it for Android already.
 - [Configure your app on LinkedIn](https://www.linkedin.com/developer/apps).
 - Configure your application's Info.plist.
 - Important! Do not add `Whitelisting LinkedIn Custom Schemes`. See the [reason](https://github.com/tonyli508/LinkedinSwift/issues/22).


Install [tonyli508/LinkedinSwift](https://github.com/tonyli508/LinkedinSwift):
 - Add `pod 'LinkedinSwift', '~> 1.6.6'` to your `{YourApp}/ios/Podfile`.
 - Run `pod install` under `{YourApp}/ios`.


Add to your `{YourApp}/ios/{YourApp}/AppDelegate.m`:
```
- (BOOL)application:(UIApplication *)application
            openURL:(NSURL *)url
  sourceApplication:(NSString *)sourceApplication
         annotation:(id)annotation {
  if ([LinkedinSwiftHelper shouldHandleUrl:url]) {
    return [LinkedinSwiftHelper application:application openURL:url sourceApplication:sourceApplication annotation:annotation];
  }

  // ... your code

  return YES;
}
```


Add to your `{YourApp}/ios/{YourApp}/AppDelegate.h`:
```
#import <LinkedinSwift/LSHeader.h>
```

Make sure you have a Swift Bridging Header for your project. Here's [how to create one](http://www.learnswiftonline.com/getting-started/adding-swift-bridging-header/), if you don't.
Add to your Swift Bridging Header, `{YourApp}-Bridging-Header.h`:
```
#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>
#import <React/RCTEventEmitter.h>
#import <LinkedInSwift/LSHeader.h>
```


Link `react-native-linkedin-sdk`
- Open up your project in xcode and right click the package.
- Click `Add files to '{YourApp}'`.
- Select to `{YourApp}/node_modules/react-native-linkedin-sdk/ios/RNLinkedInSessionManager`.
- Click 'Add'.
- Click your project in the navigator on the left and go to `Build Settings`.
- Search for `Header Search Paths`.
- Double click on the value column.
- Add `$(SRCROOT)/../node_modules/react-native-linkedin-sdk/ios/RNLinkedInSessionManager`.



## Usage
```javascript
import LinkedInSDK from 'react-native-linkedin-sdk';

// later in your code...
async yourMethod() {
  const token = await LinkedInSDK.signIn({
    // https://developer.linkedin.com/docs/oauth2

    // iOS (Required)
    // The "API Key" value generated when you registered your application.
    clientID: 'your_client_id',

    // iOS (Required)
    clientSecret: 'your_client_secret',

    // iOS (Required)
    // A unique string value of your choice that is hard to guess. Used to prevent CSRF.
    state: 'some_state_value',

    // iOS, Android (Required)
    scopes: [
      'r_basicprofile',
      'r_emailaddress',
      'w_share',
    ],

    // iOS (Required)
    // The URI your users will be sent back to after authorization.  This value must match one of the defined OAuth 2.0 Redirect URLs in your application configuration.
    // e.g. https://www.example.com/auth/linkedin
    redirectUri: 'your_oauth2_redirect_url',
  });

  const profile = await LinkedInSDK.getRequest('https://api.linkedin.com/v1/people/~?format=json');

  console.log(token, profile);
}
```


## Tested Environments

I only tested with the following environments:
 - Swift version 3.0.2 (swiftlang-800.0.63 clang-800.0.42.1) / Target: x86_64-apple-macosx10.9
 - Xcode Version 8.2.1 (8C1002)
 - Android Studio 2.2.3 / Build #AI-145.3537739, built on December 2, 2016 / JRE: 1.8.0_112-release-b05 x86_64 / JVM: OpenJDK 64-Bit Server VM by JetBrains s.r.o


## LICENSE
```
MIT License

Copyright (c) 2017 Joon Ho Cho

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
