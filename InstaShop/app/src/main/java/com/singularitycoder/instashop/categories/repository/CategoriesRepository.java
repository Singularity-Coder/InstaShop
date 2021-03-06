package com.singularitycoder.instashop.categories.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.singularitycoder.instashop.auth.model.AuthUserItem;
import com.singularitycoder.instashop.helpers.HelperConstants;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;

import java.util.List;

import static java.lang.String.valueOf;

public final class CategoriesRepository {

    @NonNull
    private static final String TAG = "DashboardRepository";

    @NonNull
    private static CategoriesRepository _instance;

    @NonNull
    private HelperSharedPreference helperSharedPreference;


    public CategoriesRepository() {
    }

    @NonNull
    public static synchronized CategoriesRepository getInstance() {
        if (null == _instance) {
            _instance = new CategoriesRepository();
        }
        return _instance;
    }

    public final MutableLiveData<RequestStateMediator> readAuthUserData(
            @NonNull final Context context, @NonNull final String email) {

        helperSharedPreference = HelperSharedPreference.getInstance(context);

        final MutableLiveData<RequestStateMediator> liveData = new MutableLiveData<>();
        final RequestStateMediator requestStateMediator = new RequestStateMediator();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        FirebaseFirestore.getInstance()
                .collection(HelperConstants.COLL_AUTH_USERS)
                .whereEqualTo(HelperConstants.KEY_EMAIL, email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> docList = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot docSnap : docList) {
                            AuthUserItem authUserItem = docSnap.toObject(AuthUserItem.class);
                            if (authUserItem != null) {
                                Log.d(TAG, "`AuthItem: " + authUserItem);

                                if (!("").equals(valueOf(docSnap.getString("memberType")))) {
                                    authUserItem.setMemberType(valueOf(docSnap.getString("memberType")));
                                    helperSharedPreference.setMemberType(valueOf(docSnap.getString("memberType")));
                                }

                                if (!("").equals(valueOf(docSnap.getString("name")))) {
                                    authUserItem.setName(valueOf(docSnap.getString("name")));
                                    helperSharedPreference.setName(valueOf(docSnap.getString("name")));
                                }

                                if (!("").equals(valueOf(docSnap.getString("email")))) {
                                    authUserItem.setEmail(valueOf(docSnap.getString("email")));
                                    helperSharedPreference.setEmail(valueOf(docSnap.getString("email")));
                                }

                                Log.d(TAG, "firedoc id: " + docSnap.getId());
                                authUserItem.setDocId(docSnap.getId());
                                helperSharedPreference.setUserDocId(docSnap.getId());
                            }
                        }

                        requestStateMediator.set(null, UiState.SUCCESS, "Got Data!", "STATE_AUTH_USER_DATA");
                        liveData.postValue(requestStateMediator);
                    }
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveData.postValue(requestStateMediator);
                });
        return liveData;
    }

    public final MutableLiveData<RequestStateMediator> deleteAccount() {
        final MutableLiveData<RequestStateMediator> liveData = new MutableLiveData<>();
        final RequestStateMediator requestStateMediator = new RequestStateMediator();

        if (null != FirebaseAuth.getInstance().getCurrentUser()) {

            requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
            liveData.postValue(requestStateMediator);

            FirebaseAuth
                    .getInstance()
                    .getCurrentUser()
                    .delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            requestStateMediator.set(null, UiState.SUCCESS, "Got Data!", "STATE_DELETE_ACCOUNT");
                            liveData.postValue(requestStateMediator);
                        }
                    })
                    .addOnFailureListener(e -> {
                        requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                        liveData.postValue(requestStateMediator);
                    });
        }
        return liveData;
    }

    public final MutableLiveData<RequestStateMediator> updateEmail(
            @NonNull final DialogFragment dialog, @NonNull final String newEmail) {

        final MutableLiveData<RequestStateMediator> liveData = new MutableLiveData<>();
        final RequestStateMediator requestStateMediator = new RequestStateMediator();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        FirebaseAuth
                .getInstance()
                .getCurrentUser()
                .updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        requestStateMediator.set(null, UiState.SUCCESS, "Got Data!", "STATE_UPDATE_EMAIL");
                        liveData.postValue(requestStateMediator);
                        if (dialog.isVisible()) dialog.dismiss();
                        signOut();
                    }
                })
                .addOnFailureListener(e -> {
                    requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                    liveData.postValue(requestStateMediator);
                });
        return liveData;
    }

    public final MutableLiveData<RequestStateMediator> changePassword(DialogFragment dialog, String password) {

        final MutableLiveData<RequestStateMediator> liveData = new MutableLiveData<>();
        final RequestStateMediator requestStateMediator = new RequestStateMediator();

        requestStateMediator.set(null, UiState.LOADING, "Please wait...", null);
        liveData.postValue(requestStateMediator);

        if (null != FirebaseAuth.getInstance().getCurrentUser()) {
            FirebaseAuth
                    .getInstance()
                    .getCurrentUser()
                    .updatePassword(password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            requestStateMediator.set(null, UiState.SUCCESS, "Got Data!", "STATE_CHANGE_PASSWORD");
                            liveData.postValue(requestStateMediator);
                            if (dialog.isVisible()) dialog.dismiss();
                            signOut();
                        }
                    })
                    .addOnFailureListener(e -> {
                        requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                        liveData.postValue(requestStateMediator);
                    });
        }
        return liveData;
    }

    public final MutableLiveData<RequestStateMediator> signOut() {

        final MutableLiveData<RequestStateMediator> liveData = new MutableLiveData<>();
        final RequestStateMediator requestStateMediator = new RequestStateMediator();

        FirebaseAuth
                .getInstance()
                .signOut();

        requestStateMediator.set(null, UiState.SUCCESS, "Signed Out!", "STATE_SIGN_OUT");
        liveData.postValue(requestStateMediator);

        return liveData;
    }

}
