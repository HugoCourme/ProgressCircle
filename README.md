# ProgressCircle
ProgressCircle is a small Android library providing a custom component to use instead of the classic progress bar.

*Project realized in feb. 2016 during my training as engineer*

## Feature
* Circular progress with percent text
* Fully customizable

<p align="center">
  <img src="https://github.com/HugoCourme/ProgressCircle/blob/master/screenshot/layout-2016-04-24-145217.png" width="350"/>
  <img src="https://github.com/HugoCourme/ProgressCircle/blob/master/screenshot/layout-2016-04-24-145810.png" width="350"/>
  <img src="https://github.com/HugoCourme/ProgressCircle/blob/master/screenshot/layout-2016-04-24-145615.png" width="350"/>
  <img src="https://github.com/HugoCourme/ProgressCircle/blob/master/screenshot/layout-2016-04-24-145908.png" width="350"/>
</p>

## How to use
Add the specific repository to your build file :
```
repositories {
    maven {
        url "https://jitpack.io"
    }
}
```
Add the dependency in your build file :
```
	dependencies {
	        compile 'com.github.hugocourme:progresscircle:-SNAPSHOT'
	}
```
## How to customize
Here all the attribute you can use to customize your component
```xml
<attr name="progress_text_color" format="color"/>
        <attr name="progress_text" format="enum">
            <enum name="None" value="0"/>
            <enum name="Small" value="1"/>
            <enum name="Medium" value="2"/>
            <enum name="Big" value="3"/>
        </attr>
        //Border related attribute
        <attr name="progress_border_color" format="color"/>
        <attr name="progress_with_border" format="boolean"/>
        <attr name="progress_border_height" format="dimension"/>
        //Progress bar related attribute
        <attr name="progress_reached_color" format="color"/>
        <attr name="progress_unreached_color" format="color"/>
        <attr name="progress_reached_bar_height" format="dimension"/>
        <attr name="progress_unreached_bar_height" format="dimension"/>
        //Other attribute
        <attr name="progress_animation_speed" format="integer"/>
        <attr name="progress_value" format="integer"/>
```
Don't forget to add the following namespace URI to your layout file
```
xmlns:ProgressCircle="http://schemas.android.com/apk/res-auto"
```
