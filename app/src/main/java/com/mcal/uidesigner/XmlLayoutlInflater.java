package com.mcal.uidesigner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.ClipboardManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.uidesigner.R;
import com.mcal.uidesigner.widget.ClickableBorder;
import com.mcal.uidesigner.common.PositionalXMLReader;
import com.mcal.uidesigner.common.UndoManager;
import com.mcal.uidesigner.common.ValueRunnable;
import com.mcal.uidesigner.utils.Utils;

import org.jetbrains.annotations.Contract;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public abstract class XmlLayoutlInflater implements UndoManager.UndoRedoListener {
    private final Context context;
    private final XmlLayoutResourceFinder finder;
    private final ViewGroup parentView;
    private final UndoManager undoManager;
    private final Map<String, Node> id2Node = new HashMap<>();
    private final Map<String, Integer> id2Int = new HashMap<>();
    private final List<XmlLayoutEditView> editViews = new ArrayList<>();
    private boolean border;
    private Document document;
    private Throwable documentParseException;
    private boolean editMode;
    private ValueRunnable<XmlLayoutEditView> selectModeClickRunnable;
    private ArrayList<Element> selectModeNodes;
    private String xmlContent;
    private String xmlFilePath;

    public XmlLayoutlInflater(@NonNull ViewGroup parent, String xmlFilePath, String resDirPath, @NonNull UndoManager undoManager) {
        undoManager.addListener(this);
        this.undoManager = undoManager;
        this.context = parent.getContext();
        this.parentView = parent;
        this.xmlFilePath = xmlFilePath;
        this.finder = new XmlLayoutResourceFinder(this.context, resDirPath);
    }

    protected abstract void onEmptyLayoutClicked();

    protected abstract void onInflated();

    protected abstract void onViewClicked(XmlLayoutEditView xmlLayoutEditView);

    protected abstract void onXmlModified(boolean z);

    public String getXmlFilePath() {
        return this.xmlFilePath;
    }

    public void setXmlFilePath(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
        this.undoManager.addBaseVersion(xmlFilePath, this.xmlContent, 0);
        inflate();
    }

    public void init() {
        this.xmlContent = Utils.readFileAsString(this.xmlFilePath);
        this.undoManager.addBaseVersion(getXmlFilePath(), this.xmlContent, 0);
        inflate();
    }

    public String getXml() {
        return this.xmlContent;
    }

    public List<XmlLayoutEditView> getEditViews() {
        return new ArrayList<>(this.editViews);
    }

    public boolean canPaste() {
        @SuppressLint("WrongConstant") ClipboardManager cm = (ClipboardManager) this.context.getSystemService("clipboard");
        if (!cm.hasText() || cm.getText().charAt(0) != '<') {
            return false;
        }
        return true;
    }

    @SuppressLint("WrongConstant")
    public void paste() {
        this.xmlContent = ((ClipboardManager) this.context.getSystemService("clipboard")).getText().toString();
        this.undoManager.addVersion(getXmlFilePath(), this.xmlContent, 0);
        inflate();
        onXmlModified(true);
    }

    @SuppressLint("WrongConstant")
    public void copy() {
        ((ClipboardManager) this.context.getSystemService("clipboard")).setText(getXml());
    }

    public void share() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("application/xml");
        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(this.xmlFilePath)));
        intent.putExtra("android.intent.extra.SUBJECT", "XML Layout");
        intent.putExtra("android.intent.extra.TEXT", "Attached...");
        this.context.startActivity(intent);
    }

    @Override
    public void revertToVersion(@NonNull String filepath, String content, int change) {
        if (filepath.equals(getXmlFilePath())) {
            this.xmlContent = content;
            inflate();
            onXmlModified(false);
        }
    }

    @Override
    public void undoRedoStateChanged() {
    }

    public void setShowBorder(boolean show) {
        this.border = show;
        refresh();
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        this.selectModeClickRunnable = null;
        refresh();
    }

    public List<String> getAllIDs() {
        return new ArrayList<>(this.id2Node.keySet());
    }

    public void setIDAttribute(@NonNull Element node, @NonNull XmlLayoutProperties.PropertySpec property, Element otherNode, String id) {
        node.setAttribute(property.attrName, "@id/" + id);
        if (otherNode != null) {
            otherNode.setAttribute("android:id", "@+id/" + id);
        }
        serializeXml();
    }

    public void setViewID(Element node, String id) {
        if (id == null) {
            node.removeAttribute("android:id");
        } else {
            String oldId = getViewID(node);
            node.setAttribute("android:id", "@+id/" + id);
            if (oldId != null) {
                renameId(this.document, "@id/" + oldId, "@id/" + id);
            }
        }
        serializeXml();
    }

    private void renameId(Node node, String oldValue, String newValue) {
        if (node instanceof Element) {
            NamedNodeMap attributes = ((Element) node).getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Attr attr = (Attr) attributes.item(i);
                if (oldValue.equals(attr.getValue())) {
                    attr.setValue(newValue);
                }
            }
        }
        NodeList childNodes = node.getChildNodes();
        if (childNodes != null) {
            for (int i2 = 0; i2 < childNodes.getLength(); i2++) {
                renameId(childNodes.item(i2), oldValue, newValue);
            }
        }
    }

    public String suggestViewID(Element node) {
        String fileName = xmlFilePath == null ? "" : new File(xmlFilePath).getName().replace("_", "");
        if (fileName.lastIndexOf(46) != -1) {
            fileName = fileName.substring(0, fileName.lastIndexOf(46));
        }
        String nodeName = node.getNodeName();
        if (nodeName.lastIndexOf(46) != -1) {
            nodeName = nodeName.substring(nodeName.lastIndexOf(46) + 1);
        }
        String name = fileName + nodeName;
        int i = 1;
        while (id2Node.containsKey(name + i)) {
            i++;
        }
        return name + i;
    }

    public String suggestUserDrawableName() {
        return finder.suggestUserDrawableName();
    }

    public void addUserDrawable(String name, Intent data) {
        finder.addUserDrawable(name, data);
    }

    public List<String> getAllUserDrawables() {
        return finder.getAllUserDrawables();
    }

    public List<String> getAllUserStyles() {
        return finder.getAllUserStyles();
    }

    public List<AttributeValue> getAttributes(PropertyObject viewObj, PropertyObject layoutParamsObj, Element node) {
        List<AttributeValue> attributes = new ArrayList<>();
        XmlLayoutProperties.PropertySpec[] arr = XmlLayoutProperties.SORTED_PROPERTIES;
        for (XmlLayoutProperties.PropertySpec property : arr) {
            if (property.isLayoutProperty) {
                if (layoutParamsObj.hasProperty(property)) {
                    attributes.add(getPropertyValue(node, property));
                }
            } else if (viewObj.hasProperty(property)) {
                attributes.add(getPropertyValue(node, property));
            }
        }
        List<AttributeValue> result = new ArrayList<>();
        for (AttributeValue attribute : attributes) {
            if (attribute.hasValue() && !attribute.isStyled()) {
                result.add(attribute);
            }
        }
        for (AttributeValue attribute2 : attributes) {
            if (attribute2.isStyled()) {
                result.add(attribute2);
            }
        }
        for (AttributeValue attribute3 : attributes) {
            if (!attribute3.hasValue()) {
                result.add(attribute3);
            }
        }
        return result;
    }

    public void setAttribute(Element node, @NonNull XmlLayoutProperties.PropertySpec property, String value) {
        Map<String, String> eventProperties = new HashMap<>();
        eventProperties.put("attrName", property.attrName);
        eventProperties.put("value", value);
        if (value == null) {
            node.removeAttribute(property.attrName);
        } else {
            node.setAttribute(property.attrName, value);
        }
        serializeXml();
    }

    public void deleteView(@NonNull Element node) {
        node.getParentNode().removeChild(node);
        serializeXml();
    }

    public boolean hasView() {
        return this.document.getChildNodes().getLength() > 0;
    }

    public void surroundWithView(@NonNull Element node, NewWidget widget) {
        Element newNode = createElement(widget);
        Node parentNode = node.getParentNode();
        Node nextSibling = node.getNextSibling();
        parentNode.removeChild(node);
        if (nextSibling != null) {
            parentNode.insertBefore(newNode, nextSibling);
        } else {
            parentNode.appendChild(newNode);
        }
        newNode.appendChild(node);
        removeLayoutAttributes(node);
        serializeXml();
    }

    private void removeLayoutAttributes(Element node) {
        XmlLayoutProperties.PropertySpec[] arr = XmlLayoutProperties.LAYOUT_PROPERTIES;
        for (XmlLayoutProperties.PropertySpec property : arr) {
            if (!(property == XmlLayoutProperties.LAYOUT_WIDTH || property == XmlLayoutProperties.LAYOUT_HEIGHT || !node.hasAttribute(property.attrName))) {
                node.removeAttribute(property.attrName);
            }
        }
    }

    public void addViewBehind(@NonNull Element node, NewWidget widget) {
        Element newNode = createElement(widget);
        if (node.getNextSibling() != null) {
            node.getParentNode().insertBefore(newNode, node.getNextSibling());
        } else {
            node.getParentNode().appendChild(newNode);
        }
        serializeXml();
    }

    public void addViewBefore(@NonNull Element node, NewWidget widget) {
        node.getParentNode().insertBefore(createElement(widget), node);
        serializeXml();
    }

    public void addViewInside(@NonNull Element node, NewWidget widget) {
        node.appendChild(createElement(widget));
        serializeXml();
    }

    public void addViewInside(@NonNull Element node, NewWidget widget, @NonNull Element otherNode, @NonNull XmlLayoutProperties.PropertySpec layoutProperty, String id) {
        Element newNode = createElement(widget);
        node.appendChild(newNode);
        newNode.setAttribute(layoutProperty.attrName, "@id/" + id);
        otherNode.setAttribute("android:id", "@+id/" + id);
        serializeXml();
    }

    public void addView(NewWidget widget) {
        this.document.appendChild(createElement(widget));
        serializeXml();
    }

    public void onViewTaped(XmlLayoutEditView editView) {
        if (this.selectModeClickRunnable != null) {
            this.selectModeClickRunnable.run(editView);
            this.selectModeClickRunnable = null;
            refresh();
            return;
        }
        onViewClicked(editView);
    }

    public void startSelectingOtherView(@NonNull Element node, ValueRunnable<XmlLayoutEditView> ok) {
        this.selectModeClickRunnable = ok;
        this.selectModeNodes = new ArrayList<>();
        NodeList childNodes = node.getParentNode().getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child instanceof Element) {
                if (child.equals(node)) {
                    break;
                }
                this.selectModeNodes.add((Element) child);
            }
        }
        refresh();
    }

    private Element createElement(@NonNull NewWidget widget) throws DOMException {
        Element childNode = this.document.createElement(widget.elementName);
        for (Map.Entry<String, String> entry : widget.attributes.entrySet()) {
            childNode.setAttribute(entry.getKey(), entry.getValue());
        }
        return childNode;
    }

    private void serializeXml() {
        this.xmlContent = new XmlLayoutDOMSerializer().serialize(this.document);
        this.undoManager.addVersion(getXmlFilePath(), this.xmlContent, 0);
        inflate();
        onXmlModified(true);
    }

    private void inflate() {
        try {
            this.documentParseException = null;
            this.id2Node.clear();
            this.id2Int.clear();
            this.finder.reload();
            InputStream in = new ByteArrayInputStream(this.xmlContent.getBytes());
            this.document = PositionalXMLReader.readXML(in);
            in.close();
            collectIDs(this.document.getChildNodes());
        } catch (Throwable t) {
            this.documentParseException = t;
            try {
                this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
        this.selectModeClickRunnable = null;
        refresh();
    }

    @SuppressLint("SetTextI18n")
    private void refresh() {
        ViewGroup container;
        this.parentView.removeAllViews();
        if (this.border || this.editMode) {
            container = new LinearLayout(this.context);
            ((LinearLayout) container).setGravity(17);
            ClickableBorder clickableBorder = new ClickableBorder(this.context, container) {
                @Override
                protected void onClicked() {
                    if (XmlLayoutlInflater.this.editMode && !XmlLayoutlInflater.this.hasView()) {
                        XmlLayoutlInflater.this.onEmptyLayoutClicked();
                    }
                }
            };
            clickableBorder.setEnabled(this.editMode);
            this.parentView.addView(clickableBorder, -1, -1);
        } else {
            container = this.parentView;
        }
        Throwable inflateException = this.documentParseException;
        this.editViews.clear();
        if (inflateException == null) {
            try {
                inflateElements(this.document.getChildNodes(), container, null, 0);
                this.parentView.invalidate();
            } catch (Exception e) {
                inflateException = e;
            }
        }
        if (inflateException != null) {
            AppCompatTextView textView = new AppCompatTextView(this.context);
            if (!inflateException.getMessage().contains("no element")) {
                textView.setText("Can not view the layout. " + inflateException.getMessage());
            } else if (this.editMode) {
                textView.setText("No views have been added. Tap to add views.");
            } else {
                textView.setText("No views have been added.");
            }
            int p = (int) (10.0f * this.context.getResources().getDisplayMetrics().density);
            textView.setPadding(p, p, p, p);
            textView.setTextSize(20.0f);
            container.removeAllViews();
            container.addView(textView);
        }
        onInflated();
    }

    private void collectIDs(@NonNull NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == 1) {
                String id = getViewID(node);
                if (id != null && !this.id2Int.containsKey(id)) {
                    this.id2Node.put(id, node);
                    this.id2Int.put(id, Integer.valueOf(id2Int.size() + 100));
                }
                collectIDs(node.getChildNodes());
            }
        }
    }

    public String getViewID(@NonNull Node node) {
        String idValue;
        Node attr = node.getAttributes().getNamedItem("android:id");
        if (!(attr instanceof Attr) || (idValue = ((Attr) attr).getValue()) == null || !idValue.startsWith("@+id/")) {
            return null;
        }
        return idValue.substring("@+id/".length());
    }

    public String getStyle(@NonNull Element node) {
        Node styleAttr = node.getAttributes().getNamedItem("style");
        if (styleAttr instanceof Attr) {
            return ((Attr) styleAttr).getValue();
        }
        return null;
    }

    public void setStyle(Element node, String style) {
        if (style == null) {
            node.removeAttribute("style");
        } else {
            node.setAttribute("style", style);
        }
        serializeXml();
    }

    private void inflateElements(@NonNull NodeList nodes, ViewGroup parent, XmlLayoutEditView parentEditView, int depth) {
        PropertyObject viewObj;
        View actualView;
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == 1) {
                String elementName = node.getNodeName();
                View view = inflateView(node, elementName);
                if (view != null) {
                    configureView(view, this.editMode);
                    viewObj = new PropertyObject(view);
                    inflateAttributes(viewObj, node, XmlLayoutProperties.VIEW_PROPERTIES);
                } else {
                    view = new AppCompatTextView(this.context);
                    ((AppCompatTextView) view).setText(elementName);
                    int p = (int) (5.0f * this.context.getResources().getDisplayMetrics().density);
                    view.setPadding(p, p, p, p);
                    if (this.editMode) {
                        int minSize = (int) (20.0f * this.context.getResources().getDisplayMetrics().density);
                        view.setMinimumHeight(minSize);
                        view.setMinimumWidth(minSize);
                    }
                    viewObj = new PropertyObject(new View(this.context));
                }
                PropertyObject layoutParamsObj = inflateLayoutParams(node, parent);
                boolean showEditView = this.editMode && !(view instanceof TableRow) && (this.selectModeClickRunnable == null || this.selectModeNodes.contains(node));
                XmlLayoutEditView editView = new XmlLayoutEditView(this.context, showEditView ? view : null, (Element) node, viewObj, layoutParamsObj, parentEditView, depth, this);
                this.editViews.add(editView);
                if (showEditView) {
                    actualView = editView;
                } else {
                    actualView = view;
                }
                String id = getViewID(node);
                if (id != null) {
                    actualView.setId(id2Int.get(id).intValue());
                }
                if (parent != null) {
                    parent.addView(actualView, (ViewGroup.LayoutParams) layoutParamsObj.obj);
                }
                ViewGroup viewGroup = null;
                if (view instanceof ViewGroup) {
                    viewGroup = (ViewGroup) view;
                }
                inflateElements(node.getChildNodes(), viewGroup, editView, depth + 1);
            }
        }
    }

    private void configureView(View view, boolean editMode) {
        if (editMode) {
            int minSize = (int) (20.0f * this.context.getResources().getDisplayMetrics().density);
            view.setMinimumHeight(minSize);
            view.setMinimumWidth(minSize);
            if (view instanceof ViewGroup) {
                ((ViewGroup) view).setClipChildren(false);
                ((ViewGroup) view).setClipToPadding(false);
            }
            view.setFocusable(false);
            if (view.getClass() == AppCompatTextView.class || view.getClass() == AppCompatButton.class) {
                ((AppCompatTextView) view).setText(view.getClass().getSimpleName());
            }
        }
        if (view.getClass() == ListView.class) {
            fillListView((ListView) view);
        }
        if (view.getClass().equals(AppCompatSpinner.class)) {
            fillSpinner((AppCompatSpinner) view);
        }
        if (view.getClass() == ExpandableListView.class) {
            fillExpandableListView((ExpandableListView) view);
        }
    }

    private void fillExpandableListView(@NonNull ExpandableListView view) {
        view.setAdapter(new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {
                return 30;
            }

            @Override
            public int getChildrenCount(int p1) {
                return 2;
            }

            @Override
            public Object getGroup(int p1) {
                return null;
            }

            @Override
            public Object getChild(int p1, int p2) {
                return null;
            }

            @Override
            public long getGroupId(int p1) {
                return (long) p1;
            }

            @Override
            public long getChildId(int p1, int p2) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @SuppressLint({"ResourceType", "SetTextI18n"})
            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                View view2 = convertView;
                if (view2 == null) {
                    view2 = LayoutInflater.from(XmlLayoutlInflater.this.context).inflate(17367046, parent, false);
                }
                ((AppCompatTextView) view2.findViewById(16908308)).setText("Group " + (groupPosition + 1));
                return view2;
            }

            @SuppressLint({"ResourceType", "SetTextI18n"})
            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                View view2 = convertView;
                if (view2 == null) {
                    view2 = LayoutInflater.from(context).inflate(17367047, parent, false);
                }
                ((AppCompatTextView) view2.findViewById(16908308)).setText("Item " + (childPosition + 1));
                ((AppCompatTextView) view2.findViewById(16908309)).setText("Item " + (childPosition + 1));
                return view2;
            }

            @Override
            public boolean isChildSelectable(int p1, int p2) {
                return true;
            }
        });
    }

    @SuppressLint("ResourceType")
    private void fillSpinner(AppCompatSpinner view) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            items.add("Item " + i);
        }
        view.setAdapter(new ArrayAdapter<>(this.context, 17367043, items));
    }

    @SuppressLint("ResourceType")
    private void fillListView(ListView view) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            items.add("Item " + i);
        }
        view.setAdapter(new ArrayAdapter<>(this.context, 17367043, items));
    }

    @Nullable
    private View inflateView(Node node, @NonNull String elementName) {
        if (elementName.contains(".")) {
            return null;
        }
        if ("View".equals(elementName)) {
            return new View(context);
        }
        if ("View X".equals(elementName)) {
            return new View(context);
        }
        String baseStyle = this.finder.getBaseStyle(getStyle((Element) node));
        if (baseStyle != null && baseStyle.startsWith("@android:style/")) {
            if(!elementName.startsWith("androidx.appcompat.widget")) {
                try {
                    return (View) Class.forName("android.widget." + elementName).getConstructor(Context.class, AttributeSet.class, Integer.TYPE).newInstance(this.context, null, Integer.valueOf(((Integer) R.attr.class.getField("Android_" + baseStyle.substring("@android:style/".length()).replace(".", "_")).get(null)).intValue()));
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            } else {
                try {
                    return (View) Class.forName(elementName).getConstructor(Context.class, AttributeSet.class, Integer.TYPE).newInstance(this.context, null, Integer.valueOf(((Integer) R.attr.class.getField("Android_" + baseStyle.substring("@android:style/".length()).replace(".", "_")).get(null)).intValue()));
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        }
        if (baseStyle != null && baseStyle.startsWith("?android:attr/")) {
            if(!elementName.startsWith("androidx.appcompat.widget")) {
                try {
                    return (View) Class.forName("android.widget." + elementName).getConstructor(Context.class, AttributeSet.class, Integer.TYPE).newInstance(this.context, null, Integer.valueOf(((Integer) R.attr.class.getField(baseStyle.substring("?android:attr/".length())).get(null)).intValue()));
                } catch (Throwable th2) {
                    th2.printStackTrace();
                }
            } else {
                try {
                    return (View) Class.forName(elementName).getConstructor(Context.class, AttributeSet.class, Integer.TYPE).newInstance(this.context, null, Integer.valueOf(((Integer) R.attr.class.getField(baseStyle.substring("?android:attr/".length())).get(null)).intValue()));
                } catch (Throwable th2) {
                    th2.printStackTrace();
                }
            }
        }
        if(!elementName.startsWith("androidx.appcompat.widget")) {
            try {
                return (View) Class.forName("android.widget." + elementName).getConstructor(Context.class).newInstance(this.context);
            } catch (Throwable th3) {
                th3.printStackTrace();
                return null;
            }
        } else {
            try {
                return (View) Class.forName(elementName).getConstructor(Context.class).newInstance(this.context);
            } catch (Throwable th3) {
                th3.printStackTrace();
                return null;
            }
        }
    }

    @NonNull
    private PropertyObject inflateLayoutParams(Node node, ViewGroup parent) {
        ViewGroup.LayoutParams layoutParams;
        try {
            Class<?> layoutParamsClass = Class.forName(parent.getClass().getName() + "$LayoutParams");
            try {
                layoutParams = (ViewGroup.LayoutParams) layoutParamsClass.getConstructor(Integer.TYPE, Integer.TYPE).newInstance(-2, -2);
            } catch (NoSuchMethodException e) {
                layoutParams = (ViewGroup.LayoutParams) layoutParamsClass.getConstructor(ViewGroup.LayoutParams.class).newInstance(new ViewGroup.LayoutParams(-1, -1));
            }
            PropertyObject obj = new PropertyObject(layoutParams);
            String baseStyle = this.finder.getBaseStyle(getStyle((Element) node));
            if (baseStyle != null && baseStyle.startsWith("?android:attr/")) {
                try {
                    TypedArray array = this.context.getTheme().obtainStyledAttributes(((Integer) R.attr.class.getField(baseStyle.substring("?android:attr/".length())).get(null)).intValue(), new int[]{16842996, 16842997});
                    if (array.hasValue(0)) {
                        layoutParams.width = array.getLayoutDimension(0, "layout_width");
                    }
                    if (array.hasValue(1)) {
                        layoutParams.height = array.getLayoutDimension(1, "layout_height");
                    }
                    array.recycle();
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
            inflateAttributes(obj, node, XmlLayoutProperties.LAYOUT_PROPERTIES);
            return obj;
        } catch (Throwable th2) {
            return new PropertyObject(new ViewGroup.LayoutParams(-2, -2));
        }
    }

    private void inflateAttributes(PropertyObject obj, Node node, @NonNull XmlLayoutProperties.PropertySpec[] properties) {
        int min;
        for (XmlLayoutProperties.PropertySpec property : properties) {
            if (obj.hasProperty(property)) {
                Object value = getPropertyAttributeValue(node, property);
                if (this.editMode && (value instanceof Integer) && ((property == XmlLayoutProperties.LAYOUT_WIDTH || property == XmlLayoutProperties.LAYOUT_HEIGHT) && ((Integer) value).intValue() >= 0 && ((Integer) value).intValue() < (min = (int) (10.0f * this.context.getResources().getDisplayMetrics().density)))) {
                    value = Integer.valueOf(min);
                }
                obj.setProperty(property, value);
            }
        }
    }

    private Object getPropertyAttributeValue(Node node, XmlLayoutProperties.PropertySpec property) {
        Object val = getSystemStyleAttributeValue(node, property);
        return val != null ? val : getDirectPropertyAttributeValue(node, property);
    }

    @Nullable
    private Object getSystemStyleAttributeValue(Node node, XmlLayoutProperties.PropertySpec property) {
        try {
            String stringValue = getPropertyValue(node, property).value;
            if (stringValue != null && stringValue.startsWith("?android:attr/")) {
                int attrID = ((Integer) R.attr.class.getField(stringValue.substring("?android:attr/".length())).get(null)).intValue();
                switch (property.type) {
                    case Size:
                    case TextSize:
                    case LayoutSize:
                    case FloatSize:
                        TypedValue value = new TypedValue();
                        if (this.context.getTheme().resolveAttribute(attrID, value, true)) {
                            return Integer.valueOf((int) TypedValue.complexToDimension(value.data, this.context.getResources().getDisplayMetrics()));
                        }
                        break;
                    default:
                        TypedArray a = this.context.obtainStyledAttributes(new int[]{attrID});
                        if (a.hasValue(0)) {
                            switch (property.type) {
                                case Bool:
                                    Boolean valueOf = Boolean.valueOf(a.getBoolean(0, false));
                                    a.recycle();
                                    return valueOf;
                                case ID:
                                case Int:
                                case IntConstant:
                                    Integer valueOf2 = Integer.valueOf(a.getInt(0, 0));
                                    a.recycle();
                                    return valueOf2;
                                case Float:
                                    Float valueOf3 = Float.valueOf(a.getFloat(0, 0.0f));
                                    a.recycle();
                                    return valueOf3;
                                case Color:
                                    Integer valueOf4 = Integer.valueOf(a.getColor(0, 0));
                                    a.recycle();
                                    return valueOf4;
                                case DrawableResource:
                                case Drawable:
                                    Drawable drawable = a.getDrawable(0);
                                    a.recycle();
                                    return drawable;
                                case Text:
                                    String string = a.getString(0);
                                    a.recycle();
                                    return string;
                            }
                        }
                        a.recycle();
                        break;
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }

    @Nullable
    private Object getDirectPropertyAttributeValue(Node node, @NonNull XmlLayoutProperties.PropertySpec property) {
        switch (property.type) {
            case Bool:
                return getBoolAttributeValue(node, property);
            case ID:
                return getIDAttributeValue(node, property);
            case Int:
                return getIntAttributeValue(node, property);
            case IntConstant:
                return getIntConstantAttributeValue(node, property);
            case Float:
                return getFloatAttributeValue(node, property);
            case Color:
                return getColorAttributeValue(node, property);
            case DrawableResource:
                return getDrawableAttributeValue(node, property);
            case Drawable:
                Integer value = getColorAttributeValue(node, property);
                if (value != null) {
                    return new ColorDrawable(value.intValue());
                }
                return getDrawableAttributeValue(node, property);
            case Text:
                return getStringAttributeValue(node, property);
            case EnumConstant:
                return getEnumConstantAttributeValue(node, property);
            case TextAppearance:
                return getResourcePropertyValue(node, property);
            case Size:
                return getSizeAttributeValue(node, property);
            case TextSize:
                return getTextSizeAttributeValue(node, property);
            case LayoutSize:
                Integer value2 = getSizeAttributeValue(node, property);
                if (value2 != null) {
                    return value2;
                }
                Integer value22 = getIntConstantAttributeValue(node, property);
                if (value22 != null) {
                    return value22;
                }
                return null;
            case FloatSize:
                Integer value3 = getSizeAttributeValue(node, property);
                if (value3 != null) {
                    return new Float((float) value3.intValue());
                }
                return null;
            default:
                return null;
        }
    }

    @Nullable
    private Boolean getBoolAttributeValue(Node node, XmlLayoutProperties.PropertySpec property) {
        String value = getResourcePropertyValue(node, property);
        if (value == null) {
            return null;
        }
        return Boolean.valueOf("true".equals(value));
    }

    @Nullable
    private Object getDrawableAttributeValue(Node node, XmlLayoutProperties.PropertySpec property) {
        String value = getResourcePropertyValue(node, property);
        Drawable drawable = this.finder.findUserDrawable(value);
        if (drawable != null) {
            return drawable;
        }
        if (value != null && value.startsWith("@android:drawable/")) {
            try {
                return this.context.getResources().getDrawable(((Integer) R.drawable.class.getDeclaredField(value.substring("@android:drawable/".length())).get(null)).intValue());
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    private Enum<?> getEnumConstantAttributeValue(Node node, XmlLayoutProperties.PropertySpec property) {
        try {
            String value = getResourcePropertyValue(node, property);
            if (value != null) {
                Field[] arr$ = property.constantClass.getFields();
                for (Field field : arr$) {
                    String fieldName = field.getName();
                    if ((field.getModifiers() & 8) != 0 && fieldName.replace("_", "").equals(value.toUpperCase())) {
                        return (Enum) field.get(null);
                    }
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }

    @Nullable
    private Integer getIntConstantAttributeValue(Node node, XmlLayoutProperties.PropertySpec property) {
        String value = getResourcePropertyValue(node, property);
        if (value == null) {
            return null;
        }
        int result = 0;
        for (String val : value.split("\\|")) {
            Integer c = getIntConstantAttributeValue(val, property);
            if (c != null) {
                result |= c.intValue();
            }
        }
        return Integer.valueOf(result);
    }

    @Nullable
    private Integer getIntConstantAttributeValue(String value, @NonNull XmlLayoutProperties.PropertySpec property) {
        if (property.constantFieldPrefix == null) {
            try {
                return Integer.valueOf(((Integer) property.constantClass.getField(value.toUpperCase()).get(null)).intValue());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        Field[] arr$ = property.constantClass.getFields();
        for (Field field : arr$) {
            String fieldName = field.getName();
            if ((field.getModifiers() & 8) != 0 && fieldName.startsWith(property.constantFieldPrefix) && fieldName.substring(property.constantFieldPrefix.length()).replace("_", "").toUpperCase().equals(value.toUpperCase())) {
                try {
                    return Integer.valueOf(((Integer) field.get(null)).intValue());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String getStringAttributeValue(Node node, XmlLayoutProperties.PropertySpec property) {
        return getResourcePropertyValue(node, property);
    }

    @Nullable
    private Integer getColorAttributeValue(Node node, XmlLayoutProperties.PropertySpec property) {
        String value = getResourcePropertyValue(node, property);
        if (value != null) {
            try {
                if (value.charAt(0) == '#') {
                    long color = Long.parseLong(value.substring(1), 16);
                    if (value.length() == 7) {
                        color |= -16777216;
                    }
                    return Integer.valueOf((int) color);
                } else if (value.startsWith("@android:color/")) {
                    return Integer.valueOf(this.context.getResources().getColor(((Integer) R.color.class.getDeclaredField(value.substring("@android:color/".length())).get(null)).intValue()));
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    private Float getTextSizeAttributeValue(Node node, XmlLayoutProperties.PropertySpec property) {
        Integer size = getSizeAttributeValue(node, property);
        if (size != null) {
            return Float.valueOf(((float) size.intValue()) / this.context.getResources().getDisplayMetrics().scaledDensity);
        }
        return null;
    }

    @Nullable
    private Integer getSizeAttributeValue(Node node, XmlLayoutProperties.PropertySpec property) {
        String value = getResourcePropertyValue(node, property);
        if (value != null) {
            try {
                if (value.endsWith("px")) {
                    return Integer.valueOf((int) Float.parseFloat(value.substring(0, value.length() - 2)));
                }
                if (value.endsWith("dp")) {
                    return Integer.valueOf((int) (this.context.getResources().getDisplayMetrics().density * Float.parseFloat(value.substring(0, value.length() - 2))));
                } else if (value.endsWith("dip")) {
                    return Integer.valueOf((int) (this.context.getResources().getDisplayMetrics().density * Float.parseFloat(value.substring(0, value.length() - 3))));
                } else if (value.endsWith("sp")) {
                    return Integer.valueOf((int) (this.context.getResources().getDisplayMetrics().scaledDensity * Float.parseFloat(value.substring(0, value.length() - 2))));
                } else if (value.startsWith("@android:dimen/")) {
                    return Integer.valueOf(this.context.getResources().getDimensionPixelSize(((Integer) R.dimen.class.getDeclaredField(value.substring("@android:dimen/".length())).get(null)).intValue()));
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    private Integer getIntAttributeValue(Node node, XmlLayoutProperties.PropertySpec property) {
        String value = getResourcePropertyValue(node, property);
        if (value != null) {
            try {
                return Integer.valueOf(Integer.parseInt(value));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    private Float getFloatAttributeValue(Node node, XmlLayoutProperties.PropertySpec property) {
        String value = getResourcePropertyValue(node, property);
        if (value != null) {
            try {
                return Float.valueOf(Float.parseFloat(value));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    private Integer getIDAttributeValue(Node node, XmlLayoutProperties.PropertySpec property) {
        String val = getResourcePropertyValue(node, property);
        if (val != null) {
            if (val.startsWith("@+id/")) {
                String id = val.substring("@+id/".length());
                if (this.id2Int.containsKey(id)) {
                    return this.id2Int.get(id);
                }
            }
            if (val.startsWith("@id/")) {
                String id2 = val.substring("@id/".length());
                if (this.id2Int.containsKey(id2)) {
                    return this.id2Int.get(id2);
                }
            }
        }
        return null;
    }

    private String getResourcePropertyValue(Node node, XmlLayoutProperties.PropertySpec property) {
        return this.finder.findResourcePropertyValue(getPropertyValue(node, property).value);
    }

    @NonNull
    @Contract("_, _ -> new")
    private AttributeValue getPropertyValue(@NonNull Node node, @NonNull XmlLayoutProperties.PropertySpec property) {
        String value;
        Node attr = node.getAttributes().getNamedItem(property.attrName);
        if (attr instanceof Attr) {
            return new AttributeValue(property, (Attr) attr);
        }
        String style = getStyle((Element) node);
        if (style == null || (value = this.finder.findStyleAttributeValue(style, property)) == null) {
            return new AttributeValue(property);
        }
        return new AttributeValue(property, value);
    }
}
