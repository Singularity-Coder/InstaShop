package com.singularitycoder.instashop.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import static java.lang.String.valueOf;

public class CustomDialogFragment extends DialogFragment {

    @NonNull
    private final HelperGeneral helperGeneral = new HelperGeneral();

    @Nullable
    private SimpleAlertDialogListener simpleAlertDialogListener;

    @Nullable
    private ListDialogListener listDialogListener;

    @Nullable
    private ResetPasswordListener resetPasswordListener;

    public CustomDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (null != getArguments()) {
            if (("simpleAlert").equals(getArguments().getString("DIALOG_TYPE"))) {
                try {
                    simpleAlertDialogListener = (SimpleAlertDialogListener) context;
                } catch (ClassCastException e) {
                    throw new ClassCastException(getActivity().toString() + " must implement SimpleAlertDialogListener");
                }
            }

            if (("resetPasswordDialog").equals(getArguments().getString("DIALOG_TYPE"))) {
                try {
                    resetPasswordListener = (ResetPasswordListener) context;
                } catch (ClassCastException e) {
                    throw new ClassCastException(getActivity().toString() + " must implement ResetPasswordListener");
                }
            }

            if (("list").equals(getArguments().getString("DIALOG_TYPE"))) {
                try {
                    listDialogListener = (ListDialogListener) context;
                } catch (ClassCastException e) {
                    throw new ClassCastException(getActivity().toString() + " must implement ListDialogViewListener");
                }
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (null != getArguments()) {
            if (("simpleAlert").equals(getArguments().getString("DIALOG_TYPE"))) {
                simpleAlertDialog(builder);
            }

            if (("resetPasswordDialog").equals(getArguments().getString("DIALOG_TYPE"))) {
                resetPasswordDialog(builder);
            }

            if (("list").equals(getArguments().getString("DIALOG_TYPE"))) {
                if (null != getArguments().getStringArray("KEY_LIST") && null != getArguments().getString("KEY_TITLE")) {
                    String[] list = getArguments().getStringArray("KEY_LIST");
                    String title = getArguments().getString("KEY_TITLE");
                    listDialog(builder, list, title);
                }
            }
        }

        return builder.create();
    }

    @UiThread
    public void simpleAlertDialog(AlertDialog.Builder builder) {
        builder.setTitle("Delete Message");
        builder.setMessage("Are you sure you want to delete getContext() message?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setCancelable(true);
        builder
                .setPositiveButton("OK", (dialog1, id) -> {
                    simpleAlertDialogListener.onDialogPositiveClick("SIMPLE ALERT", CustomDialogFragment.this);
                })
                .setNegativeButton("CANCEL", (dialog12, id) -> {
                    simpleAlertDialogListener.onDialogNegativeClick("SIMPLE ALERT", CustomDialogFragment.this);
                })
                .setNeutralButton("LATER", (dialogInterface, id) -> {
                    simpleAlertDialogListener.onDialogNeutralClick("SIMPLE ALERT", CustomDialogFragment.this);
                });
    }

    @UiThread
    public void resetPasswordDialog(AlertDialog.Builder builder) {

        final LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(linearParams);

        final EditText etSendToEmail = new EditText(getContext());
        etSendToEmail.setHint("Type Email");
        etSendToEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        LinearLayout.LayoutParams etSendToEmailParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etSendToEmailParams.setMargins(48, 16, 48, 0);
        etSendToEmail.setLayoutParams(etSendToEmailParams);

        linearLayout.addView(etSendToEmail);

        builder.setTitle("Forgot Password");
        builder.setMessage("Enter registered Email ID to receive password reset instructions.");
        builder.setView(linearLayout);
        builder.setCancelable(false);
        builder.setPositiveButton("RESET", (dialog1, id) -> {
            if (helperGeneral.hasInternet(getContext())) {
                if (!TextUtils.isEmpty(valueOf(etSendToEmail.getText()))) {
                    resetPasswordListener.onResetClicked("RESET PASSWORD", CustomDialogFragment.this, valueOf(etSendToEmail.getText()));
                } else {
                    Toast.makeText(getContext(), "Email is Required!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("CANCEL", (dialog12, id) -> resetPasswordListener.onCancelClicked("RESET PASSWORD", CustomDialogFragment.this));
    }

    @UiThread
    public void listDialog(AlertDialog.Builder builder, String[] list, String title) {
        builder.setTitle(title);
        builder.setCancelable(false);
        String[] selectArray = list;
        builder.setItems(selectArray, (dialog, which) -> {
            for (int i = 0; i < list.length; i++) {
                if (which == i) {
                    if (null != listDialogListener) {
                        listDialogListener.onListDialogItemClicked(selectArray[i]);
                    }
                }
            }
        });
    }

    public interface SimpleAlertDialogListener {
        void onDialogPositiveClick(String dialogType, DialogFragment dialog);

        void onDialogNegativeClick(String dialogType, DialogFragment dialog);

        void onDialogNeutralClick(String dialogType, DialogFragment dialog);
    }

    public interface ResetPasswordListener {
        void onResetClicked(String dialogType, DialogFragment dialog, String email);

        void onCancelClicked(String dialogType, DialogFragment dialog);
    }

    public interface ListDialogListener {
        void onListDialogItemClicked(String listItemText);
    }
}