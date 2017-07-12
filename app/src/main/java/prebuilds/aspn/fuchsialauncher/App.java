package prebuilds.aspn.fuchsialauncher;

import android.graphics.drawable.Drawable;

/**
 * Created by Никита on 10.07.2017.
 */

public class App{
    CharSequence label;
    CharSequence name;
    Drawable icon;
    public App(CharSequence label_p,CharSequence name_p,Drawable icon_p)
    {
        label=label_p;
        name=name_p;
        icon=icon_p;
    }
    public App()
    {}
}
