package com.example.nativeandroidremote.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun Controller(
    setThrottle: (Float) -> Unit,
    setRoll: (Float, Float) -> Unit,
    resetRoll: () -> Unit
) {
    var ty by rememberSaveable { mutableFloatStateOf(0f) }
    var tyMove: Float
    var lastTouchTY = 0f
    var firstTouchX = 0f
    var firstTouchY = 0f

    setThrottle(ty)

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight()
                .pointerInput(null) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type.toString() == "Press") {
                                lastTouchTY = event.changes.first().position.y
                            }

                            if (event.type.toString() == "Move") {
                                tyMove = lastTouchTY - event.changes.first().position.y
                                ty += tyMove * 0.2f
                                ty = ty.coerceIn(0f, 120f)
                                setThrottle(ty)
                                lastTouchTY = event.changes.first().position.y

                            }
                        }
                    }
                },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .border(BorderStroke(1.dp, Color.Black))
                        .width(60.dp)
                        .fillMaxHeight(0.9f)
                ) {
                    Surface(
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxHeight(ty / 120)
                            .fillMaxWidth(1f)
                    ) {
                    }
                }
            }
        }
        Surface(
            shadowElevation = 3.dp,
            color = Color(0, 0, 0, 200),
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(null) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type.toString() == "Press") {
                                firstTouchY = event.changes.first().position.y
                                firstTouchX = event.changes.first().position.x
                            }

                            if (event.type.toString() == "Move") {
                                setRoll(
                                    (-(event.changes.first().position.y - firstTouchY) * 0.05f).coerceIn(
                                        -7f, 7f
                                    ),
                                    ((event.changes.first().position.x - firstTouchX) * 0.05f).coerceIn(
                                        -7f, 7f
                                    )
                                )
                            }

                            if (event.type.toString() == "Release") {
                                resetRoll()
                            }
                        }
                    }
                }
        ) {}
    }
}