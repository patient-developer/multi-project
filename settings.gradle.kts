rootProject.name = "multi-project"

// required, else ./gradlew :producer:build doesn't work
// also possible, in /producer folder: ../gradlew build
include("producer", "consumer")
