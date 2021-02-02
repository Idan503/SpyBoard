# SpyBoard

>  ***NOTE: This application was made for educational purposes only and should not be installed on any phone.***
  

_SpyBoard_ is an Android application that was made as a final project of Mobile Security course.
</br>
This app contains fully functional English & Hebrew keyboards that can be used anywhere on the Android device while keeping track of all words and characters that are being typed using _SpyBoard_. 


## Screenshots
  <p float="left" align="middle" padding="25">
  <img src="/screenshots/screenshot_eng.png?raw=true" width="240" />
  <img src="/screenshots/screenshot_heb.png?raw=true" width="240" /> 
</p>

>_SpyBoard_ also supports light theme mode (determined by device settings) 

## Typed Data Flow
 
The following sequence diagram represents how SpyBoard typed data is being saved to device's storage using SharedPrefrences as well as to cloud using Firebase Firestore:
  

  <p float="left" align="middle" padding="0">
  <img src="/screenshots/seq_diagram.png?raw=true" width="340" />
</p>
</br>
</br>

> "Sensitive information" typed strings that are suspected to be username & password pairs are being additionally stored in a designated list. 
  ## License

```
Copyright 2021 Idan Koren-Israeli

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
   
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```