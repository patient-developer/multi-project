import com.google.protobuf.gradle.id

plugins {
    java
    alias(libs.plugins.google.protobuf)
    alias(libs.plugins.spring.framework)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.grpc.all)
    implementation(libs.protobuf.java)
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.web)
    implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))
    implementation(libs.spring.framework.grpc)
}

val fetchProtoFiles = tasks.register<Exec>("fetchProtoFiles") {
    description = "Fetching proto files from 'foo-server' to generate gRPC client."
    val targetFolder = layout.buildDirectory.dir("proto");
    onlyIf { !targetFolder.get().asFile.exists() }
    outputs.dir(targetFolder)
    commandLine(
        "git",
        "clone",
        "--depth=1",
        "--branch=main",
        "--single-branch",
        "https://github.com/patient-developer/foo-server.git",
        layout.buildDirectory.dir("cloned").get().asFile.absolutePath
    )
    doLast {
        sync { // (3)
            from(layout.buildDirectory.dir("cloned"))
            into(targetFolder)
        }
    }
}

sourceSets.main {
    proto {
        srcDir(fetchProtoFiles)
    }
}

tasks {
    generateProto {
        addSourceDirs(files("fetchProtoFiles"))
    }
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    plugins {
        id("grpc") {
            artifact = libs.protoc.grpc.java.get().toString()
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc") {
                    option("@generated=omit")
                }
            }
        }
    }
}