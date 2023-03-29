package com.tangem.datasource.config

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.tangem.datasource.config.models.ConfigModel
import com.tangem.datasource.config.models.ConfigValueModel
import com.tangem.datasource.config.models.FeatureModel
import com.tangem.datasource.utils.AssetReader
import timber.log.Timber

/**
 * Created by Anton Zhilenkov on 16/02/2021.
 */
class FeaturesLocalLoader(
    private val assetReader: AssetReader,
    private val moshi: Moshi,
    buildEnvironment: String,
) : Loader<ConfigModel> {

    private val featuresName = "features_$buildEnvironment".replace(".restricted", "")
    private val configValuesName = "tangem-app-config/config_$buildEnvironment".replace(".restricted", "")

    override fun load(onComplete: (ConfigModel) -> Unit) {
        val config = try {
            val featureAdapter: JsonAdapter<FeatureModel> = moshi.adapter(FeatureModel::class.java)
            val valuesAdapter: JsonAdapter<ConfigValueModel> = moshi.adapter(ConfigValueModel::class.java)

            val jsonFeatures = assetReader.readAssetAsString(featuresName)
            val jsonConfigValues = assetReader.readAssetAsString(configValuesName)

            ConfigModel(featureAdapter.fromJson(jsonFeatures), valuesAdapter.fromJson(jsonConfigValues))
        } catch (ex: Exception) {
            Timber.e(ex)
            ConfigModel.empty()
        }
        onComplete(config)
    }
}
