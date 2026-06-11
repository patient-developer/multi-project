plugins {
    java
    alias(libs.plugins.spring.framework)
    alias(libs.plugins.spring.dependency.management)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

val grpcCompiledRuntimeDependencies by configurations.dependencyScope("grpcCompiledRuntimeDependencies")

val grpcCompileddRuntime by configurations.resolvable("grpcCompiledRuntime") {
    extendsFrom(grpcCompiledRuntimeDependencies)
    attributes {
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("grpc-compiled-jar"))
    }
}

dependencies {
    grpcCompiledRuntimeDependencies(project(":producer"))
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.framework.grpc)
}

tasks.register<JavaExec>("runWithGrpcCompilation") {
    classpath = grpcCompileddRuntime
}