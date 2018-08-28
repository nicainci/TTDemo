package com.lm.mvps;

import android.app.Activity;
import android.view.View;

/**
 * @Author WWC
 * @Create 2018/8/28
 * @Description presenter的接口，对外使用的时候只允许使用该接口的api进行交互。对于其子类，不允许有public的参数、方法和带参构造函数，目的是为了解耦
 */
public interface Presenter {
    void create(View view);

    void bind(Object... callerContext);

    void unbind();

    void destroy();

    Presenter add(Presenter presenter);

    boolean isInitialized();

    Activity getActivity();
}
