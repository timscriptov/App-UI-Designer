package com.mcal.uidesigner.appwizard.runtime;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

public class AppWizardActivity extends FragmentActivity {
    private int containerId;
    private int drawerContentId;
    private int drawerMenuId;
    private ActionBarDrawerToggle drawerToggle;
    private int pagerId;
    private AppWizardProject project;
    private ViewPager viewPager;

    public void saveXml(Document document, int change) {
    }

    public Document loadXml() {
        try {
            InputStream in = getAssets().open("app.xml");
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            in.close();
            return document;
        } catch (Exception e) {
            return null;
        }
    }

    protected Fragment createSectionFragment(final AppWizardProject.AppFragment section) {
        return new Fragment() {
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                if (section.getLayout() != null) {
                    return inflater.inflate(AppWizardActivity.this.getResId("layout", section.getLayout()), container, false);
                }
                return new LinearLayout(AppWizardActivity.this);
            }
        };
    }

    @SuppressLint("ResourceType")
    protected int inflateContentView() {
        LinearLayout view = new LinearLayout(this);
        view.setId(1000);
        setContentView(view);
        return view.getId();
    }

    public AppWizardProject getProject() {
        return this.project;
    }

    public AppWizardProject.AppActivity getAppActivity() {
        return getProject().getMainActivity();
    }

    @Override
    public void onCreate(Bundle bundle) {
        this.project = new AppWizardProject(this);
        setTheme(getThemeId());
        super.onCreate(bundle);
        getWindow().setSoftInputMode(2);
        refreshContent();
    }

    private int getThemeId() {
        switch (getAppActivity().getTheme()) {
            case HoloDark:
                return 16973931;
            case HoloLight:
                return 16973934;
            case HoloLightHoloActionBar:
                return getResId("style", "Theme_Holo_Light_Holo_ActionBar");
            case HoloLightDarkActionBar:
                return 16974105;
            case DeviceDefault:
            default:
                return 16974120;
            case DeviceDefaultLight:
                return 16974123;
            case DeviceDefaultLightDarkActionBar:
                return 16974143;
        }
    }

    public int getResId(String clazz, String res) {
        try {
            return ((Integer) Class.forName(getPackageName() + ".R$" + clazz).getField(res).get(null)).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (this.drawerToggle != null) {
            this.drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.drawerToggle != null) {
            this.drawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (this.drawerToggle == null || !this.drawerToggle.onOptionsItemSelected(item)) {
            return super.onMenuItemSelected(featureId, item);
        }
        return true;
    }

    @SuppressLint({"WrongConstant", "ResourceType"})
    public void refreshContent() {
        this.drawerToggle = null;
        this.viewPager = null;
        this.pagerId = 1001;
        this.drawerMenuId = 1002;
        this.drawerContentId = 1003;
        this.containerId = inflateContentView();
        ViewGroup container = (ViewGroup) findViewById(this.containerId);
        getActionBar().setDisplayShowTitleEnabled(getAppActivity().showTitle());
        setTitle(getAppActivity().getTitle());
        if (getAppActivity().showActionBar()) {
            getActionBar().show();
        } else {
            getActionBar().hide();
        }
        if (getAppActivity().showFullscreen()) {
            container.setSystemUiVisibility(4);
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    AppWizardActivity.this.getWindow().getDecorView().setSystemUiVisibility(4);
                }
            });
        } else {
            container.setSystemUiVisibility(0);
        }
        switch (getAppActivity().getType()) {
            case Tabs:
                getActionBar().setNavigationMode(2);
                getActionBar().setDisplayHomeAsUpEnabled(false);
                getActionBar().setHomeButtonEnabled(false);
                inflateTabContent(container);
                return;
            case TabsDrawer:
                getActionBar().setNavigationMode(2);
                inflateTabContent(inflateDrawerContent(container));
                return;
            case Spinner:
                getActionBar().setNavigationMode(1);
                getActionBar().setDisplayHomeAsUpEnabled(false);
                getActionBar().setHomeButtonEnabled(false);
                getActionBar().removeAllTabs();
                List<String> names = new ArrayList<>();
                for (AppWizardProject.AppFragment section : getAppActivity().getFragments()) {
                    names.add(section.getTitle());
                }
                getActionBar().setListNavigationCallbacks(new ArrayAdapter<>(getActionBar().getThemedContext(), 17367043, 16908308, names), new ActionBar.OnNavigationListener() {
                    @Override
                    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                        AppWizardActivity.this.getSupportFragmentManager().beginTransaction().replace(AppWizardActivity.this.containerId, AppWizardActivity.this.createSectionFragment(AppWizardActivity.this.getAppActivity().getFragments().get(itemPosition))).commit();
                        return true;
                    }
                });
                return;
            case SpinnerDrawer:
                getActionBar().setNavigationMode(1);
                inflateDrawerContent(container);
                getActionBar().removeAllTabs();
                List<String> names2 = new ArrayList<>();
                int count = getAppActivity().getFragments().size() - 1;
                for (int i = 0; i < count; i++) {
                    names2.add(getAppActivity().getFragments().get(i).getTitle());
                }
                getActionBar().setListNavigationCallbacks(new ArrayAdapter<>(getActionBar().getThemedContext(), 17367043, 16908308, names2), new ActionBar.OnNavigationListener() {
                    @Override
                    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                        AppWizardActivity.this.getSupportFragmentManager().beginTransaction().replace(AppWizardActivity.this.drawerContentId, AppWizardActivity.this.createSectionFragment(AppWizardActivity.this.getAppActivity().getFragments().get(itemPosition))).commit();
                        return true;
                    }
                });
                return;
            case Slider:
                getActionBar().setNavigationMode(0);
                getActionBar().setDisplayHomeAsUpEnabled(false);
                getActionBar().setHomeButtonEnabled(false);
                inflateSliderContent(container);
                return;
            case SliderDrawer:
                getActionBar().setNavigationMode(0);
                inflateSliderContent(inflateDrawerContent(container));
                return;
            case Single:
                getActionBar().setNavigationMode(0);
                getActionBar().setDisplayHomeAsUpEnabled(false);
                getActionBar().setHomeButtonEnabled(false);
                if (getAppActivity().getFragments().size() > 0) {
                    getSupportFragmentManager().beginTransaction().replace(this.containerId, createSectionFragment(getAppActivity().getFragments().get(0))).commit();
                    return;
                }
                return;
            case Drawer:
                getActionBar().setNavigationMode(0);
                inflateDrawerContent(container);
                if (getAppActivity().getFragments().size() > 0) {
                    getSupportFragmentManager().beginTransaction().replace(this.drawerContentId, createSectionFragment(getAppActivity().getFragments().get(0))).commit();
                    return;
                }
                return;
            default:
                return;
        }
    }

    @SuppressLint("ResourceType")
    private ViewGroup inflateDrawerContent(ViewGroup container) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        DrawerLayout drawerLayout = new DrawerLayout(this);
        container.addView(drawerLayout, new ViewGroup.LayoutParams(-1, -1));
        FrameLayout drawerContent = new FrameLayout(this);
        drawerContent.setId(this.drawerContentId);
        drawerLayout.addView(drawerContent, new DrawerLayout.LayoutParams(-1, -1));
        LinearLayout drawerMenu = new LinearLayout(this);
        drawerMenu.setId(this.drawerMenuId);
        DrawerLayout.LayoutParams layoutParams = new DrawerLayout.LayoutParams((int) (240.0f * getResources().getDisplayMetrics().density), -1);
        layoutParams.gravity = 8388611;
        drawerMenu.setDividerDrawable(new ColorDrawable(0));
        drawerMenu.setBackgroundDrawable(getDrawableRes(16842836));
        drawerLayout.addView(drawerMenu, layoutParams);
        this.drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, getResId("drawable", "ic_drawer"), 17039370, 17039370);
        drawerLayout.setDrawerListener(this.drawerToggle);
        drawerLayout.openDrawer(3);
        List<AppWizardProject.AppFragment> sections = getAppActivity().getFragments();
        if (sections.size() > 0) {
            getSupportFragmentManager().beginTransaction().replace(this.drawerMenuId, createSectionFragment(sections.get(sections.size() - 1))).commit();
        }
        return drawerLayout;
    }

    private Drawable getDrawableRes(int id) {
        TypedValue a = new TypedValue();
        getTheme().resolveAttribute(id, a, true);
        if (a.type < 28 || a.type > 31) {
            return getResources().getDrawable(a.resourceId);
        }
        return new ColorDrawable(a.data);
    }

    private void inflateSliderContent(ViewGroup container) {
        this.viewPager = new ViewPager(this);
        this.viewPager.setId(this.pagerId);
        container.addView(this.viewPager);
        PagerTitleStrip strip = new PagerTitleStrip(this);
        strip.setPadding(0, (int) (getResources().getDisplayMetrics().density * 4.0f), 0, (int) (getResources().getDisplayMetrics().density * 4.0f));
        ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
        layoutParams.height = -2;
        layoutParams.width = -1;
        layoutParams.gravity = 48;
        this.viewPager.addView(strip, layoutParams);
        strip.setTextColor(-1);
        strip.setBackgroundColor(-13388315);
        this.viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
    }

    private void inflateTabContent(ViewGroup container) {
        this.viewPager = new ViewPager(this);
        this.viewPager.setId(this.pagerId);
        container.addView(this.viewPager);
        this.viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        this.viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getActionBar().setSelectedNavigationItem(position);
            }
        });
        getActionBar().removeAllTabs();
        int count = getAppActivity().getType().hasDrawer() ? getAppActivity().getFragments().size() - 1 : getAppActivity().getFragments().size();
        for (int i = 0; i < count; i++) {
            getActionBar().addTab(getActionBar().newTab().setText(getAppActivity().getFragments().get(i).getTitle()).setTabListener(new ActionBar.TabListener() {
                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                }

                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                    if (viewPager != null) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }
                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                }
            }));
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return createSectionFragment(getAppActivity().getFragments().get(position));
        }

        @Override
        public int getCount() {
            if (AppWizardActivity.this.getAppActivity().getType().hasDrawer()) {
                return getAppActivity().getFragments().size() - 1;
            }
            return getAppActivity().getFragments().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getAppActivity().getFragments().get(position).getTitle();
        }
    }
}
