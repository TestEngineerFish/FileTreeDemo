import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.isTypedEvent
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.rotary.onPreRotaryScrollEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import java.io.*
import javax.swing.JFileChooser

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun FileItem(fileModel: FileModel, modifier: Modifier = Modifier) {
    var isOpen by remember(fileModel) {
        fileModel.isOpen
    }
    var isReadOnly by remember { mutableStateOf(true) }
    var isEnter by remember { mutableStateOf(false) }
    val fileBgColor by animateColorAsState(targetValue = if (isEnter) Color(0x33000000) else Color.LightGray)
    val folderBgColor by animateColorAsState(targetValue = if (isEnter) Color(0x33000000) else Color.White)

    ContextMenuArea(
        items = {
            listOf(
                ContextMenuItem("Rename") { isReadOnly = false },
                ContextMenuItem("Add") {
                    SelectFiles(selectionModel = JFileChooser.FILES_AND_DIRECTORIES) {
                        println(it.toString())
                        it.forEach {
                            copyData(it.file, fileModel.file)
                        }
                    }
                },
                ContextMenuItem("Delete") {}
            )
        }
    ) {
        Box(
            modifier = modifier.fillMaxWidth()
                .padding(start = fileModel.hierarchy * 20.dp)
                .height(48.dp)
                .background(if (fileModel.file.isFile) fileBgColor else folderBgColor)
                .onClick(
                    onDoubleClick = {
                        if (!fileModel.file.isFile) {
                            isOpen = !isOpen
                        }
                    }
                ) {

                }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val position = event.changes.first().position
                            println("pointer input: $event")
                            when (event.type) {
                                PointerEventType.Enter -> isEnter = true
                                PointerEventType.Exit -> isEnter = false
                            }
                        }
                    }
                    detectDragGestures { change, dragAmount ->
                        println("drag")
                    }
                }
                .onDrag(enabled = true, onDragStart = {
                    println("start")
                }) {
                    println("offset")
                }
                .onPreviewKeyEvent {
                    if (it.key == Key.Enter && !isReadOnly) {
                        renameFile(fileModel.file, fileModel.name.value)
                        isReadOnly = true
                        true
                    } else {
                        false
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            TextField(
                value = fileModel.name.value,
                onValueChange = {
                    fileModel.name.value = it
                },
                readOnly = isReadOnly,
                modifier = Modifier
                    .wrapContentSize(),
                textStyle = TextStyle(fontSize = 13.sp),
                maxLines = 1,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
private fun LazyListScope.files(fileList: List<FileModel>) {
    fileList.forEach { fileModel ->
        item(fileModel.file.absolutePath) {
            FileItem(
                fileModel, modifier = Modifier
                    .animateItemPlacement()
            )
        }
        if (fileModel.isOpen.value) {
            files(fileModel.files())
        }
    }
}

@Composable
internal fun FileListView(fileList: List<FileModel>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        files(fileList)
    }
}

private fun copyData(fromFile: File, toFile: File) {
    if (fromFile.isFile) {
        copyFile(fromFile, toFile)
    } else {
        println("是文件夹地址：${fromFile.absolutePath}")
        val targetFile = File("${toFile.absolutePath}/${fromFile.name}")
        if (!targetFile.exists()) {
            if (targetFile.mkdirs()) {
                println("${targetFile.absolutePath}创建成功")
            } else {
                println("${targetFile.absolutePath}创建失败🙅")
            }
        }
        fromFile.listFiles()?.forEach {
            println("复制文件：${it.absolutePath}， 到目录${targetFile.absolutePath}")
            if (it.isFile) {
                copyFile(it, targetFile)
            } else {
                copyData(it, targetFile)
            }
        }
    }
    println("移动结束")
}

private fun copyFile(fromFile: File, toFile: File) {
    if (fromFile.isFile) {
        println("将文件:${fromFile.absolutePath}, 移动到:${toFile.absolutePath}/${fromFile.name}")
        val inputStream = BufferedInputStream(FileInputStream(fromFile.absolutePath))
        val outputStream = BufferedOutputStream(FileOutputStream("${toFile.absolutePath}/${fromFile.name}"))
        val bytes = ByteArray(1024)
        var len: Int
        while (true) {
            len = inputStream.read(bytes)
            if (len == -1) {
                break
            }
            outputStream.write(bytes, 0, len)
        }
        inputStream.close()
        outputStream.close()
        println("移动文件${fromFile.name}结束")
    } else {
        println("not file")
    }
}

private fun renameFile(file: File, name: String) {
    val newFile = File("${file.parentFile.absolutePath}/${name}")
    file.renameTo(newFile)
}
