pipeline {
    agent any

    environment {
        // === À CONFIGURER ===
        // 1. Remplacez par l'URL de votre instance SonarQube
        SONAR_HOST_URL    = "http://192.168.1.17:9000"
        // 2. Choisissez une clé unique pour votre projet SonarQube
        SONAR_PROJECT_KEY = "ensah-platform-annotation"
        // 3. Remplacez par l'URL et le port de votre registre Docker Nexus
        NEXUS_URL         = "192.168.1.17:8082" 
        // 4. Donnez un nom à votre image Docker
        DOCKER_IMAGE_NAME = "platform-annotation"
    }

    stages {
        // --- ÉTAPE 1: RÉCUPÉRATION DU CODE ---
        stage('Checkout') {
            steps {
                echo 'Récupération du code depuis GitHub...'
                // Utilise le credential 'github-token' que vous avez créé dans Jenkins
                withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
                    // 5. === À CONFIGURER === 
                    // Remplacez par l'URL de votre projet GitHub. 
                    // Ce format permet de s'authentifier avec un token pour les dépôts privés.
                    git url: 'https://user:${GITHUB_TOKEN}@github.com/Maroua-rin/Collaborative-Text-Annotation-Platform-for-NLP.git', branch: 'main'
                }
            }
        }

        // --- ÉTAPE 2: TESTS UNITAIRES ---
        stage('Test') {
            steps {
                echo 'Lancement des tests unitaires avec Maven...'
                // 'bat' est pour Windows. Si votre agent Jenkins est sur Linux, utilisez 'sh'
                bat 'mvnw.cmd clean test'
            }
            post {
                always {
                    echo 'Publication des résultats des tests...'
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        // --- ÉTAPE 3: ANALYSE QUALITÉ ---
        stage('SonarQube Analysis') {
            steps {
                echo "Lancement de l'analyse SonarQube..."
                // Utilise le credential 'sonar-token'
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    bat "mvnw.cmd sonar:sonar -Dsonar.login=${SONAR_TOKEN} -Dsonar.host.url=${env.SONAR_HOST_URL} -Dsonar.projectKey=${env.SONAR_PROJECT_KEY}"
                }
            }
        }

        // --- ÉTAPE 4: BUILD IMAGE DOCKER ---
        stage('Build Image') {
            steps {
                echo 'Construction de l\'image Docker...'
                // Le tag de l l'image inclut l URL de Nexus et le numéro de build pour l unicité
                bat "docker build -t ${env.NEXUS_URL}/${env.DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} ."
            }
        }
        
        // --- ÉTAPE 5: PUSH VERS NEXUS ---
        stage('Push to Nexus') {
            steps {
                echo "Poussée de l image vers le registre Nexus..."
                // Utilise le credential 'nexus-creds' (user/pass)
                withCredentials([usernamePassword(credentialsId: 'nexus-creds', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    try {
                        bat "docker login -u ${NEXUS_USER} -p ${NEXUS_PASS} ${env.NEXUS_URL}"
                        bat "docker push ${env.NEXUS_URL}/${env.DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}"
                    } finally {
                        // Toujours se déconnecter, même si le push échoue
                        bat "docker logout ${env.NEXUS_URL}"
                    }
                }
            }
        }

        // --- ÉTAPE 6: DÉPLOIEMENT KUBERNETES ---
        stage('Deploy to K8s') {
            steps {
                echo 'Déploiement sur Kubernetes...'
                // Utilise le credential 'k8s-creds' qui contient votre fichier kubeconfig
                withKubeconfig(credentialsId: 'k8s-creds') {
                    // Applique la configuration définie dans le dossier k8s/
                    // kubectl doit être installé sur l agent Jenkins
                    bat 'kubectl apply -f k8s/'
                }
            }
        }
    }

    post {
        always {
            echo 'Nettoyage du workspace...'
            cleanWs()
        }
    }
}