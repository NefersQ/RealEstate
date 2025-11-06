pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    environment {
        SPRING_PROFILES_ACTIVE = 'test' 
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/NefersQ/RealEstate.git'
            }
        }

        stage('Build') {
            steps {
                echo 'Building the project...'
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                bat 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Archive') {
            steps {
                echo 'Archiving JAR file...'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo '✅Build and tests completed successfully!'
        }
        failure {
            echo '❌Build or tests failed.'
        }
    }
}