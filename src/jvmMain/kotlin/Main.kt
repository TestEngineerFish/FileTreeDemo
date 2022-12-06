import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetAdapter
import java.awt.dnd.DropTargetDropEvent
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.UIManager

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun FrameWindowScope.App() {
    var path by remember { mutableStateOf("/") }
    val selectedFiles = remember { mutableStateListOf<FileModel>() }
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.padding(horizontal = 50.dp).fillMaxWidth().wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    SelectFiles (selectionModel = JFileChooser.DIRECTORIES_ONLY) {
                        selectedFiles.clear()
                        selectedFiles.addAll(it)
                        path = it.first().file.absolutePath ?: ""
                    }
                }) {
                    Text("选择目录")
                }
                Text("目录：$path", modifier = Modifier.wrapContentSize(), textAlign = TextAlign.End)
            }
            FileListView(selectedFiles, modifier = Modifier.fillMaxWidth().weight(1f))
//            var target = object : DropTarget(window, object : DropTargetAdapter() {
//                @Synchronized
//                override fun drop(event: DropTargetDropEvent) {
//                    event.acceptDrop(DnDConstants.ACTION_REFERENCE)
//                    val dataFlavors = event.transferable.transferDataFlavors
//                    dataFlavors.forEach {
//                        if (it == DataFlavor.javaFileListFlavor) {
//                            val list = event.transferable.getTransferData(it) as List<*>
//                            val pathList = mutableListOf<String>()
//                            list.forEach { filePath ->
//                                pathList.add(filePath.toString())
//                                println("file: ${filePath.toString()}")
//                            }
//                            println(pathList)
//                        }
//                    }
//                    event.dropComplete(true)
//                }
//            }) {
//            }
        }
    }
}

internal fun SelectFiles(selectionModel: Int, selectedAction:(List<FileModel>) -> Unit) {
    JFileChooser().apply {
        try {
            val lookAndFeel = UIManager.getSystemLookAndFeelClassName()
            UIManager.setLookAndFeel(lookAndFeel)
            SwingUtilities.updateComponentTreeUI(this)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        fileSelectionMode = selectionModel
        val result = showOpenDialog(ComposeWindow())
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedAction(listOf(this.selectedFile.toModel()))
        }
    }
}

fun main() = application {
//    val state = rememberWindowState(width = Dp.Unspecified, height = Dp.Unspecified)
    Window(onCloseRequest = ::exitApplication, title = "FileTree") {
        App()
    }
}



