import DatePicker.DataTimePicker
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.UIManager

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun FrameWindowScope.App() {
    var path by remember { mutableStateOf("/") }
    val selectedFiles = remember { mutableStateListOf<FileModel>() }
    var show by remember { mutableStateOf(false) }
    var dateTime by remember { mutableStateOf("--") }
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Button(onClick = {
                show = !show
            }, modifier = Modifier.padding(start = 30.dp, top = 30.dp).align(Alignment.TopStart)) {
                Text("Action")
            }
            Text(
                text = dateTime,
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 30.dp, top = 30.dp).wrapContentSize().align(Alignment.TopEnd).clickable { show = !show },
            )
            DropdownMenu(expanded = show, onDismissRequest = { show = false }, modifier = Modifier.wrapContentSize().align(
                Alignment.TopStart), offset = DpOffset(x = 50.dp, y = 200.dp), focusable = true) {
                DataTimePicker(
                    modifier = Modifier
                        .width(500.dp)
                        .height(300.dp),
                ) {
                    dateTime = it
                }
            }
        }
//        Column(modifier = Modifier.fillMaxSize()) {
//            Row(
//                modifier = Modifier.padding(horizontal = 50.dp).fillMaxWidth().wrapContentHeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Button(onClick = {
//                    show = true
////                    SelectFiles (selectionModel = JFileChooser.DIRECTORIES_ONLY) {
////                        selectedFiles.clear()
////                        selectedFiles.addAll(it)
////                        path = it.first().file.absolutePath ?: ""
////                    }
//                }) {
//                    Text("选择目录")
//                }
//                Text("目录：$path", modifier = Modifier.wrapContentSize(), textAlign = TextAlign.End)
//            }
////            FileListView(selectedFiles, modifier = Modifier.fillMaxWidth().weight(1f))
//
//        }

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



