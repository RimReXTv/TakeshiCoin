package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletApp(viewModel: WalletViewModel) {
    var currentTab by remember { mutableStateOf(0) }
    
    val context = LocalContext.current
    val wallet by viewModel.walletState.collectAsStateWithLifecycle()

    var showSendDialog by remember { mutableStateOf(false) }
    var showReceiveDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Takeshi Coin",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF386A20), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Real-Engine Engaged",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    val isDarkOpt by viewModel.isDarkTheme.collectAsStateWithLifecycle()
                    val isDark = isDarkOpt ?: isSystemInDarkTheme()
                    IconButton(
                        onClick = {
                            viewModel.setTheme(!isDark)
                        },
                        modifier = Modifier.testTag("theme_toggle")
                    ) {
                        Text(
                            text = if (isDark) "☀️" else "🌙",
                            fontSize = 20.sp
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.triggerBlockchainSync()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Wallet") },
                    label = { Text("Wallet", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onBackground,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_wallet")
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(Icons.Default.Build, contentDescription = "Mining") },
                    label = { Text("Mining", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onBackground,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_mining")
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = { Icon(Icons.Default.Share, contentDescription = "Peers") },
                    label = { Text("Peers", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onBackground,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_peers")
                )
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = { Icon(Icons.Default.Lock, contentDescription = "Security") },
                    label = { Text("Security", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onBackground,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_security")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentTab) {
                0 -> WalletScreen(
                    viewModel = viewModel,
                    onSendClicked = { showSendDialog = true },
                    onReceiveClicked = { showReceiveDialog = true }
                )
                1 -> MiningScreen(viewModel = viewModel)
                2 -> PeersScreen(viewModel = viewModel)
                3 -> SecurityScreen(viewModel = viewModel)
            }

            if (showSendDialog) {
                SendCoinsDialog(
                    onDismiss = { showSendDialog = false },
                    onSend = { recipient, amountEln ->
                        viewModel.sendTransaction(recipient, amountEln) { res ->
                            if (res == "SUCCESS") {
                                Toast.makeText(context, "Transaction successfully signed and broadcasted!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Error: $res", Toast.LENGTH_LONG).show()
                            }
                        }
                        showSendDialog = false
                    }
                )
            }

            if (showReceiveDialog) {
                ReceiveCoinsDialog(
                    address = wallet?.address ?: "Generate Wallet First",
                    onDismiss = { showReceiveDialog = false }
                )
            }

            val isFetchingData by viewModel.isFetchingData.collectAsStateWithLifecycle()
            val fetchStatus by viewModel.fetchStatus.collectAsStateWithLifecycle()

            if (isFetchingData) {
                Dialog(onDismissRequest = {}) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95F)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(16.dp)
                            .testTag("loading_overlay"),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 3.dp,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("loading_spinner")
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "BLOCKCHAIN DATA RETRIEVAL",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = fetchStatus,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WalletScreen(
    viewModel: WalletViewModel,
    onSendClicked: () -> Unit,
    onReceiveClicked: () -> Unit
) {
    val wallet by viewModel.walletState.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val isDark = viewModel.isDarkTheme.collectAsStateWithLifecycle(initialValue = null).value == true
    
    val df = remember { DecimalFormat("#,##0.00") }
    val elnDf = remember { DecimalFormat("#,##0") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Card: Adaptive Metallic Rounded Geometric Card
        val gradientColors = if (isDark) {
            listOf(Color(0xFF2E4C1E), Color(0xFF1F3514))
        } else {
            listOf(Color(0xFFD7E8CD), Color(0xFFC7DCB8))
        }
        val balanceCardTextColor = if (isDark) Color(0xFFFCFDF6) else Color(0xFF111F0C)
        val balanceCardSubTextColor = if (isDark) Color(0xFFBCCBB3) else Color(0xFF42493F)
        val balanceBorderColor = if (isDark) Color(0xFF43583E) else Color(0xFFBCCBB3)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.verticalGradient(colors = gradientColors))
                .border(2.dp, balanceBorderColor, RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL BALANCE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = balanceCardSubTextColor,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF386A20), RoundedCornerShape(100.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "MAINNET",
                            fontSize = 9.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val balanceTks = (wallet?.balanceEln ?: 0L).toDouble() / 100000000.0
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = df.format(balanceTks),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = balanceCardTextColor
                    )
                    Text(
                        text = "TKS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF558B2F),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "≈ ${elnDf.format(wallet?.balanceEln ?: 0L)} eln",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = balanceCardSubTextColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onSendClicked,
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF386A20)),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("send_button")
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Send")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onReceiveClicked,
                        shape = RoundedCornerShape(100.dp),
                        border = BorderStroke(1.dp, balanceBorderColor),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0xFF1B2616) else Color.White.copy(alpha = 0.6f),
                            contentColor = if (isDark) Color(0xFFFCFDF6) else Color(0xFF386A20)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("receive_button")
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Receive")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Receive", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quick Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text("GAS ENGINE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("100 eln static", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text("ADDRESS PROTOCOL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Bech32 tks1...", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        // Transactions Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chain Activity",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Explorer Logs",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {  }
            )
        }

        // Transactions list inside column (non-nested lazy list for safe viewport performance!)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .heightIn(min = 300.dp)
        ) {
            if (transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No transactions logged.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Column {
                    transactions.forEachIndexed { index, tx ->
                        TransactionItem(
                            tx = tx,
                            isLast = index == transactions.size - 1,
                            userAddress = wallet?.address ?: ""
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(tx: DbTransaction, isLast: Boolean, userAddress: String) {
    val isIncoming = tx.recipient == userAddress
    val dateStr = remember(tx.timestamp) {
        val sdf = SimpleDateFormat("HH:mm a", Locale.getDefault())
        sdf.format(Date(tx.timestamp))
    }
    val df = remember { DecimalFormat("#,##0.00") }
    val amountTks = tx.amountEln.toDouble() / 100000000.0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    if (isIncoming) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isIncoming) Icons.Default.KeyboardArrowDown else Icons.Default.ArrowForward,
                contentDescription = null,
                tint = if (isIncoming) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (tx.sender == "Takeshi Shinohara / Miner") "Block Reward Mined" else if (isIncoming) "Received" else "Sent",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isIncoming) "from ..${tx.sender.takeLast(8)}" else "to ..${tx.recipient.takeLast(8)}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${if (isIncoming) "+" else "-"}${df.format(amountTks)} TKS",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = if (isIncoming) Color(0xFF558B2F) else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (tx.status == "PENDING") "Pending" else dateStr,
                fontSize = 11.sp,
                color = if (tx.status == "PENDING") Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (!isLast) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
    }
}

@Composable
fun MiningScreen(viewModel: WalletViewModel) {
    val isMining by viewModel.isMining.collectAsStateWithLifecycle()
    val hashRate by viewModel.hashRate.collectAsStateWithLifecycle()
    val coreTemp by viewModel.coreTemp.collectAsStateWithLifecycle()
    val coolingThrottle by viewModel.coolingThrottle.collectAsStateWithLifecycle()
    val blocks by viewModel.blocks.collectAsStateWithLifecycle()

    val rateDf = remember { DecimalFormat("0.0") }
    val tempDf = remember { DecimalFormat("0.0") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core mining panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("EcoARM ASYNC MINING ENGINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Status: ${if (isMining) "RUNNING" else "STOPPED"}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = if (isMining) Color(0xFF558B2F) else Color.Red)
                    }

                    Switch(
                        checked = isMining,
                        onCheckedChange = { viewModel.toggleMining() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF558B2F),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.testTag("mining_switch")
                    )
                }

                // Grid Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).background(if (isMining) Color.Green else Color.Red, CircleShape))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("HASH RATE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("${rateDf.format(hashRate)} MH/s", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("CORE TEMP", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("${tempDf.format(coreTemp)}°C", fontSize = 18.sp, fontWeight = FontWeight.Black, color = if (coreTemp > 65) Color.Red else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                // Thermal Throttling slider as mandated by the blueprint
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Thermal Cooldown Limit", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("$coolingThrottle% Throttle Delay", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = coolingThrottle.toFloat(),
                        onValueChange = { viewModel.updateCoolingThrottle(it.toInt()) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color(0xFF386A20),
                            thumbColor = Color(0xFF386A20),
                            inactiveTrackColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    Text("Asynchronous thermal protection prevents arm64 processor overheating under load.", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Live Hashrate Performance Wave Chart drawn via custom canvas!
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                .padding(12.dp)
        ) {
            Column {
                Text("Live Solvability & Wave Metrics", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                LiveWavesChart(isMining = isMining, hashRate = hashRate)
            }
        }

        // Solved Blocks list
        Text("Verified Mined Blocks", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            blocks.forEach { block ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("Block #${block.blockIndex}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("Hash: ${block.blockHash.take(18)}...", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Nonce: ${block.nonce}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Box(
                            modifier = Modifier
                                .background(Color(0xFFD7E8CD), RoundedCornerShape(100.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("+25.00 TKS", fontSize = 11.sp, color = Color(0xFF386A20), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveWavesChart(isMining: Boolean, hashRate: Double) {
    // Generate static point path matching current mining speeds
    val pointHistory = remember { mutableStateListOf<Float>() }
    
    // Periodically append data
    LaunchedEffect(isMining, hashRate) {
        while (true) {
            delay(500)
            val nextVal = if (isMining) {
                (hashRate.toFloat() * 10f) + Random.nextFloat() * 15f
            } else {
                0f
            }
            pointHistory.add(nextVal.coerceIn(0f, 100f))
            if (pointHistory.size > 24) {
                pointHistory.removeAt(0)
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (pointHistory.isNotEmpty()) {
            val path = Path()
            val stepX = size.width / 23f
            
            // Draw gradient fill under curve
            val fillPath = Path()
            fillPath.moveTo(0f, size.height)

            pointHistory.forEachIndexed { i, score ->
                val x = i * stepX
                val y = size.height - (score / 100f) * size.height
                if (i == 0) {
                    path.moveTo(x, y)
                    fillPath.lineTo(x, y)
                } else {
                    path.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
            }

            fillPath.lineTo((pointHistory.size - 1) * stepX, size.height)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF386A20).copy(alpha = 0.25f), Color.Transparent)
                )
            )

            drawPath(
                path = path,
                color = Color(0xFF386A20),
                style = Stroke(width = 3.dp.toPx())
            )
        } else {
            // Draw simple flat line if idle
            drawLine(
                color = Color(0xFFE0E4D7),
                start = Offset(0f, size.height * 0.9f),
                end = Offset(size.width, size.height * 0.9f),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
fun PeersScreen(viewModel: WalletViewModel) {
    val peers by viewModel.peers.collectAsStateWithLifecycle()
    var newPeerAddress by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Gossip details
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF3F4E9))
                .border(1.dp, Color(0xFFE0E4D7), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("LIBP2P MESH COMMUNICATOR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text("Gossipsub Nodes Detected", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF1A1C18))
                Text("MDNS discovery auto-syncs local subnets. Carrier DHT coordinates transactions continuously across hops.", fontSize = 11.sp, color = Color.DarkGray)
            }
        }

        // Add custom peer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newPeerAddress,
                onValueChange = { newPeerAddress = it },
                label = { Text("Peer Address (IP:Port)") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF386A20),
                    focusedLabelColor = Color(0xFF386A20)
                )
            )

            Button(
                onClick = {
                    if (newPeerAddress.isNotBlank()) {
                        viewModel.addPeer(newPeerAddress) { res ->
                            if (res == "SUCCESS") {
                                Toast.makeText(context, "Peer registered!", Toast.LENGTH_SHORT).show()
                                newPeerAddress = ""
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF386A20)),
                modifier = Modifier.height(56.dp)
            ) {
                Text("Connect")
            }
        }

        // Active peer list
        Text("Routing Tables / active links (${peers.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(peers) { peer ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE0E4D7), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(peer.address, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(peer.carrier, fontSize = 11.sp, color = Color.Gray)
                        }

                        Box(
                            modifier = Modifier
                                .background(Color(0xFFD7E8CD), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("CONNECTED", fontSize = 8.sp, color = Color(0xFF386A20), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityScreen(viewModel: WalletViewModel) {
    val wallet by viewModel.walletState.collectAsStateWithLifecycle()
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    // AI advisor chat variables
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val thinkingProcess by viewModel.chatThinkingProcess.collectAsStateWithLifecycle()

    var chatInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF3F4E9))
                .border(1.dp, Color(0xFFE0E4D7), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("SECURE KEY ENCLAVE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text("BIP-44 Protocol Setup", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF1A1C18))
                
                Spacer(modifier = Modifier.height(10.dp))
                
                Text("Path: m/44'/999'/0'/0/0 (Takeshi Coin CoinType 999)", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color(0xFF386A20))
                Text("Mnemonic Seed Words:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(wallet?.mnemonic ?: "Loading...", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = Color.DarkGray)
                
                Spacer(modifier = Modifier.height(10.dp))
                
                Button(
                    onClick = {
                        wallet?.privateKeyHex?.let {
                            clipboard.setText(AnnotatedString(it))
                            Toast.makeText(context, "Private Key copied. Keep it 100% secret!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF386A20)),
                    border = BorderStroke(1.dp, Color(0xFFBCCBB3)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Private Key hex", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Conversational AI interface (Cognitive AI Advisor with Thinking Mode)
        Text("Takeshi Cognitive Node (Thinking Mode HIGH)", fontSize = 14.sp, fontWeight = FontWeight.Bold)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE0E4D7), RoundedCornerShape(20.dp))
                .padding(12.dp)
        ) {
            Column {
                // Messages scrolled area
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chatMessages) { msg ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalAlignment = if (msg.sender == "USER") Alignment.End else Alignment.Start
                        ) {
                            // Thinking header if any
                            if (msg.thinking != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF0F1EA))
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                ) {
                                    Column {
                                        Text("🧠 Deep Thought reasoning:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text(msg.thinking, fontSize = 10.sp, color = Color.DarkGray, fontFamily = FontFamily.Monospace)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (msg.sender == "USER") Color(0xFFD7E8CD) else Color(0xFFF3F4E9)
                                    )
                                    .padding(12.dp)
                                    .widthIn(max = 280.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1A1C18)
                                )
                            }
                        }
                    }

                    // Loading thinking block
                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE8ECE2))
                                    .padding(12.dp)
                                    .fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color(0xFF386A20))
                                    Text(
                                        text = thinkingProcess ?: "Takeshi logic thinking...",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }

                // Input line
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        placeholder = { Text("Ask about blocks, hashes, cooling...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF386A20),
                            focusedLabelColor = Color(0xFF386A20)
                        )
                    )

                    IconButton(
                        onClick = {
                            if (chatInput.isNotBlank()) {
                                viewModel.sendChatMessage(chatInput)
                                chatInput = ""
                            }
                        },
                        modifier = Modifier
                            .background(Color(0xFF386A20), CircleShape)
                            .size(48.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun SendCoinsDialog(
    onDismiss: () -> Unit,
    onSend: (recipient: String, amountEln: Long) -> Unit
) {
    var recipient by remember { mutableStateOf("") }
    var amountTks by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE0E4D7), RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Transmit Takeshi Coins", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF1A1C18))
                
                OutlinedTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    label = { Text("Recipient Bech32 Address (tks1...)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF386A20),
                        focusedLabelColor = Color(0xFF386A20)
                    )
                )

                OutlinedTextField(
                    value = amountTks,
                    onValueChange = { amountTks = it },
                    label = { Text("Amount TKS") },
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF386A20),
                        focusedLabelColor = Color(0xFF386A20)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text("Tx Fee: 100 eln Micro-Gas", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Button(
                        onClick = {
                            val amtDouble = amountTks.toDoubleOrNull() ?: 0.0
                            val elnAmount = (amtDouble * 100000000.0).toLong()
                            if (recipient.isNotBlank() && elnAmount > 0) {
                                onSend(recipient, elnAmount)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF386A20)),
                        shape = RoundedCornerShape(100.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sign & Send")
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiveCoinsDialog(
    address: String,
    onDismiss: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE0E4D7), RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Receive Address", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF1A1C18))

                // Draw a beautiful QR Code placeholder visually with geometric circles & shapes
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(Color(0xFFF3F4E9), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Let's draw standard custom canvas maze blocks simulating a high quality blockchain QR hash
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val numBlocks = 8
                        val blockSize = size.width / numBlocks
                        for (r in 0 until numBlocks) {
                            for (c in 0 until numBlocks) {
                                if ((r+c) % 2 == 0 || (r == 0 && c < 3) || (r < 3 && c == 0) || (r >= 5 && c >= 5)) {
                                    drawRect(
                                        color = Color(0xFF386A20),
                                        topLeft = Offset(c * blockSize, r * blockSize),
                                        size = Size(blockSize, blockSize)
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    text = address,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3F4E9), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Close", color = Color.Gray)
                    }
                    Button(
                        onClick = {
                            clipboard.setText(AnnotatedString(address))
                            Toast.makeText(context, "Bech32 address copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF386A20)),
                        shape = RoundedCornerShape(100.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Copy Address")
                    }
                }
            }
        }
    }
}
