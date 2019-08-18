package core;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.wuli.permissiontest.ActivityCollector;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class PermissionCheckAspectJ {

    @Pointcut("execution(@core.NeedPermissons * *(..))")
    public void checkPermission() {

    }

    @Around("checkPermission()")
    public void checkPermissionPoint(ProceedingJoinPoint joinPoint) throws Throwable {

        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new AnnotationException("NeedPermissons 注解只能用于方法上");
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        NeedPermissons needPermissons = methodSignature.getMethod().getAnnotation(NeedPermissons.class);
        if (needPermissons == null) {
            return;
        }
        String[] value = needPermissons.value();
        if (value.length == 0) {
            return;
        }
        Activity activity = ActivityCollector.currentActivity();
        if (activity == null) {
            return;
        }

        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || checkPermissionGranted(activity,value)) {
            joinPoint.proceed();
        }else {
            //permission has not been granted.
            requestPermission(activity,"", 0, value);
        }




    }

    private boolean checkPermissionGranted(Activity activity,String[] permissions) {
        boolean flag = true;
        for (String p : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private void requestPermission(final Activity activity, String permissionDes, final int requestCode, final String[] permissions) {
        if (shouldShowRequestPermissionRationale(activity,permissions)) {
            /*1. 第一次请求权限时，用户拒绝了，下一次：shouldShowRequestPermissionRationale()  返回 true，应该显示一些为什么需要这个权限的说明
            2.第二次请求权限时，用户拒绝了，并选择了“不在提醒”的选项时：shouldShowRequestPermissionRationale()  返回 false
            3. 设备的策略禁止当前应用获取这个权限的授权：shouldShowRequestPermissionRationale()  返回 false*/
            //如果用户之前拒绝过此权限，再提示一次准备授权相关权限
            new AlertDialog.Builder(activity)
                    .setTitle("提示")
                    .setMessage(permissionDes)
                    .setPositiveButton("授权", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, permissions, requestCode);
                        }
                    }).show();

        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    private boolean shouldShowRequestPermissionRationale(Activity activity,String[] permissions) {
        boolean flag = false;
        for (String p : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, p)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

}
