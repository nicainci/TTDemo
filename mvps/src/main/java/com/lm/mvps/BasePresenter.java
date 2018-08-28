package com.lm.mvps;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.lm.annotation.inject.Injector;
import com.lm.annotation.inject.Injectors;
import com.lm.annotation.inject.ProviderHolder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;

/**
 * @Author WWC
 * @Create 2018/8/28
 * @Description
 */
public class BasePresenter implements Presenter {

    /**
     * root view
     */
    private View mRootView;
    /**
     * child presenter list
     */
    private List<Presenter> mChildPresenterList = new ArrayList<>();
    /**
     * 是否有效
     */
    private boolean isValid = true;
    /**
     * 是否绑定了
     */
    private boolean isBinding = false;
    /**
     * 是否初始化了
     */
    private boolean isInitialized = false;

    private Injector mInjector = null;

    @Override
    public void create(View view) {

        if (isInitialized) throw new IllegalStateException("Presenter只能被初始化一次!");

        try {
            mRootView = view;
            ButterKnife.bind(this, mRootView);
            createChildren();
            onCreate();
        } catch (Exception e) {
            isValid = false;
        }
        isInitialized = true;
    }

    private void createChildren() {
        for (Presenter presenter : mChildPresenterList) {
            presenter.create(mRootView);
        }
    }

    protected void onCreate() {

    }

    @Override
    public void bind(Object... callerContext) {
        if (!isValid) return;

        if (!isInitialized) throw new IllegalStateException("Presenter必须先初始化!");

        if (mInjector == null) {
            mInjector = Injectors.injector(getClass());
        }

        checkInjection(callerContext);

        mInjector.reset(this);

        bindChild(callerContext);

        onBind(callerContext);

        isBinding = true;
    }

    protected void onBind(Object... callerContext) {

    }

    private void bindChild(Object... callerContext) {
        for (Presenter childPresenter : mChildPresenterList) {
            childPresenter.bind(callerContext);
        }
    }

    private void checkInjection(Object... callerContexts) {
        Set<Class> allTypes = new HashSet<>();
        Set<String> allNames = new HashSet<>();
        Map<Class, Object> typeOriginMap = new HashMap<>();
        Map<String, Object> nameOriginMap = new HashMap<>();
        for (Object callerContext : callerContexts) {
            Collection<String> names = null;
            Collection<Class> types = null;
            if (callerContext instanceof Map) {
                names = ((Map) callerContext).keySet();
            } else {
                names = ProviderHolder.allFieldNames(callerContext);
                types = ProviderHolder.allTypes(callerContext);
            }

            if (types != null) {
                for (Class type : types) {
                    if (!allTypes.add(type)) {
                        throw new IllegalArgumentException("Field 类型冲突，class " + type.getCanonicalName());
                    }
                    typeOriginMap.put(type, callerContext);
                }
            }

            for (String name : names) {
                if (!allNames.add(name)) {
                    throw new IllegalArgumentException("Field key冲突，key " + name);
                }
                nameOriginMap.put(name, callerContext);
            }
        }

        if (mInjector == null) {
            return;
        }

        Set<Class> typesNeeded = mInjector.allTypes();
        Set<String> namesNeeded = mInjector.allNames();

        if (!allTypes.containsAll(typesNeeded)) {
            Set<Class> missingTypes = new HashSet<>(typesNeeded);
            missingTypes.removeAll(allTypes);
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    " Inject 类型缺失，类型 " + missingTypes + " in " + getClass().getSimpleName());
        }

        if (!allNames.containsAll(namesNeeded)) {
            Set<String> missingNames = new HashSet<>(namesNeeded);
            missingNames.removeAll(allNames);
            throw new IllegalArgumentException(
                    this.getClass().getSimpleName() + " Inject key缺失，keys " + missingNames);
        }

        String tag = getClass().getSimpleName();
        for (Class type : typesNeeded) {
            Object origin = typeOriginMap.get(type);
            Log.d(tag, "getByType " + type.getName() + " from " + origin.getClass().getName() + "@"
                    + System.identityHashCode(origin));
        }

        for (String name : namesNeeded) {
            Object origin = nameOriginMap.get(name);
            Log.d(tag, "getByName " + name + " from " + origin.getClass().getName() + "@"
                    + System.identityHashCode(origin));
        }
    }

    @Override
    public void unbind() {
        if (!isValid) return;

        if (!isInitialized) throw new IllegalStateException("Presenter必须先初始化!");

        if (!isBinding) throw new IllegalStateException("Presenter 必须处于绑定状态才能解绑!");

        onUnbind();

        unbindChild();

    }

    private void unbindChild() {
        for (Presenter childPresenter : mChildPresenterList) {
            childPresenter.unbind();
        }
    }

    protected void onUnbind() {

    }

    @Override
    public void destroy() {
        if (!isValid) return;

        if (!isInitialized) throw new IllegalStateException("Presenter必须先初始化!");

        onDestroy();

        destroyChild();
    }

    private void destroyChild() {
        for (Presenter childPresenter : mChildPresenterList) {
            childPresenter.destroy();
        }
    }

    protected void onDestroy() {

    }

    @Override
    public Presenter add(Presenter presenter) {

        if (presenter == null) {
            return this;
        }

        mChildPresenterList.add(presenter);

        if (isInitialized) {
            presenter.create(mRootView);
        }

        return this;
    }

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    public boolean isBinding() {
        return isBinding;
    }

    public View getRootView() {
        return mRootView;
    }

    protected final Context getContext() {
        return mRootView == null ? null : mRootView.getContext();
    }

    protected final Resources getResources() {
        if (getContext() == null) {
            return null;
        }
        return getContext().getResources();
    }

    protected final String getString(int id) {
        if (getContext() == null) {
            return null;
        }
        return getContext().getString(id);
    }

    @Override
    public Activity getActivity() {
        Activity activity = null;
        if (getContext().getClass().getName().contains("com.android.internal.policy.DecorContext")) {
            //android7.0开始DecorContext不能转换成Activity，通过反射获取Activity
            try {
                Field field = getContext().getClass().getDeclaredField("mPhoneWindow");
                field.setAccessible(true);
                Object obj = field.get(getContext());
                java.lang.reflect.Method m1 = obj.getClass().getMethod("getContext");
                activity = (Activity) (m1.invoke(obj));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Context context = getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        return activity;
    }
}
