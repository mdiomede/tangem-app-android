plugins {
    alias(deps.plugins.kotlin.android) apply false
    alias(deps.plugins.kotlin.jvm) apply false
    alias(deps.plugins.kotlin.serialization) apply false
    alias(deps.plugins.kotlin.kapt) apply false
    alias(deps.plugins.android.application) apply false
    alias(deps.plugins.android.library) apply false
    alias(deps.plugins.hilt.android) apply false
    alias(deps.plugins.google.services) apply false
    alias(deps.plugins.firebase.crashlytics) apply false
}

val clean by tasks.registering {
    delete(rootProject.buildDir)
}

interface Injected {
    @get:Inject
    val fs: FileSystemOperations
}

val assembleInternalQA by tasks.registering {
    group = "build"
    description = "Builds internal APK to 'build/outputs' directory"

    val appOutputApkDir = "$projectDir/app/build/outputs/apk/internal"
    val rootOutputApkDir = "$buildDir/outputs"
    val injected = objects.newInstance<Injected>()

    dependsOn(":app:assembleInternal")

    doFirst {
        injected.fs.delete {
            delete(appOutputApkDir)
            delete("$rootOutputApkDir/app-internal.apk")
        }
    }
    doLast {
        injected.fs.copy {
            from("$appOutputApkDir/app-internal.apk")
            into(rootOutputApkDir)
        }
    }
}

val assembleExternalQA by tasks.registering {
    group = "build"
    description = "Builds external APK to 'build/outputs' directory"

    val appOutputApkDir = "$projectDir/app/build/outputs/apk/external"
    val rootOutputApkDir = "$buildDir/outputs"
    val injected = objects.newInstance<Injected>()

    dependsOn(":app:assembleExternal")

    doFirst {
        injected.fs.delete {
            delete(appOutputApkDir)
            delete("$rootOutputApkDir/app-external.apk")
        }
    }
    doLast {
        injected.fs.copy {
            from("$appOutputApkDir/app-external.apk")
            into(rootOutputApkDir)
        }
    }
}

val assembleQA by tasks.registering {
    group = "build"
    description = "Builds internal and external APKs to 'build/outputs' directory"

    dependsOn(assembleInternalQA)
    dependsOn(assembleExternalQA)
}
