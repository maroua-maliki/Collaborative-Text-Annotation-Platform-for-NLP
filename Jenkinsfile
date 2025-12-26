pipeline {
    agent any

    tools {
        maven 'maven_3'
    }

    environment {
        // Changement : On utilise votre adresse IP Wi-Fi et le port 8082
        NEXUS_REGISTRY = "192.168.1.52:8082"
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
                sh 'mvn -U test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build JAR') {
            steps {
                sh 'mvn -U clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                // Création de l'image avec le tag de votre IP
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
                        # Connexion et envoi vers le registre local
                        docker login -u $NEXUS_USER -p $NEXUS_PASS $NEXUS_REGISTRY
                        docker push $NEXUS_REGISTRY/$IMAGE_NAME:$IMAGE_TAG
                    '''
                }
            }
        }
    }

    post {
        success { echo "Pipeline terminée avec succès !" }
        failure { echo "Pipeline échouée. Vérifiez l'IP ou Docker Desktop." }
    }
}