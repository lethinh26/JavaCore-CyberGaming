plugins {
    java
    application
}

group = "ra.cybergaming"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.mysql:mysql-connector-j:9.6.0")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    implementation("org.mindrot:jbcrypt:0.4")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

application {
    mainClass.set("ra.cybergaming.Main")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<JavaExec>().configureEach {
    systemProperty("file.encoding", "UTF-8")
    jvmArgs("-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8")
    standardInput = System.`in`
}