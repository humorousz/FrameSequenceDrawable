# FrameSequenceDrawable
## 介绍
Google提供的可以播放WebP动画的Drawable,代码都是从源码中拷出来的，使用到的so库也是从网上找到的，为了方便，将代码单独建立了lib工程并且放到了jitpack上,写这个的目的是为了学习大牛的思想，了解了原理后可以通过替换FrameSequence来使用第三方解析引擎WebP资源，比如可以使用facebook的Fresco中用于解析webp的lib，[Google-FrameSequenceDrawable-相关源码](https://android.googlesource.com/platform/frameworks/ex/+/refs/heads/master/framesequence)

## 如何引入到工程
-  Add the JitPack repository to your build file
``` gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

- Add the dependency
 ``` gradle
	dependencies {
	        compile 'com.github.humorousz:FrameSequenceDrawable:1.0.1-SNAPSHOT'
	}

 ```
## 如何使用
- xml
``` xml
 <com.humrousz.sequence.view.AnimatedImageView
        android:id="@+id/google_sequence_image"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_above="@+id/group"
        app:loopCount="1"
        app:loopBehavior="loop_default|loop_finite|loop_inf"
        android:scaleType="centerCrop"
        android:src="@drawable/webpRes"/>
```
- java
``` java
public void setImage(){
    AnimatedImageView mGoogleImage;
    mGoogleImage = findViewById(R.id.google_sequence_image);
    //设置重复次数
    mGoogleImage.setLoopCount(1);
    //重复行为默认 根据webp图片循环次数决定
    mGoogleImage.setLoopDefault();
    //重复行为无限
    mGoogleImage.setLoopInf();
    //重复行为为指定  跟setLoopCount有关
    mGoogleImage.setLoopFinite();
    //设置Assets下的图片
    mGoogleImage.setImageResourceFromAssets("newyear.webp");
    //设置图片通过drawable
    mGoogleImage.setImageResource(R.drawable.newyear);
    Uri uri = Uri.parse("file:"+Environment.getExternalStorageDirectory().toString()+"/animation");
    //通过添加"file:"协议，可以展示指定路径的图片，如例子中的本地资源
    mGoogleImage.setImageURI(uri);
}
```
#### 当然你也可以不使用我这里的AnimatedImageView，AnimatedImageView是我参考其它的代码后修改封装的类，直接使用FrameSequenceDrawable+ImageView也是可以的，使用方法如下
``` java
 ImageView mImage;
 InputStream in = null;
 in = getResources().getAssets().open("anim.webp");
 final FrameSequenceDrawable drawable = new FrameSequenceDrawable(in);
 drawable.setLoopCount(1);
 drawable.setLoopBehavior(FrameSequenceDrawable.LOOP_FINITE);
 drawable.setOnFinishedListener(new FrameSequenceDrawable.OnFinishedListener() {
     @Override
     public void onFinished(FrameSequenceDrawable frameSequenceDrawable) {

     }
 });
 mImage.setImageDrawable(drawable);
```
