package ds.meterscanner.mvvm.viewmodel

import ds.databinding.binding
import ds.meterscanner.R
import ds.meterscanner.mvvm.BindableViewModel
import ds.meterscanner.mvvm.invoke

class AuthViewModel : BindableViewModel() {

    val login: CharSequence by binding("")
    val password: CharSequence by binding("")
    /*val loginError = ValidatorField(login) {
        when {
            !Patterns.EMAIL_ADDRESS.matcher(it).matches() -> getString(R.string.wrong_email)
            else -> ""
        }
    }
    val passwordError = ValidatorField(password) {
        when {
            it.isEmpty() -> getString(R.string.shouldnt_be_empty)
            else -> ""
        }
    }*/

    override val runAuthScreenCommand = null

    init {
        toolbarTitle = getString(R.string.log_in)
    }

    fun onSignIn() = async {
        //loginError.validate() && passwordError.validate() || return@async
        authenticator.signIn(login.toString(), password.toString())
        finishCommand()
    }

    fun onSignUp() = async {
        //loginError.validate() && passwordError.validate() || return@async
        authenticator.signUp(login.toString(), password.toString())
        showSnackbarCommand(getString(R.string.user_created))
        onSignIn()
    }
}