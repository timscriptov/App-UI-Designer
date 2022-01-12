package com.mcal.uidesigner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.mcal.uidesigner.R;
import com.mcal.uidesigner.common.ActivityStarter;
import com.mcal.uidesigner.common.AndroidHelper;
import com.mcal.uidesigner.common.HelpActivityStarter;
import com.mcal.uidesigner.common.MessageBox;
import com.mcal.uidesigner.common.ShopActivityStarter;
import com.mcal.uidesigner.common.TextToSpeechHelper;
import com.mcal.uidesigner.common.TrainerLogo;
import com.mcal.uidesigner.common.UndoManager;
import com.mcal.uidesigner.common.ValueRunnable;
import com.mcal.uidesigner.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlLayoutDesignActivity extends AppCompatActivity {
    private static final int DARK = 3;
    private static final int DARK_SMALL = 1;
    public static final String EXTRA_DEMO = "EXTRA_LICENSED";
    public static final String EXTRA_FILE = "EXTRA_FILE";
    public static final String EXTRA_LANGUAGE = "EXTRA_LANGUAGE";
    public static final String EXTRA_STANDALONE = "EXTRA_STANDALONE";
    public static final String EXTRA_TRAINER = "EXTRA_TRAINER";
    private static final String EXTRA_TRAINER_ACTION = "EXTRA_TRAINER_ACTION";
    private static final String EXTRA_TRAINER_BUTTON = "EXTRA_TRAINER_BUTTON";
    private static final String EXTRA_TRAINER_HEADER = "EXTRA_HEADER";
    private static final String EXTRA_TRAINER_LOCALE = "EXTRA_TRAINER_LOCALE";
    private static final String EXTRA_TRAINER_RUN_BUTTON = "EXTRA_TRAINER_RUN_BUTTON";
    private static final String EXTRA_TRAINER_SOUND = "EXTRA_TRAINER_SOUND";
    private static final String EXTRA_TRAINER_SPEAK = "EXTRA_TRAINER_SPEAK";
    private static final String EXTRA_TRAINER_TASK = "EXTRA_TRAINER_TASK";
    private static final String EXTRA_TRAINER_TITLE = "EXTRA_TRAINER_TITILE";
    private static final int LIGHT = 2;
    private static final int LIGHT_SMALL = 0;
    private static final String PREF_XMLDESIGNER_EDITMODE = "PREF_XMLDESIGNER_EDITMODE";
    private static final String PREF_XMLDESIGNER_FILE = "PREF_XMLDESIGNER_FILE";
    private static final String PREF_XMLDESIGNER_VIEW = "XMLDESIGNER_VIEW";
    private LinearLayout containerView;
    private LinearLayout contentView;
    private ActionBarDrawerToggle drawerToggle;
    private XmlLayoutlInflater inflater;
    private boolean initialized;
    private boolean isDefaultProject;
    private boolean isDemo;
    private boolean isStandalone;
    private String resDirPath;
    private SoundPool soundPool;
    private int taskSound;
    private TextToSpeechHelper tts;
    private UndoManager undoManager;
    private String xmlFilePath;

    public static void show(Activity parent, String language, String filePath, boolean isDemo, boolean isStandalone) {
        Intent intent = new Intent(parent, XmlLayoutDesignActivity.class);
        intent.putExtra(EXTRA_FILE, filePath);
        intent.putExtra(EXTRA_LANGUAGE, language);
        intent.putExtra(EXTRA_DEMO, isDemo);
        intent.putExtra(EXTRA_STANDALONE, isStandalone);
        intent.putExtra(EXTRA_TRAINER, false);
        parent.startActivity(intent);
    }

    public static void showTrainer(Activity parent, String language, String filePath, int trainerRequestCode, String[] trainerHeader, String trainerLocale, String trainerTask, String trainerTaskTitle, String trainerButton, String trainerRunButton, boolean speak, boolean sound) {
        Intent intent = new Intent(parent, XmlLayoutDesignActivity.class);
        intent.putExtra(EXTRA_FILE, filePath);
        intent.putExtra(EXTRA_LANGUAGE, language);
        intent.putExtra(EXTRA_DEMO, false);
        intent.putExtra(EXTRA_STANDALONE, false);
        intent.putExtra(EXTRA_TRAINER, true);
        intent.putExtra(EXTRA_TRAINER_SOUND, sound);
        intent.putExtra(EXTRA_TRAINER_TASK, trainerTask);
        intent.putExtra(EXTRA_TRAINER_TITLE, trainerTaskTitle);
        intent.putExtra(EXTRA_TRAINER_BUTTON, trainerButton);
        intent.putExtra(EXTRA_TRAINER_HEADER, trainerHeader);
        intent.putExtra(EXTRA_TRAINER_LOCALE, trainerLocale);
        intent.putExtra(EXTRA_TRAINER_SPEAK, speak);
        intent.putExtra(EXTRA_TRAINER_RUN_BUTTON, trainerRunButton);
        parent.startActivityForResult(intent, trainerRequestCode);
    }

    public static boolean resultContinue(@NonNull Intent data) {
        return data.getIntExtra(EXTRA_TRAINER_ACTION, 0) == 1;
    }

    public static boolean resultRun(@NonNull Intent data) {
        return data.getIntExtra(EXTRA_TRAINER_ACTION, 0) == 2;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 3424345) {
            XmlLayoutPropertyEditor.addImageFromPicker(this, data);
        }
    }

    public void gotoSourceCode(int sourceLine, int sourceColumn) {
        if (this.isStandalone) {
            try {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setDataAndType(Uri.fromFile(new File(this.xmlFilePath)), "application/xml");
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            ActivityStarter.navigateTo(this, xmlFilePath, sourceLine, sourceColumn);
        }
    }

    public void showHelp(String helpUrl) {
        if (this.isStandalone) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("http://developer.android.com/reference/" + helpUrl));
            startActivity(intent);
            return;
        }
        HelpActivityStarter.showHelp(this, helpUrl, "");
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.initialized = false;
        AndroidHelper.switchLanguage(this, getIntent().getStringExtra(EXTRA_LANGUAGE));
        switch (getViewType()) {
            case 0:
            case 2:
                setTheme(R.style.ActivityThemeDesignerLight);
                break;
            case 1:
            case 3:
                setTheme(R.style.ActivityThemeDesignerDark);
                break;
        }
        getWindow().setSoftInputMode(2);
        AndroidHelper.forceOptionsMenuButton(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.designer);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Settings.ACTION_MANAGE_OVERLAY_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Settings.ACTION_MANAGE_OVERLAY_PERMISSION}, 1);
            }
        }

        AndroidHelper.makeToolbarFocusable(this);
        if (!isTrainer()) {
            AndroidHelper.setAndroidTVPadding(findViewById(R.id.designerFrame));
        }
        if (isTrainer() && AndroidHelper.isAndroidTV(this)) {
            getSupportActionBar().hide();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (isTrainer()) {
            TrainerLogo.set(getSupportActionBar(), R.mipmap.ic_launcher, getIntent().getStringArrayExtra(EXTRA_TRAINER_HEADER));
        } else {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setNavigationMode(1);
            getSupportActionBar().setListNavigationCallbacks(new ArrayAdapter<>(this, 17367049, new String[]{"Light Theme Small", "Dark Theme Small", "Light Theme", "Dark Theme"}),
                    new ActionBar.OnNavigationListener() {
                        @Override
                        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                            if (!initialized || itemPosition == XmlLayoutDesignActivity.this.getViewType()) {
                                return true;
                            }
                            setViewType(itemPosition);
                            return true;
                        }
                    });
            getSupportActionBar().setSelectedNavigationItem(getViewType());
            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.designerDrawer);
            this.drawerToggle = new ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    //R.drawable.round_menu_24,
                    17039370,
                    17039370);
            drawerLayout.setDrawerListener(this.drawerToggle);
        }
        if (isTrainer()) {
            final View header = findViewById(R.id.designerHeaderLearnTask);
            header.setVisibility(0);
            AppCompatTextView textView = header.findViewById(R.id.designerHeaderLearnTaskText);
            AppCompatTextView titleView = header.findViewById(R.id.designerHeaderLearnTaskTitle);
            final AppCompatTextView button = header.findViewById(R.id.designerHeaderLearnButton);
            titleView.setText(getIntent().getStringExtra(EXTRA_TRAINER_TITLE));
            textView.setText(Html.fromHtml(getIntent().getStringExtra(EXTRA_TRAINER_TASK)));
            button.setText(getIntent().getStringExtra(EXTRA_TRAINER_BUTTON));
            titleView.setTextSize(AndroidHelper.getTrainerHeaderFontSize(this));
            textView.setTextSize(AndroidHelper.getTrainerTextFontSize(this));
            button.setTextSize(AndroidHelper.getTrainerButtonFontSize(this));
            final TranslateAnimation anim = new TranslateAnimation(0.0f, 0.0f, -(150.0f * getResources().getDisplayMetrics().density), 0.0f) {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    super.applyTransformation(interpolatedTime, t);
                    header.invalidate();
                }
            };
            anim.setDuration(500);
            header.setVisibility(4);
            findViewById(R.id.designerHeaderLearnTaskInner).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent data = new Intent();
                    data.putExtra(XmlLayoutDesignActivity.EXTRA_TRAINER_ACTION, 1);
                    XmlLayoutDesignActivity.this.setResult(-1, data);
                    XmlLayoutDesignActivity.this.finish();
                }
            });
            findViewById(R.id.designerHeaderLearnTaskInner).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        ScaleAnimation anim2 = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, 1, 0.0f, 1, 0.5f);
                        anim2.setDuration(400);
                        anim2.setFillAfter(true);
                        button.startAnimation(anim2);
                        return;
                    }
                    ScaleAnimation anim3 = new ScaleAnimation(1.2f, 1.0f, 1.2f, 1.0f, 1, 0.0f, 1, 0.5f);
                    anim3.setDuration(400);
                    anim3.setFillAfter(true);
                    button.startAnimation(anim3);
                }
            });
            if (isFullscreenTrainerLayout()) {
                float density = getResources().getDisplayMetrics().density;
                findViewById(R.id.designerHeaderLearnTaskInner).setPadding((int) (48.0f * density), (int) (27.0f * density), (int) (48.0f * density), 0);
                findViewById(R.id.designerContent).setPadding((int) (48.0f * density), 0, (int) (48.0f * density), (int) (27.0f * density));
            }
            header.postDelayed(new Runnable() {
                @Override
                public void run() {
                    XmlLayoutDesignActivity.this.speak();
                    header.setVisibility(0);
                    header.startAnimation(anim);
                }
            }, 500);
        } else {
            findViewById(R.id.designerHeaderLearnTask).setVisibility(8);
        }
        this.contentView = new LinearLayout(this);
        ((ViewGroup) findViewById(R.id.designerContent)).addView(this.contentView);
        this.contentView.setClipChildren(false);
        this.contentView.setGravity(17);
        this.containerView = new LinearLayout(this);
        this.contentView.addView(this.containerView);
        this.containerView.setClipChildren(false);
        this.containerView.setGravity(17);
        ((ListView) findViewById(R.id.designerViewList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHierachyEntry entry = (ViewHierachyEntry) parent.getItemAtPosition(position);
                if (entry.view != null) {
                    XmlLayoutDesignActivity.this.onViewClicked(entry.view);
                } else if (entry.file != null) {
                    XmlLayoutDesignActivity.this.openLayout(entry.file.getPath());
                } else if (entry.isAddButton) {
                    XmlLayoutDesignActivity.this.createNewLayout();
                }
            }
        });
        initFromIntent(savedInstanceState == null);
        if (savedInstanceState != null) {
            this.undoManager.load(savedInstanceState);
        }
        configureBorderView();
        this.initialized = true;
    }

    protected void speak() {
        if (getIntent().getBooleanExtra(EXTRA_TRAINER_SPEAK, false)) {
            this.tts = new TextToSpeechHelper(this);
            this.tts.speak(getIntent().getStringExtra(EXTRA_TRAINER_LOCALE), getIntent().getStringExtra(EXTRA_TRAINER_TASK));
        }
        if (getIntent().getBooleanExtra(EXTRA_TRAINER_SOUND, false)) {
            this.soundPool = new SoundPool(1, 3, 0);
            this.taskSound = this.soundPool.load(this, R.raw.task, 1);
            this.soundPool.play(this.taskSound, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }

    private boolean isFullscreenTrainerLayout() {
        return AndroidHelper.isAndroidTV(this) && isTrainer();
    }

    private boolean isTrainer() {
        return getIntent().getBooleanExtra(EXTRA_TRAINER, false);
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
        configureBorderView();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.undoManager != null) {
            this.undoManager.save(bundle);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.undoManager != null && this.inflater != null) {
            this.undoManager.removeListener(this.inflater);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void configureBorderView() {
        switch (getViewType()) {
            case 0:
            case 1:
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                float density = getResources().getDisplayMetrics().density;
                this.containerView.setLayoutParams(new LinearLayout.LayoutParams(Math.min((int) (300.0f * density), (int) (((double) screenWidth) * 0.8d)), Math.min((int) (500.0f * density), (int) (((double) screenHeight) * 0.8d))));
                this.inflater.setShowBorder(true);
                return;
            default:
                this.containerView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
                this.inflater.setShowBorder(false);
                return;
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if ((intent.getFlags() & AccessibilityEventCompat.TYPE_TOUCH_INTERACTION_START) == 0) {
            initFromIntent(false);
        }
    }

    @SuppressLint("WrongConstant")
    private void initFromIntent(boolean isNewStart) {
        boolean z;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        this.isDemo = extras != null && extras.getBoolean(EXTRA_DEMO, false);
        if (extras == null || extras.getBoolean(EXTRA_STANDALONE, true)) {
            z = true;
        } else {
            z = false;
        }
        this.isStandalone = z;
        if (!this.isStandalone) {
            this.xmlFilePath = extras.getString(EXTRA_FILE);
            this.resDirPath = new File(this.xmlFilePath).getParentFile().getParentFile().getPath();
            this.isDefaultProject = false;
        } else if (extras != null && "android.intent.action.SEND".equals(intent.getAction()) && (extras.get("android.intent.extra.STREAM") instanceof Uri)) {
            Uri uri = (Uri) extras.get("android.intent.extra.STREAM");
            this.resDirPath = Utils.getDefaultResDirPath();
            this.isDefaultProject = true;
            this.xmlFilePath = Utils.createNewLayoutFile(this.resDirPath, Utils.getRealFileNameFromUri(this, uri));
            setLastFilepath(this.xmlFilePath);
            try {
                Utils.transfer(getContentResolver().openInputStream(uri), new FileOutputStream(this.xmlFilePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (extras != null && "android.intent.action.SEND".equals(intent.getAction()) && extras.getString("android.intent.extra.TEXT") != null) {
            this.resDirPath = Utils.getDefaultResDirPath();
            this.isDefaultProject = true;
            this.xmlFilePath = Utils.createNewLayoutFile(this.resDirPath, Utils.suggestNewLayoutName(this.resDirPath), extras.getString("android.intent.extra.TEXT"));
            setLastFilepath(this.xmlFilePath);
        } else if (intent.getData() == null || intent.getData().getPath() == null) {
            this.resDirPath = Utils.getDefaultResDirPath();
            this.isDefaultProject = true;
            this.xmlFilePath = getLastFilepath();
            if (this.xmlFilePath == null || !new File(this.xmlFilePath).exists()) {
                this.xmlFilePath = Utils.chooseLayoutOrCreateNew(this.resDirPath);
                setLastFilepath(this.xmlFilePath);
            }
        } else {
            this.xmlFilePath = intent.getData().getPath();
            this.resDirPath = new File(this.xmlFilePath).getParentFile().getParentFile().getPath();
            this.isDefaultProject = false;
        }
        if (this.isStandalone && isNewStart) {
            ((DrawerLayout) findViewById(R.id.designerDrawer)).openDrawer(3);
        }
        createInflater();
    }

    public void createInflater() {
        this.undoManager = new UndoManager();
        this.inflater = new XmlLayoutlInflater(this.containerView, this.xmlFilePath, this.resDirPath, this.undoManager) {
            private boolean isFirstEdit = true;

            @Override
            protected void onViewClicked(XmlLayoutEditView editView) {
                XmlLayoutDesignActivity.this.onViewClicked(editView);
            }

            @Override
            protected void onEmptyLayoutClicked() {
                XmlLayoutWidgetPicker.selectRootView(XmlLayoutDesignActivity.this, "Add...", new ValueRunnable<NewWidget>() {
                    public void run(NewWidget widget) {
                        XmlLayoutDesignActivity.this.inflater.addView(widget);
                    }
                });
            }

            @Override
            protected void onXmlModified(boolean isUserEdit) {
                XmlLayoutDesignActivity.this.invalidateOptionsMenu();
                if (XmlLayoutDesignActivity.this.isDemo) {
                    if (isUserEdit && this.isFirstEdit) {
                        this.isFirstEdit = false;
                        ShopActivityStarter.show(XmlLayoutDesignActivity.this, 0, "unlock the UI designer", "savechanges", true, true, false, true, false);
                    }
                } else if (XmlLayoutDesignActivity.this.xmlFilePath != null) {
                    try {
                        FileWriter writer = new FileWriter(XmlLayoutDesignActivity.this.xmlFilePath);
                        writer.write(XmlLayoutDesignActivity.this.inflater.getXml());
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void onInflated() {
                XmlLayoutDesignActivity.this.contentView.invalidate();
                if (getEditViews().size() > 0) {
                    getEditViews().get(0).requestFocus();
                }
                XmlLayoutDesignActivity.this.updateHierachy();
            }
        };
        this.inflater.init();
        this.inflater.setEditMode(isEditMode());
        invalidateOptionsMenu();
    }

    public void updateHierachy() {
        List<ViewHierachyEntry> entries = new ArrayList<>();
        if (this.isStandalone) {
            for (File f : Utils.findLayoutFiles(this.resDirPath)) {
                entries.add(new ViewHierachyEntry(f));
            }
            entries.add(new ViewHierachyEntry(true));
        } else {
            entries.add(new ViewHierachyEntry(new File(this.xmlFilePath)));
        }
        for (XmlLayoutEditView view : this.inflater.getEditViews()) {
            entries.add(new ViewHierachyEntry(view));
        }
        ListView viewList = (ListView) findViewById(R.id.designerViewList);
        ViewHierachyEntryAdapter adapter = (ViewHierachyEntryAdapter) viewList.getAdapter();
        if (adapter == null) {
            viewList.setAdapter((ListAdapter) new ViewHierachyEntryAdapter(this, entries));
            return;
        }
        adapter.clear();
        for (ViewHierachyEntry entry : entries) {
            adapter.add(entry);
        }
    }

    public void onViewClicked(XmlLayoutEditView editView) {
        XmlLayoutEditViewMenu.showViewEditMenu(this, editView);
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        return MessageBox.onCreateDialog(this, id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.designer_options_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        boolean z6 = false;
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.designerMenuRun).setVisible(isTrainer());
        menu.findItem(R.id.designerMenuRun).setTitle(getIntent().getStringExtra(EXTRA_TRAINER_RUN_BUTTON));
        menu.findItem(R.id.designerMenuHideBorders).setVisible(!isTrainer() && isEditMode());
        MenuItem findItem = menu.findItem(R.id.designerMenuShowBorders);
        if (isTrainer() || isEditMode()) {
            z = false;
        } else {
            z = true;
        }
        findItem.setVisible(z);
        menu.findItem(R.id.designerMenuUndo).setEnabled(this.undoManager.canUndo());
        menu.findItem(R.id.designerMenuRedo).setEnabled(this.undoManager.canRedo());
        menu.findItem(R.id.designerMenuPaste).setEnabled(this.inflater.canPaste());
        MenuItem findItem2 = menu.findItem(R.id.designerMenuPaste);
        if (!isTrainer()) {
            z2 = true;
        } else {
            z2 = false;
        }
        findItem2.setVisible(z2);
        MenuItem findItem3 = menu.findItem(R.id.designerMenuCopy);
        if (this.isDemo || this.inflater.getXml().length() <= 0) {
            z3 = false;
        } else {
            z3 = true;
        }
        findItem3.setEnabled(z3);
        MenuItem findItem4 = menu.findItem(R.id.designerMenuCopy);
        if (!isTrainer()) {
            z4 = true;
        } else {
            z4 = false;
        }
        findItem4.setVisible(z4);
        MenuItem findItem5 = menu.findItem(R.id.designerMenuShare);
        if (this.isDemo || this.inflater.getXml().length() <= 0) {
            z5 = false;
        } else {
            z5 = true;
        }
        findItem5.setEnabled(z5);
        MenuItem findItem6 = menu.findItem(R.id.designerMenuShare);
        if (!isTrainer()) {
            z6 = true;
        }
        findItem6.setVisible(z6);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(/*int featureId, **/MenuItem item) {
        if (this.drawerToggle != null && this.drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == 16908332) {
            finish();
        } else if (item.getItemId() == R.id.designerMenuRun) {
            Intent data = new Intent();
            data.putExtra(EXTRA_TRAINER_ACTION, 2);
            setResult(-1, data);
            finish();
        } else if (item.getItemId() == R.id.designerMenuShowBorders) {
            setEditMode(true);
        } else if (item.getItemId() == R.id.designerMenuHideBorders) {
            setEditMode(false);
        } else if (item.getItemId() == R.id.designerMenuUndo) {
            this.undoManager.undo();
        } else if (item.getItemId() == R.id.designerMenuRedo) {
            this.undoManager.redo();
        } else if (item.getItemId() == R.id.designerMenuPaste) {
            this.inflater.paste();
        } else if (item.getItemId() == R.id.designerMenuCopy) {
            this.inflater.copy();
        } else if (item.getItemId() == R.id.designerMenuShare) {
            this.inflater.share();
        }
        return false;
    }

    public void deleteLayout(final String filepath) {
        MessageBox.queryYesNo(this, "Delete Layout " + new File(filepath).getName(), "Really delete this layout?", new Runnable() {
            @Override
            public void run() {
                new File(filepath).delete();
                if (XmlLayoutDesignActivity.this.xmlFilePath.equals(filepath)) {
                    XmlLayoutDesignActivity.this.xmlFilePath = Utils.chooseLayoutOrCreateNew(XmlLayoutDesignActivity.this.resDirPath);
                    if (XmlLayoutDesignActivity.this.isDefaultProject) {
                        XmlLayoutDesignActivity.this.setLastFilepath(XmlLayoutDesignActivity.this.xmlFilePath);
                    }
                    XmlLayoutDesignActivity.this.createInflater();
                    return;
                }
                XmlLayoutDesignActivity.this.updateHierachy();
            }
        }, (Runnable) null);
    }

    public void createNewLayout() {
        MessageBox.queryText(this, "New Layout", "File name:", Utils.suggestNewLayoutName(this.resDirPath), new ValueRunnable<String>() {
            public void run(String name) {
                XmlLayoutDesignActivity.this.setEditMode(true);
                XmlLayoutDesignActivity.this.xmlFilePath = Utils.createNewLayoutFile(XmlLayoutDesignActivity.this.resDirPath, name);
                if (XmlLayoutDesignActivity.this.isDefaultProject) {
                    XmlLayoutDesignActivity.this.setLastFilepath(XmlLayoutDesignActivity.this.xmlFilePath);
                }
                XmlLayoutDesignActivity.this.createInflater();
            }
        });
    }

    protected void openLayout(String filepath) {
        this.xmlFilePath = filepath;
        if (this.isDefaultProject) {
            setLastFilepath(this.xmlFilePath);
        }
        createInflater();
    }

    private boolean isEditMode() {
        if (isTrainer()) {
            return true;
        }
        return getPreferences().getBoolean(PREF_XMLDESIGNER_EDITMODE, true);
    }

    public void setEditMode(boolean editMode) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(PREF_XMLDESIGNER_EDITMODE, editMode);
        editor.apply();
        invalidateOptionsMenu();
        this.inflater.setEditMode(editMode);
        configureBorderView();
        this.contentView.invalidate();
    }

    public int getViewType() {
        if (isTrainer()) {
            return 2;
        }
        return getPreferences().getInt(PREF_XMLDESIGNER_VIEW, 0);
    }

    public void setViewType(int view) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(PREF_XMLDESIGNER_VIEW, view);
        editor.apply();
        recreate();
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences("UIDesigner", 0);
    }

    private String getLastFilepath() {
        return getPreferences().getString(PREF_XMLDESIGNER_FILE, null);
    }

    public void setLastFilepath(String filepath) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREF_XMLDESIGNER_FILE, filepath);
        editor.apply();
    }


    public static class ViewHierachyEntry {
        public File file;
        public boolean isAddButton;
        public XmlLayoutEditView view;

        public ViewHierachyEntry(File file) {
            this.file = file;
        }

        public ViewHierachyEntry(boolean isAddButton) {
            this.isAddButton = true;
        }

        public ViewHierachyEntry(XmlLayoutEditView view) {
            this.view = view;
        }
    }

    public class ViewHierachyEntryAdapter extends ArrayAdapter<ViewHierachyEntry> {
        public ViewHierachyEntryAdapter(Context context, List<ViewHierachyEntry> views) {
            super(context, R.layout.designer_viewlist_entry, views);

        }

        @SuppressLint({"WrongConstant", "SetTextI18n"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int i;
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.designer_viewlist_entry, parent, false);
            }
            final ViewHierachyEntry entry = getItem(position);
            LinearLayout viewLayout = (LinearLayout) view.findViewById(R.id.designerViewlistentryViewLayout);
            LinearLayout fileLayout = (LinearLayout) view.findViewById(R.id.designerViewlistentryFileLayout);
            if (entry.view != null) {
                viewLayout.setVisibility(0);
                fileLayout.setVisibility(8);
                viewLayout.setPadding((int) (((float) ((entry.view.getDepth() * 20) + 5)) * getContext().getResources().getDisplayMetrics().density), 0, 0, 0);
                ((AppCompatTextView) view.findViewById(R.id.designerViewlistEntryName)).setText(entry.view.getNodeName());
                AppCompatImageView imageView = (AppCompatImageView) view.findViewById(R.id.designerViewlistEntryImage);
                if (entry.view.canAddInside()) {
                    i = R.drawable.round_category_24;
                } else {
                    i = R.drawable.round_widgets_24;
                }
                imageView.setImageResource(i);
            } else if (entry.file != null) {
                viewLayout.setVisibility(8);
                fileLayout.setVisibility(0);
                fileLayout.setPadding(0, 0, 0, 0);
                AppCompatRadioButton fileRadioButton = (AppCompatRadioButton) view.findViewById(R.id.designerViewlistentryFileRadioButton);
                fileRadioButton.setFocusable(false);
                fileRadioButton.setFocusableInTouchMode(false);
                fileRadioButton.setChecked(XmlLayoutDesignActivity.this.xmlFilePath.equals(entry.file.getPath()));
                fileRadioButton.setVisibility(XmlLayoutDesignActivity.this.isStandalone ? 0 : 8);
                AppCompatTextView fileNameView = (AppCompatTextView) view.findViewById(R.id.designerViewlistentryFileName);
                fileNameView.setText(entry.file.getName());
                if (XmlLayoutDesignActivity.this.xmlFilePath.equals(entry.file.getPath())) {
                    fileNameView.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    fileNameView.setTypeface(Typeface.DEFAULT);
                }
                ((AppCompatImageView) view.findViewById(R.id.designerViewlistFileImage)).setImageResource(R.drawable.round_insert_drive_file_24);
                AppCompatImageView deleteButton = (AppCompatImageView) view.findViewById(R.id.designerViewlistentryDelete);
                deleteButton.setVisibility(XmlLayoutDesignActivity.this.isStandalone ? 0 : 8);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XmlLayoutDesignActivity.this.deleteLayout(entry.file.getPath());
                    }
                });
            } else {
                viewLayout.setVisibility(8);
                fileLayout.setVisibility(0);
                fileLayout.setPadding(0, 0, 0, (int) (10.0f * getContext().getResources().getDisplayMetrics().density));
                AppCompatRadioButton fileRadioButton2 = (AppCompatRadioButton) view.findViewById(R.id.designerViewlistentryFileRadioButton);
                fileRadioButton2.setFocusable(false);
                fileRadioButton2.setFocusableInTouchMode(false);
                fileRadioButton2.setVisibility(4);
                AppCompatTextView fileNameView2 = (AppCompatTextView) view.findViewById(R.id.designerViewlistentryFileName);
                fileNameView2.setText("New layout...");
                fileNameView2.setTypeface(Typeface.DEFAULT);
                ((AppCompatImageView) view.findViewById(R.id.designerViewlistFileImage)).setImageResource(AndroidHelper.obtainImageResourceId(getContext(), R.attr.icon_add));
                ((AppCompatImageView) view.findViewById(R.id.designerViewlistentryDelete)).setVisibility(8);
            }
            return view;
        }
    }
}