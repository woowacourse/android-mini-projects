@file:OptIn(kotlinx.cinterop.BetaInteropApi::class, kotlinx.cinterop.ExperimentalForeignApi::class)

package a4.dogsignal.ui.common.component.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSDate
import platform.Foundation.NSLocale
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIApplication
import platform.UIKit.UIButton
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventTouchUpInside
import platform.UIKit.UIDatePicker
import platform.UIKit.UIDatePickerMode
import platform.UIKit.UIDatePickerStyle
import platform.UIKit.UIFont
import platform.UIKit.UILabel
import platform.UIKit.UIView
import platform.UIKit.UIViewController

@Composable
internal fun IosNativePickerDialog(
    title: String,
    date: NSDate,
    mode: UIDatePickerMode,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: (NSDate) -> Unit,
) {
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val currentOnCancelClick by rememberUpdatedState(onCancelClick)
    val currentOnConfirmClick by rememberUpdatedState(onConfirmClick)

    DisposableEffect(title, date, mode) {
        val presenter = topViewController()
        if (presenter == null) {
            currentOnDismissRequest()
            onDispose { }
        } else {
            val pickerController =
                NativePickerViewController(
                    pickerTitle = title,
                    date = date,
                    mode = mode,
                    onCancelClick = { currentOnCancelClick() },
                    onConfirmClick = { selectedDate -> currentOnConfirmClick(selectedDate) },
                )
            pickerController.modalPresentationStyle = MODAL_PRESENTATION_OVER_FULL_SCREEN

            presenter.presentViewController(
                viewControllerToPresent = pickerController,
                animated = true,
                completion = null,
            )

            onDispose {
                pickerController.dismissIfNeeded()
            }
        }
    }
}

private class NativePickerViewController(
    private val pickerTitle: String,
    private val date: NSDate,
    private val mode: UIDatePickerMode,
    private val onCancelClick: () -> Unit,
    private val onConfirmClick: (NSDate) -> Unit,
) : UIViewController(nibName = null, bundle = null) {
    private var isHandled = false
    private val sheetView = UIView()
    private val titleLabel = UILabel()
    private val datePicker = UIDatePicker()
    private val cancelButton = UIButton()
    private val confirmButton = UIButton()

    override fun viewDidLoad() {
        super.viewDidLoad()

        view.setOpaque(false)
        view.setBackgroundColor(UIColor.blackColor.colorWithAlphaComponent(DIM_ALPHA))

        sheetView.setBackgroundColor(UIColor.whiteColor)
        sheetView.layer.cornerRadius = SHEET_CORNER_RADIUS
        sheetView.layer.masksToBounds = true
        view.addSubview(sheetView)

        titleLabel.text = pickerTitle
        titleLabel.font = UIFont.boldSystemFontOfSize(TITLE_FONT_SIZE)
        titleLabel.textColor = UIColor.blackColor
        sheetView.addSubview(titleLabel)

        datePicker.datePickerMode = mode
        datePicker.preferredDatePickerStyle = UIDatePickerStyle.UIDatePickerStyleWheels
        datePicker.setDate(date, animated = false)
        datePicker.setLocale(NSLocale(localeIdentifier = KOREAN_LOCALE_IDENTIFIER))
        datePicker.setBackgroundColor(UIColor.whiteColor)
        sheetView.addSubview(datePicker)

        configureButton(
            button = cancelButton,
            title = "취소",
            backgroundColor = UIColor(red = 0.94, green = 0.96, blue = 0.98, alpha = 1.0),
            titleColor = UIColor.darkGrayColor,
            actionName = "cancelTapped",
        )
        configureButton(
            button = confirmButton,
            title = "완료",
            backgroundColor = UIColor(red = 0.46, green = 0.72, blue = 0.65, alpha = 1.0),
            titleColor = UIColor.whiteColor,
            actionName = "confirmTapped",
        )
    }

    override fun viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()

        val rootWidth = view.bounds.useContents { size.width }
        val rootHeight = view.bounds.useContents { size.height }
        val horizontalPadding = 31.0
        val buttonSpacing = 30.0
        val buttonWidth = (rootWidth - horizontalPadding * 2 - buttonSpacing) / 2

        sheetView.setFrame(
            CGRectMake(
                x = 0.0,
                y = rootHeight - SHEET_HEIGHT,
                width = rootWidth,
                height = SHEET_HEIGHT,
            ),
        )
        titleLabel.setFrame(
            CGRectMake(
                x = horizontalPadding,
                y = 36.0,
                width = rootWidth - horizontalPadding * 2,
                height = 32.0,
            ),
        )
        datePicker.setFrame(
            CGRectMake(
                x = horizontalPadding,
                y = 84.0,
                width = rootWidth - horizontalPadding * 2,
                height = PICKER_HEIGHT,
            ),
        )
        cancelButton.setFrame(
            CGRectMake(
                x = horizontalPadding,
                y = 320.0,
                width = buttonWidth,
                height = BUTTON_HEIGHT,
            ),
        )
        confirmButton.setFrame(
            CGRectMake(
                x = horizontalPadding + buttonWidth + buttonSpacing,
                y = 320.0,
                width = buttonWidth,
                height = BUTTON_HEIGHT,
            ),
        )
    }

    fun dismissIfNeeded() {
        if (!isHandled) {
            isHandled = true
            dismissViewControllerAnimated(flag = false, completion = null)
        }
    }

    @ObjCAction
    fun cancelTapped() {
        finish {
            onCancelClick()
        }
    }

    @ObjCAction
    fun confirmTapped() {
        val selectedDate = datePicker.date
        finish {
            onConfirmClick(selectedDate)
        }
    }

    private fun configureButton(
        button: UIButton,
        title: String,
        backgroundColor: UIColor,
        titleColor: UIColor,
        actionName: String,
    ) {
        button.setTitle(title, forState = 0u)
        button.setTitleColor(titleColor, forState = 0u)
        button.titleLabel?.font = UIFont.boldSystemFontOfSize(BUTTON_FONT_SIZE)
        button.setBackgroundColor(backgroundColor)
        button.layer.cornerRadius = BUTTON_CORNER_RADIUS
        button.addTarget(
            target = this,
            action = NSSelectorFromString(actionName),
            forControlEvents = UIControlEventTouchUpInside,
        )
        sheetView.addSubview(button)
    }

    private fun finish(action: () -> Unit) {
        if (isHandled) return
        isHandled = true
        dismissViewControllerAnimated(
            flag = true,
            completion = action,
        )
    }
}

private fun topViewController(): UIViewController? {
    var viewController = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return null
    while (viewController.presentedViewController != null) {
        viewController = viewController.presentedViewController!!
    }
    return viewController
}

private const val BUTTON_CORNER_RADIUS = 18.0
private const val BUTTON_FONT_SIZE = 14.0
private const val BUTTON_HEIGHT = 48.0
private const val DIM_ALPHA = 0.24
private const val KOREAN_LOCALE_IDENTIFIER = "ko_KR"
private const val MODAL_PRESENTATION_OVER_FULL_SCREEN = 5L
private const val PICKER_HEIGHT = 216.0
private const val SHEET_CORNER_RADIUS = 28.0
private const val SHEET_HEIGHT = 400.0
private const val TITLE_FONT_SIZE = 24.0
