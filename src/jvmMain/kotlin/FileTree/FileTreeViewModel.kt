import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.File

internal class FileTreeViewModel {
    var count by mutableStateOf(0)
    var openDicMap = mutableMapOf<String, String>()
}

internal data class FileModel(
    val file: File,
    val parent: FileModel? = null,
) {
    var isOpen = mutableStateOf(false)
    val hierarchy: Int get() = (parent?.hierarchy ?: -1) + 1
    var name = mutableStateOf(file.name)

    @Volatile
    private var children: List<FileModel>? = null

    fun files(forceRefresh: Boolean = false): List<FileModel> {
        if (children == null || forceRefresh) {
            return (file.listFiles()?.map { it.toModel(this) } ?: emptyList()).also {
                children = it
            }
        }
        return children!!
    }
}

internal fun File.toModel(parent: FileModel? = null): FileModel = FileModel(this, parent = parent)