package sheridan.czuberad.sideeye.Prompts

import android.app.AlertDialog
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import sheridan.czuberad.sideeye.UI.SignupActivity


class SignupPrompts {

    fun signupDriverPrompt(signupActivity: SignupActivity): String {
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(signupActivity)
        builder.setTitle("Driver Signup")
        var nameText = ""
        val name = EditText(signupActivity)

        name.setHint("Enter name")

        name.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(name)

        builder.setPositiveButton("Submit", DialogInterface.OnClickListener { dialog, which ->
                nameText = name.text.toString()

        })
        builder.show()
        return nameText
    }
}