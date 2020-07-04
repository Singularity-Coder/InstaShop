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

import com.singularitycoder.instashop.admin.view.AddProductsFragment;
import com.singularitycoder.instashop.categories.view.CategoriesFragment;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;

public final class CustomDialogFragment extends DialogFragment {

    @NonNull
    private final HelperGeneral helperGeneral = new HelperGeneral();

    @Nullable
    private SimpleAlertDialogListener simpleAlertDialogListener;

    @Nullable
    private ListDialogListener listDialogListener;

    public CustomDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (null != getArguments()) {
            if (("simpleAlert").equals(getArguments().getString("DIALOG_TYPE"))) {
                if (("activity").equals(getArguments().getString("KEY_CONTEXT_TYPE"))) {
                    try {
                        simpleAlertDialogListener = (SimpleAlertDialogListener) context;
                    } catch (ClassCastException e) {
                        throw new ClassCastException(getActivity().toString() + " must implement SimpleAlertDialogListener");
                    }
                }
            }

            if (("resetPasswordDialog").equals(getArguments().getString("DIALOG_TYPE"))) {
                try {
                    simpleAlertDialogListener = (SimpleAlertDialogListener) context;
                } catch (ClassCastException e) {
                    throw new ClassCastException(getActivity().toString() + " must implement SimpleAlertDialogListener");
                }
            }

            if (("list").equals(getArguments().getString("DIALOG_TYPE"))) {
                if (("activity").equals(getArguments().getString("KEY_CONTEXT_TYPE"))) {
                    try {
                        listDialogListener = (ListDialogListener) context;
                    } catch (ClassCastException e) {
                        throw new ClassCastException(getActivity().toString() + " must implement ListDialogViewListener");
                    }
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

            if (("updateEmailDialog").equals(getArguments().getString("DIALOG_TYPE"))) {
                updateEmail(builder);
            }

            if (("changePasswordDialog").equals(getArguments().getString("DIALOG_TYPE"))) {
                changePassword(builder);
            }

            if (("list").equals(getArguments().getString("DIALOG_TYPE"))) {
                if (null != getArguments().getStringArray("KEY_LIST") && null != getArguments().getString("KEY_TITLE")) {
                    String[] list = getArguments().getStringArray("KEY_LIST");
                    String title = getArguments().getString("KEY_TITLE");
                    String contextType = getArguments().getString("KEY_CONTEXT_TYPE");
                    String  contextObject = getArguments().getString("KEY_CONTEXT_OBJECT");
                    listDialog(builder, list, title, contextType, contextObject);
                }
            }
        }

        return builder.create();
    }

    @UiThread
    public final void simpleAlertDialog(AlertDialog.Builder builder) {
        SimpleAlertDialogListener simpleAlertDialogListener = (CategoriesFragment) getTargetFragment();

        builder.setTitle("Delete Message");
        builder.setMessage("Are you sure you want to delete getContext() message?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setCancelable(true);
        builder
                .setPositiveButton("OK", (dialog1, id) -> {
                    simpleAlertDialogListener.onAlertDialogPositiveClick("DIALOG_TYPE_SIMPLE_ALERT", CustomDialogFragment.this, null);
                })
                .setNegativeButton("CANCEL", (dialog12, id) -> {
                    simpleAlertDialogListener.onAlertDialogNegativeClick("DIALOG_TYPE_SIMPLE_ALERT", CustomDialogFragment.this);
                })
                .setNeutralButton("LATER", (dialogInterface, id) -> {
                    simpleAlertDialogListener.onAlertDialogNeutralClick("DIALOG_TYPE_SIMPLE_ALERT", CustomDialogFragment.this);
                });
    }

    @UiThread
    public final void resetPasswordDialog(AlertDialog.Builder builder) {
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
                    Map<Object, Object> map = new HashMap<>();
                    map.put("KEY_EMAIL", valueOf(etSendToEmail.getText()));
                    simpleAlertDialogListener.onAlertDialogPositiveClick("DIALOG_TYPE_RESET_PASSWORD", CustomDialogFragment.this, map);
                } else {
                    Toast.makeText(getContext(), "Email is Required!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("CANCEL", (dialog12, id) -> simpleAlertDialogListener.onAlertDialogNegativeClick("DIALOG_TYPE_RESET_PASSWORD", CustomDialogFragment.this));
    }

    @UiThread
    private void updateEmail(AlertDialog.Builder builder) {
        SimpleAlertDialogListener simpleAlertDialogListener = (CategoriesFragment) getTargetFragment();

        final LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(linearParams);

        final EditText etUpdateEmail = new EditText(getContext());
        etUpdateEmail.setHint("Type New Email");
        etUpdateEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        LinearLayout.LayoutParams etUpdateEmailParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etUpdateEmailParams.setMargins(48, 16, 48, 0);
        etUpdateEmail.setLayoutParams(etUpdateEmailParams);

        linearLayout.addView(etUpdateEmail);

        builder.setTitle("Update Email");
        builder.setMessage("Enter new Email ID!");
        builder.setView(linearLayout);
        builder.setCancelable(false);
        builder.setPositiveButton("UPDATE", (dialog1, id) -> {
            if (helperGeneral.hasInternet(getContext())) {
                if (!TextUtils.isEmpty(valueOf(etUpdateEmail.getText()))) {
                    Map<Object, Object> map = new HashMap<>();
                    map.put("KEY_EMAIL", valueOf(etUpdateEmail.getText()));
                    simpleAlertDialogListener.onAlertDialogPositiveClick("DIALOG_TYPE_UPDATE_EMAIL", CustomDialogFragment.this, map);
                } else {
                    Toast.makeText(getContext(), "Email is Required!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("CANCEL", (dialog12, id) -> simpleAlertDialogListener.onAlertDialogNegativeClick("DIALOG_TYPE_UPDATE_EMAIL", CustomDialogFragment.this));
    }

    @UiThread
    private void changePassword(AlertDialog.Builder builder) {
        SimpleAlertDialogListener simpleAlertDialogListener = (CategoriesFragment) getTargetFragment();

        final LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(linearParams);

        final EditText etNewPassword = new EditText(getContext());
        etNewPassword.setHint("Type New Password");
        etNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        LinearLayout.LayoutParams etNewPasswordParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etNewPasswordParams.setMargins(48, 16, 48, 0);
        etNewPassword.setLayoutParams(etNewPasswordParams);

        linearLayout.addView(etNewPassword);

        builder.setTitle("Change Password");
        builder.setMessage("Type new password.");
        builder.setView(linearLayout);
        builder.setCancelable(false);
        builder.setPositiveButton("CHANGE", (dialog1, id) -> {
            if (helperGeneral.hasInternet(getContext())) {
                if (!TextUtils.isEmpty(valueOf(etNewPassword.getText()))) {
                    Map<Object, Object> map = new HashMap<>();
                    map.put("KEY_PASSWORD", valueOf(etNewPassword.getText()));
                    simpleAlertDialogListener.onAlertDialogPositiveClick("DIALOG_TYPE_CHANGE_PASSWORD", CustomDialogFragment.this, map);
                } else {
                    Toast.makeText(getContext(), "Password is Required!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("CANCEL", (dialog12, id) -> simpleAlertDialogListener.onAlertDialogNegativeClick("DIALOG_TYPE_UPDATE_EMAIL", CustomDialogFragment.this));
    }

    @UiThread
    public void listDialog(AlertDialog.Builder builder, String[] list, String title, String contextType, String contextObject) {

        if (("fragment").equals(contextType) && ("AddProductsFragment").equals(contextObject)) {
            listDialogListener = (AddProductsFragment) getTargetFragment();
        }

        builder.setTitle(title);
        builder.setCancelable(false);
        String[] selectArray = list;
        builder.setItems(selectArray, (dialog, which) -> {
            for (int i = 0; i < list.length; i++) {
                if (which == i) {
                    if (null != listDialogListener) listDialogListener.onListDialogItemClick(selectArray[i]);
                }
            }
        });
    }

    public final void setSimpleAlertDialogListener(SimpleAlertDialogListener simpleAlertDialogListener) {
        this.simpleAlertDialogListener = simpleAlertDialogListener;
    }

    public interface SimpleAlertDialogListener {
        void onAlertDialogPositiveClick(String dialogType, DialogFragment dialog, Map<Object, Object> map);

        void onAlertDialogNegativeClick(String dialogType, DialogFragment dialog);

        void onAlertDialogNeutralClick(String dialogType, DialogFragment dialog);
    }

    public interface ListDialogListener {
        void onListDialogItemClick(String listItemText);
    }
}