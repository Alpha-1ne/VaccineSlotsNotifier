package me.alphaone.autostart

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.*

class AutoStartPermissionHelper private constructor() {


    fun getAutoStartPermission(context: Context): Boolean {

        when (Build.BRAND.lowercase(Locale.ENGLISH)) {

            BRAND_ASUS -> return autoStartAsus(context)

            BRAND_XIAOMI, BRAND_XIAOMI_POCO, BRAND_XIAOMI_REDMI -> return autoStartXiaomi(context)

            BRAND_LETV -> return autoStartLetv(context)

            BRAND_HONOR -> return autoStartHonor(context)

            BRAND_HUAWEI -> return autoStartHuawei(context)

            BRAND_OPPO -> return autoStartOppo(context)

            BRAND_VIVO -> return autoStartVivo(context)

            BRAND_NOKIA -> return autoStartNokia(context)

            BRAND_SAMSUNG -> return autoStartSamsung(context)

            BRAND_ONE_PLUS -> return autoStartOnePlus(context)

            else -> {
                return false
            }
        }

    }

    private fun autoStartXiaomi(context: Context): Boolean {
        if (isPackageExists()) {
            try {
                startIntent(context, PACKAGE_XIAOMI_MAIN, PACKAGE_XIAOMI_COMPONENT)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        } else {
            return false
        }

        return true
    }

    private fun autoStartAsus(context: Context): Boolean {
        if (isPackageExists()) {
            try {
                startIntent(context, PACKAGE_ASUS_MAIN, PACKAGE_ASUS_COMPONENT)
            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    startIntent(context, PACKAGE_ASUS_MAIN, PACKAGE_ASUS_COMPONENT_FALLBACK)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    return false
                }
            }
        } else {
            return false
        }

        return true
    }

    private fun autoStartLetv(context: Context): Boolean {
        if (isPackageExists()) {
            try {
                startIntent(context, PACKAGE_LETV_MAIN, PACKAGE_LETV_COMPONENT)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        } else {
            return false
        }

        return true
    }

    private fun autoStartHonor(context: Context): Boolean {
        if (isPackageExists()) {
            try {
                startIntent(context, PACKAGE_HONOR_MAIN, PACKAGE_HONOR_COMPONENT)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        } else {
            return false
        }

        return true
    }

    private fun autoStartHuawei(context: Context): Boolean {
        if (isPackageExists()) {
            try {
                startIntent(context, PACKAGE_HUAWEI_MAIN, PACKAGE_HUAWEI_COMPONENT)
            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    startIntent(context, PACKAGE_HUAWEI_MAIN, PACKAGE_HUAWEI_COMPONENT_FALLBACK)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    return false
                }
            }
        } else {
            return false
        }

        return true
    }

    private fun autoStartOppo(context: Context): Boolean {
        if (isPackageExists() || isPackageExists()) {
            try {
                startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT)
            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    startIntent(context, PACKAGE_OPPO_FALLBACK, PACKAGE_OPPO_COMPONENT_FALLBACK)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    try {
                        startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_FALLBACK_A)
                    } catch (exx: Exception) {
                        exx.printStackTrace()
                        return false
                    }
                }
            }
        } else {
            return false
        }

        return true
    }

    private fun autoStartVivo(context: Context): Boolean {
        if (isPackageExists() || isPackageExists()) {
            try {
                startIntent(context, PACKAGE_VIVO_MAIN, PACKAGE_VIVO_COMPONENT)
            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    startIntent(context, PACKAGE_VIVO_FALLBACK, PACKAGE_VIVO_COMPONENT_FALLBACK)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    try {
                        startIntent(context, PACKAGE_VIVO_MAIN, PACKAGE_VIVO_COMPONENT_FALLBACK_A)
                    } catch (exx: Exception) {
                        exx.printStackTrace()
                        return false
                    }
                }
            }
        } else {
            return false
        }

        return true
    }

    private fun autoStartNokia(context: Context): Boolean {
        if (isPackageExists()) {
            try {
                startIntent(context, PACKAGE_NOKIA_MAIN, PACKAGE_NOKIA_COMPONENT)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        } else {
            return false
        }

        return true
    }

    private fun autoStartSamsung(context: Context): Boolean {
        if (isPackageExists()) {
            try {
                startIntent(context, PACKAGE_SAMSUNG_MAIN, PACKAGE_SAMSUNG_COMPONENT)
            } catch (a: ActivityNotFoundException) {
                // Try with the another package component
                try {
                    startIntent(context, PACKAGE_SAMSUNG_MAIN, PACKAGE_SAMSUNG_COMPONENT_2)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        } else {
            return false
        }

        return true
    }

    private fun autoStartOnePlus(context: Context): Boolean {
        if (isPackageExists()) {
            try {
                startIntent(context, PACKAGE_ONE_PLUS_MAIN, PACKAGE_ONE_PLUS_COMPONENT)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        } else {
            return false
        }

        return true
    }

    @Throws(Exception::class)
    private fun startIntent(context: Context, packageName: String, componentName: String) {
        try {
            val intent = Intent()
            intent.component = ComponentName(packageName, componentName)
            context.startActivity(intent)
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw exception
        }
    }

    private fun isPackageExists(): Boolean {
        return true
    }

    companion object {

        /***
         * Xiaomi
         */
        private const val BRAND_XIAOMI = "xiaomi"
        private const val BRAND_XIAOMI_POCO = "poco"
        private const val BRAND_XIAOMI_REDMI = "redmi"
        private const val PACKAGE_XIAOMI_MAIN = "com.miui.securitycenter"
        private const val PACKAGE_XIAOMI_COMPONENT = "com.miui.permcenter.autostart.AutoStartManagementActivity"

        /***
         * Letv
         */
        private const val BRAND_LETV = "letv"
        private const val PACKAGE_LETV_MAIN = "com.letv.android.letvsafe"
        private const val PACKAGE_LETV_COMPONENT = "com.letv.android.letvsafe.AutobootManageActivity"

        /***
         * ASUS ROG
         */
        private const val BRAND_ASUS = "asus"
        private const val PACKAGE_ASUS_MAIN = "com.asus.mobilemanager"
        private const val PACKAGE_ASUS_COMPONENT = "com.asus.mobilemanager.powersaver.PowerSaverSettings"
        private const val PACKAGE_ASUS_COMPONENT_FALLBACK = "com.asus.mobilemanager.autostart.AutoStartActivity"

        /***
         * Honor
         */
        private const val BRAND_HONOR = "honor"
        private const val PACKAGE_HONOR_MAIN = "com.huawei.systemmanager"
        private const val PACKAGE_HONOR_COMPONENT = "com.huawei.systemmanager.optimize.process.ProtectActivity"

        /***
         * Huawei
         */
        private const val BRAND_HUAWEI = "huawei"
        private const val PACKAGE_HUAWEI_MAIN = "com.huawei.systemmanager"
        private const val PACKAGE_HUAWEI_COMPONENT = "com.huawei.systemmanager.optimize.process.ProtectActivity"
        private const val PACKAGE_HUAWEI_COMPONENT_FALLBACK = "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"

        /**
         * Oppo
         */
        private const val BRAND_OPPO = "oppo"
        private const val PACKAGE_OPPO_MAIN = "com.coloros.safecenter"
        private const val PACKAGE_OPPO_FALLBACK = "com.oppo.safe"
        private const val PACKAGE_OPPO_COMPONENT = "com.coloros.safecenter.permission.startup.StartupAppListActivity"
        private const val PACKAGE_OPPO_COMPONENT_FALLBACK = "com.oppo.safe.permission.startup.StartupAppListActivity"
        private const val PACKAGE_OPPO_COMPONENT_FALLBACK_A = "com.coloros.safecenter.startupapp.StartupAppListActivity"

        /**
         * Vivo
         */

        private const val BRAND_VIVO = "vivo"
        private const val PACKAGE_VIVO_MAIN = "com.iqoo.secure"
        private const val PACKAGE_VIVO_FALLBACK = "com.vivo.permissionmanager"
        private const val PACKAGE_VIVO_COMPONENT = "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
        private const val PACKAGE_VIVO_COMPONENT_FALLBACK = "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
        private const val PACKAGE_VIVO_COMPONENT_FALLBACK_A = "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"

        /**
         * Nokia
         */

        private const val BRAND_NOKIA = "nokia"
        private const val PACKAGE_NOKIA_MAIN = "com.evenwell.powersaving.g3"
        private const val PACKAGE_NOKIA_COMPONENT = "com.evenwell.powersaving.g3.exception.PowerSaverExceptionActivity"

        /***
         * Samsung
         */
        private const val BRAND_SAMSUNG = "samsung"
        private const val PACKAGE_SAMSUNG_MAIN = "com.samsung.android.lool"
        private const val PACKAGE_SAMSUNG_COMPONENT = "com.samsung.android.sm.ui.battery.BatteryActivity"
        private const val PACKAGE_SAMSUNG_COMPONENT_2 = "com.samsung.android.sm.battery.ui.BatteryActivity"

        /***
         * One plus
         */
        private const val BRAND_ONE_PLUS = "oneplus"
        private const val PACKAGE_ONE_PLUS_MAIN = "com.oneplus.security"
        private const val PACKAGE_ONE_PLUS_COMPONENT = "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"

        @JvmStatic
        fun getInstance(): AutoStartPermissionHelper {
            return AutoStartPermissionHelper()
        }

    }
}
