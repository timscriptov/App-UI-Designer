package com.mcal.uidesigner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mcal.uidesigner.common.StreamUtilities;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;

public class XmlLayoutResourceFinder {
    private final Context context;
    private final File resourcesDir;
    private Map<Integer, Map<String, String>> resourceValues;
    private Map<Integer, Map<String, String>> styleParents;
    private Map<Integer, SortedMap<String, Map<String, String>>> styles;

    public XmlLayoutResourceFinder(Context context, String resDirPath) {
        this.context = context;
        this.resourcesDir = resDirPath == null ? null : new File(resDirPath);
    }

    public void reload() {
        this.resourceValues = new HashMap<>();
        this.styles = new HashMap<>();
        this.styleParents = new HashMap<>();
        for (int sdk = 0; sdk < 30; sdk++) {
            this.styles.put(Integer.valueOf(sdk), new TreeMap<>());
            this.styleParents.put(Integer.valueOf(sdk), new HashMap<>());
            this.resourceValues.put(Integer.valueOf(sdk), new HashMap<>());
        }
        if (this.resourcesDir != null) {
            loadResources(0, new File(this.resourcesDir, "values"));
            for (int sdk2 = 1; sdk2 < 30; sdk2++) {
                loadResources(sdk2, new File(this.resourcesDir, "values-v" + sdk2));
            }
        }
    }

    private void loadResources(int sdk, File dir) {
        try {
            File[] arr = dir.listFiles();
            for (File xmlFile : arr) {
                if (xmlFile.getName().toLowerCase().endsWith(".xml")) {
                    InputStream xml = new FileInputStream(xmlFile);
                    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml);
                    xml.close();
                    loadStyles(doc, sdk);
                    loadValues(doc, sdk, "string");
                    loadValues(doc, sdk, "color");
                    loadValues(doc, sdk, "dimen");
                    loadValues(doc, sdk, "bool");
                    loadValues(doc, sdk, "integer");
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void loadValues(@NonNull Document doc, int sdk, String tag) {
        NodeList nodes = doc.getElementsByTagName(tag);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Node nameNode = node.getAttributes().getNamedItem("name");
            if (nameNode instanceof Attr) {
                String name = ((Attr) nameNode).getValue();
                this.resourceValues.get(Integer.valueOf(sdk)).put("@" + tag + "/" + name, node.getTextContent());
            }
        }
    }

    private void loadStyles(@NonNull Document doc, int sdk) throws DOMException {
        NodeList nodes = doc.getElementsByTagName("style");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Node styleNameNode = node.getAttributes().getNamedItem("name");
            if (styleNameNode instanceof Attr) {
                String styleName = ((Attr) styleNameNode).getValue();
                String parentStyleName = "";
                if (styleName.contains(".")) {
                    parentStyleName = styleName.substring(0, styleName.lastIndexOf(46));
                } else {
                    Node parentNameNode = node.getAttributes().getNamedItem("parent");
                    if (parentNameNode instanceof Attr) {
                        parentStyleName = ((Attr) parentNameNode).getValue();
                    }
                }
                this.styleParents.get(Integer.valueOf(sdk)).put(styleName, parentStyleName);
                Map<String, String> attrs = new HashMap<>();
                this.styles.get(Integer.valueOf(sdk)).put(styleName, attrs);
                NodeList itemNodes = node.getChildNodes();
                for (int j = 0; j < itemNodes.getLength(); j++) {
                    Node itemNode = itemNodes.item(j);
                    if (itemNode.getNodeName().equals("item")) {
                        Node itemNameNode = itemNode.getAttributes().getNamedItem("name");
                        if (itemNameNode instanceof Attr) {
                            attrs.put(((Attr) itemNameNode).getValue(), itemNode.getTextContent());
                        }
                    }
                }
            }
        }
    }

    public String findResourcePropertyValue(String value) {
        return findUserResourceValue(findUserAttributeValue(value));
    }

    private String findUserResourceValue(String rawValue) {
        if (rawValue != null && rawValue.startsWith("@")) {
            for (int sdk = Build.VERSION.SDK_INT; sdk >= 0; sdk--) {
                String value = this.resourceValues.get(Integer.valueOf(sdk)).get(rawValue);
                if (value != null) {
                    return value;
                }
            }
        }
        return rawValue;
    }

    private String findUserAttributeValue(String value) {
        String attrName = null;
        if (value != null && value.startsWith("?attr/")) {
            attrName = value.substring("?attr/".length());
        } else if (value != null && value.startsWith("?")) {
            attrName = value.substring(1);
        }
        if (attrName != null) {
            for (int sdk = Build.VERSION.SDK_INT; sdk >= 0; sdk--) {
                for (Map<String, String> values : this.styles.get(Integer.valueOf(sdk)).values()) {
                    if (values.containsKey(attrName)) {
                        return values.get(attrName);
                    }
                }
            }
        }
        return value;
    }

    public String findStyleAttributeValue(@NonNull String style, XmlLayoutProperties.PropertySpec property) {
        if (style.startsWith("@style/")) {
            return findStyleAttributeValue(style.substring("@style/".length()), property, new HashSet<>());
        }
        return null;
    }

    @Nullable
    private String findStyleAttributeValue(String style, XmlLayoutProperties.PropertySpec property, @NonNull Set<String> visitedStyles) {
        if (visitedStyles.contains(style)) {
            return null;
        }
        visitedStyles.add(style);
        for (int sdk = Build.VERSION.SDK_INT; sdk >= 0; sdk--) {
            Map<String, String> attrs = this.styles.get(Integer.valueOf(sdk)).get(style);
            if (attrs != null) {
                String value = attrs.get(property.attrName);
                if (value == null) {
                    return findStyleAttributeValue(this.styleParents.get(Integer.valueOf(sdk)).get(style), property, visitedStyles);
                }
                return value;
            }
        }
        return null;
    }

    public Drawable findUserDrawable(String resName) {
        if (!(this.resourcesDir == null || resName == null || !resName.startsWith("@drawable/"))) {
            String resName2 = resName.substring("@drawable/".length());

            Drawable drawablePngDensity = loadImageFile(new File(new File(this.resourcesDir, "drawable-" + getDensity()), resName2 + ".png"));
            if (drawablePngDensity != null) {
                return drawablePngDensity;
            }
            Drawable drawableJpgDensity = loadImageFile(new File(new File(this.resourcesDir, "drawable-" + getDensity()), resName2 + ".jpg"));
            if (drawableJpgDensity != null) {
                return drawableJpgDensity;
            }
            Drawable drawableNineDensity = loadNinePatchFile(new File(new File(this.resourcesDir, "drawable-" + getDensity()), resName2 + ".9.png"));
            if (drawableNineDensity != null) {
                return drawableNineDensity;
            }
            Drawable drawablePng = loadImageFile(new File(new File(this.resourcesDir, "drawable"), resName2 + ".png"));
            if (drawablePng != null) {
                return drawablePng;
            }
            Drawable drawableJpg = loadImageFile(new File(new File(this.resourcesDir, "drawable"), resName2 + ".jpg"));
            if (drawableJpg != null) {
                return drawableJpg;
            }
            Drawable drawableNine = loadNinePatchFile(new File(new File(this.resourcesDir, "drawable"), resName2 + ".9.png"));
            if (drawableNine != null) {
                return drawableNine;
            }

            String[] arrDpi = {"xxhpdi", "xhdpi", "hdpi", "mdpi", "ldpi"};
            int lenDpi = arrDpi.length;

            for (int i = 0; i < lenDpi; i++) {
                Drawable drawable = loadImageFile(new File(new File(this.resourcesDir, "drawable-" + arrDpi[i]), resName2 + ".png"));
                if (drawable != null) {
                    return drawable;
                }
            }

            for (int i = 0; i < lenDpi; i++) {
                Drawable drawable = loadImageFile(new File(new File(this.resourcesDir, "drawable-" + arrDpi[i]), resName2 + ".jpg"));
                if (drawable != null) {
                    return drawable;
                }
            }

            for (int i = 0; i < lenDpi; i++) {
                Drawable drawable = loadNinePatchFile(new File(new File(this.resourcesDir, "drawable-" + arrDpi[i]), resName2 + ".9.png"));
                if (drawable != null) {
                    return drawable;
                }
            }
        }
        return null;
    }

    @NonNull
    private String getDensity() {
        switch (this.context.getResources().getDisplayMetrics().densityDpi) {
            case 120:
                return "ldpi";
            case 160:
                return "mdpi";
            case 240:
                return "hdpi";
            case 320:
                return "xhdpi";
            default:
                return "";
        }
    }

    private Drawable loadNinePatchFile(File imageFile) {
        if (imageFile == null) {
            return null;
        }
        try {
            if (!imageFile.isFile()) {
                return null;
            }
            return NinePatchDrawable.createFromStream(new FileInputStream(imageFile), null);
        } catch (Exception e) {
            return null;
        }
    }

    private Drawable loadImageFile(File imageFile) {
        if (imageFile != null) {
            try {
                if (imageFile.isFile()) {
                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    InputStream stream = new FileInputStream(imageFile);
                    BitmapFactory.decodeStream(stream, null, o);
                    stream.close();
                    int loadedWidth = o.outWidth;
                    int loadedHeight = o.outHeight;
                    float ratio = ((float) loadedWidth) / ((float) loadedHeight);
                    int scale = 1;
                    while (true) {
                        if (loadedWidth / 2 < 500 && loadedHeight / 2 < 500) {
                            break;
                        }
                        loadedWidth /= 2;
                        loadedHeight /= 2;
                        scale *= 2;
                    }
                    int width = loadedWidth;
                    if (loadedHeight > 500) {
                        width = (int) (((float) 500) * ratio);
                    }
                    if (width > 500) {
                        int height = (int) (((float) 500) / ratio);
                    }
                    BitmapFactory.Options o2 = new BitmapFactory.Options();
                    o2.inSampleSize = scale;
                    InputStream stream2 = new FileInputStream(imageFile);
                    Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
                    stream2.close();
                    if (bitmap == null) {
                        return null;
                    }
                    return new BitmapDrawable(this.context.getResources(), bitmap);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public List<String> getAllUserStyles() {
        Set<String> result = new HashSet<>();
        for (Map<String, Map<String, String>> s : this.styles.values()) {
            Iterator<String> i = s.keySet().iterator();
            while (i.hasNext()) {
                result.add("@style/" + i.next());
            }
        }
        return new ArrayList<>(result);
    }

    public List<String> getAllUserDrawables() {
        Set<String> result = new HashSet<>();
        if (this.resourcesDir != null) {
            File[] arr = this.resourcesDir.listFiles();
            for (File drawableDir : arr) {
                if (drawableDir.isDirectory() && drawableDir.getName().startsWith("drawable")) {
                    for (File drawableFile : drawableDir.listFiles()) {
                        String name = drawableFile.getName();
                        if (name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".xml")) {
                            result.add("@drawable/" + name.substring(0, name.length() - 4));
                        }
                    }
                }
            }
        }
        return new ArrayList<>(result);
    }

    public String getBaseStyle(String style) {
        if (style == null) {
            return null;
        }
        return style.startsWith("@style/") ? getBaseStyle(style.substring("@style/".length()), new HashSet()) : style;
    }

    @Nullable
    private String getBaseStyle(String style, @NonNull Set<String> visitedStyles) {
        if (visitedStyles.contains(style)) {
            return null;
        }
        visitedStyles.add(style);
        for (int sdk = Build.VERSION.SDK_INT; sdk >= 0; sdk--) {
            String parentStyle = this.styleParents.get(Integer.valueOf(sdk)).get(style);
            if (parentStyle != null && parentStyle.length() > 0) {
                return getBaseStyle(parentStyle, visitedStyles);
            }
        }
        if (style.startsWith("android:")) {
            return "@android:style/" + style.substring("android:".length());
        }
        return "@style/" + style;
    }

    public String suggestUserDrawableName() {
        for (int i = 1; i < 1000; i++) {
            if (findUserDrawable("@drawable/image_" + i) == null) {
                return "image_" + i;
            }
        }
        return "image";
    }

    public void addUserDrawable(String name, @NonNull Intent data) {
        try {
            File dir = new File(this.resourcesDir, "drawable");
            dir.mkdirs();
            StreamUtilities.transfer(this.context.getContentResolver().openInputStream(data.getData()), new FileOutputStream(new File(dir, name + ".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
