package com.changs.android.gnuting_android.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.AlarmResult


@Composable
fun AlarmList(data: List<AlarmResult>?, onClick: () -> Unit, onLongClick: (Int) -> Unit) {
    LazyColumn(
        userScrollEnabled = true,
    ) {
        data?.let {
            items(it) { item ->
                AlarmItem(alarm = item, onClick = onClick, onLongClick = onLongClick)
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlarmItem(alarm: AlarmResult, onClick: () -> Unit, onLongClick: (Int) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = Modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = { onLongClick(alarm.id) },
                interactionSource = interactionSource,
                indication = null
            )
            .padding(bottom = 24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(R.drawable.alarm_item_img),
                contentDescription = "",
                modifier = Modifier.size(40.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp)
            ) {
                Text(
                    text = alarm.body,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.pretendard_medium)),
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alarm.time,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.pretendard_regular)),
                    color = Color(0xFF979C9E)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewAlarmItem() {
    val alarm = AlarmResult(
        "국어국문학과와 영어영문학부의 과팅이 성사되어 채팅방이 만들어졌습니다.", 1, "3분전", "", ""
    )
    Surface {
        AlarmItem(alarm = alarm, onClick = fun() {}, onLongClick = fun(_) {})
    }
}

@Preview
@Composable
fun PreviewAlarmList() {
    val alarmList = listOf(
        AlarmResult(
            "국어국문학과와 영어영문학부의 과팅이 성사되어 채팅방이 만들어졌습니다.", 1, "3분전", "", ""
        ), AlarmResult(
            "국어국문학과와 영어영문학부의 과팅이 성사되어 채팅방이 만들어졌습니다.", 1, "3분전", "", ""
        ), AlarmResult(
            "국어국문학과와 영어영문학부의 과팅이 성사되어 채팅방이 만들어졌습니다.", 1, "3분전", "", ""
        ), AlarmResult(
            "국어국문학과와 영어영문학부의 과팅이 성사되어 채팅방이 만들어졌습니다.", 1, "3분전", "", ""
        ), AlarmResult(
            "국어국문학과와 영어영문학부의 과팅이 성사되어 채팅방이 만들어졌습니다.", 1, "3분전", "", ""
        )
    )

    Surface {
        AlarmList(data = alarmList, onClick = fun() {}, onLongClick = fun(value) {})
    }
}