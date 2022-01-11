package com.mcal.uidesigner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.mcal.uidesigner.common.ColorPickerDialog;
import com.mcal.uidesigner.common.MessageBox;
import com.mcal.uidesigner.common.SizePickerDialog;
import com.mcal.uidesigner.common.ValueRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class XmlLayoutPropertyEditor {
    public static final int PICK_IMAGE_REQUEST_CODE = 3424345;
    private static AttributeValue pickImageAttribute;
    private static XmlLayoutEditView pickImageEditView;

    public static void addImageFromPicker(Activity activity, final Intent data) {
        if (pickImageEditView != null) {
            MessageBox.queryText(activity, "Choose Name", "Enter a name for the image", pickImageEditView.suggestUserDrawableName(), new ValueRunnable<String>() {
                public void run(String t) {
                    XmlLayoutPropertyEditor.pickImageEditView.addUserDrawable(t, data);
                    XmlLayoutPropertyEditor.pickImageEditView.setAttribute(XmlLayoutPropertyEditor.pickImageAttribute, "@drawable/" + t);
                    XmlLayoutEditView unused = XmlLayoutPropertyEditor.pickImageEditView = null;
                    AttributeValue unused2 = XmlLayoutPropertyEditor.pickImageAttribute = null;
                }
            });
        }
    }

    public static void queryValue(Activity activity, XmlLayoutEditView editView, AttributeValue attribute) {
        switch (attribute.property.type) {
            case Drawable:
                queryDrawable(activity, editView, attribute);
                return;
            case DrawableResource:
                queryDrawableResource(activity, editView, attribute);
                return;
            case Color:
                queryColor(activity, editView, attribute);
                return;
            case Float:
                queryTextValue(activity, editView, attribute, "1.0");
                return;
            case Int:
                queryTextValue(activity, editView, attribute, "1");
                return;
            case TextSize:
                querySize(activity, editView, attribute, attribute.value, "10sp");
                return;
            case Size:
            case FloatSize:
                querySize(activity, editView, attribute, attribute.value, "10dp");
                return;
            case LayoutSize:
                queryLayoutSize(activity, editView, attribute);
                return;
            case Bool:
                queryBoolean(activity, editView, attribute);
                return;
            case Text:
                queryTextValue(activity, editView, attribute, "");
                return;
            case Event:
                queryTextValue(activity, editView, attribute, "");
                return;
            case EnumConstant:
                queryEnumValue(activity, editView, attribute);
                return;
            case IntConstant:
                queryIntConstantValue(activity, editView, attribute);
                return;
            case ID:
                queryID(activity, editView, attribute);
                return;
            case TextAppearance:
                queryTextAppearance(activity, editView, attribute);
                return;
            default:
                queryTextValue(activity, editView, attribute, "");
                return;
        }
    }

    private static void queryTextAppearance(Activity activity, XmlLayoutEditView editView, AttributeValue attribute) {
        queryFromListOrOther(activity, editView, attribute, "?android:attr/", "?android:attr/textAppearanceSmall", "?android:attr/textAppearanceMedium", "?android:attr/textAppearanceLarge");
    }

    private static void queryID(final Activity activity, final XmlLayoutEditView editView, final AttributeValue attribute) {
        if (attribute.value == null) {
            startSelectingOtherView(activity, editView, attribute);
        } else {
            MessageBox.queryFromList(activity, attribute.property.getDisplayName(), Arrays.asList("View...", "none"), new ValueRunnable<String>() {
                public void run(String t) {
                    if (t.equals("View...")) {
                        XmlLayoutPropertyEditor.startSelectingOtherView(activity, editView, attribute);
                    } else if (t.equals("id...")) {
                        List<String> values = new ArrayList<>(editView.getAllIDs());
                        Collections.sort(values);
                        MessageBox.queryFromList(activity, attribute.property.getDisplayName(), values, new ValueRunnable<String>() {
                            public void run(String t2) {
                                editView.setIDAttribute(attribute, null, t2);
                            }
                        });
                    } else {
                        editView.setAttribute(attribute, null);
                    }
                }
            });
        }
    }

    @SuppressLint("WrongConstant")
    public static void startSelectingOtherView(final Activity activity, final XmlLayoutEditView editView, final AttributeValue attribute) {
        Toast.makeText(activity, "Select another view", 0).show();
        editView.startSelectingOtherView(new ValueRunnable<XmlLayoutEditView>() {
            public void run(XmlLayoutEditView otherView) {
                if (otherView.getViewID() == null) {
                    editView.setIDAttribute(attribute, otherView, otherView.suggestViewID());
                } else {
                    editView.setIDAttribute(attribute, null, otherView.getViewID());
                }
                Toast.makeText(activity, "View was selected for attribute " + attribute.property.getDisplayName(), 0).show();
            }
        });
    }

    private static void queryIntConstantValue(Activity activity, XmlLayoutEditView editView, AttributeValue attribute) {
        if ("android:visibility".equals(attribute.property.attrName)) {
            querySingleValue(activity, editView, attribute, "visible", "invisible", "gone");
        } else if ("android:orientation".equals(attribute.property.attrName)) {
            querySingleValue(activity, editView, attribute, "horizontal", "vertical");
        } else if ("android:typeface".equals(attribute.property.attrName)) {
            querySingleValue(activity, editView, attribute, "normal", "sans", "serif", "monospace");
        } else if ("android:alignmentMode".equals(attribute.property.attrName)) {
            querySingleValue(activity, editView, attribute, "alignBounds", "alignMargins");
        } else if ("android:textAlignment".equals(attribute.property.attrName)) {
            querySingleValue(activity, editView, attribute, "inherit", "gravity", "textStart", "textEnd", "center", "viewStart", "viewEnd");
        } else if ("android.view.Gravity".equals(attribute.property.constantClassName)) {
            queryMultipleValues(activity, editView, attribute, "top", "bottom", "left", "right", "center", "center_vertical", "center_horizontal", "fill", "fill_vertical", "fill_horizontal", "clip_vertical", "clip_horizontal", "start", "end");
        } else if (attribute.property.constantFieldPrefix == null || attribute.property.setterProxyClass == null) {
            queryTextValue(activity, editView, attribute, "");
        } else {
            List<String> values = new ArrayList<>();
            Field[] arr$ = attribute.property.constantClass.getFields();
            for (Field field : arr$) {
                String fieldName = field.getName();
                if ((field.getModifiers() & 8) != 0 && fieldName.startsWith(attribute.property.constantFieldPrefix)) {
                    values.add(fieldName.substring(attribute.property.constantFieldPrefix.length()).replace("_", ""));
                }
            }
            Collections.sort(values);
            queryMultipleValues(activity, editView, attribute, values);
        }
    }

    private static void queryMultipleValues(Activity activity, XmlLayoutEditView editView, AttributeValue attribute, List<String> values) {
        queryMultipleValues(activity, editView, attribute, (String[]) values.toArray(new String[values.size()]));
    }

    private static void queryMultipleValues(Activity activity, final XmlLayoutEditView editView, final AttributeValue attribute, String... values) {
        ArrayList<String> displayValues = new ArrayList<>();
        for (String value : values) {
            displayValues.add(AttributeValue.getDisplayValue(value));
        }
        MessageBox.queryMultipleValues(activity, attribute.property.getDisplayName(), Arrays.asList(values), displayValues, attribute.value, new ValueRunnable<String>() {
            public void run(String value2) {
                editView.setAttribute(attribute, value2);
            }
        });
    }

    private static void queryEnumValue(Activity activity, XmlLayoutEditView editView, AttributeValue attribute) {
        if ("android.widget.ImageView$ScaleType".equals(attribute.property.constantClassName)) {
            querySingleValue(activity, editView, attribute, "matrix", "fitXY", "fitStart", "fitCenter", "fitEnd", "center", "centerCrop", "centerInside");
        } else if ("android.text.TextUtils$TruncateAt".equals(attribute.property.constantClassName)) {
            querySingleValue(activity, editView, attribute, "start", "middle", "end", "marquee");
        } else {
            queryTextValue(activity, editView, attribute, "");
        }
    }

    private static void querySingleValue(Activity activity, final XmlLayoutEditView editView, final AttributeValue attribute, final String... values) {
        final ArrayList<String> listValues = new ArrayList<>();
        for (String value : values) {
            listValues.add(AttributeValue.getDisplayValue(value));
        }
        listValues.add("none");
        MessageBox.queryIndexFromList(activity, attribute.property.getDisplayName(), listValues, new ValueRunnable<Integer>() {
            public void run(Integer i) {
                if (((String) listValues.get(i.intValue())).equals("none")) {
                    editView.setAttribute(attribute, null);
                } else {
                    editView.setAttribute(attribute, values[i.intValue()]);
                }
            }
        });
    }

    private static void queryDrawable(final Activity activity, final XmlLayoutEditView editView, final AttributeValue attribute) {
        if (attribute.value == null) {
            MessageBox.queryFromList(activity, attribute.property.getDisplayName(), Arrays.asList("Color...", "Drawable...", "none"), new ValueRunnable<String>() {
                public void run(String t) {
                    if (t.equals("Color...")) {
                        XmlLayoutPropertyEditor.queryColor(activity, editView, attribute);
                    } else if (t.equals("Drawable...")) {
                        XmlLayoutPropertyEditor.queryDrawableResource(activity, editView, attribute);
                    } else {
                        editView.setAttribute(attribute, null);
                    }
                }
            });
        } else if (attribute.value.startsWith("#")) {
            queryColor(activity, editView, attribute);
        } else {
            queryDrawableResource(activity, editView, attribute);
        }
    }


    public static void queryImageFromPicker(Activity activity, XmlLayoutEditView editView, AttributeValue attribute) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
        pickImageEditView = editView;
        pickImageAttribute = attribute;
    }

    public static void queryDrawableResource(final Activity activity, final XmlLayoutEditView editView, final AttributeValue attribute) {
        final ArrayList<String> values2 = new ArrayList<>(editView.getAllUserDrawables());
        Collections.sort(values2);
        final List<String> listValues = new ArrayList<>();
        Iterator<String> i = values2.iterator();
        while (i.hasNext()) {
            listValues.add(AttributeValue.getDisplayValue(i.next()));
        }
        listValues.add("other...");
        listValues.add("add...");
        listValues.add("none");
        MessageBox.queryIndexFromList(activity, attribute.property.getDisplayName(), listValues, new ValueRunnable<Integer>() {
            public void run(Integer i) {
                String t = (String) listValues.get(i.intValue());
                if (t.equals("none")) {
                    editView.setAttribute(attribute, null);
                } else if (t.equals("other...")) {
                    XmlLayoutPropertyEditor.queryTextValue(activity, editView, attribute, "@drawable/");
                } else if (t.equals("add...")) {
                    XmlLayoutPropertyEditor.queryImageFromPicker(activity, editView, attribute);
                } else {
                    editView.setAttribute(attribute, (String) values2.get(i.intValue()));
                }
            }
        });
    }

    private static void queryFromListOrOther(Activity activity, XmlLayoutEditView editView, AttributeValue attribute, String prefix, String... values) {
        queryFromListOrOther(activity, editView, attribute, prefix, Arrays.asList(values));
    }

    private static void queryFromListOrOther(final Activity activity, final XmlLayoutEditView editView, final AttributeValue attribute, final String defaultValue, List<String> values) {
        final ArrayList<String> values2 = new ArrayList<>(values);
        Collections.sort(values2);
        final List<String> listValues = new ArrayList<>();
        Iterator<String> i = values2.iterator();
        while (i.hasNext()) {
            listValues.add(AttributeValue.getDisplayValue(i.next()));
        }
        listValues.add("other...");
        listValues.add("none");
        MessageBox.queryIndexFromList(activity, attribute.property.getDisplayName(), listValues, new ValueRunnable<Integer>() {
            public void run(Integer i) {
                String t = (String) listValues.get(i.intValue());
                if (t.equals("none")) {
                    editView.setAttribute(attribute, null);
                } else if (t.equals("other...")) {
                    XmlLayoutPropertyEditor.queryTextValue(activity, editView, attribute, defaultValue);
                } else {
                    editView.setAttribute(attribute, (String) values2.get(i.intValue()));
                }
            }
        });
    }


    public static void queryColor(Activity activity, final XmlLayoutEditView editView, final AttributeValue attribute) {
        MessageBox.showDialog(activity, new ColorPickerDialog(attribute.property.getDisplayName(), attribute.value, new ColorPickerDialog.ColorRunnable() {
            @Override
            public void run(int color, String hexColor) {
                editView.setAttribute(attribute, hexColor);
            }
        }));
    }

    private static void queryBoolean(Activity activity, XmlLayoutEditView editView, AttributeValue attribute) {
        querySingleValue(activity, editView, attribute, "true", "false");
    }

    private static void queryLayoutSize(final Activity activity, final XmlLayoutEditView editView, final AttributeValue attribute) {
        MessageBox.queryFromList(activity, attribute.property.getDisplayName(), Arrays.asList("Wrap Content", "Match Parent", "Fixed size..."), new ValueRunnable<String>() {
            public void run(String t) {
                if (t.equals("Wrap Content")) {
                    editView.setAttribute(attribute, "wrap_content");
                } else if (t.equals("Match Parent")) {
                    editView.setAttribute(attribute, "match_parent");
                } else {
                    String v = "10dp";
                    if (!"match_parent".equals(attribute.value) && !"wrap_content".equals(attribute.value)) {
                        v = attribute.value;
                    }
                    XmlLayoutPropertyEditor.querySize(activity, editView, attribute, v, "10dp");
                }
            }
        });
    }


    public static void querySize(Activity activity, final XmlLayoutEditView editView, final AttributeValue attribute, String attributeValue, String defaultValue) {
        String v = attributeValue;
        if (v == null) {
            v = defaultValue;
        }
        MessageBox.showDialog(activity, new SizePickerDialog(attribute.property.getDisplayName(), v, new ValueRunnable<String>() {
            public void run(String t) {
                if (t.length() == 0) {
                    t = null;
                }
                editView.setAttribute(attribute, t);
            }
        }, new Runnable() {
            @Override
            public void run() {
                editView.setAttribute(attribute, null);
            }
        }));
    }


    public static void queryTextValue(Activity activity, final XmlLayoutEditView editView, final AttributeValue attribute, String defaultValue) {
        String v = attribute.value;
        if (v == null) {
            v = defaultValue;
        }
        MessageBox.queryText(activity, attribute.property.getDisplayName(), null, "None", v, new ValueRunnable<String>() {
            public void run(String t) {
                if (t.length() == 0) {
                    t = null;
                }
                editView.setAttribute(attribute, t);
            }
        }, new Runnable() {
            @Override
            public void run() {
                editView.setAttribute(attribute, null);
            }
        });
    }

    public static void queryStyle(final Activity activity, final XmlLayoutEditView editView) {
        List<String> styles = new ArrayList<>(editView.getAllUserStyles());
        Collections.sort(styles);
        styles.add("other...");
        styles.add("none");
        MessageBox.queryFromList(activity, "Style", styles, new ValueRunnable<String>() {
            public void run(String t) {
                if (t.equals("none")) {
                    editView.setStyle(null);
                }
                if (t.equals("other...")) {
                    MessageBox.queryText(activity, "Style", null, "None", editView.getStyle(), new ValueRunnable<String>() {
                        public void run(String t2) {
                            if (t2.length() == 0) {
                                editView.setStyle(null);
                            } else {
                                editView.setStyle(t2);
                            }
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            editView.setStyle(null);
                        }
                    });
                } else {
                    editView.setStyle(t);
                }
            }
        });
    }

    public static void queryID(Activity activity, final XmlLayoutEditView editView) {
        String id;
        if (editView.getViewID() != null) {
            id = editView.getViewID();
        } else {
            id = editView.suggestViewID();
        }
        MessageBox.queryText(activity, "ID", null, "None", id, new ValueRunnable<String>() {
            public void run(String t) {
                if (t.length() == 0) {
                    t = null;
                }
                editView.setViewID(t);
            }
        }, new Runnable() {
            @Override
            public void run() {
                editView.setViewID(null);
            }
        });
    }
}
