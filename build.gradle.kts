plugins {
    id("java")
    // use 'gradle shadowJar' to build "fat jar" with all deps
    // see build/libs/league-sim-0.9-SNAPSHOT-all.jar
    id("com.gradleup.shadow") version "9.0.0-beta16"
}

group = "com.hitchhikerprod.league"
version = "0.9-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.yaml:snakeyaml:2.4")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    test {
        useJUnitPlatform()
    }
    withType<Jar> {
        manifest {
            attributes["Main-Class"] = "com.hitchhikerprod.league.Main"
        }
    }
}
