[![Build Status](https://travis-ci.com/xyz-fly/HttpJsonLogging.svg?branch=main)](https://travis-ci.com/xyz-fly/HttpJsonLogging)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.xyz-fly/HttpJsonLogging/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.xyz-fly/HttpJsonLogging)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![MinSdk](https://img.shields.io/badge/%20MinSdk%20-%2021%2B%20-f0ad4e.svg)](https://android-arsenal.com/api?level=21)

# HttpJsonLogging
A json logging works on okhttp3

# Download
So just add the dependency to your project build.gradle file:
```groovy
dependencies {
    implementation 'com.github.xyz-fly:HttpJsonLogging:1.0'
}
```

## Add HttpJsonLoggingInterceptor
```kotlin
val build = OkHttpClient.Builder()
...
// add HttpJsonLoggingInterceptor
build.addInterceptor(HttpJsonLoggingInterceptor())
...
```

# License

    Copyright 2021 xyz-fly

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
