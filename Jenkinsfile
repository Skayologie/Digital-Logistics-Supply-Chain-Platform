pipeline {
    agent any // Run on any available agent

    tools {
        // Assumes you have 'Maven 3' configured in
        // Jenkins -> Global Tool Configuration
        maven 'Maven 3'
    }

    stages {
        stage('Clone Repository') {
            steps {
                // Clones the specified repository and branch
                git branch: 'main',
                    url: 'git@github.com:JawadBoulmal/Digital-Logistics-Supply-Chain-Platform.git',
                    credentialsId: '12d9e65e-ac5d-489a-939d-daddb61bd18b'
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    // This command cleans the project, runs all tests,
                    // and packages the application into a .jar file
                    sh 'mvn clean package'

                    // Find the built JAR file for the next step
                    def jarFile = findFiles(glob: 'target/*.jar')[0]
                    env.JAR_PATH = jarFile.path
                    echo "Found JAR: ${env.JAR_PATH}"
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                // Saves the .jar file (called an "artifact") with this
                // build. You can download it from the Jenkins build page.
                archiveArtifacts artifacts: env.JAR_PATH, fingerprint: true
            }
        }
    }

    post {
        // This block runs after all stages are complete
        always {
            echo 'Pipeline finished.'
            // Clean up workspace to save disk space
            cleanWs()
        }
        success {
            echo 'Pipeline succeeded! All tests passed.'
        }
        failure {
            echo 'Pipeline failed. Check the tests.'
        }
    }
}