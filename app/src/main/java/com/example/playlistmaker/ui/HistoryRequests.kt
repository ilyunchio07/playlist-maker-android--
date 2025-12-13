package com.example.playlistmaker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.theme.YP_TEXT_GRAY

@Composable
fun HistoryRequests(
    historyList: List<String>,
    onClick: (String) -> Unit,
    onClearHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        historyList.forEachIndexed { index, query ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(query) }
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = YP_TEXT_GRAY,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = query,
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (index != historyList.lastIndex) {
                Divider(color = YP_TEXT_GRAY.copy(alpha = 0.2f), thickness = 1.dp)
            }
        }

        Divider(color = YP_TEXT_GRAY.copy(alpha = 0.2f), thickness = 1.dp)

        Text(
            text = stringResource(id = R.string.clear_history),
            fontSize = 14.sp,
            color = YP_TEXT_GRAY,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClearHistoryClick)
                .padding(vertical = 12.dp, horizontal = 12.dp)
        )
    }
}
