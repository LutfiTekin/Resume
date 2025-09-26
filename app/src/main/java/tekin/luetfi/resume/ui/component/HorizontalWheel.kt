package tekin.luetfi.resume.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.abs


@Composable
fun AnimatedConfirmation(
    modifier: Modifier,
    items: List<String>,
    finalText: String,
    onAnimationEnd: () -> Unit
) {
    // Use the passed list, and scroll to show the finalText
    HorizontalWheelReadOnly(
        modifier = modifier.padding(vertical = 8.dp),
        items = items,
        displayItem = finalText,
        onAnimationEnd = onAnimationEnd
    )
}

@Composable
fun AnimatedConfirmationIndeterminate(
    modifier: Modifier,
    items: List<String>
) {
    var animationTrigger by remember {
        mutableIntStateOf(1)
    }
    val verdict by remember(animationTrigger) {
        derivedStateOf {
            items.shuffled().first()
        }
    }


    // Use the passed list, and scroll to show the finalText
    HorizontalWheelReadOnly(
        modifier = modifier.padding(vertical = 8.dp),
        items = items,
        displayItem = verdict,
        onAnimationEnd = {
            animationTrigger++
        }
    )
}




@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalWheelReadOnly(
    modifier: Modifier = Modifier,
    items: List<String>,
    displayItem: String,
    onAnimationEnd: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current

    // Find the index of displayItem and the furthest position
    val targetIndex = items.indexOf(displayItem).coerceAtLeast(0)
    val indexFromStart = abs(0 - targetIndex)
    val indexFromEnd = abs(items.lastIndex - targetIndex)
    // Initial will be 0 or last index, whichever is further from target
    val startIndex = if (indexFromStart > indexFromEnd) 0 else items.lastIndex

    // Simplest start index is 0. The initial scroll will fix the position anyway.
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)


    // --- Core Logic: Centering Scroll with 5-Step Animation ---
    LaunchedEffect(targetIndex) {
        val currentIndex = startIndex
        val steps = 5
        val delta = targetIndex - currentIndex
        for (i in 1..steps) {
            val waypointIndex = currentIndex + (delta * i / steps)
            listState.animateScrollToItem(waypointIndex)
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        listState.animateScrollToItem(targetIndex)
        val layoutInfo = listState.layoutInfo
        val targetItemInfo = layoutInfo.visibleItemsInfo.find { it.index == targetIndex }
        // Wait for the next frame for layout to complete and update listState.layoutInfo
        delay(10) // Small delay to ensure layout pass completion
        if (targetItemInfo != null) {
            // Calculate the PERFECT scroll offset for centering.
            val viewportCenter = layoutInfo.viewportSize.width / 2
            val itemCenter = targetItemInfo.offset + targetItemInfo.size / 2
            // The distance we need to scroll: The current center position of the item
            // MINUS the center position of the viewport.
            // A positive value means the item is currently to the right of the center.
            val scrollDistanceToCenter = itemCenter - viewportCenter
            //scroll exact position of the target index to the center
            listState.animateScrollBy(scrollDistanceToCenter.toFloat())
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(500)
            onAnimationEnd()
        }
    }

    Box(modifier = modifier) {
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            flingBehavior = snapBehavior,
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.Center,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(items.size) { index ->
                val text = items[index]

                // --- Centered Item Index Calculation for Visual Effects ---
                val centerIndex by remember {
                    derivedStateOf {
                        val layoutInfo = listState.layoutInfo
                        if (layoutInfo.visibleItemsInfo.isNotEmpty()) {
                            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                            // Find the visible item whose center is closest to the viewport center
                            layoutInfo.visibleItemsInfo.minByOrNull { itemInfo ->
                                abs((itemInfo.offset + itemInfo.size / 2) - viewportCenter)
                            }?.index ?: listState.firstVisibleItemIndex
                        } else {
                            listState.firstVisibleItemIndex
                        }
                    }
                }

                val distanceFromCenter = abs(index - centerIndex)
                val alpha = when (distanceFromCenter) {
                    0 -> 1f
                    1 -> 0.75f
                    2 -> 0.25f
                    else -> 0f
                }

                Text(
                    text = text.uppercase(Locale.ENGLISH),
                    modifier = Modifier
                        // DO NOT use .fillMaxWidth() here. It forces all items to the same width
                        // breaking the variable-width requirement.
                        .alpha(alpha)
                        .padding(horizontal = 12.dp),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = if (distanceFromCenter == 0) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}







val phoneticMap = linkedMapOf(
    "A" to "Alfa", "B" to "Bravo", "C" to "Charlie", "D" to "Delta",
    "E" to "Echo", "F" to "Foxtrot", "G" to "Golf", "H" to "Hotel",
    "I" to "India", "J" to "Juliett", "K" to "Kilo", "L" to "Lima",
    "M" to "Mike", "N" to "November", "O" to "Oscar", "P" to "Papa",
    "Q" to "Quebec", "R" to "Romeo", "S" to "Sierra", "T" to "Tango",
    "U" to "Uniform", "V" to "Victor", "W" to "Whiskey", "X" to "X-ray",
    "Y" to "Yankee", "Z" to "Zulu",
    "0" to "Zero", "1" to "One", "2" to "Two", "3" to "Three",
    "4" to "Four", "5" to "Five", "6" to "Six", "7" to "Seven",
    "8" to "Eight", "9" to "Nine"
)