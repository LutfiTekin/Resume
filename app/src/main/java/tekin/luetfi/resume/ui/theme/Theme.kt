package tekin.luetfi.resume.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography

val CvGreen = Color(0xFF27AE60)
val CvBlue = Color(0xFF0073B1)
val CvDarkText = Color(0xFF333333)
val CvDivider = Color(0xFFEEEEEE)
val CvTimeline = Color(0xFFCCCCCC)

val LightColors: ColorScheme = lightColorScheme(
    primary = CvGreen,
    secondary = CvBlue,
    onPrimary = Color.White,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = CvDarkText,
    surface = Color.White,
    onSurface = CvDarkText,
    outline = CvDivider
)

val DarkColors: ColorScheme = darkColorScheme(
    primary = CvGreen,
    secondary = CvBlue,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    outline = CvTimeline
)

val CvTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.15.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        fontStyle = FontStyle.Italic
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        color = Color.Gray
    )
)

@Composable
fun CvTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CvTypography,
        content = content
    )
}