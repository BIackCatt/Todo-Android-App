package com.example.todolist.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material.icons.outlined.NotInterested
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationBarItem(
    val selected: ImageVector,
    val unselected: ImageVector,
    val label: String,
    val pageCount: Int,
)

@Composable
fun BottomNavigation(
    onCompletedClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    pageIndex: Int,
    onClick: (Int) -> Unit,
) {
    val items = listOf(
        BottomNavigationBarItem(
            selected = Icons.Filled.NotInterested,
            unselected = Icons.Outlined.NotInterested,
            label = "Uncompleted",
            pageCount = 0
        ),
        BottomNavigationBarItem(
            selected = Icons.Filled.CheckCircle,
            unselected = Icons.Filled.CheckCircleOutline,
            label = "Completed",
            pageCount = 1,
        )
    )
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                onClick = { if (index == 1) onCompletedClick(1) else onClick(0) },
                selected = index == pageIndex,
                icon = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = modifier
                    ) {
                        Icon(
                            imageVector = if (pageIndex == index) item.selected else item.unselected,
                            contentDescription = null
                        )
                        Text(
                            text = item.label,
                            maxLines = 1
                        )
                    }
                }
            )
        }
    }
}