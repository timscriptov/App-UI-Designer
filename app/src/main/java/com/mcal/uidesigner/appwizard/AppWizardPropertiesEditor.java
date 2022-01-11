package com.mcal.uidesigner.appwizard;

import com.mcal.designer.R;
import com.mcal.uidesigner.appwizard.runtime.AppWizardProject;
import com.mcal.uidesigner.common.MessageBox;
import com.mcal.uidesigner.common.PropertiesDialog;
import com.mcal.uidesigner.common.ValueRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppWizardPropertiesEditor {
    private final AppWizardDesignActivity activity;
    private AppWizardProject.AppFragment currentFragment;

    public AppWizardPropertiesEditor(AppWizardDesignActivity activity) {
        this.activity = activity;
    }

    public void showProperties() {
        if (this.currentFragment != null) {
            MessageBox.showDialog(this.activity, new PropertiesDialog("Activity \"" + this.activity.getProject().getMainActivity().getTitle() + "\" > Fragment \"" + this.currentFragment.getTitle() + "\"", getFragmentCommands(this.currentFragment)));
        } else {
            MessageBox.showDialog(this.activity, new PropertiesDialog("Activity \"" + this.activity.getProject().getMainActivity().getTitle() + "\"", getAppActivityCommands(this.activity.getProject().getMainActivity())));
        }
    }

    private List<? extends PropertiesDialog.PropertyCommand> getFragmentCommands(final AppWizardProject.AppFragment appFragment) {
        List<PropertiesDialog.PropertyCommand> cmds = new ArrayList<>();
        cmds.add(new PropertiesDialog.PropertyCommand() {
            @Override
            public void run() {
                currentFragment = null;
                showProperties();
            }

            @Override
            public String getName() {
                return "Activity";
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_goto;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }

            @Override
            public boolean canRun() {
                return true;
            }
        });
        if (appFragment.getActivity().getFragments().size() > 1) {
            cmds.add(new PropertiesDialog.PropertyCommand() {
                @Override
                public void run() {
                    appFragment.delete();
                    currentFragment = null;
                    showProperties();
                }

                @Override
                public String getName() {
                    return "Delete";
                }

                @Override
                public int getIconAttr() {
                    return R.attr.icon_delete;
                }

                @Override
                public String getHelpUrl() {
                    return null;
                }

                @Override
                public boolean canRun() {
                    return true;
                }
            });
        }
        addStringProperty(cmds, "Title", appFragment.getTitle(), new ValueRunnable<String>() {
            public void run(String t) {
                appFragment.setTitle(t);
            }
        });
        cmds.add(new PropertiesDialog.PropertyCommand() {
            @Override
            public void run() {
            }

            @Override
            public String getName() {
                return getPropertyTitle("Layout", appFragment.getLayout());
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_manage;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }

            @Override
            public boolean canRun() {
                return true;
            }
        });
        return cmds;
    }

    private List<? extends PropertiesDialog.PropertyCommand> getAppActivityCommands(final AppWizardProject.AppActivity appActivity) {
        List<PropertiesDialog.PropertyCommand> cmds = new ArrayList<>();
        for (final AppWizardProject.AppFragment section : appActivity.getFragments()) {
            cmds.add(new PropertiesDialog.PropertyCommand() {
                @Override
                public void run() {
                    currentFragment = section;
                    showProperties();
                }

                @Override
                public String getName() {
                    return "Fragment &quot;<b>" + section.getTitle() + "</b>&quot;";
                }

                @Override
                public int getIconAttr() {
                    return R.attr.icon_goto;
                }

                @Override
                public String getHelpUrl() {
                    return null;
                }

                @Override
                public boolean canRun() {
                    return true;
                }
            });
        }
        cmds.add(new PropertiesDialog.PropertyCommand() {
            @Override
            public void run() {
                currentFragment = appActivity.addFragment();
                showProperties();
            }

            @Override
            public String getName() {
                return "Add Fragment";
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_add;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }

            @Override
            public boolean canRun() {
                return true;
            }
        });
        addStringProperty(cmds, "Title", appActivity.getTitle(), new ValueRunnable<String>() {
            public void run(String t) {
                appActivity.setTitle(t);
            }
        });
        addNavigationProperty(cmds, appActivity);
        addThemeProperty(cmds, appActivity);
        addBooleanProperty(cmds, "Show Title", appActivity.showTitleValue(), new ValueRunnable<Boolean>() {
            public void run(Boolean b) {
                appActivity.setShowTitle(b);
            }
        });
        addBooleanProperty(cmds, "Show Action Bar", appActivity.showActionBarValue(), new ValueRunnable<Boolean>() {
            public void run(Boolean b) {
                appActivity.setShowActionBar(b);
            }
        });
        addBooleanProperty(cmds, "Fullscreen", appActivity.showFullscreenValue(), new ValueRunnable<Boolean>() {
            public void run(Boolean b) {
                appActivity.setShowFullscreen(b);
            }
        });
        return cmds;
    }

    private void addNavigationProperty(List<PropertiesDialog.PropertyCommand> cmds, final AppWizardProject.AppActivity app) {
        cmds.add(new PropertiesDialog.PropertyCommand() {
            @Override
            public void run() {
                MessageBox.queryFromList(activity, "Navigation", AppWizardProject.NavigationType.getPossibleNames(), new ValueRunnable<String>() {
                    public void run(String t) {
                        app.setNavigationType(AppWizardProject.NavigationType.forName(t));
                    }
                });
            }

            @Override
            public String getName() {
                return getPropertyTitle("Navigation", app.getType().name);
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_manage;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }

            @Override
            public boolean canRun() {
                return true;
            }
        });
    }

    private void addThemeProperty(List<PropertiesDialog.PropertyCommand> cmds, final AppWizardProject.AppActivity app) {
        cmds.add(new PropertiesDialog.PropertyCommand() {
            @Override
            public void run() {
                MessageBox.queryFromList(activity, "Theme", AppWizardProject.Theme.getPossibleNames(), new ValueRunnable<String>() {
                    public void run(String t) {
                        app.setTheme(AppWizardProject.Theme.forName(t));
                    }
                });
            }

            @Override
            public String getName() {
                return getPropertyTitle("Theme", app.getTheme().name);
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_manage;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }

            @Override
            public boolean canRun() {
                return true;
            }
        });
    }

    private void addStringProperty(List<PropertiesDialog.PropertyCommand> cmds, final String title, final String value, final ValueRunnable<String> ok) {
        cmds.add(new PropertiesDialog.PropertyCommand() {
            @Override
            public void run() {
                MessageBox.queryText(activity, title, (String) null, value, new ValueRunnable<String>() {
                    public void run(String t) {
                        ok.run(t);
                    }
                });
            }

            @Override
            public String getName() {
                return getStringPropertyTitle(title, value);
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_manage;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }

            @Override
            public boolean canRun() {
                return true;
            }
        });
    }

    private void addBooleanProperty(List<PropertiesDialog.PropertyCommand> cmds, final String title, final Boolean value, final ValueRunnable<Boolean> ok) {
        cmds.add(new PropertiesDialog.PropertyCommand() {
            @Override
            public void run() {
                MessageBox.queryFromList(activity, title, Arrays.asList("true", "false", "none"), new ValueRunnable<String>() {
                    public void run(String t) {
                        new ValueRunnable<String>() {
                            public void run(String t2) {
                                if ("none".equals(t2)) {
                                    ok.run(null);
                                } else if ("true".equals(t2)) {
                                    ok.run(true);
                                } else {
                                    ok.run(false);
                                }
                            }
                        }.run(t);
                    }
                });
            }

            @Override
            public String getName() {
                return AppWizardPropertiesEditor.this.getPropertyTitle(title, value == null ? null : Boolean.toString(value.booleanValue()));
            }

            @Override
            public int getIconAttr() {
                return R.attr.icon_manage;
            }

            @Override
            public String getHelpUrl() {
                return null;
            }

            @Override
            public boolean canRun() {
                return true;
            }
        });
    }

    public String getStringPropertyTitle(String property, String value) {
        return value == null ? property : property + " = <b>&quot;" + value + "&quot;</b>";
    }

    public String getPropertyTitle(String property, String value) {
        return value == null ? property : property + " = <b>" + value + "</b>";
    }
}
