package com.obsbot.happyinn.apps.happyinninobsbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * 延迟注入 JankStats，用于在整个应用中跟踪卡顿情况
     */
/*    @Inject
    lateinit var lazyStats: dagger.Lazy<JankStats>*/

    /**
     * 网络监控器，用于监控网络连接状态
     */
/*    @Inject
    lateinit var networkMonitor: NetworkMonitor*/

    /**
     * 时区监控器，用于监控时区变化
     */
/*    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor*/

    /**
     * 分析助手，用于收集和报告应用使用数据
     */
/*    @Inject
    lateinit var analyticsHelper: AnalyticsHelper*/

    /**
     * 用户新闻资源仓库，用于管理用户相关的新闻内容
     */
/*    @Inject
    lateinit var userNewsResourceRepository: UserNewsResourceRepository*/



    private val viewModel: MainActivityViewModel by viewModels()

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