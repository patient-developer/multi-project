import com.google.protobuf.gradle.id

plugins {
    `java-library`
    alias(libs.plugins.google.protobuf)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

sourceSets.main {
    java {
        srcDir("build/generated/sources/proto/main/grpc")
        srcDir("build/generated/sources/proto/main/java")
    }
}

val grpcCompiledJar by tasks.registering(Jar::class) {
    archiveClassifier.set("grpcCompiled")
    from(sourceSets.main.get().output)
}

configurations {
    consumable("grpcCompiledJars") {
        attributes {
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("grpc-compiled-jar"))
        }
    }
}

artifacts {
    add("grpcCompiledJars", grpcCompiledJar)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.grpc.all)
    implementation(libs.protobuf.java)
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