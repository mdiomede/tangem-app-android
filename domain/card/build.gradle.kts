plugins {
    alias(deps.plugins.kotlin.jvm)
    alias(deps.plugins.kotlin.kapt)
    id("configuration")
}

dependencies {
    implementation(project(":domain:core"))
    // TODO: Remove after new card scan result was implemented
    implementation(project(":domain:models"))

    implementation(deps.tangem.card.core)

    implementation(deps.hilt.core)
    kapt(deps.hilt.kapt)

    implementation(deps.moshi.kotlin)
}
