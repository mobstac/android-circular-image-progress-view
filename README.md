# A circular progress view for android. Also allows to have an icon at the centre.


![Demo](/media/demo.gif)

## Add to your project

```groovy
compile 'com.mobstac.circularimageprogress:CircularImageProgressView:0.1.3'
```

[ ![Download](https://api.bintray.com/packages/mobstac/maven/CircularImageProgressView/images/download.svg) ](https://bintray.com/mobstac/maven/CircularImageProgressView/_latestVersion) 

## Usage

#### Add the view to XML

```xml
<com.mobstac.circularimageprogress.CircularImageProgressView
        android:id="@+id/circular_image_progress"
        android:layout_width="160dp"
        android:layout_height="160dp"
        app:image="@drawable/my_icon"
        app:max="100" />
```

## Configurable properties

#### In `xml`

```xml
<com.mobstac.circularimageprogress.CircularImageProgressView
        <!--Set max progress-->
        app:max="100"
        <!--Set current progress-->
        app:progress="40"
        <!--Set progress circle color-->
        app:progress_color="#432123"
        <!--Set progress background color-->
        app:progress_background_color="#212121"
        <!--Set image tint color, leave empty for no tint-->
        app:image_tint="#FFFFFF"
        <!--Set width of the progress circle, should be between 5 and 75-->
        app:progress_width="#FFFFFF"
        <!--Set icon resource-->
        app:image="R.drawable.my_icon" />
```


#### In `Java`


```java
CircularImageProgressView circularImageProgressView = (CircularImageProgressView) findViewById(R.id.circular_image_progress);
//Set progress circle width
circularImageProgressView.setCircleWidth(45);
//Set current progress
circularImageProgressView.setProgress(40);
//Set image tint
circularImageProgressView.setImageTint(Color.WHITE);
//Set image resource
circularImageProgressView.setImageResource(R.drawable.ic_action_name);
//Set max progress
circularImageProgressView.setMax(50);
//Get current progress
circularImageProgressView.getProgress();
//Get max progress
circularImageProgressView.getMax();
//Get circle width
circularImageProgressView.getCircleWidth();
//Clear image tint
circularImageProgressView.clearImageTint();
//Make image visible
circularImageProgressView.showImage();
//Make progress circle visible
circularImageProgressView.showProgress();
//Hide image
circularImageProgressView.hideImage();
//Hide progress circle
circularImageProgressView.hideProgress();

```
