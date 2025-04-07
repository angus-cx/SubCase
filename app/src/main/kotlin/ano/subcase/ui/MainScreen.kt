package ano.subcase.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import ano.subcase.CaseStatus
import ano.subcase.R
import ano.subcase.service.ServiceManager
import ano.subcase.ui.theme.Blue
import ano.subcase.util.SubStore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavController) {

    if (!CaseStatus.isServiceRunning.value) {
        ServiceManager.startService()
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (CaseStatus.isServiceRunning.value) {
                CaseWebView()
            } else {
                WebViewPlaceholder()
            }
        }

        SettingFloatingButton(navController)

        if (CaseStatus.showUpdateDialog.value) {
            UpdateDialog()
        }
    }
}

@Composable
fun WebViewPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SubStore 正在启动...",
            fontSize = 20.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun UpdateDialog() {

    var isUpdating = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(text = "检测到 SubStore 有新版本", fontSize = 18.sp)
        },
        text = {
            Column {
                if (SubStore.remoteFrontendVersion != SubStore.localFrontendVersion) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 5.dp)
                    ) {
                        Text("前端")
                        Text("(${SubStore.localFrontendVersion} -> ${SubStore.remoteFrontendVersion})")
                    }
                }

                if (SubStore.remoteBackendVersion != SubStore.localBackendVersion) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Text("后端")
                        Text("(${SubStore.localBackendVersion} -> ${SubStore.remoteBackendVersion})")
                    }
                }
            }
        },
        confirmButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.clickable {
                        if (isUpdating.value) {
                            return@clickable
                        }
                        CaseStatus.showUpdateDialog.value = false
                    },
                    color = Color.Gray,
                    text = "取消",
                )
                Spacer(modifier = Modifier.padding(10.dp))
                TextButton(
                    onClick = {
                        if (isUpdating.value) {
                            return@TextButton
                        }
                        isUpdating.value = true
                        GlobalScope.launch {
                            if (SubStore.remoteFrontendVersion != SubStore.localFrontendVersion) {
                                SubStore.updateFrontend()
                            }
                            if (SubStore.remoteBackendVersion != SubStore.localBackendVersion) {
                                SubStore.updateBackend()
                            }
                            isUpdating.value = false
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White,
                        containerColor = Blue
                    )
                ) {
                    Text("更新")
                }
            }
        },
    )
}

@Preview
@Composable
fun CaseWebView() {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { CaseStatus.webView }
    )
}

@Composable
fun SettingFloatingButton(navController: NavController) {
    val haptic = LocalHapticFeedback.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val (screenWidth, screenHeight, fabSize) = with(density) {
        Triple(
            configuration.screenWidthDp.dp.toPx(),
            configuration.screenHeightDp.dp.toPx(),
            56.dp.toPx()
        )
    }

    // 设置默认位置在右侧中下位置
    var offsetX by remember { mutableFloatStateOf(screenWidth - fabSize - with(density) { 16.dp.toPx() }) } // 右侧留出16dp边距
    var offsetY by remember { mutableFloatStateOf((screenHeight - fabSize) * 3 / 4) } // 垂直居中

    // 添加边距限制，避免被系统栏遮挡
    val statusBarHeight =
        with(density) { WindowInsets.statusBars.getTop(this).toDp().toPx() } // 状态栏高度
    val navBarHeight =
        with(density) { WindowInsets.navigationBars.getTop(this).toDp().toPx() } // 导航栏高度
    val minY = statusBarHeight
    val maxY = screenHeight - navBarHeight - fabSize



    FloatingActionButton(
        shape = CircleShape,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            navController.navigate("setting_screen")
        },
        containerColor = Color.Blue,
        contentColor = Color.White,
        modifier = Modifier
            .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()

                    // 计算新的位置
                    val newX = offsetX + dragAmount.x
                    val newY = offsetY + dragAmount.y

                    // 限制在屏幕范围内，并考虑系统栏
                    offsetX = newX.coerceIn(0f, screenWidth - fabSize)
                    offsetY = newY.coerceIn(minY, maxY)
                }
            }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_settings),
            contentDescription = "设置",
            modifier = Modifier.size(24.dp)
        )
    }
}