#!/usr/bin/env groovy

// params
def nodeLabel = 'ops-alfred3-aws'

// variables
def repositoryKey = 'skin-config'
def numToKeepStr = '10'
def eventKeyStatic

pipeline {

    agent {
        node { label "${nodeLabel}" }
    }

    tools {
        nodejs 'Node 8.11.3'
    }

    options {
        timestamps()
        ansiColor('xterm')
        buildDiscarder(logRotator(numToKeepStr: numToKeepStr))
    }

    stages {

        stage('Run Main build') {
            steps {
                script {
                    echo "Run Main build"
                    if (env.CHANGE_BRANCH) {
                        echo "Branch Source Name: ${env.CHANGE_TITLE}"
                        echo "Branch Target Name: ${env.CHANGE_TARGET}"
                        echo "Commit SHA: ${env.GIT_COMMIT}"
                        echo "Event Key: ${eventKeyStatic}"
                        build job: 'Playback-Web-CI/skin-config-commits-pull-requests', parameters: [
                        string(name: 'commitHash', value: env.GIT_COMMIT),
                        string(name: 'branchNameFrom', value: env.CHANGE_BRANCH),
                        string(name: 'branchNameTo', value: env.CHANGE_TARGET),
                        string(name: 'eventKey', value: "pr")]
                    }                       
                    else {
                        echo "Branch Name: ${env.GIT_BRANCH}"
                        echo "Commit SHA: ${env.GIT_COMMIT}"
                        echo "Event Key: ${eventKeyStatic}"
                        build job: 'Playback-Web-CI/skin-config-commits-pull-requests', parameters: [
                        string(name: 'commitHash', value: env.GIT_COMMIT),
                        string(name: 'branchNameFrom', value: env.GIT_BRANCH),
                        string(name: 'eventKey', value: "commit")]
                    }
                    sh 'printenv'
                }
            }
        }
    }
}
