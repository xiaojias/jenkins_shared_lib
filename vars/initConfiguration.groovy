#!groovy

def getParametersFromYaml(String pathName, String fileName){
    // Retrive yaml file for the parametes in yaml format from Git Repository

    def yaml_file="${fileName}"
    def yaml_dir = "${pathName}"
    // def yaml_dir = 'pipeline_std/templates/example'

    def yaml_repo = 'git@github.com:xiaojias/devops-cicd.git'
    def yaml_credential = 'github-credential-649788479'
    def yaml_branch = 'pipeline_std'

    def PData

    // println "${yaml_branch}"
    deleteDir()	
    checkout([$class: 'GitSCM', 
            branches: [[name: "${yaml_branch}"]],
            userRemoteConfigs: [[credentialsId: "${yaml_credential}", url: "${yaml_repo}"]]]
            )
    logOutput = sh returnStdout: true, script: 'git log -n 1'
    println logOutput
    
    def yaml_file_fullpath = "${yaml_dir}/${yaml_file}".toLowerCase()
    archiveArtifacts("${yaml_file_fullpath}")

    PData = readYaml(file : "${yaml_file_fullpath}")
    deleteDir()    // Clean Data
    writeYaml(file : "${yaml_file}", data: PData)
}
