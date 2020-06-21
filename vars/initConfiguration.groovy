#!groovy
def getParametersFromYaml(String fileName, PData){
    // Return PData wich contains all the parametes in yaml format reading by readYaml()
    // pipeline_yaml="${fileName}"
    def yaml_file = 'merged_yamls_updated.yaml'

    def yaml_repo = 'ssh://git@github.com:xiaojias/devops-cicd.git'
    def yaml_credential = 'credential-github-xiaojias'
    def yaml_branch = 'pipeline_std'
    def yaml_dir = 'pipeline_std/templates/example'

    println "${yaml_branch}"
    deleteDir()	
    checkout([$class: 'GitSCM', 
            branches: [[name: "${yaml_branch}"]],   // Regarding environment
            userRemoteConfigs: [[url: "${yaml_repo}"]]],
            credentialsId: "${yaml_credential}"
            )
    logOutput = sh returnStdout: true, script: 'git log -n 1'
    println logOutput

    def yaml_file_fullpath = "${yaml_dir}/${yaml_file}".toLowerCase()
    archiveArtifacts("${yaml_file_fullpath}")

    PData = readYaml(file : "${yaml_file_fullpath}")
}