package com.mcal.uidesigner;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.mcal.uidesigner.common.PositionalXMLReader;
import com.mcal.uidesigner.proxy.ProxyGridLayoutParams;
import com.mcal.uidesigner.proxy.ProxyMarginLayoutParams;
import com.mcal.uidesigner.proxy.ProxyRelativeLayoutParams;
import com.mcal.uidesigner.proxy.ProxyScrollView;
import com.mcal.uidesigner.proxy.ProxyTextView;
import com.mcal.uidesigner.proxy.ProxyView;
import com.mcal.uidesigner.proxy.ProxyViewPaddings;
import com.mcal.uidesigner.proxy.material.ProxyBottomNavigationView;
import com.mcal.uidesigner.proxy.androidx.ProxyCoordinatorLayoutParams;
import com.mcal.uidesigner.proxy.material.ProxyMaterialButton;
import com.mcal.uidesigner.proxy.material.ProxyTextInputLayout;
import com.mcal.uidesigner.view.IncludeLayout;
import com.mcal.uidesigner.view.ViewLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XmlLayoutProperties {
    public static PropertySpec LAYOUT_TORIGHTOF;
    public static PropertySpec LAYOUT_TOLEFTOF;
    public static PropertySpec LAYOUT_BELOW;
    public static PropertySpec LAYOUT_ABOVE;
    public static PropertySpec LAYOUT_WIDTH;
    public static PropertySpec LAYOUT_HEIGHT;
    public static List<PropertySpec> LAYOUT_PROPERTIES = new ArrayList<>();
    public static List<PropertySpec> SORTED_PROPERTIES = new ArrayList<>();
    public static List<PropertySpec> VIEW_PROPERTIES = new ArrayList<>();

    public static List<PropertySpec> MATERIAL_TEXT_INPUT_LAYOUT = new ArrayList<>();
    public static List<PropertySpec> MATERIAL_BUTTON = new ArrayList<>();

    static {
        LAYOUT_TORIGHTOF = new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_toRightOf", ProxyRelativeLayoutParams.class, "setRightOf()", PropertyType.ID);
        LAYOUT_TOLEFTOF = new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_toLeftOf", ProxyRelativeLayoutParams.class, "setLeftOf()", PropertyType.ID);
        LAYOUT_BELOW = new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_below", ProxyRelativeLayoutParams.class, "setBelow()", PropertyType.ID);
        LAYOUT_ABOVE = new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_above", ProxyRelativeLayoutParams.class, "setAbove()", PropertyType.ID);
        LAYOUT_WIDTH = new PropertySpec(ViewGroup.LayoutParams.class, "android:layout_width", "width", PropertyType.LayoutSize);
        LAYOUT_HEIGHT = new PropertySpec(ViewGroup.LayoutParams.class, "android:layout_height", "height", PropertyType.LayoutSize);

        LAYOUT_PROPERTIES.addAll(Arrays.asList(
                LAYOUT_WIDTH,
                LAYOUT_HEIGHT,

                new PropertySpec(ViewGroup.MarginLayoutParams.class, "android:layout_margin", ProxyMarginLayoutParams.class, "setMargin()", PropertyType.Size),
                new PropertySpec(ViewGroup.MarginLayoutParams.class, "android:layout_marginLeft", "leftMargin", PropertyType.Size),
                new PropertySpec(ViewGroup.MarginLayoutParams.class, "android:layout_marginRight", "rightMargin", PropertyType.Size),
                new PropertySpec(ViewGroup.MarginLayoutParams.class, "android:layout_marginTop", "topMargin", PropertyType.Size),
                new PropertySpec(ViewGroup.MarginLayoutParams.class, "android:layout_marginBottom", "bottomMargin", PropertyType.Size),
                new PropertySpec(ViewGroup.MarginLayoutParams.class, "android:layout_marginStart", "setMarginStart()", PropertyType.Size),
                new PropertySpec(ViewGroup.MarginLayoutParams.class, "android:layout_marginEnd", "setMarginEnd()", PropertyType.Size),

                new PropertySpec(LinearLayout.MarginLayoutParams.class, "android:layout_gravity", "gravity", PropertyType.IntConstant, Gravity.class, null),
                new PropertySpec(LinearLayout.MarginLayoutParams.class, "android:layout_weight", "weight", PropertyType.Float),

                LAYOUT_BELOW,
                LAYOUT_ABOVE,
                LAYOUT_TORIGHTOF,
                LAYOUT_TOLEFTOF,

                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_toEndOf", ProxyRelativeLayoutParams.class, "setEndOf()", PropertyType.ID),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_toStartOf", ProxyRelativeLayoutParams.class, "setStartOf()", PropertyType.ID),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_alignBaseline", ProxyRelativeLayoutParams.class, "setAlignBaseline()", PropertyType.ID),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_alignBottom", ProxyRelativeLayoutParams.class, "setAlignBottom()", PropertyType.ID),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_alignTop", ProxyRelativeLayoutParams.class, "setAlignTop()", PropertyType.ID),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_alignEnd", ProxyRelativeLayoutParams.class, "setAlignEnd()", PropertyType.ID),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_alignStart", ProxyRelativeLayoutParams.class, "setAlignStart()", PropertyType.ID),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_alignRight", ProxyRelativeLayoutParams.class, "setAlignRight()", PropertyType.ID),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_alignLeft", ProxyRelativeLayoutParams.class, "setAlignLeft()", PropertyType.ID),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_alignParentBottom", ProxyRelativeLayoutParams.class, "setAlignParentBottom()", PropertyType.Bool),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_alignParentEnd", ProxyRelativeLayoutParams.class, "setAlignParentEnd()", PropertyType.Bool),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_alignParentLeft", ProxyRelativeLayoutParams.class, "setAlignParentLeft()", PropertyType.Bool),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_alignParentRight", ProxyRelativeLayoutParams.class, "setAlignParentRight()", PropertyType.Bool),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_alignParentStart", ProxyRelativeLayoutParams.class, "setAlignParentStart()", PropertyType.Bool),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_alignParentTop", ProxyRelativeLayoutParams.class, "setAlignParentTop()", PropertyType.Bool),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_centerHorizontal", ProxyRelativeLayoutParams.class, "setCenterHorizontal()", PropertyType.Bool),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_centerVertical", ProxyRelativeLayoutParams.class, "setCenterVertical()", PropertyType.Bool),
                new PropertySpec(RelativeLayout.LayoutParams.class, "android:layout_centerInParent", ProxyRelativeLayoutParams.class, "setCenterInParent()", PropertyType.Bool),

                new PropertySpec(CoordinatorLayout.LayoutParams.class, "android:layout_anchor", ProxyCoordinatorLayoutParams.class, "setAnchorId()", PropertyType.ID),
                new PropertySpec(CoordinatorLayout.LayoutParams.class, "android:layout_anchorGravity", ProxyCoordinatorLayoutParams.class, "setAnchorGravity()", PropertyType.IntConstant, Gravity.class, "17"),

                new PropertySpec(GridLayout.LayoutParams.class, "android:layout_gravity", ProxyGridLayoutParams.class, "setGravity()", PropertyType.IntConstant, Gravity.class, null),
                new PropertySpec(GridLayout.LayoutParams.class, "android:layout_column", ProxyGridLayoutParams.class, "setColumn()", PropertyType.Int),
                new PropertySpec(GridLayout.LayoutParams.class, "android:layout_columnSpan", ProxyGridLayoutParams.class, "setColumnSpan()", PropertyType.Int),
                new PropertySpec(GridLayout.LayoutParams.class, "android:layout_row", ProxyGridLayoutParams.class, "setRow()", PropertyType.Int),
                new PropertySpec(GridLayout.LayoutParams.class, "android:layout_rowSpan", ProxyGridLayoutParams.class, "setRowSpan()", PropertyType.Int),

                new PropertySpec(FrameLayout.LayoutParams.class, "android:layout_gravity", "gravity", PropertyType.IntConstant, Gravity.class, null),

                new PropertySpec(TableRow.LayoutParams.class, "android:layout_span", "span", PropertyType.Int),
                new PropertySpec(TableRow.LayoutParams.class, "android:layout_column", PositionalXMLReader.COLUMN, PropertyType.Int),

                new PropertySpec(AbsoluteLayout.LayoutParams.class, "android:layout_x", "x", PropertyType.Size),
                new PropertySpec(AbsoluteLayout.LayoutParams.class, "android:layout_y", "y", PropertyType.Size)));


        VIEW_PROPERTIES.addAll(Arrays.asList(
                new PropertySpec(View.class, "android:padding", ProxyViewPaddings.class, "setPadding()", PropertyType.Size),
                new PropertySpec(View.class, "android:paddingLeft", ProxyViewPaddings.class, "setPaddingLeft()", PropertyType.Size),
                new PropertySpec(View.class, "android:paddingRight", ProxyViewPaddings.class, "setPaddingRight()", PropertyType.Size),
                new PropertySpec(View.class, "android:paddingTop", ProxyViewPaddings.class, "setPaddingTop()", PropertyType.Size),
                new PropertySpec(View.class, "android:paddingBottom", ProxyViewPaddings.class, "setPaddingBottom()", PropertyType.Size),
                new PropertySpec(View.class, "android:paddingStart", ProxyViewPaddings.class, "setPaddingStart()", PropertyType.Size),
                new PropertySpec(View.class, "android:paddingEnd", ProxyViewPaddings.class, "setPaddingEnd()", PropertyType.Size),
                new PropertySpec(View.class, "android:alpha", "setAlpha()", PropertyType.Float),
                new PropertySpec(View.class, "android:scaleX", "setScaleX()", PropertyType.Float),
                new PropertySpec(View.class, "android:scaleY", "setScaleY()", PropertyType.Float),
                new PropertySpec(View.class, "android:translationX", "setTranslationX()", PropertyType.FloatSize),
                new PropertySpec(View.class, "android:translationY", "setTranslationY()", PropertyType.FloatSize),
                new PropertySpec(View.class, "android:translationZ", "setTranslationZ()", PropertyType.FloatSize),
                new PropertySpec(View.class, "android:rotation", "setRotation()", PropertyType.Float),
                new PropertySpec(View.class, "android:rotationX", "setRotationX()", PropertyType.Float),
                new PropertySpec(View.class, "android:rotationY", "setRotationY()", PropertyType.Float),
                new PropertySpec(View.class, "android:elevation", "setElevation()", PropertyType.FloatSize),
                new PropertySpec(View.class, "android:enabled", "setEnabled()", PropertyType.Bool),
                new PropertySpec(View.class, "android:minHeight", "setMinimumHeight()", PropertyType.Size),
                new PropertySpec(View.class, "android:minWidth", "setMinimumWidth()", PropertyType.Size),
                new PropertySpec(View.class, "android:textAlignment", "setTextAlignment()", PropertyType.IntConstant, View.class, "TEXT_ALIGNMENT"),
                new PropertySpec(View.class, "android:visibility", "setVisibility()", PropertyType.IntConstant),
                new PropertySpec(View.class, "android:contentDescription", "setContentDescription()", PropertyType.Text),
                new PropertySpec(View.class, "android:background", "setBackgroundDrawable()", PropertyType.Drawable),
                new PropertySpec(View.class, "android:backgroundTint", ProxyView.class, "setBackgroundTint()", PropertyType.Color),
                new PropertySpec(View.class, "android:backgroundTintMode", ProxyView.class, "setBackgroundTintMode()", PropertyType.IntConstant, ProxyMaterialButton.class, "TINTMODE"),
                new PropertySpec(View.class, "android:onClick", "", PropertyType.Event),

                new PropertySpec(ViewGroup.class, "android:clipChildren", "setClipChildren()", PropertyType.Bool),
                new PropertySpec(ViewGroup.class, "android:clipToPadding", "setClipToPadding()", PropertyType.Bool),
                new PropertySpec(ViewGroup.class, "android:layoutMode", "setLayoutMode()", PropertyType.IntConstant, ViewGroup.class, "LAYOUT_MODE"),
                new PropertySpec(ViewGroup.class, "android:splitMotionEvents", "setMotionEventSplittingEnabled()", PropertyType.Bool),

                new PropertySpec(LinearLayout.class, "android:orientation", "setOrientation()", PropertyType.IntConstant),
                new PropertySpec(LinearLayout.class, "android:gravity", "setGravity()", PropertyType.IntConstant, Gravity.class, null),
                new PropertySpec(LinearLayout.class, "android:baselineAligned", "setBaselineAligned()", PropertyType.Bool),
                new PropertySpec(LinearLayout.class, "android:baselineAlignedChildIndex", "setBaselineAlignedChildIndex()", PropertyType.Int),
                new PropertySpec(LinearLayout.class, "android:measureWithLargestChild", "setMeasureWithLargestChildEnabled()", PropertyType.Bool),
                new PropertySpec(LinearLayout.class, "android:weightSum", "setWeightSum()", PropertyType.Float),
                new PropertySpec(LinearLayout.class, "android:divider", "setDividerDrawable()", PropertyType.Drawable),
                new PropertySpec(LinearLayout.class, "android:dividerPadding", "setDividerPadding()", PropertyType.Size),
                new PropertySpec(LinearLayout.class, "android:showDividers", "setShowDividers()", PropertyType.IntConstant, LinearLayout.class, "SHOW_DIVIDER"),

                new PropertySpec(RelativeLayout.class, "android:gravity", "setGravity()", PropertyType.IntConstant, Gravity.class, null),
                new PropertySpec(RelativeLayout.class, "android:ignoreGravity", "setIgnoreGravity()", PropertyType.ID),

                new PropertySpec(FrameLayout.class, "android:foregroundGravity", "setForegroundGravity()", PropertyType.IntConstant, Gravity.class, null),
                new PropertySpec(FrameLayout.class, "android:measureAllChildren", "setMeasureAllChildren()", PropertyType.Bool),

                new PropertySpec(GridLayout.class, "android:columnCount", "setColumnCount()", PropertyType.Int),
                new PropertySpec(GridLayout.class, "android:rowCount", "setRowCount()", PropertyType.Int),
                new PropertySpec(GridLayout.class, "android:orientation", "setOrientation()", PropertyType.IntConstant),
                new PropertySpec(GridLayout.class, "android:columnOrderPreserved", "setColumnOrderPreserved()", PropertyType.Bool),
                new PropertySpec(GridLayout.class, "android:rowOrderPreserved", "setRowOrderPreserved()", PropertyType.Bool),
                new PropertySpec(GridLayout.class, "android:useDefaultMargins", "setUseDefaultMargins()", PropertyType.Bool),
                new PropertySpec(GridLayout.class, "android:alignmentMode", "setAlignmentMode()", PropertyType.IntConstant, GridLayout.class, "ALIGN"),

                new PropertySpec(TextView.class, "android:textAppearance", ProxyTextView.class, "setTextAppearance()", PropertyType.TextAppearance),
                new PropertySpec(TextView.class, "tools:text", "setText()", PropertyType.Text),
                new PropertySpec(TextView.class, "android:text", "setText()", PropertyType.Text),
                new PropertySpec(TextView.class, "android:html", ProxyTextView.class, "setTextHtml()", PropertyType.Text),
                new PropertySpec(TextView.class, "android:hint", "setHint()", PropertyType.Text),
                new PropertySpec(TextView.class, "android:height", "setHeight()", PropertyType.Size),
                new PropertySpec(TextView.class, "android:width", "setWidth()", PropertyType.Size),
                new PropertySpec(TextView.class, "android:maxHeight", "setMaxHeight()", PropertyType.Size),
                new PropertySpec(TextView.class, "android:maxWidth", "setMaxWidth()", PropertyType.Size),
                new PropertySpec(TextView.class, "android:ems", "setEms()", PropertyType.Int),
                new PropertySpec(TextView.class, "android:minEms", "setMinEms()", PropertyType.Int),
                new PropertySpec(TextView.class, "android:maxEms", "setMaxEms()", PropertyType.Int),
                new PropertySpec(TextView.class, "android:gravity", "setGravity()", PropertyType.IntConstant, Gravity.class, null),
                new PropertySpec(TextView.class, "android:textAllCaps", "setAllCaps()", PropertyType.Bool),
                new PropertySpec(TextView.class, "android:textScaleX", "setTextScaleX()", PropertyType.Float),
                new PropertySpec(TextView.class, "android:textScaleY", "setTextScaleY()", PropertyType.Float),
                new PropertySpec(TextView.class, "android:textIsSelectable", "setTextIsSelectable()", PropertyType.Bool),
                new PropertySpec(TextView.class, "android:singleLine", "setSingleLine()", PropertyType.Bool),
                new PropertySpec(TextView.class, "android:lines", "setLines()", PropertyType.Int),
                new PropertySpec(TextView.class, "android:minLines", "setMinLines()", PropertyType.Int),
                new PropertySpec(TextView.class, "android:maxLines", "setMaxLines()", PropertyType.Int),
                new PropertySpec(TextView.class, "android:textColor", "setTextColor()", PropertyType.Color),
                new PropertySpec(TextView.class, "android:textColorHighlight", "setHighlightColor()", PropertyType.Color),
                new PropertySpec(TextView.class, "android:textColorHint", "setHintTextColor()", PropertyType.Color),
                new PropertySpec(TextView.class, "android:textColorLink", "setLinkTextColor()", PropertyType.Color),
                new PropertySpec(TextView.class, "android:ellipsize", "setEllipsize()", PropertyType.EnumConstant, android.text.TextUtils.TruncateAt.class, null),
                new PropertySpec(TextView.class, "android:textStyle", ProxyTextView.class, "setTextStyle()", PropertyType.IntConstant, ProxyTextView.class, "TEXTSTYLE"),
                new PropertySpec(TextView.class, "android:typeface", ProxyTextView.class, "setTypeface()", PropertyType.IntConstant, ProxyTextView.class, "TYPEFACE"),
                new PropertySpec(TextView.class, "android:inputType", ProxyTextView.class, "setInputType()", PropertyType.IntConstant, ProxyTextView.class, "INPUTTYPE"),
                new PropertySpec(TextView.class, "android:textSize", "setTextSize()", PropertyType.TextSize),
                new PropertySpec(TextView.class, "android:shadowColor", ProxyTextView.class, "setShadowColor()", PropertyType.Color),
                new PropertySpec(TextView.class, "android:shadowDx", ProxyTextView.class, "setShadowDx()", PropertyType.Float),
                new PropertySpec(TextView.class, "android:shadowDy", ProxyTextView.class, "setShadowDy()", PropertyType.Float),
                new PropertySpec(TextView.class, "android:shadowRadius", ProxyTextView.class, "setShadowRadius()", PropertyType.Float),

                new PropertySpec(ScrollView.class, "android:fillViewport", "setFillViewport()", PropertyType.Bool),
                new PropertySpec(ScrollView.class, "android:scrollbars", ProxyScrollView.class, "setScrollBars()", PropertyType.IntConstant),

                new PropertySpec(ImageView.class, "android:src", "setImageDrawable()", PropertyType.DrawableResource),
                new PropertySpec(ImageView.class, "android:scaleType", "setScaleType()", PropertyType.EnumConstant, ImageView.ScaleType.class, null),
                new PropertySpec(ImageView.class, "android:adjustViewBounds", "setAdjustViewBounds()", PropertyType.Bool),
                new PropertySpec(ImageView.class, "android:baseLine", "setBaseLine()", PropertyType.Size),
                new PropertySpec(ImageView.class, "android:baselineAlignBottom", "setBaselineAlignBottom()", PropertyType.Bool),
                new PropertySpec(ImageView.class, "android:cropToPadding", "setCropToPadding()", PropertyType.Bool),
                new PropertySpec(ImageView.class, "android:maxHeight", "setMaxHeight()", PropertyType.Size),
                new PropertySpec(ImageView.class, "android:maxWidth", "setMaxWidth()", PropertyType.Size),

                new PropertySpec(ProgressBar.class, "android:indeterminate", "setIndeterminate()", PropertyType.Bool),
                new PropertySpec(ProgressBar.class, "android:indeterminateOnly", "setIndeterminate()", PropertyType.Bool),
                new PropertySpec(ProgressBar.class, "android:indeterminateDrawable", "setIndeterminateDrawable()", PropertyType.DrawableResource),
                new PropertySpec(ProgressBar.class, "android:progressDrawable", "setProgressDrawable()", PropertyType.DrawableResource),

                new PropertySpec(SeekBar.class, "android:thumb", "setThumb()", PropertyType.DrawableResource),

                new PropertySpec(Switch.class, "android:switchMinWidth", "setSwitchMinWidth()", PropertyType.Size),
                new PropertySpec(Switch.class, "android:switchPadding", "setSwitchPadding()", PropertyType.Size),
                new PropertySpec(Switch.class, "android:textOff", "setTextOff()", PropertyType.Text),
                new PropertySpec(Switch.class, "android:textOn", "setTextOn()", PropertyType.Text),
                new PropertySpec(Switch.class, "android:thumbTextPadding", "setThumbTextPadding()", PropertyType.Size),
                new PropertySpec(Switch.class, "android:thumb", "setThumbDrawable()", PropertyType.Drawable),
                new PropertySpec(Switch.class, "android:track", "setTrackDrawable()", PropertyType.Drawable),

                new PropertySpec(ToggleButton.class, "android:textOff", "setTextOff()", PropertyType.Text),
                new PropertySpec(ToggleButton.class, "android:textOn", "setTextOn()", PropertyType.Text),

                new PropertySpec(Spinner.class, "android:gravity", "setGravity()", PropertyType.IntConstant, Gravity.class, null),
                new PropertySpec(Spinner.class, "android:dropDownWidth", "setDropDownWidth()", PropertyType.Size),
                new PropertySpec(Spinner.class, "android:dropDownHorizontalOffset", "setDropDownHorizontalOffset()", PropertyType.Size),
                new PropertySpec(Spinner.class, "android:prompt", "setPrompt()", PropertyType.Text),
                new PropertySpec(Spinner.class, "android:dropDownVerticalOffset", "setDropDownVerticalOffset()", PropertyType.Size),
                new PropertySpec(Spinner.class, "android:popupBackground", "setPopupBackgroundDrawable()", PropertyType.Drawable),

                new PropertySpec(RatingBar.class, "android:numStars", "setNumStars()", PropertyType.Int),
                new PropertySpec(RatingBar.class, "android:minHeight", "setMinHeight()", PropertyType.Size),
                new PropertySpec(RatingBar.class, "android:rating", "setRating()", PropertyType.Float),
                new PropertySpec(RatingBar.class, "android:stepSize", "setStepSize()", PropertyType.Float),
                new PropertySpec(RatingBar.class, "android:isIndicator", "setIsIndicator()", PropertyType.Bool),
                new PropertySpec(RatingBar.class, "android:progressDrawable", "setProgressDrawable()", PropertyType.DrawableResource),

                new PropertySpec(DatePicker.class, "android:calendarViewShown", "setCalendarViewShown()", PropertyType.Bool),
                new PropertySpec(DatePicker.class, "android:spinnersShown", "setSpinnersShown()", PropertyType.Bool),

                new PropertySpec(ListView.class, "android:divider", "setDivider()", PropertyType.Drawable),
                new PropertySpec(ListView.class, "android:dividerHeight", "setDividerHeight()", PropertyType.Size),

                new PropertySpec(AppCompatTextView.class, "android:dividerHeight", "setDividerHeight()", PropertyType.Size),

                new PropertySpec(BottomNavigationView.class, "app:menu", ProxyBottomNavigationView.class, "setMenu()", PropertyType.Menu),
                new PropertySpec(BottomNavigationView.class, "app:itemIconTint", ProxyBottomNavigationView.class, "setItemIconTint()", PropertyType.Color),
                new PropertySpec(BottomNavigationView.class, "app:itemTextColor", ProxyBottomNavigationView.class, "setItemTextColor()", PropertyType.Color),
                new PropertySpec(ViewLayout.class, "class", "setViewClass()", PropertyType.Text),
                new PropertySpec(IncludeLayout.class, "layout", "setLayout()", PropertyType.Text)

        ));

        MATERIAL_BUTTON.addAll(Arrays.asList(new XmlLayoutProperties.PropertySpec(MaterialButton.class, "app:backgroundTint", ProxyMaterialButton.class, "setBackgroundTint()", XmlLayoutProperties.PropertyType.Color),
                new XmlLayoutProperties.PropertySpec(MaterialButton.class, "app:backgroundTintMode", ProxyMaterialButton.class, "setBackgroundTintMode()", XmlLayoutProperties.PropertyType.IntConstant, PorterDuff.Mode.class, PorterDuff.Mode.SRC_IN.toString()),

                new XmlLayoutProperties.PropertySpec(MaterialButton.class, "app:checkable", "setCheckable()", XmlLayoutProperties.PropertyType.Bool),
                new XmlLayoutProperties.PropertySpec(MaterialButton.class, "app:cornerRadius", "setCornerRadius()", XmlLayoutProperties.PropertyType.Size),
                new XmlLayoutProperties.PropertySpec(MaterialButton.class, "app:icon", "setIcon()", XmlLayoutProperties.PropertyType.DrawableResource),
                new XmlLayoutProperties.PropertySpec(MaterialButton.class, "app:iconGravity", ProxyMaterialButton.class, "setIconGravity()", XmlLayoutProperties.PropertyType.IntConstant, ProxyMaterialButton.class, "ICONGRAVITY"),
                new XmlLayoutProperties.PropertySpec(MaterialButton.class, "app:iconPadding", "setIconPadding()", XmlLayoutProperties.PropertyType.Size),
                new XmlLayoutProperties.PropertySpec(MaterialButton.class, "app:iconSize", "setIconSize()", XmlLayoutProperties.PropertyType.Size),

                new XmlLayoutProperties.PropertySpec(MaterialButton.class, "app:iconTint", ProxyMaterialButton.class, "setIconTint()", XmlLayoutProperties.PropertyType.Color),
                new XmlLayoutProperties.PropertySpec(MaterialButton.class, "app:iconTintMode", ProxyMaterialButton.class, "setIconTintMode()", XmlLayoutProperties.PropertyType.IntConstant, ProxyMaterialButton.class, "TINTMODE"),

                new XmlLayoutProperties.PropertySpec(MaterialButton.class, "app:rippleColor", ProxyMaterialButton.class, "setRippleColor()", XmlLayoutProperties.PropertyType.Color),
                new XmlLayoutProperties.PropertySpec(MaterialButton.class, "app:strokeColor", ProxyMaterialButton.class, "setStrokeColor()", XmlLayoutProperties.PropertyType.Color),
                new XmlLayoutProperties.PropertySpec(MaterialButton.class, "app:strokeWidth", ProxyMaterialButton.class, "setStrokeWidth()", XmlLayoutProperties.PropertyType.Size)));

        MATERIAL_TEXT_INPUT_LAYOUT.addAll(Arrays.asList(new PropertySpec(TextInputLayout.class, "android:hint", "setHint()", PropertyType.Text),
                new PropertySpec(TextInputLayout.class, "app:boxBackgroundColor", "setBoxBackgroundColor()", PropertyType.Color),
                new PropertySpec(TextInputLayout.class, "app:elevation", "setElevation()", PropertyType.Size),
                new PropertySpec(TextInputLayout.class, "app:boxBackgroundMode", "setBoxBackgroundMode()", PropertyType.Size, TextInputLayout.BoxBackgroundMode.class, null),
                new PropertySpec(TextInputLayout.class, "app:boxCornerRadiusTopStart", ProxyTextInputLayout.class, "setBoxCornerRadiusTopStart()", PropertyType.Size),
                new PropertySpec(TextInputLayout.class, "app:boxCornerRadiusTopEnd", ProxyTextInputLayout.class, "setBoxCornerRadiusTopEnd()", PropertyType.Size),
                new PropertySpec(TextInputLayout.class, "app:boxCornerRadiusBottomStart", ProxyTextInputLayout.class, "setBoxCornerRadiusBottomStart()", PropertyType.Size),
                new PropertySpec(TextInputLayout.class, "app:boxCornerRadiusBottomEnd", ProxyTextInputLayout.class, "setBoxCornerRadiusBottomEnd()", PropertyType.Size),
                new PropertySpec(TextInputLayout.class, "app:boxStrokeWidth", "setBoxStrokeWidth()", PropertyType.Size),
                new PropertySpec(TextInputLayout.class, "app:boxStrokeWidthFocused", "setBoxStrokeWidthFocused()", PropertyType.Size),
                new PropertySpec(TextInputLayout.class, "app:boxStrokeColor", "setBoxStrokeColor()", PropertyType.Color),
                new PropertySpec(TextInputLayout.class, "app:boxStrokeErrorColor", "setBoxStrokeErrorColor()", PropertyType.Color),
                new PropertySpec(TextInputLayout.class, "app:counterEnabled", "setCounterEnabled()", PropertyType.Bool),
                new PropertySpec(TextInputLayout.class, "app:counterMaxLength", "setCounterMaxLength()", PropertyType.Text),
                new PropertySpec(TextInputLayout.class, "app:counterTextColor", "setCounterTextColor()", PropertyType.Color),

                new PropertySpec(TextInputLayout.class, "app:counterTextAppearance", "setCounterTextAppearance()", PropertyType.TextAppearance),
                new PropertySpec(TextInputLayout.class, "app:hintTextAppearance", "setHintTextAppearance()", PropertyType.TextAppearance),
                new PropertySpec(TextInputLayout.class, "app:errorTextAppearance", "setErrorTextAppearance()", PropertyType.TextAppearance),
                new PropertySpec(TextInputLayout.class, "app:counterOverflowTextAppearance", "setCounterOverflowTextAppearance()", PropertyType.TextAppearance),
                new PropertySpec(TextInputLayout.class, "app:placeholderTextAppearance", "setPlaceholderTextAppearance()", PropertyType.TextAppearance),
                new PropertySpec(TextInputLayout.class, "app:helperTextTextAppearance", "setHelperTextTextAppearance()", PropertyType.TextAppearance),

                new PropertySpec(TextInputLayout.class, "android:textColorHint", ProxyTextInputLayout.class, "setDefaultHintTextColor()", PropertyType.Color),
                new PropertySpec(TextInputLayout.class, "app:hintTextColor", ProxyTextInputLayout.class, "setHintTextColor()", PropertyType.Color)));

        VIEW_PROPERTIES.addAll(MATERIAL_TEXT_INPUT_LAYOUT);
        VIEW_PROPERTIES.addAll(MATERIAL_BUTTON);

        SORTED_PROPERTIES.addAll(LAYOUT_PROPERTIES);
        SORTED_PROPERTIES.addAll(VIEW_PROPERTIES);
    }

    public enum PropertyType {
        IntConstant(Integer.TYPE),
        EnumConstant(Enum.class),
        LayoutSize(Integer.TYPE),
        Size(Integer.TYPE),
        FloatSize(Float.class),
        TextSize(Float.class),
        Text(CharSequence.class),
        Float(Float.class),
        Int(Integer.TYPE),
        Bool(Boolean.TYPE),
        ID(Integer.TYPE),
        Color(Integer.TYPE),
        Drawable(Drawable.class),
        DrawableResource(Drawable.class),
        TextAppearance(String.class),
        Event(CharSequence.class),

        Menu(String.class);
        public final Class<?> valueType;

        PropertyType(Class<?> cls) {
            this.valueType = cls;
        }
    }

    public static class PropertySpec {
        public String attrName;
        public Class<?> constantClazz;
        public String constantClassName;
        public String constantFieldPrefix;
        public boolean isLayoutProperty;
        public String setterName;
        public Class<?> setterProxyClazz;
        public Class<?> targetClazz;
        public PropertyType type;
        private final String displayName;

        public PropertySpec(Class<?> clazz, String name, Class<?> setterProxyClazz, String setterName, PropertyType type, Class<?> constantClazz, String constantFieldPrefix) {
            this(clazz, name, setterName, type, constantClazz, constantFieldPrefix);
            this.setterProxyClazz = setterProxyClazz;
        }

        public PropertySpec(Class<?> clazz, String name, String setterName, PropertyType type, @NonNull Class<?> constantClazz, String constantFieldPrefix) {
            this(clazz, name, setterName, type);
            this.constantClazz = constantClazz;
            this.constantClassName = constantClazz.getName();
            this.constantFieldPrefix = constantFieldPrefix;
        }

        public PropertySpec(Class<?> clazz, String name, Class<?> setterProxyClazz, String setterName, PropertyType type) {
            this(clazz, name, setterName, type);
            this.setterProxyClazz = setterProxyClazz;
        }

        public PropertySpec(Class<?> clazz, @NonNull String name, String setterName, PropertyType type) {
            this.constantClazz = clazz;
            this.targetClazz = clazz;
            this.setterName = setterName;
            this.attrName = name;
            this.displayName = name;
            this.type = type;
            this.isLayoutProperty = name.startsWith("layout_");
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
