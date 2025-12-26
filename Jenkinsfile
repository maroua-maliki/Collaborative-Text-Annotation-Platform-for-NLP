pipeline {
    agent any

    // Charge l'outil Docker configuré dans Jenkins (Global Tool Configuration)
    tools {
        dockerTool 'docker'
    }

    environment {
        NEXUS_REGISTRY = "nexus:8081"
        IMAGE_NAME = "java-app"
        IMAGE_TAG = "v1"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master',
                    url: 'https://github.com/maroua-maliki/Collaborative-Text-Annotation-Platform-for-NLP.git',
                    credentialsId: 'github-token'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build JAR') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                // Utilisation de sudo si nécessaire ou vérification du binaire
                sh 'docker build -t $NEXUS_REGISTRY/$IMAGE_NAME:$IMAGE_TAG .'
            }
        }

        stage('Push to Nexus') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-creds',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {
                    sh '''
                        docker login -u $NEXUS_USER -p $NEXUS_PASS $NEXUS_REGISTRY
                        docker push $NEXUS_REGISTRY/$IMAGE_NAME:$IMAGE_TAG
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline terminée avec succès !"
        }
        failure {
            echo "Pipeline échouée. Vérifie l'installation de Docker sur l'agent Jenkins."
        }
    }
}