package com.mcal.uidesigner.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.uidesigner.R;
import com.mcal.uidesigner.XmlLayoutDesignActivity;

import java.util.ArrayList;
import java.util.List;

public class PropertiesDialog extends MessageBox {
    private final List<PropertyCommand> enabledCommands = new ArrayList<>();
    private final String title;

    public PropertiesDialog(String title, @NonNull List<? extends PropertyCommand> commands) {
        this.title = title;
        for (PropertyCommand command : commands) {
            if (command.canRun()) {
                this.enabledCommands.add(command);
            }
        }
    }

    @Override
    protected Dialog buildDialog(Activity activity) {
        ListView listView = new ListView(activity);
        listView.setAdapter((ListAdapter) new PropertyCommandEntryAdapter(activity, this.enabledCommands));
        final AlertDialog dialog = new AlertDialog.Builder(activity).setCancelable(true).setView(listView).setTitle(this.title).create();
        dialog.setCanceledOnTouchOutside(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                ((PropertyCommand) enabledCommands.get(position)).run();
            }
        });
        return dialog;
    }

    public interface PropertyCommand {
        boolean canRun();

        String getHelpUrl();

        int getIconAttr();

        String getName();

        void run();
    }

    private static class PropertyCommandEntryAdapter extends ArrayAdapter<PropertyCommand> {
        public PropertyCommandEntryAdapter(Context context, List<PropertyCommand> commands) {
            super(context, R.layout.propertydialog_entry, commands);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int i = 0;
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.propertydialog_entry, parent, false);
            }
            PropertyCommand command = getItem(position);
            ((AppCompatTextView) view.findViewById(R.id.widgetmenuEntryName)).setText(Html.fromHtml(command.getName()));
            ((AppCompatImageView) view.findViewById(R.id.widgetmenuEntryImage)).setImageResource(AndroidHelper.obtainImageResourceId(getContext(), command.getIconAttr()));
            final String helpUrl = command.getHelpUrl();
            View helpView = view.findViewById(R.id.widgetmenuHelpButton);
            if (helpUrl == null) {
                i = 8;
            }
            helpView.setVisibility(i);
            if (helpUrl != null) {
                helpView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((XmlLayoutDesignActivity) getContext()).showHelp(helpUrl);
                    }
                });
            }
            return view;
        }
    }
}
