pipeline {
    agent any

    tools {
        maven 'Maven3'   // Usa el nombre que hayas configurado en Global Tool Configuration
        jdk 'JDK17'      // Igual, usa el nombre que hayas configurado
    }

    environment {
        SONARQUBE_ENV = 'SonarQube'    // Nombre que diste en Jenkins > Configure System > SonarQube servers
        DOCKER_IMAGE = 'amk77/auth-service'
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
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat """
                        docker login -u %DOCKER_USER% -p %DOCKER_PASS%
                        docker push %DOCKER_IMAGE%
                    """
                }
            }
        }
    }
}
