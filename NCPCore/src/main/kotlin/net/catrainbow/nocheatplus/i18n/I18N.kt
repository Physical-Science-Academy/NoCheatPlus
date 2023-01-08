package net.catrainbow.nocheatplus.i18n


import net.catrainbow.nocheatplus.NoCheatPlus
import java.io.FileInputStream
import java.util.Properties


/**
 * I18N Multi Language
 *
 * @author Elaina1314
 */
class I18N {
    companion object {
        private var translations =  Properties().apply { load(FileInputStream("${NoCheatPlus.instance.dataFolder}/translations.properties")) }

        @JvmStatic
        fun getString(key: String, vararg args: Any): String {
            val text = translations.getProperty(key) ?: return key
            return if (args.isEmpty()) text else String.format(text, *args)
        }


    }
}