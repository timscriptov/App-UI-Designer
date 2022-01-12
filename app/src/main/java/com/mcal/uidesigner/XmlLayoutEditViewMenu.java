package com.mcal.uidesigner;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.mcal.uidesigner.R;
import com.mcal.uidesigner.common.MessageBox;
import com.mcal.uidesigner.common.PropertiesDialog;
import com.mcal.uidesigner.common.ValueRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XmlLayoutEditViewMenu {
    public static void showViewEditMenu(Activity activity, @NonNull XmlLayoutEditView editView) {
        MessageBox.showDialog(activity, new PropertiesDialog(editView.getPath(), getContextCommands(activity, editView)));
    }

    @NonNull
    private static List<PropertiesDialog.PropertyCommand> getContextCommands(Activity activity, XmlLayoutEditView editView) {
        List<PropertiesDialog.PropertyCommand> commands = new ArrayList<>();
        commands.addAll(getFixedContextCommands(activity, editView));
        commands.addAll(getPropertyCommands(activity, editView));
        return commands;
    }

    @NonNull
    private static List<PropertiesDialog.PropertyCommand> getPropertyCommands(final Activity activity, @NonNull final XmlLayoutEditView editView) {
        String name;
        List<PropertiesDialog.PropertyCommand> result = new ArrayList<>();
        for (final AttributeValue attribute : editView.getAttributes()) {
            if (attribute.isStyled()) {
                name = attribute.property.getDisplayName() + " styled <b>" + attribute.getDisplayValue() + "</b>";
            } else if (attribute.hasValue()) {
                name = attribute.property.getDisplayName() + " = <b>" + attribute.getDisplayValue() + "</b>";
            } else {
                name = attribute.property.getDisplayName();
            }
            String finalName = name;
            result.add(new PropertiesDialog.PropertyCommand() {
                @Override
                public void run() {
                    XmlLayoutPropertyEditor.queryValue(activity, editView, attribute);
                }

                @Override
                public String getName() {
                    return finalName;
                }

                @Override
                public boolean canRun() {
                    return true;
                }

                @Override
                public int getIconAttr() {
                    return R.attr.icon_manage;
                }

                @Override
                public String getHelpUrl() {
                    return "android/R.attr.html#" + finalName;
                }
            });
        }
        return result;
    }

    @NonNull
    private static List<PropertiesDialog.PropertyCommand> getFixedContextCommands(final Activity activity, final XmlLayoutEditView editView) {
        return Arrays.asList(new PropertiesDialog.PropertyCommand() {
            @Override
            public String getName() {
                return "Parent View...";
            }

            @Override
            public boolean canRun() {
                return editView.getParentView() != null;
            }

            @Override
            public void run() {
                XmlLayoutEditViewMenu.showViewEditMenu(activity, editView.getParentView());
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_goto;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }
        }, new PropertiesDialog.PropertyCommand() {
            @Override
            public String getName() {
                return "Source code...";
            }

            @Override
            public boolean canRun() {
                return activity instanceof XmlLayoutDesignActivity;
            }

            @Override
            public void run() {
                if (activity instanceof XmlLayoutDesignActivity) {
                    editView.gotoSourceCode((XmlLayoutDesignActivity) activity);
                }
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_goto;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }
        }, new PropertiesDialog.PropertyCommand() {
            @Override
            public String getName() {
                return "Add inside...";
            }

            @Override
            public boolean canRun() {
                return editView.canAddInside();
            }

            @Override
            public void run() {
                XmlLayoutWidgetPicker.selectView(activity, "Add inside " + editView.getNodeName() + "...", new ValueRunnable<NewWidget>() {
                    public void run(NewWidget widget) {
                        editView.addViewInside(widget);
                    }
                });
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_add;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }
        }, new PropertiesDialog.PropertyCommand() {
            @Override
            public String getName() {
                return "Add above...";
            }

            @Override
            public boolean canRun() {
                return editView.canAddAbove();
            }

            @Override
            public void run() {
                XmlLayoutWidgetPicker.selectView(activity, "Add above " + editView.getNodeName() + "...", new ValueRunnable<NewWidget>() {
                    public void run(NewWidget widget) {
                        editView.addViewAbove(widget);
                    }
                });
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_add;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }
        }, new PropertiesDialog.PropertyCommand() {
            @Override
            public String getName() {
                return "Add below...";
            }

            @Override
            public boolean canRun() {
                return editView.canAddBelow();
            }

            @Override
            public void run() {
                XmlLayoutWidgetPicker.selectView(activity, "Add below " + editView.getNodeName() + "...", new ValueRunnable<NewWidget>() {
                    public void run(NewWidget widget) {
                        editView.addViewBelow(widget);
                    }
                });
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_add;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }
        }, new PropertiesDialog.PropertyCommand() {
            @Override
            public String getName() {
                return "Add before...";
            }

            @Override
            public boolean canRun() {
                return editView.canAddBefore();
            }

            @Override
            public void run() {
                XmlLayoutWidgetPicker.selectView(activity, "Add before " + editView.getNodeName() + "...", new ValueRunnable<NewWidget>() {
                    public void run(NewWidget widget) {
                        editView.addViewBefore(widget);
                    }
                });
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_add;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }
        }, new PropertiesDialog.PropertyCommand() {
            @Override
            public String getName() {
                return "Add behind...";
            }

            @Override
            public boolean canRun() {
                return editView.canAddBehind();
            }

            @Override
            public void run() {
                XmlLayoutWidgetPicker.selectView(activity, "Add behind " + editView.getNodeName() + "...", new ValueRunnable<NewWidget>() {
                    public void run(NewWidget widget) {
                        editView.addViewBehind(widget);
                    }
                });
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_add;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }
        }, new PropertiesDialog.PropertyCommand() {
            @Override
            public String getName() {
                return "Surround with...";
            }

            @Override
            public boolean canRun() {
                return true;
            }

            @Override
            public void run() {
                XmlLayoutWidgetPicker.selectSurroundView(activity, "Surround " + editView.getNodeName() + " with...", new ValueRunnable<NewWidget>() {
                    public void run(NewWidget widget) {
                        editView.surroundWithView(widget);
                    }
                });
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_add;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }
        }, new PropertiesDialog.PropertyCommand() {
            @Override
            public String getName() {
                return "Delete";
            }

            @Override
            public boolean canRun() {
                return true;
            }

            @Override
            public void run() {
                editView.delete();
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_delete;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }
        }, new PropertiesDialog.PropertyCommand() {
            @Override
            public String getName() {
                if (editView.getViewID() == null) {
                    return "ID";
                }
                return "ID = <b>" + editView.getViewID() + "</b>";
            }

            @Override
            public boolean canRun() {
                return true;
            }

            @Override
            public void run() {
                XmlLayoutPropertyEditor.queryID(activity, editView);
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_manage;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }
        }, new PropertiesDialog.PropertyCommand() {
            @Override
            public String getName() {
                if (editView.getStyle() == null) {
                    return "Style";
                }
                return "Style = <b>" + AttributeValue.getDisplayValue(editView.getStyle()) + "</b>";
            }

            @Override
            public boolean canRun() {
                return true;
            }

            @Override
            public void run() {
                XmlLayoutPropertyEditor.queryStyle(activity, editView);
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_manage;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }
        });
    }
}