package prebuilds.aspn.fuchsialauncher;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

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
    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof App){
            App ptr = (App) v;
            retVal = ptr.name.toString().equals(this.name.toString());
        }

        return retVal;
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.name != null ? this.name.toString().hashCode() : 0);
        return hash;
    }
}
