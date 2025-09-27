package tekin.luetfi.resume.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NetworkMonitor @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    private val upstream = callbackFlow {


        fun updateState() {
            if (isOnlineNow()) {
                trySend(true)
            } else {
                trySend(false)
            }
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) = updateState()
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) = updateState()
            override fun onLost(network: Network) = updateState()
            override fun onUnavailable() = updateState()
        }

        val req = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        // Initial state check
        updateState()

        connectivityManager.registerNetworkCallback(req, callback)
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)

        }
    }

    val isOnline: StateFlow<Boolean> = upstream
        .distinctUntilChanged()
        .stateIn(scope, SharingStarted.WhileSubscribed(5_000), false)

    private fun isOnlineNow(): Boolean {
        val n = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(n) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}