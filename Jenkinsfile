pipeline {
    agent any

    tools {
        maven 'Maven 3'   // make sure it matches your Jenkins tool name
    }

    environment {
        SONARQUBE = 'SonarQubeServer' // must match the name in Jenkins → System config
    }

    stages {

        stage('Clone Repository') {
            steps {
                git branch: 'main',
                        url: 'git@github.com:JawadBoulmal/Digital-Logistics-Supply-Chain-Platform.git',
                        credentialsId: '12d9e65e-ac5d-489a-939d-daddb61bd18b'
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    try {
                        // Run tests and build the project — this will also trigger JaCoCo coverage
                        sh 'mvn clean verify'
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

                    // Generate JaCoCo report
                    echo "📊 Generating JaCoCo coverage report..."
                    sh 'mvn jacoco:report'
                }
            }
            post {
                always {
                    // Publish JaCoCo results to Jenkins
                    jacoco execPattern: 'target/jacoco.exec',
                            classPattern: 'target/classes',
                            sourcePattern: 'src/main/java',
                            inclusionPattern: '**/*.class'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    // Analyze code quality with SonarQube
                    sh 'mvn sonar:sonar -Dsonar.projectKey=Digital-Logistics-Supply-Chain-Platform'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    // Wait for SonarQube to finish the analysis and get quality gate status
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
