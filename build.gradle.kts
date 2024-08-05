import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL
import java.util.*

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlinVersion.get()}")
        classpath("org.jetbrains.dokka:dokka-base:${libs.versions.dokkaVersion.get()}")
    }
}

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    id("com.vanniktech.maven.publish") version "0.29.0"
}

repositories {
    mavenCentral()
}

kotlin {
    val releaseBuild = project.hasProperty("release")
    val libName      = project.name.lowercase(Locale.getDefault())

    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    js  {
        browser {
            testTask {
                enabled = false
            }
        }

        compilations.all {
            kotlinOptions {
                moduleKind = "umd"
                sourceMap  = !releaseBuild
                if (sourceMap) {
                    sourceMapEmbedSources = "always"
                }
            }
        }
    }

    @Suppress("OPT_IN_USAGE")
    wasmJs {
        browser {
            testTask { enabled = false }
        }
        compilations.all {
            kotlinOptions {
                moduleKind = "umd"
                sourceMap  = !releaseBuild
                if (sourceMap) {
                    sourceMapEmbedSources = "always"
                }
            }
        }
    }

    listOf(
        iosX64               (),
        iosArm64             (),
        iosSimulatorArm64    (),
        watchosX64           (),
        watchosArm64         (),
        watchosSimulatorArm64(),
        macosX64             (),
        macosArm64           (),
        tvosX64              (),
        tvosArm64            (),
        tvosSimulatorArm64   (),
    ).forEach {
        it.binaries.framework {
            baseName = libName
            isStatic = true
            binaryOption("bundleVersion", project.version.toString())
        }

        val isMacOS   = System.getProperty("os.name"   ) == "Mac OS X"
        val osVersion = System.getProperty("os.version").toDoubleOrNull() ?: 0.0

        // Use this flag if using MacOS 14 or newer
        if (isMacOS && osVersion >= 14.0) {
//            it.binaries.sharedLib {
//                baseName = libName
//            }
            it.compilations.all {
                compilerOptions.configure {
                    freeCompilerArgs.add("-linker-options")
                    freeCompilerArgs.add("-ld64"          )
                }
            }
        }
    }

    androidNativeX64  ()
    androidNativeArm32()

    listOf(
        mingwX64  (), // Windows x64
        linuxX64  (),
        linuxArm64(),
    ).forEach {
        it.binaries.sharedLib {
            baseName = libName
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(kotlin("stdlib-common"))
        }

        commonTest.dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }

        jvmTest.dependencies {
            implementation(libs.bundles.test.libs)
            implementation(kotlin("test-junit"))
        }

        jsTest.dependencies {
            implementation(kotlin("test-js"))
        }

        val wasmJsTest by getting {
            dependencies {
                implementation(kotlin("test-wasm-js"))
            }
        }
    }
}

fun DokkaBaseConfiguration.configDokka() {
    homepageLink                          = "https://github.com/cvb941/measured"
    customAssets                          = listOf(file("logo-icon.svg"))
    footerMessage                         = "(c) 2024 Nacular"
    separateInheritedMembers              = true
    mergeImplicitExpectActualDeclarations = true
}

tasks.withType<DokkaTask>().configureEach {
    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        configDokka()
    }
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

tasks.dokkaHtml {
    moduleName.set(project.name.capitalized())

    outputDirectory.set(layout.buildDirectory.dir("javadoc"))

    dokkaSourceSets.configureEach {
        includeNonPublic.set(false)

        // Do not output deprecated members. Applies globally, can be overridden by packageOptions
        skipDeprecated.set(true)

        // Emit warnings about not documented members. Applies globally, also can be overridden by packageOptions
        reportUndocumented.set(true)

        // Do not create index pages for empty packages
        skipEmptyPackages.set(true)

        includes.from("Module.md")

        sourceLink {
            localDirectory.set(rootProject.projectDir)
            remoteUrl.set(URL("https://github.com/cvb941/measured/tree/master"))
            remoteLineSuffix.set("#L")
        }

        externalDocumentationLink {
            url.set(URL("https://kotlinlang.org/api/latest/jvm/stdlib/"))
        }
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

    signAllPublications()

    pom {
        name.set("Measured")
        description.set("Intuitive, type-safe units for Kotlin")
        inceptionYear.set("2024")
        url.set("https://github.com/cvb941/measured")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("cvb941")
                name.set("Lukas Kusik")
                url.set("https://github.com/cvb941/")
            }
        }
        scm {
            url.set("https://github.com/cvb941/measured")
            connection.set("scm:git:git://github.com/cvb941/measured.git")
            developerConnection.set("scm:git:ssh://git@github.com/cvb941/measured.git")
        }
    }
}

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
//    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().download    = false
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "16.0.0"
}

//rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
//    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().disableGranularWorkspaces()
//}
