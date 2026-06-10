package a4.dogsignal.ui.common.component.picker

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

@Composable
internal fun AndroidPickerDialogEffect(
    key: Any,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    createDialog: (Context, handleDialogAction: (() -> Unit) -> Unit) -> AlertDialog,
) {
    val context = LocalContext.current
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val currentOnCancelClick by rememberUpdatedState(onCancelClick)

    DisposableEffect(context, key) {
        var isHandled = false

        fun handleDialogAction(action: () -> Unit) {
            if (!isHandled) {
                isHandled = true
                action()
            }
        }

        val dialog = createDialog(context, ::handleDialogAction)

        dialog.setOnCancelListener {
            handleDialogAction(currentOnDismissRequest)
        }
        dialog.setOnDismissListener {
            handleDialogAction(currentOnDismissRequest)
        }
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).text = "완료"
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).apply {
                text = "취소"
                setOnClickListener {
                    handleDialogAction(currentOnCancelClick)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()

        onDispose {
            if (dialog.isShowing) {
                isHandled = true
                dialog.dismiss()
            }
        }
    }
}
