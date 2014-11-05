//
// DO NOT EDIT THIS FILE, IT HAS BEEN GENERATED USING AndroidAnnotations 3.0.1.
//


package com.example.easyandutilsdemo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import com.example.easyandutilsdemo.R.id;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class EBeanTest_
    extends EBeanTest
    implements OnViewChangedListener
{

    private Context context_;
    private Handler handler_ = new Handler(Looper.getMainLooper());

    private EBeanTest_(Context context) {
        context_ = context;
        init_();
    }

    public static EBeanTest_ getInstance_(Context context) {
        return new EBeanTest_(context);
    }

    private void init_() {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        context = context_;
        if (context_ instanceof MainActivity) {
            activity = ((MainActivity) context_);
        } else {
            Log.w("EBeanTest_", (("Due to Context class "+ context_.getClass().getSimpleName())+", the @RootContext MainActivity won't be populated"));
        }
        doSomethingAfterInject();
    }

    public void rebind(Context context) {
        context_ = context;
        init_();
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        textView1 = ((TextView) hasViews.findViewById(id.textView1));
    }

    @Override
    public void updateTv(final int i) {
        handler_.post(new Runnable() {


            @Override
            public void run() {
                EBeanTest_.super.updateTv(i);
            }

        }
        );
    }

    @Override
    public void backThread() {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {


            @Override
            public void execute() {
                try {
                    EBeanTest_.super.backThread();
                } catch (Throwable e) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                }
            }

        }
        );
    }

}
