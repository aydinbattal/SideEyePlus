package sheridan.czuberad.sideeye.`Application Logic`

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.Services.DriverService

class IndependentDriverLogic {
    private val driverService = DriverService()

    @Composable
    fun getCurrentDriverInfo(): Driver {

        var driver by remember{ mutableStateOf(Driver()) }

        LaunchedEffect(key1 = Unit){
            driverService.fetchCurrentUser {
                if(it != null){
                    driver = it
                }
            }
        }

        return driver

    }
}