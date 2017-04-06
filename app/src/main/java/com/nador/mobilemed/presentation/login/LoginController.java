package com.nador.mobilemed.presentation.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.nador.mobilemed.presentation.base.BaseController;
import com.nador.mobilemed.presentation.dashboard.DashboardController;
import com.nador.mobilemed.presentation.presenter.LoginControllerPresenter;
import com.nador.mobilemed.presentation.widget.BulkyAlertDialog;
import com.nador.mobilemed.presentation.widget.BulkyInputDialog;
import com.nador.mobilemed.R;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by nador on 20/06/16.
 */
public class LoginController extends BaseController<ILoginController, LoginControllerPresenter> implements ILoginController {

    @Bind(R.id.usernameField)
    EditText mUsernameField;
    @Bind(R.id.passwordField)
    EditText mPasswordField;
    @Bind(R.id.rememberCheckBox)
    CheckBox mRememberCheckBox;

    @OnClick(R.id.forgotPasswordTextView)
    protected void onForgotPasswordTextViewTapped() {
        initiateForgotPassword();
    }

    @OnClick(R.id.loginButton)
    protected void onLoginButtonTapped() {
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();

        if (username.isEmpty()) {
            Context context = getActivity();
            BulkyAlertDialog alertDialog = new BulkyAlertDialog(context, context.getString(R.string.LOGIN_ALERT_EMPTY_USERNAME));
            alertDialog.show();
            return;
        }

        if (password.isEmpty()) {
            Context context = getActivity();
            BulkyAlertDialog alertDialog = new BulkyAlertDialog(context, context.getString(R.string.LOGIN_ALERT_EMPTY_PASSWORD));
            alertDialog.show();
            return;
        }

        showLoading();
        getPresenter().login(username, password, mRememberCheckBox.isChecked());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_login, container, false);
    }

    @NonNull
    @Override
    public LoginControllerPresenter createPresenter() {
        return new LoginControllerPresenter(getActivity());
    }

    @Override
    public boolean handleBack() {
        return true;
    }

    @Override
    public void loginSucceeded() {
        hideLoading();
        navigateToDashboard();
    }

    @Override
    public void loginFailed(final String message) {
        hideLoading();
        Context context = getActivity();
        BulkyAlertDialog alertDialog = new BulkyAlertDialog(context, context.getString(R.string.LOGIN_ALERT_LOGIN_UNSUCCESSFUL));
        alertDialog.show();
    }

    @Override
    public void newPasswordRequireData() {
        hideLoading();

        Context context = getActivity();
        String message = String.format(context.getString(R.string.LOGIN_NEWPASSWORD_HINT));
        BulkyInputDialog dialog = new BulkyInputDialog(context, message);
        dialog.addAffirmativeAction(context.getString(R.string.LOGIN_FORGOT_BUTTON_OK), v -> {
            if (dialog.getFirstInput().isEmpty() || dialog.getSecondInput().isEmpty()) {
                return;
            }

            showLoading();
            getPresenter().setNewPasswordRequiredData(dialog.getFirstInput(), dialog.getSecondInput());
            dialog.dismiss();
        });
        dialog.addCancelAction(v -> {
            getPresenter().cancelNewPasswordFlow();
            dialog.dismiss();
        });

        dialog.setFirstEditText(context.getString(R.string.LOGIN_FIELD_PASSWORD), true);
        dialog.setSecondEditText(context.getString(R.string.LOGIN_FORGOT_FIELD_PASSWORD_REPEAT), true);
        dialog.show();
    }

    @Override
    public void newPasswordNoMatch() {
        hideLoading();

        Context context = getActivity();
        BulkyAlertDialog alertDialog = new BulkyAlertDialog(context, context.getString(R.string.LOGIN_ALERT_FORGOT_PASSWORD_UNEQUAL));
        alertDialog.addAffirmativeAction(context.getString(R.string.LOGIN_FORGOT_BUTTON_OK), v1 -> {
            newPasswordRequireData();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    @Override
    public void forgotPasswordRequireData(String destination, String deliveryMethod) {
        hideLoading();

        Context context = getActivity();
        String message = String.format(context.getString(R.string.LOGIN_FORGOT_VERIFICATION), deliveryMethod, destination);
        BulkyInputDialog dialog = new BulkyInputDialog(context, message);
        dialog.addAffirmativeAction(context.getString(R.string.LOGIN_FORGOT_BUTTON_DONE), v -> {
            if (dialog.getFirstInput().isEmpty() || dialog.getSecondInput().isEmpty() || dialog.getThirdInput().isEmpty()) {
                return;
            }

            showLoading();
            getPresenter().setForgotPasswordRequiredData(dialog.getFirstInput(), dialog.getSecondInput(), dialog.getThirdInput());
            dialog.dismiss();
        });
        dialog.addCancelAction(v -> dialog.dismiss());

        dialog.setFirstEditText(context.getString(R.string.LOGIN_FORGOT_FIELD_VERIFICATION), false);
        dialog.setSecondEditText(context.getString(R.string.LOGIN_FIELD_PASSWORD), true);
        dialog.setThirdEditText(context.getString(R.string.LOGIN_FORGOT_FIELD_PASSWORD_REPEAT), true);
        dialog.show();
    }

    @Override
    public void forgotPasswordSucceeded() {
        hideLoading();

        BulkyAlertDialog dialog = new BulkyAlertDialog(getActivity(), "Password reset successful!");
        dialog.show();
    }

    @Override
    public void forgotPasswordFailed(String message) {
        hideLoading();

        BulkyAlertDialog dialog = new BulkyAlertDialog(getActivity(), "Could not get new password!");
        dialog.show();
    }

    @Override
    public void forgotPasswordNoMatch(final String destination, final String deliveryMethod) {
        hideLoading();

        Context context = getActivity();
        BulkyAlertDialog alertDialog = new BulkyAlertDialog(context, context.getString(R.string.LOGIN_ALERT_FORGOT_PASSWORD_UNEQUAL));
        alertDialog.addAffirmativeAction(context.getString(R.string.LOGIN_FORGOT_BUTTON_OK), v1 -> {
            forgotPasswordRequireData(destination, deliveryMethod);
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    private void navigateToDashboard() {
        getRouter().pushController(RouterTransaction.with(new DashboardController())
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }

    private void initiateForgotPassword() {
        Context context = getActivity();
        BulkyInputDialog dialog = new BulkyInputDialog(context, context.getString(R.string.LOGIN_FORGOT_USERNAME));
        dialog.addAffirmativeAction(context.getString(R.string.LOGIN_FORGOT_BUTTON_NEXT), v -> {
            if (dialog.getFirstInput().isEmpty()) {
                return;
            }
            showLoading();
            getPresenter().forgotPassword(dialog.getFirstInput().toString());
            dialog.dismiss();
        });
        dialog.addCancelAction(v -> {
            dialog.dismiss();
        });

        dialog.setFirstEditText(context.getString(R.string.LOGIN_FIELD_USERNAME), false);
        dialog.show();
    }
}
