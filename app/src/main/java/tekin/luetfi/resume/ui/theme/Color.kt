package tekin.luetfi.resume.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

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