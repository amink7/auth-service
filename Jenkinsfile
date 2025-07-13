pipeline {
    agent any

    tools {
        maven 'Maven 3.8.4'    // ⚠️ Usa el nombre exacto que configuraste en Jenkins → Global Tool Configuration
    }

    environment {
        SONARQUBE = 'SonarQube'                 // Nombre que pusiste en Jenkins → Configure System → SonarQube servers
        DOCKER_IMAGE = 'amk77/auth-service'     // Tu repo en DockerHub
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/amink7/auth-service.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE}") {
                    sh 'mvn verify sonar:sonar -Dsonar.projectKey=auth-service'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE:latest .'
            }
        }

        stage('Push to DockerHub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push $DOCKER_IMAGE:latest
                        docker logout
                    '''
                }
            }
        }
    }
}
