package com.furryos.launcher;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.*;
import android.provider.Settings;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root, content;
    boolean drawer = false;
    int bg = Color.rgb(246,241,252), ink = Color.rgb(62,53,77), accent = Color.rgb(143,113,190);

    int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + .5f); }
    GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d;
    }
    TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }
    LinearLayout column() { LinearLayout l = new LinearLayout(this); l.setOrientation(1); return l; }
    LinearLayout row() { LinearLayout l = new LinearLayout(this); l.setOrientation(0); l.setGravity(Gravity.CENTER_VERTICAL); return l; }
    void pad(View v, int a) { v.setPadding(dp(a),dp(a),dp(a),dp(a)); }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(bg); getWindow().setNavigationBarColor(bg);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        showHome();
    }
    void base() {
        root = column(); root.setBackgroundColor(bg);
        setContentView(root);
    }
    void showHome() {
        drawer = false; base();
        LinearLayout top = column(); pad(top,22);
        TextView brand = text("🐾  FurryOS", 19, ink, true);
        top.addView(brand);
        String date = new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(new Date());
        TextView day = text(date, 13, 0xff81768f, false); day.setPadding(0,dp(5),0,0); top.addView(day);
        root.addView(top);
        LinearLayout hero = column(); hero.setGravity(Gravity.CENTER); hero.setBackground(shape(0xffe9def7,28));
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, dp(190)); hp.setMargins(dp(18),dp(18),dp(18),dp(12));
        TextView fox = text("🦊", 54, ink, false); fox.setGravity(Gravity.CENTER);
        TextView clock = text(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()), 36, ink, true); clock.setGravity(Gravity.CENTER);
        TextView hello = text("пушистого тебе дня!", 14, 0xff786a8b, false); hello.setGravity(Gravity.CENTER);
        hero.addView(fox); hero.addView(clock); hero.addView(hello); root.addView(hero,hp);

        TextView label = text("Избранное", 16, ink, true); label.setPadding(dp(22),dp(8),0,dp(8)); root.addView(label);
        LinearLayout favorites = row(); favorites.setGravity(Gravity.CENTER); favorites.setPadding(dp(8),0,dp(8),0);
        addShortcut(favorites,"☎","Телефон",Intent.ACTION_DIAL);
        addShortcut(favorites,"✉","Сообщения",Intent.ACTION_MAIN, "com.google.android.apps.messaging");
        addShortcut(favorites,"🌐","Интернет",Intent.ACTION_VIEW,"https://www.google.com");
        addShortcut(favorites,"⚙","Настройки",Settings.ACTION_SETTINGS);
        root.addView(favorites);
        View spacer = new View(this); root.addView(spacer,new LinearLayout.LayoutParams(1,0,1));
        TextView all = text("Все приложения   ⠿",15,Color.WHITE,true); all.setGravity(Gravity.CENTER);
        all.setBackground(shape(accent,22)); LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(-1,dp(52)); ap.setMargins(dp(22),dp(12),dp(22),dp(22));
        root.addView(all,ap); all.setOnClickListener(v -> showDrawer());
    }
    void addShortcut(LinearLayout parent,String emoji,String name,String action) { addShortcut(parent,emoji,name,action,null); }
    void addShortcut(LinearLayout parent,String emoji,String name,String action,String data) {
        LinearLayout item = column(); item.setGravity(Gravity.CENTER); item.setPadding(dp(7),dp(10),dp(7),dp(10));
        TextView icon = text(emoji,25,ink,false); icon.setGravity(Gravity.CENTER); icon.setBackground(shape(Color.WHITE,20));
        item.addView(icon,new LinearLayout.LayoutParams(dp(58),dp(58)));
        TextView title = text(name,11,ink,true); title.setGravity(Gravity.CENTER); title.setPadding(0,dp(7),0,0); item.addView(title);
        parent.addView(item,new LinearLayout.LayoutParams(0,-2,1));
        item.setOnClickListener(v -> {
            try {
                Intent i;
                if (data != null && data.startsWith("http")) i = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(data));
                else if (data != null) i = getPackageManager().getLaunchIntentForPackage(data);
                else i = new Intent(action);
                if (i != null) startActivity(i); else Toast.makeText(this,"Приложение не найдено",Toast.LENGTH_SHORT).show();
            } catch(Exception e) { Toast.makeText(this,"Не удалось открыть",Toast.LENGTH_SHORT).show(); }
        });
    }
    void showDrawer() {
        drawer = true; base();
        LinearLayout head = row(); pad(head,20);
        TextView title = text("Приложения",22,ink,true); head.addView(title,new LinearLayout.LayoutParams(0,-2,1));
        TextView back = text("⌂",26,accent,true); head.addView(back); back.setOnClickListener(v -> showHome());
        root.addView(head);
        EditText search = new EditText(this); search.setSingleLine(true); search.setHint("Поиск приложений"); search.setTextSize(14);
        search.setBackground(shape(Color.WHITE,18)); search.setPadding(dp(16),0,dp(16),0);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1,dp(48)); sp.setMargins(dp(16),dp(4),dp(16),dp(12)); root.addView(search,sp);
        ScrollView scroll = new ScrollView(this); LinearLayout list = column(); list.setPadding(dp(16),0,dp(16),dp(16)); scroll.addView(list); root.addView(scroll);
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN,null); intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(intent,0);
        Collections.sort(apps,(a,b)->String.CASE_INSENSITIVE_ORDER.compare(a.loadLabel(pm).toString(),b.loadLabel(pm).toString()));
        for (ResolveInfo info: apps) {
            String name = info.loadLabel(pm).toString();
            LinearLayout item = row(); item.setPadding(dp(10),dp(9),dp(10),dp(9));
            ImageView icon = new ImageView(this); icon.setImageDrawable(info.loadIcon(pm)); item.addView(icon,new LinearLayout.LayoutParams(dp(38),dp(38)));
            TextView label = text(name,15,ink,false); label.setPadding(dp(14),0,0,0); item.addView(label);
            list.addView(item); item.setOnClickListener(v -> {
                try { startActivity(pm.getLaunchIntentForPackage(info.activityInfo.packageName)); }
                catch(Exception e) { Toast.makeText(this,"Не удалось открыть",Toast.LENGTH_SHORT).show(); }
            });
        }
        search.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s,int st,int c,int a) {}
            public void onTextChanged(CharSequence s,int st,int before,int count) {
                for(int i=0;i<list.getChildCount();i++) {
                    View v=list.getChildAt(i); TextView tv=(TextView)((LinearLayout)v).getChildAt(1);
                    v.setVisibility(tv.getText().toString().toLowerCase().contains(s.toString().toLowerCase())?View.VISIBLE:View.GONE);
                }
            }
            public void afterTextChanged(android.text.Editable e) {}
        });
    }
    @Override public void onBackPressed() { if(drawer) showHome(); else super.onBackPressed(); }
}
