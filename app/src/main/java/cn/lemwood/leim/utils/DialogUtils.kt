package cn.lemwood.leim.utils

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast

/**
 * 对话框工具类
 * 提供各种常用的对话框功能
 */
object DialogUtils {
    
    /**
     * 显示简单提示对话框
     */
    fun showAlert(
        context: Context,
        title: String? = null,
        message: String,
        positiveText: String = "确定",
        onPositive: (() -> Unit)? = null
    ): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText) { _, _ -> onPositive?.invoke() }
            .setCancelable(true)
            .show()
    }
    
    /**
     * 显示确认对话框
     */
    fun showConfirm(
        context: Context,
        title: String? = null,
        message: String,
        positiveText: String = "确定",
        negativeText: String = "取消",
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null
    ): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText) { _, _ -> onPositive?.invoke() }
            .setNegativeButton(negativeText) { _, _ -> onNegative?.invoke() }
            .setCancelable(true)
            .show()
    }
    
    /**
     * 显示三选项对话框
     */
    fun showThreeChoice(
        context: Context,
        title: String? = null,
        message: String,
        positiveText: String = "确定",
        negativeText: String = "取消",
        neutralText: String = "稍后",
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null,
        onNeutral: (() -> Unit)? = null
    ): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText) { _, _ -> onPositive?.invoke() }
            .setNegativeButton(negativeText) { _, _ -> onNegative?.invoke() }
            .setNeutralButton(neutralText) { _, _ -> onNeutral?.invoke() }
            .setCancelable(true)
            .show()
    }
    
    /**
     * 显示单选列表对话框
     */
    fun showSingleChoice(
        context: Context,
        title: String? = null,
        items: Array<String>,
        checkedItem: Int = -1,
        onItemSelected: ((Int, String) -> Unit)? = null
    ): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setSingleChoiceItems(items, checkedItem) { dialog, which ->
                onItemSelected?.invoke(which, items[which])
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .setCancelable(true)
            .show()
    }
    
    /**
     * 显示多选列表对话框
     */
    fun showMultiChoice(
        context: Context,
        title: String? = null,
        items: Array<String>,
        checkedItems: BooleanArray? = null,
        positiveText: String = "确定",
        negativeText: String = "取消",
        onItemsSelected: ((BooleanArray) -> Unit)? = null
    ): AlertDialog {
        val selectedItems = checkedItems ?: BooleanArray(items.size) { false }
        
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMultiChoiceItems(items, selectedItems) { _, which, isChecked ->
                selectedItems[which] = isChecked
            }
            .setPositiveButton(positiveText) { _, _ ->
                onItemsSelected?.invoke(selectedItems)
            }
            .setNegativeButton(negativeText, null)
            .setCancelable(true)
            .show()
    }
    
    /**
     * 显示普通列表对话框
     */
    fun showList(
        context: Context,
        title: String? = null,
        items: Array<String>,
        onItemSelected: ((Int, String) -> Unit)? = null
    ): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setItems(items) { dialog, which ->
                onItemSelected?.invoke(which, items[which])
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .setCancelable(true)
            .show()
    }
    
    /**
     * 显示输入对话框
     */
    fun showInput(
        context: Context,
        title: String? = null,
        message: String? = null,
        hint: String? = null,
        defaultText: String? = null,
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        positiveText: String = "确定",
        negativeText: String = "取消",
        onInput: ((String) -> Unit)? = null
    ): AlertDialog {
        val editText = EditText(context).apply {
            this.hint = hint
            this.setText(defaultText)
            this.inputType = inputType
            this.selectAll()
        }
        
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setView(editText)
            .setPositiveButton(positiveText) { _, _ ->
                val input = editText.text.toString().trim()
                onInput?.invoke(input)
            }
            .setNegativeButton(negativeText, null)
            .setCancelable(true)
            .show()
    }
    
    /**
     * 显示密码输入对话框
     */
    fun showPasswordInput(
        context: Context,
        title: String? = null,
        message: String? = null,
        hint: String? = "请输入密码",
        positiveText: String = "确定",
        negativeText: String = "取消",
        onInput: ((String) -> Unit)? = null
    ): AlertDialog {
        return showInput(
            context = context,
            title = title,
            message = message,
            hint = hint,
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            positiveText = positiveText,
            negativeText = negativeText,
            onInput = onInput
        )
    }
    
    /**
     * 显示数字输入对话框
     */
    fun showNumberInput(
        context: Context,
        title: String? = null,
        message: String? = null,
        hint: String? = null,
        defaultValue: Int? = null,
        positiveText: String = "确定",
        negativeText: String = "取消",
        onInput: ((Int?) -> Unit)? = null
    ): AlertDialog {
        return showInput(
            context = context,
            title = title,
            message = message,
            hint = hint,
            defaultText = defaultValue?.toString(),
            inputType = InputType.TYPE_CLASS_NUMBER,
            positiveText = positiveText,
            negativeText = negativeText,
            onInput = { input ->
                val number = input.toIntOrNull()
                onInput?.invoke(number)
            }
        )
    }
    
    /**
     * 显示进度对话框
     */
    @Suppress("DEPRECATION")
    fun showProgress(
        context: Context,
        title: String? = null,
        message: String = "请稍候...",
        cancelable: Boolean = false
    ): ProgressDialog {
        return ProgressDialog(context).apply {
            if (title != null) setTitle(title)
            setMessage(message)
            setCancelable(cancelable)
            show()
        }
    }
    
    /**
     * 显示水平进度对话框
     */
    @Suppress("DEPRECATION")
    fun showHorizontalProgress(
        context: Context,
        title: String? = null,
        message: String = "请稍候...",
        max: Int = 100,
        cancelable: Boolean = false
    ): ProgressDialog {
        return ProgressDialog(context).apply {
            if (title != null) setTitle(title)
            setMessage(message)
            setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            this.max = max
            setCancelable(cancelable)
            show()
        }
    }
    
    /**
     * 显示自定义视图对话框
     */
    fun showCustomView(
        context: Context,
        view: View,
        title: String? = null,
        positiveText: String? = null,
        negativeText: String? = null,
        neutralText: String? = null,
        cancelable: Boolean = true,
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null,
        onNeutral: (() -> Unit)? = null
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(cancelable)
        
        if (title != null) builder.setTitle(title)
        
        if (positiveText != null) {
            builder.setPositiveButton(positiveText) { _, _ -> onPositive?.invoke() }
        }
        
        if (negativeText != null) {
            builder.setNegativeButton(negativeText) { _, _ -> onNegative?.invoke() }
        }
        
        if (neutralText != null) {
            builder.setNeutralButton(neutralText) { _, _ -> onNeutral?.invoke() }
        }
        
        return builder.show()
    }
    
    /**
     * 显示底部选择对话框
     */
    fun showBottomSheet(
        context: Context,
        title: String? = null,
        items: Array<String>,
        onItemSelected: ((Int, String) -> Unit)? = null
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        
        if (title != null) {
            builder.setTitle(title)
        }
        
        builder.setItems(items) { dialog, which ->
            onItemSelected?.invoke(which, items[which])
            dialog.dismiss()
        }
        
        return builder.show()
    }
    
    /**
     * 显示加载对话框（现代样式）
     */
    fun showLoading(
        context: Context,
        message: String = "加载中...",
        cancelable: Boolean = false,
        onCancel: (() -> Unit)? = null
    ): AlertDialog {
        // 这里可以创建一个自定义的加载视图
        // 暂时使用简单的文本显示
        return AlertDialog.Builder(context)
            .setMessage(message)
            .setCancelable(cancelable)
            .setOnCancelListener { onCancel?.invoke() }
            .show()
    }
    
    /**
     * 显示错误对话框
     */
    fun showError(
        context: Context,
        title: String = "错误",
        message: String,
        buttonText: String = "确定",
        onDismiss: (() -> Unit)? = null
    ): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonText) { _, _ -> onDismiss?.invoke() }
            .setCancelable(true)
            .setOnDismissListener { onDismiss?.invoke() }
            .show()
    }
    
    /**
     * 显示成功对话框
     */
    fun showSuccess(
        context: Context,
        title: String = "成功",
        message: String,
        buttonText: String = "确定",
        onDismiss: (() -> Unit)? = null
    ): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonText) { _, _ -> onDismiss?.invoke() }
            .setCancelable(true)
            .setOnDismissListener { onDismiss?.invoke() }
            .show()
    }
    
    /**
     * 显示警告对话框
     */
    fun showWarning(
        context: Context,
        title: String = "警告",
        message: String,
        positiveText: String = "确定",
        negativeText: String = "取消",
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null
    ): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText) { _, _ -> onPositive?.invoke() }
            .setNegativeButton(negativeText) { _, _ -> onNegative?.invoke() }
            .setCancelable(true)
            .show()
    }
    
    /**
     * 显示退出确认对话框
     */
    fun showExitConfirm(
        context: Context,
        title: String = "退出确认",
        message: String = "确定要退出应用吗？",
        onConfirm: (() -> Unit)? = null
    ): AlertDialog {
        return showConfirm(
            context = context,
            title = title,
            message = message,
            positiveText = "退出",
            negativeText = "取消",
            onPositive = onConfirm
        )
    }
    
    /**
     * 显示删除确认对话框
     */
    fun showDeleteConfirm(
        context: Context,
        title: String = "删除确认",
        message: String = "确定要删除吗？此操作不可撤销。",
        onConfirm: (() -> Unit)? = null
    ): AlertDialog {
        return showConfirm(
            context = context,
            title = title,
            message = message,
            positiveText = "删除",
            negativeText = "取消",
            onPositive = onConfirm
        )
    }
    
    /**
     * 显示权限说明对话框
     */
    fun showPermissionRationale(
        context: Context,
        title: String = "权限说明",
        message: String,
        onGranted: (() -> Unit)? = null,
        onDenied: (() -> Unit)? = null
    ): AlertDialog {
        return showConfirm(
            context = context,
            title = title,
            message = message,
            positiveText = "授权",
            negativeText = "拒绝",
            onPositive = onGranted,
            onNegative = onDenied
        )
    }
    
    /**
     * 显示Toast消息
     */
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
    
    /**
     * 显示长Toast消息
     */
    fun showLongToast(context: Context, message: String) {
        showToast(context, message, Toast.LENGTH_LONG)
    }
    
    /**
     * 关闭对话框
     */
    fun dismissDialog(dialog: DialogInterface?) {
        try {
            dialog?.dismiss()
        } catch (e: Exception) {
            LogUtils.e("Failed to dismiss dialog", e)
        }
    }
    
    /**
     * 对话框构建器
     */
    class Builder(private val context: Context) {
        private var title: String? = null
        private var message: String? = null
        private var view: View? = null
        private var positiveText: String? = null
        private var negativeText: String? = null
        private var neutralText: String? = null
        private var cancelable: Boolean = true
        private var onPositive: (() -> Unit)? = null
        private var onNegative: (() -> Unit)? = null
        private var onNeutral: (() -> Unit)? = null
        private var onCancel: (() -> Unit)? = null
        
        fun setTitle(title: String) = apply { this.title = title }
        fun setMessage(message: String) = apply { this.message = message }
        fun setView(view: View) = apply { this.view = view }
        fun setPositiveButton(text: String, onClick: (() -> Unit)? = null) = apply {
            this.positiveText = text
            this.onPositive = onClick
        }
        fun setNegativeButton(text: String, onClick: (() -> Unit)? = null) = apply {
            this.negativeText = text
            this.onNegative = onClick
        }
        fun setNeutralButton(text: String, onClick: (() -> Unit)? = null) = apply {
            this.neutralText = text
            this.onNeutral = onClick
        }
        fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
        fun setOnCancelListener(onCancel: (() -> Unit)?) = apply { this.onCancel = onCancel }
        
        fun show(): AlertDialog {
            val builder = AlertDialog.Builder(context)
            
            title?.let { builder.setTitle(it) }
            message?.let { builder.setMessage(it) }
            view?.let { builder.setView(it) }
            
            positiveText?.let { text ->
                builder.setPositiveButton(text) { _, _ -> onPositive?.invoke() }
            }
            
            negativeText?.let { text ->
                builder.setNegativeButton(text) { _, _ -> onNegative?.invoke() }
            }
            
            neutralText?.let { text ->
                builder.setNeutralButton(text) { _, _ -> onNeutral?.invoke() }
            }
            
            builder.setCancelable(cancelable)
            onCancel?.let { callback ->
                builder.setOnCancelListener { callback() }
            }
            
            return builder.show()
        }
    }
}