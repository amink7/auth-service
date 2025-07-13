pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "amk77/auth-service:latest"
        DOCKER_CREDENTIALS_ID = "dockerhub-credentials"
    }

    stages {
        stage('Build & Test') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('SonarQube Analysis') {
            environment {
                SONARQUBE_ENV = 'SonarQube'
            }
            steps {
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage('Push to DockerHub') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ${DOCKER_IMAGE}
                    """
                }
            }
        }
    }
}
