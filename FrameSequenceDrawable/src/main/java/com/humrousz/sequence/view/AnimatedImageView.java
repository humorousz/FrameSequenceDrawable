package com.humrousz.sequence.view;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.support.rastermill.FrameSequence;
import android.support.rastermill.FrameSequenceDrawable;
import android.support.rastermill.R;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author Created by zhangzhiquan on 2018/2/6.
 */


public class AnimatedImageView extends AppCompatImageView {
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";
    private static final List<String> SUPPORTED_RESOURCE_TYPE_NAMES = Arrays.asList("raw", "drawable", "mipmap");

    private int mLoopCount = 1;
    private int mLoopBehavior = FrameSequenceDrawable.LOOP_DEFAULT;
    private FrameSequenceDrawable mAnimatedSrcDrawable;
    private FrameSequenceDrawable mAnimatedBgDrawable;
    private OnFinishedListener mFinishedListener;
    private FrameSequenceDrawable.OnFinishedListener mDrawableFinishedListener;

    public interface OnFinishedListener {
        /**
         * Called when a AnimatedImageView has finished looping.
         *
         * Note that this is will not be called if the drawable is explicitly
         * stopped, or marked invisible.
         */
        void onFinished();
    }

    /**
     * A corresponding superclass constructor wrapper.
     *
     * @param context
     * @see ImageView#ImageView(Context)
     */
    public AnimatedImageView(Context context) {
        super(context);
    }

    /**
     * Like equivalent from superclass but also try to interpret src and background
     *
     * @param context
     * @param attrs
     * @see ImageView#ImageView(Context, AttributeSet)
     */
    public AnimatedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Like equivalent from superclass but also try to interpret src and background
     * attributes as Animateds.
     *
     * @param context
     * @param attrs
     * @param defStyle
     * @see ImageView#ImageView(Context, AttributeSet, int)
     */
    public AnimatedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mDrawableFinishedListener = new FrameSequenceDrawable.OnFinishedListener() {
            @Override
            public void onFinished(FrameSequenceDrawable drawable) {
                if(mFinishedListener != null) {
                    mFinishedListener.onFinished();
                }
            }
        };
        if(attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AnimatedImageView);
            mLoopCount = attributes.getInt(R.styleable.AnimatedImageView_loopCount, -1);
            if (mLoopCount != -1) {
                //set loop count so loop mode is LOOP_FINITE
                setLoopFinite();
            } else {
                //not set loop count so loop mode is set value default LOOP_DEFAULT
                mLoopBehavior = attributes.getInt(R.styleable.AnimatedImageView_loopBehavior, FrameSequenceDrawable.LOOP_DEFAULT);
            }
            attributes.recycle();

            int srcId = attrs.getAttributeResourceValue(ANDROID_NS, "src", 0);
            if(srcId > 0) {
                String srcTypeName = context.getResources().getResourceTypeName(srcId);
                if(SUPPORTED_RESOURCE_TYPE_NAMES.contains(srcTypeName)) {
                    if(!setAnimatedResource(true, srcId)) {
                        super.setImageResource(srcId);
                    }
                }
            }

            int bgId = attrs.getAttributeResourceValue(ANDROID_NS, "background", 0);
            if(bgId > 0) {
                String bgTypeName = context.getResources().getResourceTypeName(bgId);
                if(SUPPORTED_RESOURCE_TYPE_NAMES.contains(bgTypeName)) {
                    if(!setAnimatedResource(false, bgId)) {
                        super.setBackgroundResource(bgId);
                    }
                }
            }
        }
    }

    private boolean setAnimatedResource(boolean isSrc, int resId) {
        Resources res = getResources();
        if (res != null) {
            try {
                InputStream inputStream = getInputStreamByResource(res, resId);
                FrameSequenceDrawable drawable = new FrameSequenceDrawable(inputStream);
                drawable.setLoopCount(mLoopCount);
                drawable.setOnFinishedListener(mDrawableFinishedListener);
                if (isSrc) {
                    setImageDrawable(drawable);
                    if(mAnimatedSrcDrawable != null) {
                        mAnimatedSrcDrawable.destroy();
                    }
                    mAnimatedSrcDrawable = drawable;
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    setBackground(drawable);
                    if(mAnimatedBgDrawable != null) {
                        mAnimatedBgDrawable.destroy();
                    }
                    mAnimatedBgDrawable = drawable;
                } else {
                    setBackgroundDrawable(drawable);
                    if(mAnimatedBgDrawable != null) {
                        mAnimatedBgDrawable.destroy();
                    }
                    mAnimatedBgDrawable = drawable;
                }
                return true;
            } catch (Exception e) {
                //ignored
            }
        }
        return false;
    }

    /**
     * Sets the content of this AnimatedImageView to the specified Uri.
     * If uri destination is not a Animated then {@link ImageView#setImageURI(Uri)}
     * is called as fallback.
     * For supported URI schemes see: {@link ContentResolver#openAssetFileDescriptor(Uri, String)}.
     *
     * @param uri The Uri of an image
     */
    @Override
    public void setImageURI(Uri uri) {
        if (!setAnimatedImageUri(this, uri)) {
            super.setImageURI(uri);
        }
    }

    @Override
    public void setImageResource(int resId) {
        if (!setAnimatedResource(true, resId)) {
            super.setImageResource(resId);
        }
    }

    @Override
    public void setBackgroundResource(int resId) {
        if (!setAnimatedResource(false, resId)) {
            super.setBackgroundResource(resId);
        }
    }

    /**
     * Set the image from assets
     * @param path
     * @return
     */
    public boolean setImageResourceFromAssets(String path){
        AssetManager am = getContext().getResources().getAssets();
        try {
            InputStream inputStream = am.open(path);
            FrameSequenceDrawable drawable = new FrameSequenceDrawable(inputStream);
            drawable.setLoopCount(mLoopCount);
            drawable.setOnFinishedListener(mDrawableFinishedListener);
            setImageDrawable(drawable);
            if(mAnimatedSrcDrawable != null) {
                mAnimatedSrcDrawable.destroy();
            }
            mAnimatedSrcDrawable = drawable;
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean setAnimatedImageUri(ImageView imageView, Uri uri) {
        if (uri != null) {
            try {
                InputStream inputStream = getInputStreamByUri(imageView.getContext(), uri);
                FrameSequenceDrawable frameSequenceDrawable = new FrameSequenceDrawable(inputStream);
                frameSequenceDrawable.setLoopCount(mLoopCount);
                frameSequenceDrawable.setOnFinishedListener(mDrawableFinishedListener);
                imageView.setImageDrawable(frameSequenceDrawable);
                if(mAnimatedSrcDrawable != null) {
                    mAnimatedSrcDrawable.destroy();
                }
                mAnimatedSrcDrawable = frameSequenceDrawable;
                return true;
            } catch (Exception e) {
                //ignored
            }
        }
        return false;
    }

    private InputStream getInputStreamByResource(Resources resources, int resId) {
        return resources.openRawResource(resId);
    }

    private InputStream getInputStreamByUri(Context context, Uri uri) throws IOException {
        //workaround for #128
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            return new FileInputStream(new File(uri.getPath()));
        } else {
            return context.getResources().getAssets().open(uri.getPath());
        }
    }

    public void setLoopCount(int count) {
        mLoopCount = count;
        setLoopFinite();
        if (mAnimatedBgDrawable != null) {
            mAnimatedBgDrawable.setLoopCount(mLoopCount);
        }
        if (mAnimatedSrcDrawable != null) {
            mAnimatedSrcDrawable.setLoopCount(mLoopCount);
        }
    }

    public void setLoopDefault() {
        mLoopBehavior = FrameSequenceDrawable.LOOP_DEFAULT;
    }

    public void setLoopFinite() {
        mLoopBehavior = FrameSequenceDrawable.LOOP_FINITE;
    }

    public void setLoopInf() {
        mLoopBehavior = FrameSequenceDrawable.LOOP_INF;
    }



    public void stopAnimation(){
        if(mAnimatedSrcDrawable != null) {
            mAnimatedSrcDrawable.stop();
        }
    }

    public void setOnFinishedListener(OnFinishedListener listener) {
        mFinishedListener = listener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mAnimatedBgDrawable != null) {
            mAnimatedBgDrawable.destroy();
        }
        if(mAnimatedSrcDrawable != null) {
            mAnimatedSrcDrawable.destroy();
        }
    }
}
