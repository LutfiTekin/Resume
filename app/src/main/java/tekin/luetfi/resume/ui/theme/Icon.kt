package tekin.luetfi.resume.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AnalyzeIcon: ImageVector
    @Composable
    get() {
        return ImageVector.Builder(
            name = "Analyze",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Document body
            path(fill = SolidColor(MaterialTheme.colorScheme.tertiary)) {
                moveTo(5f, 3f)
                lineTo(13f, 3f)     // top edge to fold base
                lineTo(18f, 8f)     // fold tip
                lineTo(18f, 21f)    // right edge
                lineTo(5f, 21f)     // bottom
                close()             // left edge back to start
            }
            // Folded corner triangle
            path(fill = SolidColor(Color.White)) { // cut-out look
                moveTo(13f, 3f)
                lineTo(13f, 8f)
                lineTo(18f, 8f)
                close()
            }
            // Text lines
            path(fill = SolidColor(Color.White)) {
                moveTo(7f, 11f); lineTo(12.5f, 11f); lineTo(12.5f, 12f); lineTo(7f, 12f); close()
                moveTo(7f, 13.5f); lineTo(13.5f, 13.5f); lineTo(13.5f, 14.5f); lineTo(7f, 14.5f); close()
                moveTo(7f, 16f); lineTo(11f, 16f); lineTo(11f, 17f); lineTo(7f, 17f); close()
            }
            // Magnifying glass: circle centered at (14.5, 14.5) radius 3.5
            path(fill = SolidColor(MaterialTheme.colorScheme.tertiary)) {
                val r = 3.5f
                moveTo(14.5f + r, 14.5f)
                arcToRelative(r, r, 0f, true, true, -2 * r, 0f)
                arcToRelative(r, r, 0f, true, true,  2 * r, 0f)
                close()
            }
            // Magnifying glass: inner hole to make it ring
            path(fill = SolidColor(Color.White)) {
                val r = 2.4f
                moveTo(14.5f + r, 14.5f)
                arcToRelative(r, r, 0f, true, true, -2 * r, 0f)
                arcToRelative(r, r, 0f, true, true,  2 * r, 0f)
                close()
            }
            // Magnifying glass: handle
            path(fill = SolidColor(MaterialTheme.colorScheme.tertiary)) {
                moveTo(17f, 17f)
                lineTo(20.2f, 20.2f)
                lineTo(19.2f, 21.2f)
                lineTo(16f, 18f)
                close()
            }
        }.build()
    }
