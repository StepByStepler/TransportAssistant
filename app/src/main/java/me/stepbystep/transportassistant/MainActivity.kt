package me.stepbystep.transportassistant

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import me.stepbystep.transportassistant.compose.account.AccountScreen
import me.stepbystep.transportassistant.compose.account.LoginScreen
import me.stepbystep.transportassistant.compose.info.EditFuel
import me.stepbystep.transportassistant.compose.info.EditRepair
import me.stepbystep.transportassistant.compose.info.MainInfoScreen
import me.stepbystep.transportassistant.compose.stats.DriverStatistics
import me.stepbystep.transportassistant.compose.stats.RepairDetailsMenu
import me.stepbystep.transportassistant.compose.stats.StatisticsMenu
import me.stepbystep.transportassistant.data.MileageData
import me.stepbystep.transportassistant.network.HttpClient
import me.stepbystep.transportassistant.util.asMutableProperty
import me.stepbystep.transportassistant.util.sharedProperty

class MainActivity : AppCompatActivity() {

    val httpClient = HttpClient { login.get() }
    val gson = Gson()

    var openedMenu by mutableStateOf<OpenedPopupMenu?>(null)

    var mileageData by mutableStateOf(emptyArray<MileageData>())
    var login = sharedProperty<String>("login").asMutableProperty(this)
    var needsReset by mutableStateOf(false, neverEqualPolicy())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadAll()
        supportActionBar?.hide()

        if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        setContent {
            MaterialTheme {
                needsReset // read mutable state value
                loadAll()
                val login = login.get()
                if (login != null) {
                    App(login)
                } else {
                    LoginScreen()
                }
            }
        }
    }

    private fun loadAll() {
        loadMileageData()
    }

    private fun loadMileageData() {
        if (login.get() == null) {
            mileageData = emptyArray()
            return
        }

        httpClient.get("/mileageData") {
            mileageData = gson.fromJson(it, Array<MileageData>::class.java)
            println("LOADED MILEAGE DATA: ${mileageData.contentToString()}")
        }
    }

    @Composable
    fun App(login: String) {
        var selectedPage by remember { mutableStateOf(AppPage.Update) }

        Box {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(bottom = 56.dp)
            ) {
                Log.i(null, "Rendering $selectedPage")
                when (selectedPage) {
                    AppPage.Update -> MainInfoScreen(this@MainActivity, login)
                    AppPage.Statistics -> DriverStatistics(this@MainActivity)
                    AppPage.Account -> AccountScreen(this@MainActivity, login)
                }
            }

            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Bottom) {
                BottomNavigation {
                    AppPage.values().forEach { page ->
                        BottomNavigationItem(
                            icon = { Icon(page.icon, null) },
                            label = { Text(page.displayName) },
                            selected = selectedPage == page,
                            onClick = {
                                selectedPage = page
                            }
                        )
                    }
                }
            }

            EditFuel()
            EditRepair()
            StatisticsMenu()
            RepairDetailsMenu()
        }
    }
}