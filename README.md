# Squads 

#### A simple android project for DevOps (HoGent)

#### To avoid flakiness, we highly recommend that you turn off system animations on the virtual or physical devices used for testing. On your device, under Settings > Developer options, disable the following 3 settings:
#### Window animation scale Transition animation scale Animator duration scale

### Build With 🏗️
- [Kotlin] - Programming language for Android
- [Hilt-Dagger] - Standard library to incorporate Dagger dependency injection into an Android application.
- [Retrofit] -  A type-safe HTTP client for Android and Java.
- [Room] - SQLite object mapping library.
- [LiveData] - Data objects that notify views when the underlying database changes.
- [ViewModel] - Stores UI-related data that isn't destroyed on UI changes.
- [ViewBinding] - Generates a binding class for each XML layout file present in that module and allows you to more easily write code that interacts with views.
- [Jetpack Navigation] - Navigation refers to the interactions that allow users to navigate across, into, and back out from the different pieces of content within your app
- [Picasso] - An image loading and caching library for Android focused on smooth scrolling
- [Espresso] - Use Espresso to write concise, beautiful, and reliable Android UI tests.
- [Timber] - This is a logger with a small, extensible API which provides utility on top of Android's normal Log class.
- [Material] - Material Components for Android (MDC-Android) help developers execute Material Design. 

  [ViewModel]: <https://developer.android.com/topic/libraries/architecture/viewmodel>
  [Jetpack Navigation]: <https://developer.android.com/guide/navigation/>
  [Hilt-Dagger]: <https://dagger.dev/hilt/>
  [DataStore]: <https://developer.android.com/topic/libraries/architecture/datastore>
  [ViewBinding]: <https://developer.android.com/topic/libraries/view-binding>
  [LiveData]: <https://developer.android.com/topic/libraries/architecture/livedata/>
  [Retrofit]: <https://square.github.io/retrofit/>
  [ViewModel]: <https://developer.android.com/topic/libraries/architecture/viewmodel>
  [Picasso]: <https://square.github.io/picasso>
  [Kotlin]: <https://kotlinlang.org>
  [Coroutines]: <https://kotlinlang.org/docs/coroutines-overview.html>
  [MVVM (Model View View-Model)]: <https://developer.android.com/jetpack/guide#recommended-app-arch>
  [News Api]: <https://newsapi.org/>
  [Room]: <https://developer.android.com/training/data-storage/room/>
  [Espresso]: <https://developer.android.com/training/testing/espresso>
  [Timber]: <https://github.com/JakeWharton/timber#download>
  [Material]: <https://github.com/material-components/material-components-android>
  


### Project Architecture 🗼

This app uses [MVVM (Model View View-Model)] architecture.

![alt text](https://github.com/KadirKuruca/NewsApp-MVVM-Hilt-Room-Retrofit/blob/master/mvvm_architecture.png?raw=true)

#### The app has following packages:
1. **model**: business logic and data: It contains all the data accessing and manipulating components.
2. **di**: Dependency providing classes using Dagger.
3. **view**: presentation: View classes.
4. **viewmodel**: presentation logic: ViewModels to store and manage the data so it is ready to be presented.
5. **utils**: Utility classes.

### Testing creditentials:
1. **username**: user@mail.com
2. **password**: password
