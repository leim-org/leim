package cn.lemwood.leim.utils

import android.content.Context
import android.text.Spanned
import android.widget.TextView
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.file.FileSchemeHandler
import java.util.concurrent.Executors

/**
 * Markdown 处理工具类
 * 提供 Markdown 文本的解析、渲染和编辑功能
 */
object MarkdownUtils {
    
    private var markwon: Markwon? = null
    private var editor: MarkwonEditor? = null
    
    /**
     * 初始化 Markwon 实例
     */
    fun initialize(context: Context) {
        if (markwon == null) {
            markwon = Markwon.builder(context)
                .usePlugin(HtmlPlugin.create())
                .usePlugin(ImagesPlugin.create { plugin ->
                    plugin.addSchemeHandler(FileSchemeHandler.createWithAssets(context))
                })
                .build()
            
            editor = MarkwonEditor.create(markwon!!)
        }
    }
    
    /**
     * 将 Markdown 文本转换为 Spanned 对象
     */
    fun parseMarkdown(context: Context, markdown: String): Spanned {
        initialize(context)
        return markwon!!.toMarkdown(markdown)
    }
    
    /**
     * 在 TextView 中设置 Markdown 内容
     */
    fun setMarkdown(context: Context, textView: TextView, markdown: String) {
        initialize(context)
        markwon!!.setMarkdown(textView, markdown)
    }
    
    /**
     * 为 EditText 添加 Markdown 编辑支持
     */
    fun setupMarkdownEditor(context: Context, editText: android.widget.EditText) {
        initialize(context)
        editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor!!))
    }
    
    /**
     * 检查文本是否包含 Markdown 语法
     */
    fun containsMarkdown(text: String): Boolean {
        val markdownPatterns = arrayOf(
            "\\*\\*.*?\\*\\*",      // 粗体
            "\\*.*?\\*",            // 斜体
            "__.*?__",              // 粗体
            "_.*?_",                // 斜体
            "`.*?`",                // 行内代码
            "```[\\s\\S]*?```",     // 代码块
            "^#{1,6}\\s",           // 标题
            "^\\*\\s",              // 无序列表
            "^\\d+\\.\\s",          // 有序列表
            "\\[.*?\\]\\(.*?\\)",   // 链接
            "!\\[.*?\\]\\(.*?\\)",  // 图片
            "^>\\s",                // 引用
            "^---$",                // 分割线
            "\\|.*\\|"              // 表格
        )
        
        return markdownPatterns.any { pattern ->
            Regex(pattern, RegexOption.MULTILINE).containsMatchIn(text)
        }
    }
    
    /**
     * 转义 Markdown 特殊字符
     */
    fun escapeMarkdown(text: String): String {
        val specialChars = arrayOf("*", "_", "`", "[", "]", "(", ")", "#", "+", "-", ".", "!", "|", "\\")
        var escaped = text
        
        specialChars.forEach { char ->
            escaped = escaped.replace(char, "\\$char")
        }
        
        return escaped
    }
    
    /**
     * 移除 Markdown 语法，返回纯文本
     */
    fun stripMarkdown(markdown: String): String {
        var text = markdown
        
        // 移除代码块
        text = text.replace(Regex("```[\\s\\S]*?```"), "")
        
        // 移除行内代码
        text = text.replace(Regex("`.*?`"), "")
        
        // 移除粗体和斜体
        text = text.replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
        text = text.replace(Regex("__(.*?)__"), "$1")
        text = text.replace(Regex("\\*(.*?)\\*"), "$1")
        text = text.replace(Regex("_(.*?)_"), "$1")
        
        // 移除链接，保留文本
        text = text.replace(Regex("\\[([^\\]]+)\\]\\([^\\)]+\\)"), "$1")
        
        // 移除图片
        text = text.replace(Regex("!\\[([^\\]]*)\\]\\([^\\)]+\\)"), "$1")
        
        // 移除标题标记
        text = text.replace(Regex("^#{1,6}\\s*", RegexOption.MULTILINE), "")
        
        // 移除列表标记
        text = text.replace(Regex("^[*+-]\\s+", RegexOption.MULTILINE), "")
        text = text.replace(Regex("^\\d+\\.\\s+", RegexOption.MULTILINE), "")
        
        // 移除引用标记
        text = text.replace(Regex("^>\\s*", RegexOption.MULTILINE), "")
        
        // 移除分割线
        text = text.replace(Regex("^---+$", RegexOption.MULTILINE), "")
        
        // 移除表格分隔符
        text = text.replace(Regex("\\|"), " ")
        
        return text.trim()
    }
    
    /**
     * 格式化代码块
     */
    fun formatCodeBlock(code: String, language: String = ""): String {
        return "```$language\n$code\n```"
    }
    
    /**
     * 格式化行内代码
     */
    fun formatInlineCode(code: String): String {
        return "`$code`"
    }
    
    /**
     * 格式化粗体文本
     */
    fun formatBold(text: String): String {
        return "**$text**"
    }
    
    /**
     * 格式化斜体文本
     */
    fun formatItalic(text: String): String {
        return "*$text*"
    }
    
    /**
     * 格式化链接
     */
    fun formatLink(text: String, url: String): String {
        return "[$text]($url)"
    }
    
    /**
     * 格式化图片
     */
    fun formatImage(altText: String, url: String): String {
        return "![$altText]($url)"
    }
    
    /**
     * 格式化引用
     */
    fun formatQuote(text: String): String {
        return text.lines().joinToString("\n") { "> $it" }
    }
    
    /**
     * 格式化标题
     */
    fun formatHeading(text: String, level: Int): String {
        val hashes = "#".repeat(level.coerceIn(1, 6))
        return "$hashes $text"
    }
    
    /**
     * 格式化无序列表
     */
    fun formatUnorderedList(items: List<String>): String {
        return items.joinToString("\n") { "* $it" }
    }
    
    /**
     * 格式化有序列表
     */
    fun formatOrderedList(items: List<String>): String {
        return items.mapIndexed { index, item -> "${index + 1}. $item" }.joinToString("\n")
    }
    
    /**
     * 获取 Markdown 文本的预览（截取前几行）
     */
    fun getPreview(markdown: String, maxLines: Int = 3): String {
        val plainText = stripMarkdown(markdown)
        val lines = plainText.lines().take(maxLines)
        val preview = lines.joinToString("\n").take(100)
        
        return if (plainText.length > preview.length) {
            "$preview..."
        } else {
            preview
        }
    }
    
    /**
     * 检查是否为有效的 Markdown 表格
     */
    fun isValidTable(text: String): Boolean {
        val lines = text.lines().filter { it.trim().isNotEmpty() }
        if (lines.size < 2) return false
        
        // 检查是否有表格分隔符行
        val separatorLine = lines.getOrNull(1)
        return separatorLine?.matches(Regex("^\\s*\\|?\\s*:?-+:?\\s*(\\|\\s*:?-+:?\\s*)*\\|?\\s*$")) == true
    }
    
    /**
     * 格式化表格
     */
    fun formatTable(headers: List<String>, rows: List<List<String>>): String {
        if (headers.isEmpty() || rows.isEmpty()) return ""
        
        val maxColumns = maxOf(headers.size, rows.maxOfOrNull { it.size } ?: 0)
        val columnWidths = (0 until maxColumns).map { col ->
            val headerWidth = headers.getOrNull(col)?.length ?: 0
            val dataWidth = rows.maxOfOrNull { it.getOrNull(col)?.length ?: 0 } ?: 0
            maxOf(headerWidth, dataWidth, 3)
        }
        
        val headerRow = headers.mapIndexed { index, header ->
            header.padEnd(columnWidths[index])
        }.joinToString(" | ", "| ", " |")
        
        val separatorRow = columnWidths.joinToString("-|-", "|-", "-|") { "-".repeat(it) }
        
        val dataRows = rows.map { row ->
            (0 until maxColumns).map { col ->
                (row.getOrNull(col) ?: "").padEnd(columnWidths[col])
            }.joinToString(" | ", "| ", " |")
        }
        
        return listOf(headerRow, separatorRow).plus(dataRows).joinToString("\n")
    }
}