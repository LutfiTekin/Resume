package tekin.luetfi.resume.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AnalyzeIcon: ImageVector
    get() {
        return ImageVector.Builder(
            name = "Analyze",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Bar chart
            path(fill = SolidColor(Color.Black)) {
                moveTo(4f, 10f)
                lineTo(6f, 10f)
                lineTo(6f, 20f)
                lineTo(4f, 20f)
                close()

                moveTo(10f, 6f)
                lineTo(12f, 6f)
                lineTo(12f, 20f)
                lineTo(10f, 20f)
                close()

                moveTo(16f, 14f)
                lineTo(18f, 14f)
                lineTo(18f, 20f)
                lineTo(16f, 20f)
                close()
            }
            // Magnifying glass
            path(fill = SolidColor(Color.Black)) {
                moveTo(15.5f, 3f)
                arcToRelative(6.5f, 6.5f, 0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -4.6f,
                    dy1 = 11.1f
                )
                lineToRelative(-3.5f, 3.5f)
                lineToRelative(-1.4f, -1.4f)
                lineToRelative(3.5f, -3.5f)
                arcToRelative(6.5f, 6.5f, 0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 6f,
                    dy1 = -9.7f
                )
                close()
            }
        }.build()
    }


