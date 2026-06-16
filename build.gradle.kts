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

tasks.register<Exec>("fetchProtoFiles") {
    description = "Fetching proto files from 'foo-server' to generate gRPC client."
    val clonedFolder = layout.buildDirectory.dir("cloned"); // (1)
    onlyIf { !file(clonedFolder).exists() } // apply (1)
    outputs.dir(layout.buildDirectory.files("proto")) // (2)
    commandLine(
        "git",
        "clone",
        "--depth=1",
        "--branch=main",
        "--single-branch",
        "https://github.com/patient-developer/foo-server.git",
        clonedFolder // apply (1)
    )
    doLast {
        sync { // (3)
            from("$clonedFolder/src/main/proto") // apply (1)
            into(outputs.files) // apply (2)
        }
    }
}

sourceSets.main {
    proto {
        srcDir(layout.buildDirectory.dir("proto"))
    }
}

tasks {
    generateProto {
        addSourceDirs(files("fetchProtoFiles"))
//        dependsOn("fetchProtoFiles")
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