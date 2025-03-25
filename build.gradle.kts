plugins {
    id("java")
	// id("Application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Add jar files to compile java code
    implementation(files("libs/algs4.jar"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

// application {
// 	mainClass = "PuzzleChecker"
// }
