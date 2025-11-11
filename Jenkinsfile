pipeline {
    agent any

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main',
                    url: 'git@github.com:JawadBoulmal/Digital-Logistics-Supply-Chain-Platform.git',
                    credentialsId: '12d9e65e-ac5d-489a-939d-daddb61bd18b'
            }
        }

        stage('Build') {
            steps {
                echo 'Building the project...'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying application...'
            }
        }
    }
}
