package com.joprovost.r8bemu.io.awt;

import java.awt.*;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MacOS {
    static boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    public static Runnable fullScreenToggle(Window window, Runnable windowEnteringFullScreen, Runnable windowExitingFullScreen) throws ReflectiveOperationException {
        Class<?> FullScreenUtilities = Class.forName("com.apple.eawt.FullScreenUtilities");
        Method setWindowCanFullScreen = FullScreenUtilities.getMethod("setWindowCanFullScreen", Window.class, Boolean.TYPE);
        setWindowCanFullScreen.invoke(FullScreenUtilities, window, true);

        Class<?> Application = Class.forName("com.apple.eawt.Application");
        Method fullScreenMethod = Application.getMethod("requestToggleFullScreen", Window.class);
        Method getApplication = Application.getMethod("getApplication");
        Object applicationObject = getApplication.invoke(Application);

        Class<?> FullScreenListener = Class.forName("com.apple.eawt.FullScreenListener");
        Object listener = Proxy.newProxyInstance(FullScreenListener.getClassLoader(), new Class<?>[]{FullScreenListener}, (proxy, method, args) -> {
            if (method.getName().equals("windowEnteringFullScreen")) {
                windowEnteringFullScreen.run();
            }
            if (method.getName().equals("windowExitingFullScreen")) {
                windowExitingFullScreen.run();
            }
            return null;
        });

        Method addFullScreenListenerTo = FullScreenUtilities.getMethod("addFullScreenListenerTo", Window.class, FullScreenListener);
        addFullScreenListenerTo.invoke(FullScreenUtilities, window, listener);

        return () -> {
            try {
                fullScreenMethod.invoke(applicationObject, window);
            } catch (ReflectiveOperationException ex) {
            }
        };
    }
}
