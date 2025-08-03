package com.example.arplacementapp.utils

import android.content.Context
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.*

object ARUtils {

    fun checkARAvailability(context: Context): ARAvailability {
        return when (ArCoreApk.getInstance().checkAvailability(context)) {
            ArCoreApk.Availability.SUPPORTED_INSTALLED -> ARAvailability.SUPPORTED_INSTALLED
            ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD -> ARAvailability.SUPPORTED_APK_TOO_OLD
            ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> ARAvailability.SUPPORTED_NOT_INSTALLED
            ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> ARAvailability.UNSUPPORTED_DEVICE_NOT_CAPABLE
            else -> ARAvailability.UNKNOWN_ERROR
        }
    }

    fun createARSession(context: Context): Session? {
        return try {
            val session = Session(context)
            val config = Config(session).apply {
                planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
                lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            }
            session.configure(config)
            session
        } catch (ex: UnavailableArcoreNotInstalledException) {
            null
        } catch (ex: UnavailableApkTooOldException) {
            null
        } catch (ex: UnavailableSdkTooOldException) {
            null
        } catch (ex: UnavailableDeviceNotCompatibleException) {
            null
        } catch (ex: Exception) {
            null
        }
    }
}

enum class ARAvailability {
    SUPPORTED_INSTALLED,
    SUPPORTED_APK_TOO_OLD,
    SUPPORTED_NOT_INSTALLED,
    UNSUPPORTED_DEVICE_NOT_CAPABLE,
    UNKNOWN_ERROR
}