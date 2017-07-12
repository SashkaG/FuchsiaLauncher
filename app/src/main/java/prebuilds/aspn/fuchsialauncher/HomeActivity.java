package prebuilds.aspn.fuchsialauncher;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.SearchView;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_TOP_SLEEPING;

public class HomeActivity extends AppCompatActivity {

    ImageView Home;
    RecyclerView rv;
    Dialog apps;
    List<ActivityManager.RunningAppProcessInfo> procInfos;
    TextView time;
    TextView battery_text;
    ArrayList<App> runningApps=new ArrayList<>();
    boolean isShowing=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FullScreencall();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        LinearLayout ll = (LinearLayout) findViewById(R.id.main);
        ll.setBackground(wallpaperDrawable);

        rv=(RecyclerView)findViewById(R.id.runningApp);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(HomeActivity.this);
        rv.setLayoutManager(llm);
        rv.setAdapter(new appAdapter3(HomeActivity.this,runningApps));
        getRunningApps();
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((ItemTouchHelperAdapter) rv.getAdapter());
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);

        battery_text = (TextView)findViewById(R.id.battery_text);
        Intent battery = HomeActivity.this.registerReceiver(new PowerLevelReciver(),new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryPct = (int)(level*100 / (float)scale);
        battery_text.setText(String.valueOf(batteryPct)+"%");

        HomeActivity.this.registerReceiver(new TimeReciver(),new IntentFilter(Intent.ACTION_TIME_TICK));
        time = (TextView)findViewById(R.id.time);
        Time now = new Time();
        now.setToNow();
        time.setText(now.format("%H:%M"));

        apps = new Dialog(HomeActivity.this,R.style.NewDialog);
        apps.requestWindowFeature(Window.FEATURE_NO_TITLE);
        apps.setCancelable(false);
        Home = ((ImageView)findViewById(R.id.home));
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowing)
                {
                    hideApps();
                    isShowing=false;
                }
                else {
                    createApps();
                    apps.show();
                    isShowing=true;
                }
            }
        });

    }
    protected void getRunningApps(){
        AsyncTask<Void,Void,Void> runn = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ActivityManager actvityManager = (ActivityManager)HomeActivity.this.getSystemService( ACTIVITY_SERVICE );
                procInfos = actvityManager.getRunningAppProcesses();
                return null;
            }
            @Override
            protected void onPostExecute(Void result)
            {
                PackageManager pm = HomeActivity.this.getPackageManager();
                runningApps.clear();
                for(ActivityManager.RunningAppProcessInfo info : procInfos)
                {
                    App app = new App();
                    try {
                        ApplicationInfo info2 = pm.getApplicationInfo(info.processName,PackageManager.GET_META_DATA);
                        app.label=info2.loadLabel(pm);
                        app.name=info2.packageName;
                        app.icon=info2.loadIcon(pm);
                        Intent i = pm.getLaunchIntentForPackage(info2.packageName);
                        if(i!=null&&i.hasCategory(Intent.CATEGORY_LAUNCHER))
                        {
                            runningApps.add(app);
                        }

                    } catch (Exception e) {
                    }

                }
                rv.getAdapter().notifyDataSetChanged();
            }

        };
        runn.execute();

    }
    @Override
    protected void onResume()
    {
        super.onResume();
        getRunningApps();
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        if(isShowing)
        {
            hideApps();
        }
    }
    private void showApps() {
        View view = apps.findViewById(R.id.main);
        int centerX = view.getWidth() / 2;
        int centerY = view.getHeight();
        float startRadius = 0;
        float endRadius = 1080;
        Animator animator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
        animator.setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                final GridView AllAps = (GridView)apps.findViewById(R.id.allaps);
                final PackageManager manager = getPackageManager();
                final ArrayList<App> Allapps = new ArrayList<App>();
                final ArrayList<App> RecentApps = new ArrayList<App>();
                final HorizontalGridView GoogleApps = (HorizontalGridView)apps.findViewById(R.id.google);
                final Intent i = new Intent(Intent.ACTION_MAIN, null);
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                AsyncTask<Void,Void,Void> my = new AsyncTask<Void, Void ,Void>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
                        for(ResolveInfo ri:availableActivities) {
                            App app = new App();
                            app.label = ri.loadLabel(manager);
                            app.name = ri.activityInfo.packageName;
                            app.icon = ri.activityInfo.loadIcon(manager);
                            if(app.name.toString().contains("google"))
                            {
                                RecentApps.add(app);
                            }
                            Allapps.add(app);
                        }
                        String topPackageName ;

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);
                        AllAps.setAdapter(new appAdapter(HomeActivity.this,Allapps));
                        GoogleApps.setAdapter(new appAdapter2(HomeActivity.this,RecentApps));
                        int padding_in_dp = 22;  // 6 dps
                        final float scale = HomeActivity.this.getResources().getDisplayMetrics().density;
                        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
                        AllAps.setVerticalSpacing(padding_in_px);
                        GoogleApps.setHorizontalSpacing(padding_in_px*3);
                        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
                        GoogleApps.setItemAnimator(itemAnimator);
                        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                        final SearchView searchView = (SearchView) apps.findViewById(R.id.search);
                        AllAps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> av, View v, int pos,
                                                    long id) {
                                hideApps();
                                Intent i = manager.getLaunchIntentForPackage(Allapps.get(pos).name.toString());
                                HomeActivity.this.startActivity(i);
                            }
                        });

                        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                        searchView.setSubmitButtonEnabled(true);
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                appAdapter main = ((appAdapter)AllAps.getAdapter());
                                main.getFilter().filter(newText);
                                return true;
                            }
                        });
                    }
                };
                my.execute();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }
    private void createApps()
    {
        apps.setContentView(R.layout.apps);



        apps.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if(isShowing)
                    {
                        isShowing=false;
                        hideApps();
                    }
                }
                return true;
            }
        });
        apps.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                WindowManager.LayoutParams lp= apps.getWindow().getAttributes();
                lp.dimAmount=0.6f;  // dimAmount between 0.0f and 1.0f, 1.0f is completely dark
                apps.getWindow().setAttributes(lp);
                apps.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                showApps();
            }
        });
    }
    public void hideApps()
    {
        isShowing=false;
        View view = apps.findViewById(R.id.main);
        int centerX = view.getWidth() / 2;
        int centerY = view.getHeight();
        float startRadius = 1080;
        float endRadius = 0;
        Animator animator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
        animator.setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                apps.dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }
    @Override
    public void onBackPressed() {
        return;
    }
    public void FullScreencall() {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
    }
    public class PowerLevelReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent battery) {
            int level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int)(level*100 / (float)scale);
            if(battery_text!=null)
            {
                battery_text.setText(String.valueOf(batteryPct)+"%");
            }
        }
    }
    public class TimeReciver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(time!=null)
            {
                Time now = new Time();
                now.setToNow();
                time.setText(now.format("%H:%M"));
            }

        }
    }


}
