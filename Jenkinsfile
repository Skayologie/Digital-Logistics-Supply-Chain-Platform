pipeline {
    agent any

    tools {
        maven 'Maven 3'   // Matches Jenkins tool name
    }

    stages {

        stage('Build & Test') {
            steps {
                script {
                    try {
                        sh 'mvn clean verify'
                        sh 'mvn jacoco:report'
                    } catch (e) {
                        error "Maven build failed: ${e.message}"
                    }

                    // Find JAR file
                    def jarFiles = findFiles(glob: 'target/*.jar')
                    if (jarFiles.length > 0) {
                        env.JAR_PATH = jarFiles[0].path
                        echo "✅ Found JAR: ${env.JAR_PATH}"
                    } else {
                        error "❌ Build succeeded, but no .jar file was found in target/."
                    }
                }
            }
            post {
                always {
                    // Publish JaCoCo results to Jenkins from the .exec file
                    jacoco execPattern: 'target/jacoco.exec',
                            classPattern: 'target/classes',
                            sourcePattern: 'src/main/java',
                            inclusionPattern: '**/*.class',
                            minimumInstructionCoverage: '50',
                            minimumLineCoverage: '50',
                            changeBuildStatus: true
                }
            }
        }
        stage('SonarQube Analysis') {
            steps {
                // This step pulls the server config by name directly
                withSonarQubeEnv('SonarQubeServer') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=Digital-Logistics-Supply-Chain-Platform -Dsonar.coverage.jacoco.xmlReportPaths=target/jacoco.exec'                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    // Wait for SonarQube to finish and check the gate status
                    timeout(time: 5, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: true
                    }
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: env.JAR_PATH, fingerprint: true
            }
        }
    }

    post {
        always {
            echo '🧹 Cleaning up workspace...'
            cleanWs()
        }
        success {
            echo '🎉 Pipeline succeeded! All stages passed.'
        }
        failure {
            echo '❗ Pipeline failed. Check build logs or SonarQube results.'
        }
    }
}