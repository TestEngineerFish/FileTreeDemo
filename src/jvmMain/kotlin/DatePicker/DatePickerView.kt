package DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun DatePickerColumn(
    pairList: List<Pair<Int, String>>,
    selectedIndex: Int = 0,
    itemHeight: Dp = 50.dp,
    itemWidth: Dp = 50.dp,
    focusColor: Color = MaterialTheme.colors.primary,
    unfocusedColor: Color = Color(0xFFC5C7CF),
    changeAction: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(selectedIndex) {
        var initIndex = 0
        for (index in pairList.indices) {
            if (selectedIndex == pairList[index].first) {
                initIndex = index
                break
            }
        }
        listState.animateScrollToItem(initIndex)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(top = itemHeight / 2, bottom = itemHeight / 2)
            .height(itemHeight * 5),
    ) {
        itemsIndexed(items = pairList, key = { _, pair -> pair.first }) { index, pair ->
            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .width(itemWidth)
                    .clickable {
                        changeAction(pair.first)
                    }
                    .padding(horizontal = 5.dp),
                Alignment.Center
            ) {
                Text(
                    text = pair.second,
                    color = if (selectedIndex == pair.first) focusColor
                    else unfocusedColor
                )
            }
        }
    }
//
//    val offsetValue = listState.firstVisibleItemScrollOffset
//    val firstIndex = listState.firstVisibleItemIndex
////    listState.snapshotFlow
//    println("test: ${offsetValue}, index: ${firstIndex}")
//    LaunchedEffect(listState.firstVisibleItemScrollOffset) {
//
//        if (!listState.isScrollInProgress) {
//            println("滑动结束")
//        } else {
//            println("滑动ing")
//        }
//    }
}

@Composable
fun DataTimePicker(
    modifier: Modifier = Modifier,
    updateAction:(String)->Unit
) {

    val itemHeight = 50.dp
    val viewModel = remember {
        DatePickerViewModel()
    }

    var selectedYear by remember { viewModel.selectedYear }
    var selectedMonth by remember { viewModel.selectedMonth }
    var selectedDay by remember { viewModel.selectedDay }
    var selectedHour by remember { viewModel.selectedHour }
    var selectedMinute by remember { viewModel.selectedMinute }
    var selectedSecond by remember { viewModel.selectedSecond }

    LaunchedEffect(Unit) {
        viewModel.initDate()
    }
    LaunchedEffect(key1 = selectedMonth) {
        viewModel.updateDays()
    }

    // 显示信息
    val timeStr = "${selectedYear}-${selectedMonth}-${selectedDay} ${selectedHour}:${selectedMinute}:${selectedSecond}"

    LaunchedEffect(key1 = timeStr) {
        updateAction(timeStr)
    }

    Box(
        modifier = modifier,
        Alignment.Center
    ) {
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface),
            Arrangement.SpaceEvenly,
            Alignment.CenterVertically
        ) {

            DatePickerColumn(pairList = viewModel.years, itemHeight = itemHeight, itemWidth = 70.dp, selectedIndex = selectedYear) {
                selectedYear = it
            }

            DatePickerColumn(pairList = viewModel.months, itemHeight = itemHeight, itemWidth = 50.dp, selectedIndex = selectedMonth) {
                selectedMonth = it
            }

            DatePickerColumn(pairList = viewModel.days.toList(), itemHeight = itemHeight, itemWidth = 50.dp, selectedIndex = selectedDay) {
                selectedDay = it
            }

            DatePickerColumn(pairList = viewModel.hours, itemHeight = itemHeight, itemWidth = 50.dp, selectedIndex = selectedHour) {
                selectedHour = it
            }

            DatePickerColumn(pairList = viewModel.minutes, itemHeight = itemHeight, itemWidth = 50.dp, selectedIndex = selectedMinute) {
                selectedMinute = it
            }

            DatePickerColumn(pairList = viewModel.seconds, itemHeight = itemHeight, itemWidth = 50.dp, selectedIndex = selectedSecond) {
                selectedSecond = it
            }
        }

//        Column {
//            Divider(
//                Modifier.padding(
//                    start = 15.dp,
//                    end = 15.dp,
//                    bottom = itemHeight
//                ),
//                thickness = 1.dp
//            )
//            Divider(
//                Modifier.padding(
//                    start = 15.dp,
//                    end = 15.dp
//                ),
//                thickness = 1.dp
//            )
//        }
    }
}
