pipeline {
    agent any

    stages {
        stage('System Info') {
            steps {
                script {
                    echo 'Gathering system information...'

                    // Check Git version
                    echo 'Checking Git version...'
                    sh 'git --version'

                    // Check Maven version
                    echo 'Checking Maven version...'
                    sh './mvnw -v'

                    // Check Java version
                    echo 'Checking Java version...'
                    sh 'java -version'
                }
            }
        }

        stage('Checkout') {
            steps {
                script {
                    echo 'Checking out the code from GitHub...'
                }
                // Checkout code from GitHub using token authentication and specify the branch
                git branch: 'main', credentialsId: 'github-login', url: 'https://github.com/Ravikumar-Pawar/studentmanagement.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    echo 'Building the project...'
                }
                // Build the project using Maven wrapper
                sh './mvnw clean install'
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo 'Deploying and running on local system...'

                    // Define the local deployment directory and log directory
                    def deployDir = '/home/rpawar/Desktop/deployment'
                    def logDir = "${deployDir}/logs"

                    // Ensure the deployment and log directories exist
                    sh "mkdir -p ${deployDir}"
                    sh "mkdir -p ${logDir}"

                    // Kill any existing processes on port 8081
                    echo 'Checking for and killing any processes running on port 8081...'
                    sh '''
                        if lsof -i :8081; then
                            echo "Killing processes running on port 8081..."
                            lsof -t -i :8081 | xargs kill -9
                        else
                            echo "No processes running on port 8081."
                        fi
                    '''

                    // Copy the built artifacts to the deployment directory
                    sh "cp -r target/* ${deployDir}/"

                    // Change to the deployment directory
                    dir(deployDir) {
                        // Start the application in the background
                        echo 'Starting the application in the background...'
                        sh "nohup java -jar studentmanagement-0.0.1-SNAPSHOT.jar > logs/app.log 2>&1 &"
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
