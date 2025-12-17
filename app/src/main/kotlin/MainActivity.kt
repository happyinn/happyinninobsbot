import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioTheme

//import androidx.compose.ui.tooling.preview.Preview
//import com.obsbot.happyinn.apps.happyinninobsbot.ui.theme.HappyinninobsbotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HioTheme {
                Greeting("OBSBOT")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
