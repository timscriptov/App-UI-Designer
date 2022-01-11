package com.mcal.uidesigner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mcal.designer.R;
import com.mcal.uidesigner.appwizard.AppWizardDesignActivity;
import com.mcal.uidesigner.common.MessageBox;
import com.mcal.uidesigner.common.ValueRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlLayoutWidgetPicker {

    public static void selectRootView(Activity activity, String title, ValueRunnable<NewWidget> ok) {
        selectView(activity, title, false, true, ok);
    }

    public static void selectSurroundView(Activity activity, String title, ValueRunnable<NewWidget> ok) {
        selectView(activity, title, true, false, ok);
    }

    public static void selectView(Activity activity, String title, ValueRunnable<NewWidget> ok) {
        selectView(activity, title, false, false, ok);
    }

    public static void setTextAppearance(TextView textView, int attrID) {
        Resources.Theme theme = textView.getContext().getTheme();
        TypedValue styleID = new TypedValue();
        if (theme.resolveAttribute(attrID, styleID, true)) {
            textView.setTextAppearance(textView.getContext(), styleID.data);
        }
    }

    private static void selectView(Activity activity, final String title, boolean onlyLayouts, final boolean onlyRootViews, final ValueRunnable<NewWidget> ok) {
        final List<List<Widget>> widgets = getWidgetsToShow(onlyLayouts, onlyRootViews, activity instanceof AppWizardDesignActivity);
        MessageBox.showDialog(activity, new MessageBox() {
            @Override
            public Dialog buildDialog(Activity activity2) {
                ExpandableListView listView = new ExpandableListView(activity2);
                listView.setAdapter(new WidgetListEntryAdapter(activity2, widgets));
                final AlertDialog dialog = new AlertDialog.Builder(activity2).setCancelable(true).setView(listView).setTitle(title).create();
                listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        dialog.dismiss();
                        Widget widget = (Widget) ((List) widgets.get(groupPosition)).get(childPosition);
                        Map<String, String> attributes = new HashMap<>();
                        attributes.putAll(widget.getAttributes());
                        if (onlyRootViews) {
                            if (!attributes.containsKey("android:layout_width")) {
                                attributes.put("android:layout_width", "match_parent");
                            }
                            if (!attributes.containsKey("android:layout_height")) {
                                attributes.put("android:layout_height", "match_parent");
                            }
                        } else {
                            if (!attributes.containsKey("android:layout_width")) {
                                attributes.put("android:layout_width", "wrap_content");
                            }
                            if (!attributes.containsKey("android:layout_height")) {
                                attributes.put("android:layout_height", "wrap_content");
                            }
                        }
                        ok.run(new NewWidget(widget.getElementName(), attributes));
                        return true;
                    }
                });
                dialog.setCanceledOnTouchOutside(true);
                return dialog;
            }
        });
    }

    private static List<List<Widget>> getWidgetsToShow(boolean onlyLayouts, boolean onlyRootViews, boolean onlyAppwizard) {
        List<List<Widget>> widgets = new ArrayList<>();
        String category = "";
        Widget[] arr$ = Widget.values();
        for (Widget widget : arr$) {
            if ((!onlyLayouts || widget.isLayout()) && ((!onlyRootViews || widget.isRootView()) && (!onlyAppwizard || !widget.isAppLayout()))) {
                if (!category.equals(widget.getCategory())) {
                    widgets.add(new ArrayList<>());
                    category = widget.getCategory();
                }
                widgets.get(widgets.size() - 1).add(widget);
            }
        }
        return widgets;
    }

    public enum Widget {
        Button("Button", "Widget", new WidgetPreview() {
            @Override
            public View create(Context context) {
                Button button = new Button(context);
                button.setText("Button");
                button.setFocusable(false);
                return button;
            }
        }, "Button", "android:text", "Button"),
        ButtonSmall("Button (small)", "Widget", new WidgetPreview() {
            @Override
            public View create(Context context) {
                Button button = new Button(context, null, 16842825);
                button.setText("Small Button");
                button.setFocusable(false);
                return button;
            }
        }, "Button", "style", "?android:attr/buttonStyleSmall", "android:text", "Small Button"),
        ImageButton("ImageButton", "Widget", new WidgetPreview() {
            @SuppressLint("ResourceType")
            @Override
            public View create(Context context) {
                ImageButton button = new ImageButton(context);
                button.setImageResource(17301560);
                button.setFocusable(false);
                return button;
            }
        }, "ImageButton", "android:src", "@android:drawable/ic_menu_close_clear_cancel"),
        BarButton("Bar Button", "Widget", new WidgetPreview() {
            @Override
            public View create(Context context) {
                Button button = new Button(context, null, 16843567);
                button.setText("Bar Button");
                button.setFocusable(false);
                return button;
            }
        }, "Button", "style", "?android:attr/buttonBarButtonStyle", "android:text", "Bar Button"),
        BarImageButton("BarImageButton", "Widget", new WidgetPreview() {
            @SuppressLint("ResourceType")
            @Override
            public View create(Context context) {
                ImageButton button = new ImageButton(context, null, 16843567);
                button.setImageResource(17301560);
                button.setFocusable(false);
                return button;
            }
        }, "ImageButton", "style", "?android:attr/buttonBarButtonStyle", "android:src", "@android:drawable/ic_menu_close_clear_cancel"),
        ToggleButton("ToggleButton", "Widget", new WidgetPreview() {
            @Override
            public View create(Context context) {
                ToggleButton button = new ToggleButton(context);
                button.setFocusable(false);
                return button;
            }
        }),
        Switch("Switch", "Widget", new WidgetPreview() {
            @Override
            public View create(Context context) {
                Switch preview = new Switch(context);
                preview.setFocusable(false);
                return preview;
            }
        }),
        CheckBox("CheckBox", "Widget", new WidgetPreview() {
            @Override
            public View create(Context context) {
                CheckBox preview = new CheckBox(context);
                preview.setFocusable(false);
                preview.setText("CheckBox");
                return preview;
            }
        }),
        RadioButton("RadioButton", "Widget", new WidgetPreview() {
            @Override
            public View create(Context context) {
                RadioButton preview = new RadioButton(context);
                preview.setFocusable(false);
                preview.setText("RadioButton");
                return preview;
            }
        }),
        SeekBar("SeekBar", "Widget", new WidgetPreview() {
            @Override
            public View create(Context context) {
                SeekBar bar = new SeekBar(context);
                bar.setFocusable(false);
                LinearLayout preview = new LinearLayout(context);
                preview.addView(bar, new LinearLayout.LayoutParams((int) (150.0f * context.getResources().getDisplayMetrics().density), -2));
                return preview;
            }
        }),
        TextView("TextView", "View", new WidgetPreview() {
            @Override
            public View create(Context context) {
                TextView textView = new TextView(context);
                textView.setText("Text");
                return textView;
            }
        }, "TextView", "android:text", "Text"),
        TextViewSmall("TextView (small)", "View", new WidgetPreview() {
            @Override
            public View create(Context context) {
                TextView textView = new TextView(context);
                textView.setText("Small Text");
                XmlLayoutWidgetPicker.setTextAppearance(textView, 16842818);
                return textView;
            }
        }, "TextView", "android:textAppearance", "?android:attr/textAppearanceSmall", "android:text", "Small Text"),
        TextViewMedium("TextView (medium)", "View", new WidgetPreview() {
            @Override
            public View create(Context context) {
                TextView textView = new TextView(context);
                textView.setText("Medium Text");
                XmlLayoutWidgetPicker.setTextAppearance(textView, 16842817);
                return textView;
            }
        }, "TextView", "android:textAppearance", "?android:attr/textAppearanceMedium", "android:text", "Medium Text"),
        TextViewLarge("TextView (large)", "View", new WidgetPreview() {
            @Override
            public View create(Context context) {
                TextView textView = new TextView(context);
                textView.setText("Large Text");
                XmlLayoutWidgetPicker.setTextAppearance(textView, 16842816);
                return textView;
            }
        }, "TextView", "android:textAppearance", "?android:attr/textAppearanceLarge", "android:text", "Large Text"),
        DividerVertical("Vertical Divider", "View", new WidgetPreview() {
            @Override
            public View create(final Context context) {
                View view = new View(context) {
                    @Override
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        setMeasuredDimension((int) (30.0f * context.getResources().getDisplayMetrics().density), (int) (1.0f * context.getResources().getDisplayMetrics().density));
                    }
                };
                view.setBackgroundDrawable(context.obtainStyledAttributes(new int[]{16843530}).getDrawable(0));
                return view;
            }
        }, "View", "android:background", "?android:attr/dividerVertical", "android:layout_height", "1dp", "android:layout_width", "match_parent"),
        DividerHorizontal("Horizontal Divider", "View", new WidgetPreview() {
            @Override
            public View create(final Context context) {
                View view = new View(context) {
                    @Override
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        setMeasuredDimension((int) (1.0f * context.getResources().getDisplayMetrics().density), (int) (30.0f * context.getResources().getDisplayMetrics().density));
                    }
                };
                view.setBackgroundDrawable(context.obtainStyledAttributes(new int[]{16843564}).getDrawable(0));
                return view;
            }
        }, "View", "android:background", "?android:attr/dividerHorizontal", "android:layout_width", "1dp", "android:layout_height", "match_parent"),
        ImageView("ImageView", "View", new WidgetPreview() {
            @SuppressLint("ResourceType")
            @Override
            public View create(Context context) {
                ImageView preview = new ImageView(context);
                preview.setImageResource(17301533);
                return preview;
            }
        }, "ImageView", "android:src", "@android:drawable/ic_delete"),
        ProgressBar("ProgressBar", "View", new WidgetPreview() {
            @Override
            public View create(Context context) {
                return new ProgressBar(context);
            }
        }),
        ProgressBarLarge("ProgressBar (large)", "View", new WidgetPreview() {
            @Override
            public View create(Context context) {
                return new ProgressBar(context, null, 16842874);
            }
        }, "ProgressBar", "style", "?android:attr/progressBarStyleLarge"),
        ProgressBarHorizontal("ProgressBar (horizontal)", "View", new WidgetPreview() {
            @Override
            public View create(Context context) {
                ProgressBar preview = new ProgressBar(context, null, 16842872);
                preview.setMax(100);
                preview.setProgress(50);
                return preview;
            }
        }, "ProgressBar", "style", "?android:attr/progressBarStyleHorizontal"),
        EditText("EditText", "Text Field", "EditText", "android:ems", "10"),
        EditTextMultiLine("EditText (multiline)", "Text Field", "EditText", "android:inputType", "textMultiLine", "android:ems", "10"),
        EditTextPassword("EditText (password)", "Text Field", "EditText", "android:inputType", "textPassword", "android:ems", "10"),
        EditTextNumberPassword("EditText (number password)", "Text Field", "EditText", "android:inputType", "numberPassword", "android:ems", "10"),
        EditTextEMail("EditText (email)", "Text Field", "EditText", "android:inputType", "textEmailAddress", "android:ems", "10"),
        EditTextPasswordName("EditText (name)", "Text Field", "EditText", "android:inputType", "textPersonName", "android:ems", "10"),
        EditTextPasswordPhone("EditText (phone)", "Text Field", "EditText", "android:inputType", "phone", "android:ems", "10"),
        EditTextAddress("EditText (address)", "Text Field", "EditText", "android:inputType", "textPostalAddress", "android:ems", "10"),
        EditTextTime("EditText (time)", "Text Field", "EditText", "android:inputType", "time", "android:ems", "10"),
        EditTextDate("EditText (date)", "Text Field", "EditText", "android:inputType", "date", "android:ems", "10"),
        EditTextNumber("EditText (number)", "Text Field", "EditText", "android:inputType", "number", "android:ems", "10"),
        EditTextNumberSigned("EditText (signed number)", "Text Field", "EditText", "android:inputType", "numberSigned", "android:ems", "10"),
        EditTextDecimal("EditText (decimal)", "Text Field", "EditText", "android:inputType", "numberDecimal", "android:ems", "10"),
        DatePicker("DatePicker", "Advanced Widget"),
        TimePicker("TimePicker", "Advanced Widget"),
        NumberPicker("NumberPicker", "Advanced Widget"),
        RatingBar("RatingBar", "Advanced Widget"),
        ListView("List View", "Adapter View", "ListView", new String[0]),
        ExpandableListView("Expandable List View", "Adapter View", "ExpandableListView", new String[0]),
        Spinner("Spinner", "Adapter View"),
        RelativeLayout("RelativeLayout", "Layout"),
        LinearLayoutH("LinearLayout (horizontal)", "Layout", "LinearLayout", "android:orientation", "horizontal"),
        LinearLayoutV("LinearLayout (vertical)", "Layout", "LinearLayout", "android:orientation", "vertical"),
        ScrollView("ScrollView", "Scroll View"),
        HorizontalScrollView("HorizontalScrollView", "Scroll View"),
        ButtonBar("Button Bar", "Advanced Layout", "LinearLayout", "style", "?android:attr/buttonBarStyle", "android:orientation", "horizontal"),
        GridLayout("GridLayout", "Advanced Layout", "GridLayout", "rowCount", "1", "columnCount", "1"),
        FrameLayout("FrameLayout", "Advanced Layout"),
        RadioGroup("RadioGroup", "Advanced Layout", "RadioGroup", "android:orientation", "vertical"),
        TableLayout("TableLayout", "Advanced Layout"),
        TableRow("TableRow", "Advanced Layout"),
        AbsoluteLayout("AbsoluteLayout", "Advanced Layout"),
        DrawerLayout("Drawer Layout", "App Layout", "android.support.v4.widget.DrawerLayout", new String[0]),
        ViewPager("View Pager", "App Layout", "android.support.v4.widget.ViewPager", new String[0]);
        //AppCompatButton("AppCompatButton", "App Layout", "android.support.v4.widget.AppCompatButton", new String[0]);

        private final Map<String, String> attributes;
        private final String category;
        private final String elementName;
        private final String name;
        private final WidgetPreview preview;

        Widget(String name, String category) {
            this(name, category, name, new String[0]);
        }

        Widget(String name, String category, WidgetPreview preview) {
            this(name, category, preview, name, new String[0]);
        }

        Widget(String name, String category, String elementName, String... attributes) {
            this(name, category, null, elementName, attributes);
        }

        Widget(String name, String category, WidgetPreview preview, String elementName, String... attributes) {
            this.name = name;
            this.elementName = elementName;
            this.category = category;
            this.preview = preview;
            this.attributes = new HashMap<>();
            for (int i = 0; i < attributes.length; i += 2) {
                this.attributes.put(attributes[i], attributes[i + 1]);
            }
        }

        public Map<String, String> getAttributes() {
            return this.attributes;
        }

        public String getElementName() {
            return this.elementName;
        }

        public String getName() {
            return this.name;
        }

        public boolean isAppLayout() {
            return "App Layout".equals(this.category);
        }

        public boolean isRootView() {
            return isLayout() || "Adapter View".equals(this.category);
        }

        public boolean isLayout() {
            return "Layout".equals(this.category) || "Advanced Layout".equals(this.category) || "Scroll View".equals(this.category) || "App Layout".equals(this.category);
        }

        public String getCategory() {
            return this.category;
        }

        public View createPreview(Context context) {
            if (this.preview != null) {
                try {
                    View view = this.preview.create(context);
                    if (view == null) {
                        return view;
                    }
                    view.setClickable(false);
                    return view;
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
            return null;
        }

        public String getHelpUrl() {
            return "android/widget/" + this.elementName + ".html";
        }
    }

    public interface WidgetPreview {
        View create(Context context);
    }

    private static class WidgetListEntryAdapter extends BaseExpandableListAdapter {
        private final Context context;
        private final List<List<Widget>> widgets;

        public WidgetListEntryAdapter(Context context, List<List<Widget>> widgets) {
            this.context = context;
            this.widgets = widgets;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(this.context).inflate(R.layout.designer_widgetlist_group, parent, false);
            }
            ((TextView) view.findViewById(R.id.widgetlistGroupName)).setText(((Widget) getChild(groupPosition, 0)).getCategory());
            return view;
        }

        @SuppressLint("WrongConstant")
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(this.context).inflate(R.layout.designer_widgetlist_entry, parent, false);
            }
            Widget widget = (Widget) getChild(groupPosition, childPosition);
            View preview = widget.createPreview(this.context);
            TextView nameView = (TextView) view.findViewById(R.id.widgetlistName);
            nameView.setText(widget.getName());
            nameView.setVisibility(preview == null ? 0 : 8);
            ViewGroup previewContainer = (ViewGroup) view.findViewById(R.id.widgetlistPreview);
            previewContainer.setVisibility(preview != null ? 0 : 8);
            previewContainer.removeAllViews();
            if (preview != null) {
                previewContainer.addView(preview, new ViewGroup.LayoutParams(-2, -2));
            }
            final String helpUrl = widget.getHelpUrl();
            View helpView = view.findViewById(R.id.widgetlistHelp);
            helpView.setVisibility(helpUrl == null ? 8 : 0);
            if (helpUrl != null) {
                helpView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((XmlLayoutDesignActivity) WidgetListEntryAdapter.this.context).showHelp(helpUrl);
                    }
                });
            }
            return view;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.widgets.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.widgets.get(groupPosition).get(childPosition);
        }

        @Override
        public int getGroupCount() {
            return this.widgets.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.widgets.get(groupPosition).size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return (long) groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
