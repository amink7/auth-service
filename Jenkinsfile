pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'amk77/auth-service:latest'
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'  // ⚠️ Tu ID de credenciales en Jenkins
        SONARQUBE_ENV = 'SonarQube'  // ⚠️ Nombre de tu servidor Sonar configurado en Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/amink7/auth-service.git'
            }
        }

        stage('Build & Test') {
            steps {
                bat 'mvn clean install'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    bat 'mvn sonar:sonar'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                bat "docker build -t %DOCKER_IMAGE% ."
            }
        }

        stage('Push to DockerHub') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat """
                        echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin
                        docker push %DOCKER_IMAGE%
                    """
                }
            }
        }
    }
}
