package com.mcal.uidesigner.appwizard;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcal.designer.R;
import com.mcal.uidesigner.NewWidget;
import com.mcal.uidesigner.XmlLayoutEditView;
import com.mcal.uidesigner.XmlLayoutEditViewMenu;
import com.mcal.uidesigner.XmlLayoutWidgetPicker;
import com.mcal.uidesigner.XmlLayoutlInflater;
import com.mcal.uidesigner.appwizard.runtime.AppWizardProject;
import com.mcal.uidesigner.common.UndoManager;
import com.mcal.uidesigner.common.ValueRunnable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AppWizardDesignFragment extends Fragment {
    private static final String ARG_SECTION_ID = "ARG_SECTION_ID";
    private static final String ARG_SECTION_LAYOUT = "ARG_SECTION_LAYOUT";
    private static final String ARG_SECTION_TITLE = "ARG_SECTION_TITLE";
    private XmlLayoutlInflater inflater;

    public static AppWizardDesignFragment create(AppWizardProject.AppFragment section) {
        AppWizardDesignFragment fragment = new AppWizardDesignFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_TITLE, section.getTitle());
        args.putString(ARG_SECTION_LAYOUT, section.getLayout());
        args.putInt(ARG_SECTION_ID, section.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup content = (ViewGroup) layoutInflater.inflate(R.layout.appwizard_section, container, false);
        ViewGroup layoutContainer = (ViewGroup) content.findViewById(R.id.appwizardSectionContainer);
        content.setClipChildren(false);
        final int sectionId = getArguments().getInt(ARG_SECTION_ID);
        String resDirPath = getDesignActivity().getResDirPath();
        String xmlFilePath = getDesignActivity().getLayoutFilePath(getArguments().getString(ARG_SECTION_LAYOUT));
        if (!(getUndoManager() == null || this.inflater == null)) {
            getUndoManager().removeListener(this.inflater);
        }
        this.inflater = new XmlLayoutlInflater(layoutContainer, xmlFilePath, resDirPath, getUndoManager()) {
            @Override
            protected void onEmptyLayoutClicked() {
                XmlLayoutWidgetPicker.selectRootView(getActivity(), "Add...", new ValueRunnable<NewWidget>() {
                    public void run(NewWidget widget) {
                        inflater.addView(widget);
                    }
                });
            }

            @Override
            protected void onViewClicked(XmlLayoutEditView editView) {
                XmlLayoutEditViewMenu.showViewEditMenu(getActivity(), editView);
            }

            @Override
            protected void onXmlModified(boolean isUserEdit) {
                if (inflater.getXmlFilePath() == null) {
                    inflater.setXmlFilePath(getDesignActivity().createFragmentLayout(sectionId));
                }
                saveXml(inflater.getXmlFilePath());
            }

            @Override
            protected void onInflated() {
            }
        };
        this.inflater.init();
        this.inflater.setShowBorder(false);
        refreshEditMode();
        return content;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getUndoManager() != null && this.inflater != null) {
            getUndoManager().removeListener(this.inflater);
        }
    }

    public void saveXml(String xmlFilePath) {
        try {
            new File(xmlFilePath).getParentFile().mkdirs();
            FileWriter writer = new FileWriter(xmlFilePath);
            writer.write(this.inflater.getXml());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private UndoManager getUndoManager() {
        return getDesignActivity().getUndoManager();
    }

    public AppWizardDesignActivity getDesignActivity() {
        return (AppWizardDesignActivity) getActivity();
    }

    public void refreshEditMode() {
        this.inflater.setEditMode(getDesignActivity().isEditMode());
    }
}
